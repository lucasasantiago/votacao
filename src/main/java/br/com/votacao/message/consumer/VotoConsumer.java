package br.com.votacao.message.consumer;

import br.com.votacao.domain.entity.Voto;
import br.com.votacao.message.VotoMessage;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.VotoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VotoConsumer {
    private static final Logger log = LoggerFactory.getLogger(VotoConsumer.class);

    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;

    public VotoConsumer(VotoRepository votoRepository, PautaRepository pautaRepository) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
    }

    @RabbitListener(queues = "pautas.votos.registrar")
    @Transactional
    public void processarVoto(VotoMessage mensagem) {
        log.info("Processando voto da fila: Associado {} na Pauta {}", mensagem.associadoId(), mensagem.pautaId());

        try {
            if (votoRepository.existsByPautaIdAndAssociadoId(mensagem.pautaId(), mensagem.associadoId())) {
                log.warn("Voto duplicado detectado no banco. Ignorando... Associado: {}", mensagem.associadoId());
                return;
            }

            Voto novoVoto = new Voto();
            novoVoto.setAssociadoId(mensagem.associadoId());
            novoVoto.setVoto(mensagem.voto());

            novoVoto.setPauta(pautaRepository.getReferenceById(mensagem.pautaId()));

            votoRepository.save(novoVoto);

            log.info("Voto persistido com sucesso! Pauta: {}, Sessão: {}", mensagem.pautaId(), mensagem.sessaoId());

        } catch (Exception e) {
            log.error("Erro crítico ao processar voto do associado {}: {}",mensagem.associadoId(), e.getMessage());
            throw e;
        }
    }
}
