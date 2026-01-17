package br.com.votacao.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotacaoCacheServiceTest {

    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private ValueOperations<String, Object> valueOperations;
    @Mock private SetOperations<String, Object> setOperations;

    private VotacaoCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new VotacaoCacheService(redisTemplate);
    }

    @Test
    @DisplayName("1. Deve sinalizar sessão aberta no Redis com tempo de expiração")
    void deveSinalizarSessaoAberta() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cacheService.sinalizarSessaoAberta(1L, 100L, 5);

        verify(valueOperations).set("pauta:1:sessao:ativa", "100", 5, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("2. Deve registrar voto no Set do Redis e definir expiração de 24h")
    void deveRegistrarVotoNoCache() {
        String chave = "pauta:1:votos_registrados";
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        cacheService.registrarVotoNoCache(1L, "ASSOC_01");

        verify(setOperations).add(chave, "ASSOC_01");
        verify(redisTemplate).expire(chave, 24, TimeUnit.HOURS);
    }

    @Test
    @DisplayName("3. Deve verificar se associado já votou consultando o Set do Redis")
    void deveVerificarSeJaVotou() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember(anyString(), any(Object.class))).thenReturn(Boolean.TRUE);

        boolean jaVotou = cacheService.jaVotou(1L, "ASSOC_01");

        assertTrue(jaVotou);
        verify(setOperations).isMember("pauta:1:votos_registrados", "ASSOC_01");
    }
}