package br.edu.utfpr.gestoreventosmd.repository;

import br.edu.utfpr.gestoreventosmd.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
}
