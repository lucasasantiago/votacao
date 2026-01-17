package br.com.votacao.util;

import br.com.votacao.domain.enums.ResultadoVotacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class VotacaoUtilsTest {

    @Test
    @DisplayName("1. Deve retornar APROVADA quando votos SIM forem maiores que NÃO")
    void deveRetornarAprovada() {
        ResultadoVotacao resultado = VotacaoUtils.calcularResultado(10, 5);
        assertEquals(ResultadoVotacao.APROVADA, resultado);
    }

    @Test
    @DisplayName("2. Deve retornar REPROVADA quando votos NÃO forem maiores que SIM")
    void deveRetornarReprovada() {
        ResultadoVotacao resultado = VotacaoUtils.calcularResultado(3, 8);
        assertEquals(ResultadoVotacao.REPROVADA, resultado);
    }

    @Test
    @DisplayName("3. Deve retornar EMPATE quando a quantidade de votos for igual")
    void deveRetornarEmpate() {
        ResultadoVotacao resultado = VotacaoUtils.calcularResultado(5, 5);
        assertEquals(ResultadoVotacao.EMPATE, resultado);
    }

    @Test
    @DisplayName("4. Deve lançar exceção ao tentar instanciar a classe via reflexão")
    void deveLancarExcecaoAoInstanciar() throws NoSuchMethodException {
        Constructor<VotacaoUtils> constructor = VotacaoUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals("Classe utilitária não deve ser instanciada.", exception.getCause().getMessage());
    }
}