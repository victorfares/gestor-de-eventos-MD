package br.edu.utfpr.gestoreventosmd.controller;

import br.edu.utfpr.gestoreventosmd.dto.LoginRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.LoginResponseDTO;
import br.edu.utfpr.gestoreventosmd.service.AutenticacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(autenticacaoService.login(dto));
    }
}
