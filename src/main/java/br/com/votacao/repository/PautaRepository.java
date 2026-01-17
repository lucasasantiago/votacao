package br.com.votacao.repository;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.dto.VotoContagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PautaRepository extends JpaRepository<Pauta, Long> {

    @Query("SELECT p FROM Pauta p LEFT JOIN FETCH p.sessao s " +
            "WHERE p.id = :pautaId AND (s IS NULL OR s.fim > :agora)")
    Optional<Pauta> findPautaComSessaoAtiva(@Param("pautaId") Long pautaId, @Param("agora") LocalDateTime agora);

    @Query(value = """
                SELECT
                    COUNT(*) FILTER (WHERE voto = 'SIM') as votosSim,
                    COUNT(*) FILTER (WHERE voto = 'NAO') as votosNao
                FROM votos
                WHERE pauta_id = :pautaId
                """, nativeQuery = true)
    VotoContagem votosContagem(@Param("pautaId") Long pautaId);
}
