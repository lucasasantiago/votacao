package br.com.votacao.repository;

import br.com.votacao.domain.entity.Voto;
import br.com.votacao.domain.enums.TipoVoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);

    long countByPautaIdAndVoto(Long pautaId, TipoVoto voto);
}
