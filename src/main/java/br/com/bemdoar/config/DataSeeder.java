package br.com.bemdoar.config;

import br.com.bemdoar.entity.CategoriaNecessidade;
import br.com.bemdoar.entity.Instituicao;
import br.com.bemdoar.entity.Necessidade;
import br.com.bemdoar.entity.Usuario;
import br.com.bemdoar.entity.Campanha;
import br.com.bemdoar.entity.OportunidadeVoluntariado;
import br.com.bemdoar.entity.AcaoSocial;
import br.com.bemdoar.entity.Comunicado;
import br.com.bemdoar.enums.PerfilUsuario;
import br.com.bemdoar.enums.Prioridade;
import br.com.bemdoar.enums.SituacaoNecessidade;
import br.com.bemdoar.enums.SituacaoCampanha;
import br.com.bemdoar.enums.SituacaoOportunidade;
import br.com.bemdoar.enums.SituacaoAcaoSocial;
import br.com.bemdoar.enums.SituacaoComunicado;
import br.com.bemdoar.enums.TipoComunicado;
import br.com.bemdoar.repository.CategoriaNecessidadeRepository;
import br.com.bemdoar.repository.InstituicaoRepository;
import br.com.bemdoar.repository.NecessidadeRepository;
import br.com.bemdoar.repository.UsuarioRepository;
import br.com.bemdoar.repository.CampanhaRepository;
import br.com.bemdoar.repository.OportunidadeVoluntariadoRepository;
import br.com.bemdoar.repository.AcaoSocialRepository;
import br.com.bemdoar.repository.ComunicadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cria os dados iniciais na PRIMEIRA vez que o projeto sobe.
 * Se ja existir dado no banco, nao faz nada (pode rodar quantas vezes quiser).
 *
 * RN02 - a conta ADMINISTRADOR e configurada previamente, nunca nasce
 *        do cadastro publico.
 *
 * Esta classe ja esta pronta. NINGUEM precisa alterar este arquivo.
 *
 * ================= CONTAS DE TESTE DO PROJETO =================
 *   ADMIN    admin@bemdoar.com    /  admin123
 *   USUARIO  maria@email.com      /  maria123
 * =============================================================
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final CategoriaNecessidadeRepository categoriaRepository;
    private final NecessidadeRepository necessidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final CampanhaRepository campanhaRepository;
    private final OportunidadeVoluntariadoRepository oportunidadeRepository;
    private final AcaoSocialRepository acaoRepository;
    private final ComunicadoRepository comunicadoRepository;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      InstituicaoRepository instituicaoRepository,
                      CategoriaNecessidadeRepository categoriaRepository,
                      NecessidadeRepository necessidadeRepository,
                      PasswordEncoder passwordEncoder,
                      CampanhaRepository campanhaRepository,
                      OportunidadeVoluntariadoRepository oportunidadeRepository,
                      AcaoSocialRepository acaoRepository,
                      ComunicadoRepository comunicadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.categoriaRepository = categoriaRepository;
        this.necessidadeRepository = necessidadeRepository;
        this.passwordEncoder = passwordEncoder;
        this.campanhaRepository = campanhaRepository;
        this.oportunidadeRepository = oportunidadeRepository;
        this.acaoRepository = acaoRepository;
        this.comunicadoRepository = comunicadoRepository;
    }

    @Override
    public void run(String... args) {
        criarUsuarios();
        criarInstituicao();
        criarCategoriasENecessidades();
        criarDadosDemonstrativos();
    }

    private void criarUsuarios() {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome("Administrador BemDoar");
        admin.setEmail("admin@bemdoar.com");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setTelefone("(11) 90000-0000");
        admin.setDataNascimento(LocalDate.of(1990, 1, 1));
        admin.setPerfil(PerfilUsuario.ADMINISTRADOR);
        admin.setAtivo(true);
        admin.setAceiteTermos(true);
        admin.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(admin);

        Usuario comum = new Usuario();
        comum.setNome("Maria Doadora");
        comum.setEmail("maria@email.com");
        comum.setSenha(passwordEncoder.encode("maria123"));
        comum.setTelefone("(11) 91111-1111");
        comum.setDataNascimento(LocalDate.of(1998, 5, 20));
        comum.setPerfil(PerfilUsuario.USUARIO);
        comum.setAtivo(true);
        comum.setAceiteTermos(true);
        comum.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(comum);

        System.out.println(">>> BemDoar: contas criadas - admin@bemdoar.com/admin123 e maria@email.com/maria123");
    }

    private void criarInstituicao() {
        if (instituicaoRepository.count() > 0) {
            return;
        }

        Instituicao i = new Instituicao();
        i.setNome("Instituicao BemDoar");
        i.setDescricao("Organizacao social dedicada ao apoio de familias em situacao de vulnerabilidade.");
        i.setMissao("Promover dignidade por meio de doacoes, voluntariado e acoes sociais.");
        i.setAreaAtuacao("Assistencia social");
        i.setPublicoAtendido("Familias em situacao de vulnerabilidade social");
        i.setCep("00000-000");
        i.setLogradouro("Rua Exemplo");
        i.setNumero("100");
        i.setBairro("Centro");
        i.setCidade("Sao Paulo");
        i.setEstado("SP");
        i.setTelefone("(11) 3000-0000");
        i.setEmail("contato@bemdoar.com");
        i.setHorarioAtendimento("Segunda a sexta, 9h as 17h");
        instituicaoRepository.save(i);
    }

    private void criarCategoriasENecessidades() {
        if (categoriaRepository.count() > 0) {
            return;
        }

        CategoriaNecessidade alimentos = novaCategoria("Alimentos", "Alimentos nao pereciveis");
        CategoriaNecessidade roupas = novaCategoria("Roupas e agasalhos", "Vestuario em bom estado");
        CategoriaNecessidade higiene = novaCategoria("Higiene pessoal", "Itens de higiene e limpeza");
        novaCategoria("Material escolar", "Cadernos, lapis e mochilas");

        categoriaRepository.saveAll(List.of(alimentos, roupas, higiene));

        novaNecessidade("Cestas basicas", "Cestas basicas completas para 50 familias.",
                alimentos, 50, Prioridade.ALTA);
        novaNecessidade("Cobertores", "Cobertores de solteiro para a campanha de inverno.",
                roupas, 100, Prioridade.MEDIA);
        novaNecessidade("Kits de higiene", "Kit com sabonete, pasta de dente e shampoo.",
                higiene, 80, Prioridade.BAIXA);
    }

    private CategoriaNecessidade novaCategoria(String nome, String descricao) {
        CategoriaNecessidade c = new CategoriaNecessidade();
        c.setNome(nome);
        c.setDescricao(descricao);
        c.setAtivo(true);
        return categoriaRepository.save(c);
    }

    private void novaNecessidade(String titulo, String descricao,
                                 CategoriaNecessidade categoria,
                                 int quantidade, Prioridade prioridade) {
        Necessidade n = new Necessidade();
        n.setTitulo(titulo);
        n.setDescricao(descricao);
        n.setCategoria(categoria);
        n.setQuantidadeNecessaria(quantidade);
        n.setQuantidadeRecebida(0);
        n.setPrioridade(prioridade);
        n.setSituacao(SituacaoNecessidade.ABERTA);
        n.setDataCriacao(LocalDateTime.now());
        necessidadeRepository.save(n);
    }

    private void criarDadosDemonstrativos() {
        if (campanhaRepository.count() == 0) {
            Campanha c = new Campanha();
            c.setTitulo("Campanha de inverno");
            c.setDescricao("Arrecadacao de cobertores e roupas para os meses mais frios.");
            c.setObjetivo("Apoiar familias em situacao de vulnerabilidade durante o inverno.");
            c.setDataInicio(LocalDate.now().minusDays(5));
            c.setDataFim(LocalDate.now().plusMonths(2));
            c.setMetaMinima(100);
            c.setSituacao(SituacaoCampanha.ATIVA);
            necessidadeRepository.findAll().stream().filter(n -> n.getTitulo().equals("Cobertores")).findFirst()
                    .ifPresent(n -> c.getNecessidades().add(n));
            campanhaRepository.save(c);
        }
        if (oportunidadeRepository.count() == 0) {
            OportunidadeVoluntariado o = new OportunidadeVoluntariado();
            o.setTitulo("Organizacao de doacoes");
            o.setDescricao("Ajude a separar e organizar os itens recebidos.");
            o.setAtividade("Triagem de doacoes"); o.setVagas(10);
            o.setDataAtividade(LocalDate.now().plusWeeks(2)); o.setHorario("09:00 as 13:00");
            o.setLocal("Sede da Instituicao BemDoar"); o.setIdadeMinima(16);
            o.setRequisitos("Disposicao para trabalho em equipe."); o.setSituacao(SituacaoOportunidade.ABERTA);
            oportunidadeRepository.save(o);
        }
        if (acaoRepository.count() == 0) {
            AcaoSocial a = new AcaoSocial();
            a.setTitulo("Dia da solidariedade"); a.setDescricao("Entrega de kits e atividades para as familias atendidas.");
            a.setObjetivo("Promover acolhimento e distribuir os itens arrecadados.");
            a.setDataInicio(LocalDate.now().plusMonths(1)); a.setDataFim(LocalDate.now().plusMonths(1));
            a.setLocal("Centro comunitario"); a.setPublicoAtendido("Familias cadastradas");
            a.setEstimativaBeneficiados(80); a.setSituacao(SituacaoAcaoSocial.PLANEJADA);
            acaoRepository.save(a);
        }
        if (comunicadoRepository.count() == 0) {
            Comunicado c = new Comunicado();
            c.setTitulo("Bem-vindo ao BemDoar"); c.setConteudo("Acompanhe nossas campanhas, oportunidades e acoes sociais.");
            c.setTipo(TipoComunicado.PUBLICO); c.setSituacao(SituacaoComunicado.PUBLICADO);
            c.setDataCriacao(LocalDateTime.now()); c.setDataPublicacao(LocalDateTime.now());
            comunicadoRepository.save(c);
        }
    }
}
