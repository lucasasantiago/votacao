package br.com.votacao.service;

import br.com.votacao.domain.enums.ResultadoVotacao;
import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.dto.VotoContagem;
import br.com.votacao.dto.response.ResultadoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.message.VotoMessage;
import br.com.votacao.message.producer.VotoProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock private VotacaoCacheService cacheService;
    @Mock private VotoProducer votoProducer;
    @Mock private PautaService pautaService;

    @InjectMocks private VotoService votoService;

    @Test
    @DisplayName("1. Deve registrar voto com sucesso quando sessão ativa e associado ainda não votou")
    void deveVotarComSucesso() {
        Long pautaId = 1L;
        String associadoId = "ASSOC_01";
        Long sessaoId = 100L;

        when(cacheService.obterSessaoAtiva(pautaId)).thenReturn(sessaoId);
        when(cacheService.jaVotou(pautaId, associadoId)).thenReturn(false);

        assertDoesNotThrow(() -> votoService.votar(pautaId, associadoId, TipoVoto.SIM));

        verify(cacheService).registrarVotoNoCache(pautaId, associadoId);
        verify(votoProducer).enviarVoto(any(VotoMessage.class));
    }

    @Test
    @DisplayName("2. Deve lançar exceção quando não houver sessão ativa")
    void deveFalharQuandoSessaoInexistente() {
        when(cacheService.obterSessaoAtiva(1L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> votoService.votar(1L, "ID", TipoVoto.SIM));

        assertEquals("Não existe sessão aberta para esta pauta.", ex.getMessage());
    }

    @Test
    @DisplayName("3. Deve lançar exceção quando associado já tiver votado")
    void deveFalharQuandoAssociadoJaVotou() {
        when(cacheService.obterSessaoAtiva(1L)).thenReturn(100L);
        when(cacheService.jaVotou(1L, "ID")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> votoService.votar(1L, "ID", TipoVoto.SIM));

        assertEquals("Associado já votou", ex.getMessage());
    }

    @Test
    @DisplayName("4. Deve retornar o resultado formatado corretamente")
    void deveRetornarResultadoFormatado() {
        VotoContagem mockContagem = new VotoContagem(10L, 5L);
        when(pautaService.votosContagem(1L)).thenReturn(mockContagem);

        ResultadoVotacaoResponse res = votoService.resultado(1L);

        assertEquals(10L, res.votosSim());
        assertEquals(5L, res.votosNao());
        assertEquals(ResultadoVotacao.APROVADA.name(), res.resultadoVotacao().name());
    }
}