package br.com.votacao.scheduler;

import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.message.producer.ResultadoProducer;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.SessaoVotacaoRepository;
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
            String resultado = calcularResultado(sessaoVotacao);

            resultadoProducer.enviarResultado(sessaoVotacao.getId(), resultado);

            sessaoVotacao.setProcessada(Boolean.TRUE);
            sessaoVotacaoRepository.save(sessaoVotacao);
        });
    }

    private String calcularResultado(SessaoVotacao sessaoVotacao) {
        List<Object[]> contagemVotos = pautaRepository.contarVotosAgrupados(sessaoVotacao.getId());

        long sim = 0;
        long nao = 0;

        for (Object[] registro : contagemVotos) {
            String tipoVoto = registro[0].toString();
            long total = (long) registro[1];

            if (TipoVoto.SIM.name().equalsIgnoreCase(tipoVoto)) {
                sim = total;
            } else if (TipoVoto.NAO.name().equalsIgnoreCase(tipoVoto)) {
                nao = total;
            }
        }

        log.info("Resultado calculado para a Sessão {}: SIM={}, NAO={}", sessaoVotacao.getId(), sim, nao);

        if (sim > nao) return "APROVADA";
        if (nao > sim) return "REPROVADA";
        return "EMPATE";
    }
}
