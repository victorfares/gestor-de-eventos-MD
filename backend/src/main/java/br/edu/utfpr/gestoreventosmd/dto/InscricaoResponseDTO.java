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
public class InscricaoResponseDTO {

    private UUID id;
    private String nome;
    private String email;
    private String idadeOuSerie;
    private LocalDateTime dataInscricao;
    private UUID eventoId;
}
