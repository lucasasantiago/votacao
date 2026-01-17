package br.com.votacao.message;

import br.com.votacao.domain.entity.Voto;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VotoConsumer {
    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private static final Logger log = LoggerFactory.getLogger(VotoConsumer.class);

    public VotoConsumer(VotoRepository votoRepository, PautaRepository pautaRepository) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
    }

    @RabbitListener(queues = "pautas.votos.registrar")
    public void processar(VotoMessage mensagem) {
        log.info("Consumindo voto da fila para associado: {}", mensagem.associadoId());

        // Checagem final de duplicidade no banco (garante que o processo é seguro)
        if (votoRepository.existsByPautaIdAndAssociadoId(mensagem.pautaId(), mensagem.associadoId())) {
            log.warn("Voto duplicado ignorado no Consumer: {}", mensagem.associadoId());
            return;
        }

        Voto novoVoto = new Voto();
        novoVoto.setAssociadoId(mensagem.associadoId());
        novoVoto.setVoto(mensagem.voto());
        novoVoto.setPauta(pautaRepository.getReferenceById(mensagem.pautaId()));

        votoRepository.save(novoVoto);
        log.info("Voto persistido com sucesso no Postgres.");
    }
}
