package br.com.bemdoar.entity;

import br.com.bemdoar.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * RF01, RF02, RF03 - conta de acesso ao BemDoar.
 * RN03: o e-mail e unico no sistema inteiro.
 * RN04: a senha NUNCA e gravada em texto puro (ver UsuarioService).
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Sempre o hash BCrypt. Nunca a senha digitada. */
    @Column(nullable = false, length = 100)
    private String senha;

    @Column(length = 20)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "aceite_termos", nullable = false)
    private boolean aceiteTermos;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
}
