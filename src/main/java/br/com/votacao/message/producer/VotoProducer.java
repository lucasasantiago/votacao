package br.com.votacao.message.producer;

import br.com.votacao.message.VotoMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class VotoProducer {

    private final RabbitTemplate rabbitTemplate;
    public static final String FILA_VOTOS = "pautas.votos.registrar";

    public VotoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarVoto(VotoMessage mensagem) {
        rabbitTemplate.convertAndSend(FILA_VOTOS, mensagem);
    }
}
