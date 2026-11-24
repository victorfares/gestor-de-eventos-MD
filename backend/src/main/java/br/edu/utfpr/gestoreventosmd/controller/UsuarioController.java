package br.edu.utfpr.gestoreventosmd.controller;

import br.edu.utfpr.gestoreventosmd.dto.UsuarioRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.UsuarioResponseDTO;
import br.edu.utfpr.gestoreventosmd.service.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO criado = usuarioService.criar(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + criado.getId())).body(criado);
    }

    @GetMapping("/solicitacoes")
    public ResponseEntity<List<UsuarioResponseDTO>> listarSolicitacoes() {
        return ResponseEntity.ok(usuarioService.listarSolicitacoes());
    }

    @PostMapping("/solicitacoes/{id}/aprovar")
    public ResponseEntity<UsuarioResponseDTO> aprovar(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.aprovar(id));
    }

    @DeleteMapping("/solicitacoes/{id}")
    public ResponseEntity<Void> recusar(@PathVariable UUID id) {
        usuarioService.recusar(id);
        return ResponseEntity.noContent().build();
    }
}
