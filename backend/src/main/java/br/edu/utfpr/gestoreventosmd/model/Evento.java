package br.edu.utfpr.gestoreventosmd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @Size(max = 2000)
    @Column(length = 2000)
    private String descricao;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime data;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String local;

    @NotNull
    @Min(1)
    @Column(name = "limite_vagas", nullable = false)
    private Integer limiteVagas;
}
