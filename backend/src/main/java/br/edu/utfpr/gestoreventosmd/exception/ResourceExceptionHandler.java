package br.edu.utfpr.gestoreventosmd.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(
            ResourceNotFoundException e,
            HttpServletRequest request) {
        return montarErro(HttpStatus.NOT_FOUND, "Recurso não encontrado", e.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> business(
            BusinessException e,
            HttpServletRequest request) {
        return montarErro(HttpStatus.CONFLICT, "Regra de negócio violada", e.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> messageNotReadable(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {
        return montarErro(
                HttpStatus.BAD_REQUEST,
                "Requisição malformada",
                "Corpo da requisição inválido. Verifique o JSON e os formatos dos campos "
                        + "(datas no padrão yyyy-MM-ddTHH:mm:ss e ids no formato UUID).",
                request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> argumentTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        String message = "Valor inválido para o parâmetro '" + e.getName() + "': " + e.getValue();
        Class<?> tipo = e.getRequiredType();
        if (tipo != null && tipo.isEnum()) {
            message += ". Valores aceitos: " + Arrays.stream(tipo.getEnumConstants())
                    .map(valor -> valor.toString().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(", "));
        }
        return montarErro(HttpStatus.BAD_REQUEST, "Parâmetro inválido", message, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Erro de validação");
        err.setMessage("Um ou mais campos estão inválidos. Verifique a lista de erros.");
        err.setPath(request.getRequestURI());

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.adicionarErro(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    private ResponseEntity<StandardError> montarErro(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request) {
        StandardError err = StandardError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(err);
    }
}
