package br.edu.utfpr.gestoreventosmd.exception;

import br.edu.utfpr.gestoreventosmd.service.EventoFotoService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> badCredentials(
            BadCredentialsException e,
            HttpServletRequest request) {
        return montarErro(HttpStatus.UNAUTHORIZED, "Não autorizado", "E-mail ou senha inválidos.", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StandardError> authentication(
            AuthenticationException e,
            HttpServletRequest request) {
        return montarErro(
                HttpStatus.UNAUTHORIZED,
                "Não autorizado",
                "Faça login para acessar este recurso. Se já fez, seu acesso pode ter expirado.",
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> accessDenied(
            AccessDeniedException e,
            HttpServletRequest request) {
        return montarErro(
                HttpStatus.FORBIDDEN,
                "Acesso negado",
                "Você não tem permissão para acessar este recurso.",
                request);
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<StandardError> uploadMuitoGrande(
            MaxUploadSizeExceededException e,
            HttpServletRequest request) {
        return montarErro(
                HttpStatus.CONTENT_TOO_LARGE,
                "Arquivo muito grande",
                "As fotos enviadas passam do tamanho permitido. Cada foto pode ter no máximo "
                        + EventoFotoService.TAMANHO_MAXIMO_MB + " MB, e o evento aceita até "
                        + EventoFotoService.LIMITE_FOTOS + " fotos.",
                request);
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
