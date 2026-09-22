package br.edu.utfpr.gestoreventosmd.repository;

import br.edu.utfpr.gestoreventosmd.model.Inscricao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    List<Inscricao> findByEventoId(Long eventoId);

    boolean existsByEventoIdAndEmailIgnoreCase(Long eventoId, String email);

    long countByEventoId(Long eventoId);
}
