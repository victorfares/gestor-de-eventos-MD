package br.edu.utfpr.gestoreventosmd.repository;

import br.edu.utfpr.gestoreventosmd.model.Inscricao;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoRepository extends JpaRepository<Inscricao, UUID> {

    List<Inscricao> findByEventoId(UUID eventoId);

    boolean existsByEventoIdAndEmailIgnoreCase(UUID eventoId, String email);

    long countByEventoId(UUID eventoId);
}
