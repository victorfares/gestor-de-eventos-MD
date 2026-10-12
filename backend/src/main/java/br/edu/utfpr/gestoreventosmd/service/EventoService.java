package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.EventoRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.EventoResponseDTO;
import br.edu.utfpr.gestoreventosmd.exception.BusinessException;
import br.edu.utfpr.gestoreventosmd.exception.ResourceNotFoundException;
import br.edu.utfpr.gestoreventosmd.model.Evento;
import br.edu.utfpr.gestoreventosmd.model.Usuario;
import br.edu.utfpr.gestoreventosmd.repository.EventoRepository;
import br.edu.utfpr.gestoreventosmd.repository.InscricaoRepository;
import br.edu.utfpr.gestoreventosmd.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscricaoRepository inscricaoRepository;

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
                .criador(buscarUsuarioPorId(dto.getUsuarioId()))
                .build();

        return paraResponse(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoRequestDTO dto) {
        Evento evento = buscarEntidadePorIdComBloqueio(id);

        long inscritas = inscricaoRepository.countByEventoId(id);
        if (dto.getLimiteVagas() < inscritas) {
            throw new BusinessException("O limite de vagas não pode ser menor que o número de inscritas ("
                    + inscritas + ").");
        }

        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setData(dto.getData());
        evento.setLocal(dto.getLocal());
        evento.setLimiteVagas(dto.getLimiteVagas());
        evento.setCriador(buscarUsuarioPorId(dto.getUsuarioId()));

        return paraResponse(eventoRepository.save(evento));
    }

    @Transactional
    public void excluir(Long id) {
        Evento evento = buscarEntidadePorId(id);
        eventoRepository.delete(evento);
    }

    public Evento buscarEntidadePorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento não encontrado para o ID: " + id));
    }

    public Evento buscarEntidadePorIdComBloqueio(Long id) {
        return eventoRepository.buscarPorIdComBloqueio(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento não encontrado para o ID: " + id));
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado para o ID: " + id));
    }

    private EventoResponseDTO paraResponse(Evento evento) {
        return EventoResponseDTO.builder()
                .id(evento.getId())
                .titulo(evento.getTitulo())
                .descricao(evento.getDescricao())
                .data(evento.getData())
                .local(evento.getLocal())
                .limiteVagas(evento.getLimiteVagas())
                .vagasDisponiveis(Math.max(0, evento.getLimiteVagas()
                        - inscricaoRepository.countByEventoId(evento.getId())))
                .usuarioId(evento.getCriador() != null ? evento.getCriador().getId() : null)
                .build();
    }
}
