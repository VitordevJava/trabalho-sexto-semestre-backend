package br.com.bemdoar.service;

import br.com.bemdoar.dto.*;
import br.com.bemdoar.entity.Usuario;
import br.com.bemdoar.enums.PerfilUsuario;
import br.com.bemdoar.exception.RecursoNaoEncontradoException;
import br.com.bemdoar.exception.RegraNegocioException;
import br.com.bemdoar.repository.UsuarioRepository;
import br.com.bemdoar.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * RF01, RF02, RF03 - cadastro, login e perfil proprio.
 *
 * Esta classe ja esta pronta. NINGUEM precisa alterar este arquivo.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ---------------------------------------------------------------
    // RF01 - cadastro publico. RN02: sempre nasce USUARIO.
    // ---------------------------------------------------------------
    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {

        if (!request.senha().equals(request.confirmacaoSenha())) {
            throw new RegraNegocioException("As senhas nao coincidem.");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Este e-mail ja esta em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha())); // RN04
        usuario.setTelefone(request.telefone());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setPerfil(PerfilUsuario.USUARIO);
        usuario.setAtivo(true);
        usuario.setAceiteTermos(true);
        usuario.setDataCadastro(LocalDateTime.now());

        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    // ---------------------------------------------------------------
    // RF02 - login. Mensagem generica de proposito: nao revela se o
    // e-mail existe (criterio de aceite do RF02).
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha invalidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new RegraNegocioException("E-mail ou senha invalidos.");
        }

        if (!usuario.isAtivo()) {
            throw new RegraNegocioException("Conta indisponivel para acesso.");
        }

        String token = jwtService.gerarToken(usuario.getEmail(), usuario.getPerfil().name());

        return new LoginResponse(token, usuario.getId(), usuario.getNome(),
                usuario.getEmail(), usuario.getPerfil().name());
    }

    // ---------------------------------------------------------------
    // RF03 - perfil proprio
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public UsuarioResponse verPerfil(Long usuarioId) {
        return UsuarioResponse.de(buscarEntidade(usuarioId));
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(Long usuarioId, AtualizarPerfilRequest request) {
        Usuario usuario = buscarEntidade(usuarioId);

        // RN03 - o e-mail continua unico depois da troca
        if (usuarioRepository.existsByEmailAndIdNot(request.email(), usuarioId)) {
            throw new RegraNegocioException("Este e-mail ja esta em uso.");
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setTelefone(request.telefone());
        // dataNascimento NAO entra aqui: e somente leitura para o usuario (RF03).

        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public void alterarSenha(Long usuarioId, AlterarSenhaRequest request) {
        Usuario usuario = buscarEntidade(usuarioId);

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta.");
        }
        if (!request.novaSenha().equals(request.confirmacaoSenha())) {
            throw new RegraNegocioException("As senhas nao coincidem.");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
    }

    /** RF03 - desativar preserva todo o historico (RN17). */
    @Transactional
    public void desativarConta(Long usuarioId) {
        Usuario usuario = buscarEntidade(usuarioId);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado."));
    }
}
