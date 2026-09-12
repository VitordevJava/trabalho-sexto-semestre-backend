package br.com.bemdoar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FrenteUmIntegracaoTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void cicloCompletoSoAtualizaProgressoAoReceber() throws Exception {
        String maria = token("maria@email.com", "maria123");
        String admin = token("admin@bemdoar.com", "admin123");
        JsonNode necessidade = primeiraNecessidade();
        long necessidadeId = necessidade.get("id").asLong();
        int quantidadeInicial = necessidade.get("quantidadeRecebida").asInt();

        JsonNode doacao = criarDoacao(maria, necessidadeId, 10);
        long doacaoId = doacao.get("id").asLong();

        mvc.perform(get("/api/doacoes/minhas").header("Authorization", bearer(maria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(doacaoId))
                .andExpect(jsonPath("$[0].situacao").value("PENDENTE"));
        progresso(necessidadeId, quantidadeInicial);

        mvc.perform(patch("/api/doacoes/{id}/confirmar", doacaoId)
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("CONFIRMADA"));
        progresso(necessidadeId, quantidadeInicial);

        mvc.perform(patch("/api/doacoes/{id}/receber", doacaoId)
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("RECEBIDA"));
        progresso(necessidadeId, quantidadeInicial + 10);

        mvc.perform(patch("/api/doacoes/{id}/cancelar", doacaoId)
                        .header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"nao deve cancelar\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Esta transicao nao e permitida."));

        mvc.perform(get("/api/transparencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doacoesRecebidas").isNumber());
    }

    @Test
    void protegeDetalheDeOutroDoadorEExigeMotivoDoAdministrador() throws Exception {
        String maria = token("maria@email.com", "maria123");
        String admin = token("admin@bemdoar.com", "admin123");
        cadastrarJoao();
        String joao = token("joao@email.com", "joao1234");
        long doacaoId = criarDoacao(maria, primeiraNecessidade().get("id").asLong(), 1)
                .get("id").asLong();

        mvc.perform(get("/api/doacoes/{id}", doacaoId)
                        .header("Authorization", bearer(joao)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/doacoes/{id}", doacaoId)
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk());

        mvc.perform(patch("/api/doacoes/{id}/cancelar", doacaoId)
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Informe o motivo do cancelamento."));
    }

    @Test
    void rejeitaQuantidadeNaoPositiva() throws Exception {
        String maria = token("maria@email.com", "maria123");
        long necessidadeId = primeiraNecessidade().get("id").asLong();
        String corpo = corpoDoacao(necessidadeId, 0);
        mvc.perform(post("/api/doacoes")
                        .header("Authorization", bearer(maria))
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.quantidade").exists());
    }

    @Test
    void rejeitaDoacaoParaNecessidadeEncerrada() throws Exception {
        String maria = token("maria@email.com", "maria123");
        String admin = token("admin@bemdoar.com", "admin123");
        long necessidadeId = primeiraNecessidade().get("id").asLong();

        mvc.perform(patch("/api/necessidades/{id}/encerrar", necessidadeId)
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
        mvc.perform(post("/api/doacoes")
                        .header("Authorization", bearer(maria))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDoacao(necessidadeId, 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Este destino nao esta recebendo novas contribuicoes."));
    }

    private JsonNode criarDoacao(String token, long necessidadeId, int quantidade) throws Exception {
        String resposta = mvc.perform(post("/api/doacoes")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDoacao(necessidadeId, quantidade)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.situacao").value("PENDENTE"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(resposta);
    }

    private String corpoDoacao(long necessidadeId, int quantidade) {
        return """
                {"necessidadeId":%d,"item":"Cesta de teste","quantidade":%d,
                 "observacao":"Teste automatizado","formaEntrega":"ENTREGA_NA_INSTITUICAO"}
                """.formatted(necessidadeId, quantidade);
    }

    private JsonNode primeiraNecessidade() throws Exception {
        String resposta = mvc.perform(get("/api/necessidades"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(resposta).get("content").get(0);
    }

    private void progresso(long necessidadeId, int esperado) throws Exception {
        mvc.perform(get("/api/necessidades/{id}", necessidadeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeRecebida").value(esperado));
    }

    private void cadastrarJoao() throws Exception {
        String corpo = """
                {"nome":"Joao Teste","email":"joao@email.com","senha":"joao1234",
                 "confirmacaoSenha":"joao1234","telefone":"11999999999",
                 "dataNascimento":"1995-05-10","aceiteTermos":true}
                """;
        mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());
    }

    private String token(String email, String senha) throws Exception {
        String corpo = "{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}";
        String resposta = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(resposta).get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
