package br.com.bemdoar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RF05 - classificacao reutilizavel das necessidades.
 * RN05: o nome nao pode repetir (ignorando maiusculas/minusculas).
 *       Categoria em uso nao e apagada: e desativada.
 *
 * ESTA E A ENTIDADE-MOLDE. O CRUD dela e o modelo que todos copiam.
 */
@Entity
@Table(name = "categoria_necessidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaNecessidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo;
}
