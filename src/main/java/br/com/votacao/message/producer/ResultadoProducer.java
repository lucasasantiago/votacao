package br.com.votacao.message.producer;

import br.com.votacao.configuration.RabbitMQConfig;
import br.com.votacao.message.ResultadoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResultadoProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(ResultadoProducer.class);

    public ResultadoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarResultado(ResultadoMessage mensagem) {
        log.info("Enviando resultado para a fila {}. mensagem={}", RabbitMQConfig.FILA_RESULTADO_VOTACAO, mensagem);

        rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_RESULTADO_VOTACAO, mensagem);
    }
}