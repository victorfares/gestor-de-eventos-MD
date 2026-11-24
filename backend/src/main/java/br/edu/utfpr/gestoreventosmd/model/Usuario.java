package br.edu.utfpr.gestoreventosmd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String senha;

    // O valor padrão no banco deixa aprovados os membros que já existiam antes do autocadastro.
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'APROVADO'")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusUsuario status = StatusUsuario.APROVADO;

    // O padrão no banco permite adicionar a coluna em tabelas que já têm membros.
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void registrarCriacao() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}
