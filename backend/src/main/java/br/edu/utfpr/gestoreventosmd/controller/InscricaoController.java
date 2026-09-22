package br.edu.utfpr.gestoreventosmd.controller;

import br.edu.utfpr.gestoreventosmd.dto.InscricaoRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.InscricaoResponseDTO;
import br.edu.utfpr.gestoreventosmd.service.InscricaoService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoService inscricaoService;

    @PostMapping
    public ResponseEntity<InscricaoResponseDTO> registrar(@Valid @RequestBody InscricaoRequestDTO dto) {
        InscricaoResponseDTO criada = inscricaoService.registrar(dto);
        URI localizacao = URI.create("/api/inscricoes/" + criada.getId());
        return ResponseEntity.created(localizacao).body(criada);
    }
}
