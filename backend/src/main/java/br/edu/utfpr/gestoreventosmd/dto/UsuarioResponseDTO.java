package br.edu.utfpr.gestoreventosmd.dto;

import br.edu.utfpr.gestoreventosmd.model.StatusUsuario;
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
public class UsuarioResponseDTO {

    private UUID id;
    private String nome;
    private String email;
    private StatusUsuario status;
    private LocalDateTime criadoEm;
}
