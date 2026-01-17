package br.com.votacao.message.consumer;

import br.com.votacao.domain.enums.ResultadoVotacao;
import br.com.votacao.message.ResultadoMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class ResultadoConsumerTest {

    @InjectMocks
    private ResultadoConsumer resultadoConsumer;

    @Test
    @DisplayName("1. Deve consumir a mensagem de resultado e realizar o log sem erros")
    void deveReceberResultadoEProcessarLogs() {
        ResultadoMessage mensagem = new ResultadoMessage(1L, 50L, 10L, ResultadoVotacao.APROVADA.name());
        assertDoesNotThrow(() -> resultadoConsumer.receberResultado(mensagem));
    }
}