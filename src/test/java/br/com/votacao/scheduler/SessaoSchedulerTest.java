package br.com.votacao.scheduler;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.dto.VotoContagem;
import br.com.votacao.message.ResultadoMessage;
import br.com.votacao.message.producer.ResultadoProducer;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoSchedulerTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private ResultadoProducer resultadoProducer;

    @InjectMocks
    private SessaoScheduler sessaoScheduler;

    @Test
    @DisplayName("1. Deve processar sessões expiradas, enviar resultado e marcar como processada")
    void deveProcessarSessoesExpiradasComSucesso() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setId(10L);
        sessao.setPauta(pauta);
        sessao.setProcessada(false);

        VotoContagem contagem = new VotoContagem(10L, 5L);

        when(sessaoVotacaoRepository.buscarSessoesExpiradas(any(LocalDateTime.class)))
                .thenReturn(List.of(sessao));
        when(pautaRepository.votosContagem(1L)).thenReturn(contagem);

        sessaoScheduler.verificarSessoesFinalizadas();

        verify(resultadoProducer, times(1)).enviarResultado(any(ResultadoMessage.class));
        verify(sessaoVotacaoRepository, times(1)).save(argThat(s -> s.getIsProcessada().equals(Boolean.TRUE)));
    }

    @Test
    @DisplayName("2. Não deve processar nada quando não houver sessões expiradas")
    void naoDeveProcessarQuandoNaoHouverSessoes() {
        when(sessaoVotacaoRepository.buscarSessoesExpiradas(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        sessaoScheduler.verificarSessoesFinalizadas();

        verifyNoInteractions(resultadoProducer);
        verify(sessaoVotacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. Deve processar múltiplas sessões expiradas")
    void deveProcessarMultiplasSessoes() {
        SessaoVotacao s1 = criarSessaoMock(1L, 10L);
        SessaoVotacao s2 = criarSessaoMock(2L, 20L);

        when(sessaoVotacaoRepository.buscarSessoesExpiradas(any(LocalDateTime.class)))
                .thenReturn(List.of(s1, s2));
        when(pautaRepository.votosContagem(anyLong())).thenReturn(new VotoContagem(1L, 1L));

        sessaoScheduler.verificarSessoesFinalizadas();

        verify(resultadoProducer, times(2)).enviarResultado(any(ResultadoMessage.class));
        verify(sessaoVotacaoRepository, times(2)).save(any(SessaoVotacao.class));
    }

    private SessaoVotacao criarSessaoMock(Long pautaId, Long sessaoId) {
        Pauta p = new Pauta();
        p.setId(pautaId);
        SessaoVotacao s = new SessaoVotacao();
        s.setId(sessaoId);
        s.setPauta(p);
        return s;
    }
}