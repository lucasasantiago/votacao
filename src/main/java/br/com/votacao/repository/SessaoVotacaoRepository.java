package br.com.votacao.repository;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    Optional<SessaoVotacao> findByPautaId(Long pautaId);

    @Query("SELECT s FROM SessaoVotacao s WHERE s.fim <= :agora AND s.isProcessada = false")
    List<SessaoVotacao> buscarSessoesExpiradas(@Param("agora") LocalDateTime agora);
}
