package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.EventoResponseDTO;
import br.edu.utfpr.gestoreventosmd.exception.BusinessException;
import br.edu.utfpr.gestoreventosmd.exception.ResourceNotFoundException;
import br.edu.utfpr.gestoreventosmd.model.Evento;
import br.edu.utfpr.gestoreventosmd.model.EventoFoto;
import br.edu.utfpr.gestoreventosmd.repository.EventoFotoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventoFotoService {

    public static final int LIMITE_FOTOS = 6;
    public static final int TAMANHO_MAXIMO_MB = 4;
    private static final long TAMANHO_MAXIMO = TAMANHO_MAXIMO_MB * 1024L * 1024L;
    private static final Set<String> TIPOS_ACEITOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private final EventoFotoRepository eventoFotoRepository;
    private final EventoService eventoService;

    @Transactional
    public EventoResponseDTO adicionar(UUID eventoId, List<MultipartFile> arquivos) {
        if (arquivos == null || arquivos.isEmpty()) {
            throw new BusinessException("Envie ao menos uma foto.");
        }

        Evento evento = eventoService.buscarEntidadePorId(eventoId);
        long existentes = eventoFotoRepository.countByEventoId(eventoId);
        if (existentes + arquivos.size() > LIMITE_FOTOS) {
            long restantes = Math.max(0, LIMITE_FOTOS - existentes);
            throw new BusinessException("Cada evento pode ter no máximo " + LIMITE_FOTOS + " fotos. "
                    + (restantes == 0
                            ? "Este evento já está completo; remova uma foto antes de enviar outra."
                            : "Você ainda pode enviar " + restantes + (restantes == 1 ? " foto." : " fotos.")));
        }

        arquivos.forEach(this::validar);

        int ordem = (int) existentes;
        for (MultipartFile arquivo : arquivos) {
            try {
                eventoFotoRepository.save(EventoFoto.builder()
                        .evento(evento)
                        .tipoConteudo(arquivo.getContentType())
                        .conteudo(arquivo.getBytes())
                        .ordem(ordem++)
                        .build());
            } catch (IOException e) {
                throw new BusinessException("Não foi possível ler uma das fotos enviadas.");
            }
        }

        return eventoService.buscarPorId(eventoId);
    }

    @Transactional(readOnly = true)
    public EventoFoto buscar(UUID eventoId, UUID fotoId) {
        EventoFoto foto = eventoFotoRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada."));
        if (!foto.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Foto não encontrada.");
        }
        return foto;
    }

    @Transactional
    public void excluir(UUID eventoId, UUID fotoId) {
        EventoFoto foto = buscar(eventoId, fotoId);
        eventoFotoRepository.delete(foto);
    }

    private void validar(MultipartFile arquivo) {
        String nome = nomeDoArquivo(arquivo);
        if (arquivo.isEmpty()) {
            throw new BusinessException("A foto " + nome + " está vazia.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new BusinessException(mensagemTamanhoExcedido(nome, arquivo.getSize()));
        }
        if (arquivo.getContentType() == null || !TIPOS_ACEITOS.contains(arquivo.getContentType())) {
            throw new BusinessException("A foto " + nome + " não está em um formato aceito. "
                    + "Envie fotos em JPG, PNG, WEBP ou GIF.");
        }
    }

    private static String mensagemTamanhoExcedido(String nome, long tamanhoBytes) {
        String tamanho = String.format(Locale.forLanguageTag("pt-BR"), "%.1f", tamanhoBytes / (1024.0 * 1024.0));
        return "A foto " + nome + " tem " + tamanho + " MB e passa do limite de " + TAMANHO_MAXIMO_MB
                + " MB por foto. Reduza a imagem ou escolha outra.";
    }

    private String nomeDoArquivo(MultipartFile arquivo) {
        String nome = arquivo.getOriginalFilename();
        return nome == null || nome.isBlank() ? "enviada" : "\"" + nome + "\"";
    }
}
