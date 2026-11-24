package br.edu.utfpr.gestoreventosmd.controller;

import br.edu.utfpr.gestoreventosmd.dto.CadastroRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.LoginRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.LoginResponseDTO;
import br.edu.utfpr.gestoreventosmd.dto.UsuarioResponseDTO;
import br.edu.utfpr.gestoreventosmd.service.AutenticacaoService;
import br.edu.utfpr.gestoreventosmd.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(autenticacaoService.login(dto));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> solicitarCadastro(@Valid @RequestBody CadastroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.solicitarCadastro(dto));
    }
}
