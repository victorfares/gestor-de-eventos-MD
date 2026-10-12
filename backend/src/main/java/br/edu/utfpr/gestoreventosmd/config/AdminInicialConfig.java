package br.edu.utfpr.gestoreventosmd.config;

import br.edu.utfpr.gestoreventosmd.model.Usuario;
import br.edu.utfpr.gestoreventosmd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInicialConfig {

    @Bean
    public ApplicationRunner criarAdminInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.nome}") String nome,
            @Value("${app.admin.email}") String email,
            @Value("${app.admin.senha}") String senha) {
        return args -> {
            if (usuarioRepository.existsByEmailIgnoreCase(email)) {
                return;
            }
            usuarioRepository.save(Usuario.builder()
                    .nome(nome)
                    .email(email)
                    .senha(passwordEncoder.encode(senha))
                    .build());
        };
    }
}
