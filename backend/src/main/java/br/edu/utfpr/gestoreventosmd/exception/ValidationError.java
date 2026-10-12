package br.edu.utfpr.gestoreventosmd.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ValidationError extends StandardError {

    private final List<CampoErro> errors = new ArrayList<>();

    public void adicionarErro(String campo, String mensagem) {
        errors.add(new CampoErro(campo, mensagem));
    }
}
