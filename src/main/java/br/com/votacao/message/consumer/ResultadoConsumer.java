package br.com.votacao.message;

import br.com.votacao.configuration.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ResultadoConsumer {

    private static final Logger log = LoggerFactory.getLogger(ResultadoConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.FILA_RESULTADO_VOTACAO)
    public void receberResultado(ResultadoMessage mensagem) {
        log.info("### MENSAGEM RECEBIDA DA FILA ###");
        log.info("Pauta ID: {}", mensagem.getPautaId());
        log.info("Resultado: {}", mensagem.getResultado());
    }
}
