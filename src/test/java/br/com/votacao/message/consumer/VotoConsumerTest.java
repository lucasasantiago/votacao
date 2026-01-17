package br.com.votacao.message.consumer;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.Voto;
import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.message.VotoMessage;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.VotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotoConsumerTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private VotoConsumer votoConsumer;

    @Test
    @DisplayName("1. Deve salvar o voto no banco quando não for duplicado")
    void deveProcessarVotoComSucesso() {
        VotoMessage mensagem = new VotoMessage(1L, 10L, "assoc-1", TipoVoto.SIM);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "assoc-1")).thenReturn(false);
        when(pautaRepository.getReferenceById(1L)).thenReturn(new Pauta());

        votoConsumer.processarVoto(mensagem);

        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    @DisplayName("2. Não deve salvar o voto se o associado já tiver votado (Idempotência)")
    void deveIgnorarVotoDuplicado() {
        VotoMessage mensagem = new VotoMessage(1L, 10L, "assoc-1", TipoVoto.SIM);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "assoc-1")).thenReturn(true);

        votoConsumer.processarVoto(mensagem);

        verify(votoRepository, never()).save(any(Voto.class));
        verifyNoInteractions(pautaRepository);
    }
}