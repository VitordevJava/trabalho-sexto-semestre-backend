package br.com.bemdoar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BemDoarIntegracaoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String token(String email, String senha) throws Exception {
        String resposta = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode corpo = json.readTree(resposta);
        return corpo.get("token").asText();
    }

    @Test void areasPublicasFuncionamSemLogin() throws Exception {
        mvc.perform(get("/api/necessidades")).andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray());
        mvc.perform(get("/api/campanhas")).andExpect(status().isOk());
        mvc.perform(get("/api/oportunidades")).andExpect(status().isOk());
    }

    @Test void perfilExigeAutenticacao() throws Exception {
        mvc.perform(get("/api/auth/eu")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/auth/conta")).andExpect(status().isUnauthorized());
    }

    @Test void administradorAcessaGestaoEUsuarioNao() throws Exception {
        mvc.perform(get("/api/categorias").header("Authorization", "Bearer " + token("admin@bemdoar.com", "admin123")))
                .andExpect(status().isOk());
        mvc.perform(get("/api/categorias").header("Authorization", "Bearer " + token("maria@email.com", "maria123")))
                .andExpect(status().isForbidden());
    }

    @Test void validacaoDeCampanhaRejeitaPeriodoInvertido() throws Exception {
        String corpo = """
                {"titulo":"Teste","descricao":"Descricao","objetivo":"Objetivo","dataInicio":"2026-10-10","dataFim":"2026-10-01","metaMinima":10}
                """;
        mvc.perform(post("/api/campanhas").header("Authorization", "Bearer " + token("admin@bemdoar.com", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
    }
}
