package br.com.votacao.repository;

import br.com.votacao.domain.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    @Query("SELECT s FROM SessaoVotacao s WHERE s.fim <= :agora AND s.isProcessada = false")
    List<SessaoVotacao> buscarSessoesExpiradas(@Param("agora") LocalDateTime agora);
}
