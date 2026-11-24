package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.EventoResponseDTO;
import br.edu.utfpr.gestoreventosmd.exception.BusinessException;
import br.edu.utfpr.gestoreventosmd.exception.ResourceNotFoundException;
import br.edu.utfpr.gestoreventosmd.model.Evento;
import br.edu.utfpr.gestoreventosmd.model.EventoFoto;
import br.edu.utfpr.gestoreventosmd.repository.EventoFotoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventoFotoService {

    static final int LIMITE_FOTOS = 6;
    private static final long TAMANHO_MAXIMO = 4L * 1024 * 1024;
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
            throw new BusinessException("Cada evento pode ter no máximo " + LIMITE_FOTOS + " fotos.");
        }

        int ordem = (int) existentes;
        for (MultipartFile arquivo : arquivos) {
            validar(arquivo);
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
        if (arquivo.isEmpty()) {
            throw new BusinessException("Uma das fotos está vazia.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new BusinessException("Cada foto pode ter no máximo 4 MB.");
        }
        if (arquivo.getContentType() == null || !TIPOS_ACEITOS.contains(arquivo.getContentType())) {
            throw new BusinessException("Envie fotos em JPG, PNG, WEBP ou GIF.");
        }
    }
}
