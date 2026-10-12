package br.edu.utfpr.gestoreventosmd.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    private String tipo;
    private Instant expiraEm;
    private UsuarioResponseDTO usuario;
}
