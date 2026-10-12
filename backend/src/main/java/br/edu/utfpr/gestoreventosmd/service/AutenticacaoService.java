package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.LoginRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.LoginResponseDTO;
import br.edu.utfpr.gestoreventosmd.dto.UsuarioResponseDTO;
import br.edu.utfpr.gestoreventosmd.model.Usuario;
import br.edu.utfpr.gestoreventosmd.repository.UsuarioRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtEncoder jwtEncoder;
    private final Duration validadeToken;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiracao-horas}") long expiracaoHoras) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtEncoder = jwtEncoder;
        this.validadeToken = Duration.ofHours(expiracaoHoras);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha()));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(validadeToken);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gestor-eventos-md")
                .subject(usuario.getId().toString())
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .claim("nome", usuario.getNome())
                .claim("email", usuario.getEmail())
                .claim("roles", List.of("ADMIN"))
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();

        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .expiraEm(expiraEm)
                .usuario(UsuarioResponseDTO.builder()
                        .id(usuario.getId())
                        .nome(usuario.getNome())
                        .email(usuario.getEmail())
                        .build())
                .build();
    }
}
