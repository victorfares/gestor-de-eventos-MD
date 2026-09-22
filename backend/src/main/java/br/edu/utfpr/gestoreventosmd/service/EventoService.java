package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.EventoRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.EventoResponseDTO;
import br.edu.utfpr.gestoreventosmd.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.gestoreventosmd.model.Evento;
import br.edu.utfpr.gestoreventosmd.repository.EventoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll().stream()
                .map(this::paraResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        return paraResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public EventoResponseDTO criar(EventoRequestDTO dto) {
        Evento evento = Evento.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .data(dto.getData())
                .local(dto.getLocal())
                .limiteVagas(dto.getLimiteVagas())
                .build();

        return paraResponse(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoRequestDTO dto) {
        Evento evento = buscarEntidadePorId(id);
        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setData(dto.getData());
        evento.setLocal(dto.getLocal());
        evento.setLimiteVagas(dto.getLimiteVagas());

        return paraResponse(eventoRepository.save(evento));
    }

    @Transactional
    public void excluir(Long id) {
        Evento evento = buscarEntidadePorId(id);
        eventoRepository.delete(evento);
    }

    public Evento buscarEntidadePorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Evento não encontrado para o ID: " + id));
    }

    private EventoResponseDTO paraResponse(Evento evento) {
        return EventoResponseDTO.builder()
                .id(evento.getId())
                .titulo(evento.getTitulo())
                .descricao(evento.getDescricao())
                .data(evento.getData())
                .local(evento.getLocal())
                .limiteVagas(evento.getLimiteVagas())
                .build();
    }
}
