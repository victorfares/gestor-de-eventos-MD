package br.edu.utfpr.gestoreventosmd.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String titulo;

    @Size(max = 2000)
    private String descricao;

    @NotNull
    private LocalDateTime data;

    @NotBlank
    @Size(max = 200)
    private String local;

    @NotNull
    @Min(1)
    private Integer limiteVagas;
}
