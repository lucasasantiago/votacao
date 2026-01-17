package br.com.votacao.message.producer;

import br.com.votacao.configuration.RabbitMQConfig;
import br.com.votacao.message.ResultadoMessage;
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
public class ResultadoProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ResultadoProducer resultadoProducer;

    @Test
    @DisplayName("1. Deve enviar o resultado da votação para a fila configurada")
    void deveEnviarResultadoComSucesso() {
        ResultadoMessage mensagem = new ResultadoMessage(1L, 100L, 50L, "APROVADO");

        resultadoProducer.enviarResultado(mensagem);

        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.FILA_RESULTADO_VOTACAO), eq(mensagem));
    }
}