package br.com.votacao.service;

import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    @Test
    @DisplayName("1. Deve persistir uma sessão de votação com sucesso")
    void deveSalvarSessaoComSucesso() {
        SessaoVotacao sessaoParaSalvar = new SessaoVotacao();
        sessaoParaSalvar.setId(1L);

        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoParaSalvar);

        SessaoVotacao sessaoSalva = sessaoVotacaoService.save(new SessaoVotacao());

        assertNotNull(sessaoSalva);
        assertEquals(1L, sessaoSalva.getId());

        verify(sessaoVotacaoRepository, times(1)).save(any(SessaoVotacao.class));
    }
}