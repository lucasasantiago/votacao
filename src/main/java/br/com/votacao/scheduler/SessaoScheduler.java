package br.com.votacao.scheduler;

import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.dto.VotoContagem;
import br.com.votacao.message.ResultadoMessage;
import br.com.votacao.message.producer.ResultadoProducer;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.SessaoVotacaoRepository;
import br.com.votacao.util.VotacaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SessaoScheduler {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final ResultadoProducer resultadoProducer;
    private static final Logger log = LoggerFactory.getLogger(SessaoScheduler.class);

    public SessaoScheduler(PautaRepository pautaRepository, SessaoVotacaoRepository sessaoVotacaoRepository, ResultadoProducer resultadoProducer) {
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.resultadoProducer = resultadoProducer;
    }

    @Scheduled(fixedDelay = 30000)
    public void verificarSessoesFinalizadas() {
        log.info("Iniciando consulta das sessões expiradas.");

        List<SessaoVotacao> sessoesEncerradas = sessaoVotacaoRepository.buscarSessoesExpiradas(LocalDateTime.now());

        sessoesEncerradas.forEach(sessaoVotacao -> {
            resultadoProducer.enviarResultado(buildResultadoMessage(sessaoVotacao));

            sessaoVotacao.setProcessada(Boolean.TRUE);
            sessaoVotacaoRepository.save(sessaoVotacao);
        });
    }

    private ResultadoMessage buildResultadoMessage(SessaoVotacao sessaoVotacao) {
        VotoContagem contagemVotos = pautaRepository.votosContagem(sessaoVotacao.getPauta().getId());

        log.info("Resultado calculado para a Sessão {}: Sim={}, Não={}", sessaoVotacao.getId(), contagemVotos.votosSim(), contagemVotos.votosNao());

        return new ResultadoMessage(sessaoVotacao.getPauta().getId(),
                                    contagemVotos.votosSim(),
                                    contagemVotos.votosNao(),
                                    VotacaoUtils.calcularResultado(contagemVotos.votosSim(), contagemVotos.votosNao()).name());
    }
}
