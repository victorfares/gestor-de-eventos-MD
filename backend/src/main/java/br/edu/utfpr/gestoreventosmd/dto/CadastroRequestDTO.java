package br.edu.utfpr.gestoreventosmd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadastroRequestDTO {

    @NotNull(message = "Informe seu nome completo (nome e sobrenome).")
    @Pattern(regexp = "^\\s*\\S+(\\s+\\S+)+\\s*$", message = "Informe seu nome completo (nome e sobrenome).")
    @Size(max = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
    private String senha;
}
