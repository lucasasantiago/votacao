package br.com.votacao.repository;

import br.com.votacao.domain.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PautaRepository extends JpaRepository<Pauta, Long> {

    // Mantenha esta query, mas certifique-se que na classe Voto
    // o atributo que liga à Pauta se chama exatamente "pauta"
    @Query("SELECT v.voto, COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId GROUP BY v.voto")
    List<Object[]> contarVotosAgrupados(@Param("pautaId") Long pautaId);
}
