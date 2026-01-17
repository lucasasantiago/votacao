package br.com.votacao.message.producer;

import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.message.VotoMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VotoProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VotoProducer votoProducer;

    @Test
    @DisplayName("1. Deve enviar a mensagem de voto para a fila correta via RabbitTemplate")
    void deveEnviarVotoComSucesso() {
        VotoMessage mensagem = new VotoMessage(1L, 10L, "assoc-123", TipoVoto.SIM);

        votoProducer.enviarVoto(mensagem);

        verify(rabbitTemplate).convertAndSend(eq(VotoProducer.FILA_VOTOS), eq(mensagem));
    }
}