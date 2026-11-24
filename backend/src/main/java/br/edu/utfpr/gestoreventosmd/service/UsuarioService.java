package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.CadastroRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.UsuarioRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.UsuarioResponseDTO;
import br.edu.utfpr.gestoreventosmd.exception.BusinessException;
import br.edu.utfpr.gestoreventosmd.exception.ResourceNotFoundException;
import br.edu.utfpr.gestoreventosmd.model.StatusUsuario;
import br.edu.utfpr.gestoreventosmd.model.Usuario;
import br.edu.utfpr.gestoreventosmd.repository.UsuarioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findByStatus(StatusUsuario.APROVADO).stream()
                .sorted(Comparator.comparing(Usuario::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(this::paraResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarSolicitacoes() {
        return usuarioRepository.findByStatus(StatusUsuario.PENDENTE).stream()
                .sorted(Comparator.comparing(Usuario::getCriadoEm,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::paraResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        return paraResponse(salvarNovo(dto.getNome(), dto.getEmail(), dto.getSenha(), StatusUsuario.APROVADO));
    }

    @Transactional
    public UsuarioResponseDTO solicitarCadastro(CadastroRequestDTO dto) {
        return paraResponse(salvarNovo(dto.getNome(), dto.getEmail(), dto.getSenha(), StatusUsuario.PENDENTE));
    }

    @Transactional
    public UsuarioResponseDTO aprovar(UUID id) {
        Usuario usuario = buscarSolicitacao(id);
        usuario.setStatus(StatusUsuario.APROVADO);
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void recusar(UUID id) {
        usuarioRepository.delete(buscarSolicitacao(id));
    }

    private Usuario salvarNovo(String nome, String email, String senha, StatusUsuario status) {
        String emailLimpo = email.trim();
        usuarioRepository.findByEmailIgnoreCase(emailLimpo).ifPresent(existente -> {
            if (existente.getStatus() == StatusUsuario.PENDENTE) {
                throw new BusinessException(
                        "Já existe uma solicitação de cadastro com este e-mail aguardando aprovação.");
            }
            throw new BusinessException("Já existe um membro cadastrado com este e-mail.");
        });

        return usuarioRepository.save(Usuario.builder()
                .nome(nome.trim().replaceAll("\\s+", " "))
                .email(emailLimpo)
                .senha(passwordEncoder.encode(senha))
                .status(status)
                .build());
    }

    private Usuario buscarSolicitacao(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de cadastro não encontrada."));
        if (usuario.getStatus() != StatusUsuario.PENDENTE) {
            throw new BusinessException("Este cadastro já foi aprovado.");
        }
        return usuario;
    }

    private UsuarioResponseDTO paraResponse(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .status(usuario.getStatus())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }
}
