package br.edu.utfpr.gestoreventosmd.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampoErro {

    private String campo;
    private String mensagem;
}
