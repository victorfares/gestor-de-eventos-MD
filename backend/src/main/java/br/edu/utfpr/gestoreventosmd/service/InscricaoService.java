package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.InscricaoRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.InscricaoResponseDTO;
import br.edu.utfpr.gestoreventosmd.model.Evento;
import br.edu.utfpr.gestoreventosmd.model.Inscricao;
import br.edu.utfpr.gestoreventosmd.repository.InscricaoRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoService eventoService;

    @Transactional
    public InscricaoResponseDTO registrar(InscricaoRequestDTO dto) {
        Evento evento = eventoService.buscarEntidadePorId(dto.getEventoId());

        Inscricao inscricao = Inscricao.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .idadeOuSerie(dto.getIdadeOuSerie())
                .dataInscricao(LocalDateTime.now())
                .evento(evento)
                .build();

        return paraResponse(inscricaoRepository.save(inscricao));
    }

    private InscricaoResponseDTO paraResponse(Inscricao inscricao) {
        return InscricaoResponseDTO.builder()
                .id(inscricao.getId())
                .nome(inscricao.getNome())
                .email(inscricao.getEmail())
                .idadeOuSerie(inscricao.getIdadeOuSerie())
                .dataInscricao(inscricao.getDataInscricao())
                .eventoId(inscricao.getEvento().getId())
                .build();
    }
}
