package br.edu.utfpr.gestoreventosmd.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResponseDTO {

    private UUID id;
    private String titulo;
    private String descricao;
    private LocalDateTime data;
    private String local;
    private Integer limiteVagas;
    private Long vagasDisponiveis;
    private UUID usuarioId;
}
