package br.edu.utfpr.gestoreventosmd.controller;

import br.edu.utfpr.gestoreventosmd.dto.ArquivoExportadoDTO;
import br.edu.utfpr.gestoreventosmd.dto.EventoRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.EventoResponseDTO;
import br.edu.utfpr.gestoreventosmd.dto.InscricaoResponseDTO;
import br.edu.utfpr.gestoreventosmd.dto.PeriodoEvento;
import br.edu.utfpr.gestoreventosmd.service.EventoService;
import br.edu.utfpr.gestoreventosmd.service.InscricaoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final InscricaoService inscricaoService;

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listar(
            @RequestParam(required = false) PeriodoEvento periodo) {
        return ResponseEntity.ok(eventoService.listar(periodo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(eventoService.buscarPorId(id));
    }

    @GetMapping("/{id}/inscricoes")
    public ResponseEntity<List<InscricaoResponseDTO>> listarInscritas(@PathVariable UUID id) {
        return ResponseEntity.ok(inscricaoService.recuperarInscritosPorEvento(id));
    }

    @GetMapping("/{id}/inscricoes/exportar")
    public ResponseEntity<byte[]> exportarInscritas(@PathVariable UUID id) {
        ArquivoExportadoDTO arquivo = inscricaoService.exportarInscritosCsv(id);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(arquivo.nomeArquivo(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(arquivo.conteudo());
    }

    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(
            @Valid @RequestBody EventoRequestDTO dto,
            @AuthenticationPrincipal Jwt usuarioLogado) {
        EventoResponseDTO criado = eventoService.criar(dto, UUID.fromString(usuarioLogado.getSubject()));
        URI localizacao = URI.create("/api/eventos/" + criado.getId());
        return ResponseEntity.created(localizacao).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody EventoRequestDTO dto) {
        return ResponseEntity.ok(eventoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        eventoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
