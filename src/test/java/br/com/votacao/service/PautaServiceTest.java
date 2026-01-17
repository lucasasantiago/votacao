package br.com.votacao.service;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.dto.request.AbrirSessaoRequest;
import br.com.votacao.dto.response.PautaResponse;
import br.com.votacao.dto.response.SessaoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.mapper.PautaMapper;
import br.com.votacao.mapper.SessaoVotacaoMapper;
import br.com.votacao.repository.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;
    @Mock
    private SessaoVotacaoService sessaoVotacaoService;
    @Mock
    private VotacaoCacheService cacheService;
    @Mock
    private PautaMapper pautaMapper;
    @Mock
    private SessaoVotacaoMapper sessaoVotacaoMapper;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("1. Deve criar uma pauta com sucesso")
    void deveCriarPautaComSucesso() {
        String titulo = "Pauta Teste";
        Pauta pauta = new Pauta();
        pauta.setTitulo(titulo);

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);
        when(pautaMapper.toResponse(any(Pauta.class))).thenReturn(new PautaResponse());

        PautaResponse response = pautaService.criarPauta(titulo);

        assertNotNull(response);
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    @DisplayName("2. Deve abrir sessão com tempo padrão de 1 minuto quando request for nulo")
    void deveAbrirSessaoComTempoPadrao() {
        Long pautaId = 1L;
        Pauta pauta = new Pauta();
        pauta.setId(pautaId);

        when(pautaRepository.findPautaComSessaoAtiva(eq(pautaId), any())).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoService.save(any(SessaoVotacao.class))).thenAnswer(i -> i.getArguments()[0]);
        when(sessaoVotacaoMapper.toResponse(any())).thenReturn(new SessaoVotacaoResponse());

        pautaService.abrirSessao(pautaId, null);

        verify(sessaoVotacaoService).save(argThat(sessao ->
                sessao.getFim().isAfter(sessao.getInicio()) &&
                        sessao.getFim().isBefore(sessao.getInicio().plusSeconds(61))
        ));
        verify(cacheService).sinalizarSessaoAberta(eq(pautaId), any(), eq(1));
    }

    @Test
    @DisplayName("3. Deve lançar exceção ao tentar abrir sessão de pauta inexistente")
    void deveLancarExcecaoPautaNaoEncontrada() {
        when(pautaRepository.findPautaComSessaoAtiva(anyLong(), any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pautaService.abrirSessao(1L, new AbrirSessaoRequest())
        );
        assertEquals("Pauta não encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("4. Deve lançar exceção quando a pauta já possuir uma sessão")
    void deveLancarExcecaoSessaoJaAberta() {
        Pauta pauta = new Pauta();
        pauta.setSessao(new SessaoVotacao());

        when(pautaRepository.findPautaComSessaoAtiva(anyLong(), any())).thenReturn(Optional.of(pauta));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                pautaService.abrirSessao(1L, new AbrirSessaoRequest())
        );
        assertEquals("Sessão já aberta", ex.getMessage());
    }
}