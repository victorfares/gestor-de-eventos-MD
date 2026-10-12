package br.edu.utfpr.gestoreventosmd.repository;

import br.edu.utfpr.gestoreventosmd.model.Evento;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, UUID> {

    List<Evento> findAllByOrderByDataAsc();

    List<Evento> findByDataGreaterThanEqualOrderByDataAsc(LocalDateTime referencia);

    List<Evento> findByDataLessThanOrderByDataDesc(LocalDateTime referencia);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Evento e where e.id = :id")
    Optional<Evento> buscarPorIdComBloqueio(@Param("id") UUID id);
}
