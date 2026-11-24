package br.edu.utfpr.gestoreventosmd.repository;

import br.edu.utfpr.gestoreventosmd.model.EventoFoto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoFotoRepository extends JpaRepository<EventoFoto, UUID> {

    @Query("select f.id from EventoFoto f where f.evento.id = :eventoId order by f.ordem, f.id")
    List<UUID> findIdsByEventoId(@Param("eventoId") UUID eventoId);

    long countByEventoId(UUID eventoId);
}
