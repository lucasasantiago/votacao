package br.com.votacao.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String FILA_RESULTADO_VOTACAO = "pautas.votos.resultado";
    public static final String FILA_PROCESSAMENTO_VOTOS = "pautas.votos.registrar";

    @Bean
    public Queue filaResultado() {
        return new Queue(FILA_RESULTADO_VOTACAO, true);
    }

    @Bean
    public Queue filaProcessamento() {
        return new Queue(FILA_PROCESSAMENTO_VOTOS, true);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
