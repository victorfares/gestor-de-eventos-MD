package br.edu.utfpr.gestoreventosmd.service;

import br.edu.utfpr.gestoreventosmd.dto.ArquivoExportadoDTO;
import br.edu.utfpr.gestoreventosmd.dto.InscricaoRequestDTO;
import br.edu.utfpr.gestoreventosmd.dto.InscricaoResponseDTO;
import br.edu.utfpr.gestoreventosmd.exception.BusinessException;
import br.edu.utfpr.gestoreventosmd.model.Evento;
import br.edu.utfpr.gestoreventosmd.model.Inscricao;
import br.edu.utfpr.gestoreventosmd.repository.InscricaoRepository;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscricaoService {

    private static final DateTimeFormatter FORMATO_DATA_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final InscricaoRepository inscricaoRepository;
    private final EventoService eventoService;

    @Transactional
    public InscricaoResponseDTO registrar(InscricaoRequestDTO dto) {
        // O bloqueio serializa inscrições concorrentes no mesmo evento, evitando
        // que a contagem de vagas e a checagem de duplicidade fiquem desatualizadas.
        Evento evento = eventoService.buscarEntidadePorIdComBloqueio(dto.getEventoId());
        LocalDateTime agora = LocalDateTime.now();

        if (evento.getData().isBefore(agora)) {
            throw new BusinessException("As inscrições para este evento estão encerradas, pois ele já aconteceu.");
        }

        if (inscricaoRepository.existsByEventoIdAndEmailIgnoreCase(evento.getId(), dto.getEmail())) {
            throw new BusinessException("Este e-mail já está inscrito neste evento.");
        }

        if (inscricaoRepository.countByEventoId(evento.getId()) >= evento.getLimiteVagas()) {
            throw new BusinessException("Não há vagas disponíveis para este evento.");
        }

        Inscricao inscricao = Inscricao.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .idadeOuSerie(dto.getIdadeOuSerie())
                .dataInscricao(agora)
                .evento(evento)
                .build();

        return paraResponse(inscricaoRepository.save(inscricao));
    }

    @Transactional(readOnly = true)
    public List<InscricaoResponseDTO> recuperarInscritosPorEvento(UUID eventoId) {
        Evento evento = eventoService.buscarEntidadePorId(eventoId);
        return inscricaoRepository.findByEventoIdOrderByDataInscricaoAsc(evento.getId()).stream()
                .map(this::paraResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArquivoExportadoDTO exportarInscritosCsv(UUID eventoId) {
        Evento evento = eventoService.buscarEntidadePorId(eventoId);
        List<Inscricao> inscritas = inscricaoRepository.findByEventoIdOrderByDataInscricaoAsc(evento.getId());

        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("Nome;E-mail;Idade/Série;Data da inscrição;Presença\r\n");
        for (Inscricao inscricao : inscritas) {
            csv.append(campoCsv(inscricao.getNome())).append(';')
                    .append(campoCsv(inscricao.getEmail())).append(';')
                    .append(campoCsv(inscricao.getIdadeOuSerie())).append(';')
                    .append(inscricao.getDataInscricao().format(FORMATO_DATA_CSV)).append(';')
                    .append("\r\n");
        }

        String nomeArquivo = "inscritas-" + gerarSlug(evento.getTitulo()) + ".csv";
        return new ArquivoExportadoDTO(nomeArquivo, csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String campoCsv(String valor) {
        String texto = valor == null ? "" : valor;
        // Impede que o Excel interprete dados enviados pelo formulário público como fórmulas.
        if (!texto.isEmpty() && "=+-@\t\r".indexOf(texto.charAt(0)) >= 0) {
            texto = "'" + texto;
        }
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r")) {
            texto = "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private String gerarSlug(String titulo) {
        String slug = Normalizer.normalize(titulo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isEmpty() ? "evento" : slug;
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
