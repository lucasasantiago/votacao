package br.com.votacao.controller;

import br.com.votacao.domain.enums.ResultadoVotacao;
import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.dto.request.VotoRequest;
import br.com.votacao.dto.response.ResultadoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotoService votoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("1. Deve registrar voto 'Sim' com sucesso")
    void deveRegistrarVotoSimComSucesso() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", TipoVoto.SIM.getDescricao());

        mockMvc.perform(post("/v1/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(votoService).votar(1L, "assoc-1", TipoVoto.SIM);
    }

    @Test
    @DisplayName("2. Deve retornar Erro 400 quando o formato do voto é inválido (Ex: TALVEZ)")
    void deveRetornarErroVotoInvalido() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", "TALVEZ");

        mockMvc.perform(post("/v1/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(votoService);
    }

    @Test
    @DisplayName("3. Deve retornar Erro 400 quando lança BusinessException (Ex: Sessão Fechada)")
    void deveRetornarErroNegocio() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", TipoVoto.SIM.getDescricao());

        doThrow(new BusinessException("Sessão encerrada para esta pauta."))
                .when(votoService).votar(anyLong(), anyString(), any(TipoVoto.class));

        mockMvc.perform(post("/v1/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Deve retornar Erro 400 quando o JSON está incompleto (Validação Bean Validation)")
    void deveRetornarErroValidacaoCampos() throws Exception {
        VotoRequest request = new VotoRequest(null, TipoVoto.SIM.getDescricao());

        mockMvc.perform(post("/v1/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Deve retornar o resultado da pauta com sucesso")
    void deveRetornarResultadoSucesso() throws Exception {
        ResultadoVotacaoResponse response = new ResultadoVotacaoResponse(1L, 10L, 2L, ResultadoVotacao.APROVADA);

        when(votoService.resultado(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/votos/pauta/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(1))
                .andExpect(jsonPath("$.votosSim").value(10))
                .andExpect(jsonPath("$.votosNao").value(2))
                .andExpect(jsonPath("$.resultadoVotacao").value(ResultadoVotacao.APROVADA.name()));
    }

    @Test
    @DisplayName("6 - Deve aceitar 'Não' com acento corretamente")
    void deveAceitarNaoComAcento() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", TipoVoto.NAO.getDescricao());

        mockMvc.perform(post("/v1/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(votoService).votar(1L, "assoc-1", TipoVoto.NAO);
    }
}