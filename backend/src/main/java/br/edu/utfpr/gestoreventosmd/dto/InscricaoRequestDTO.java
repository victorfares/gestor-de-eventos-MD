package br.edu.utfpr.gestoreventosmd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscricaoRequestDTO {

    @NotNull(message = "Informe seu nome completo (nome e sobrenome).")
    @Pattern(regexp = "^\\s*\\S+(\\s+\\S+)+\\s*$", message = "Informe seu nome completo (nome e sobrenome).")
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(max = 80)
    private String idadeOuSerie;

    @NotNull
    private UUID eventoId;
}
