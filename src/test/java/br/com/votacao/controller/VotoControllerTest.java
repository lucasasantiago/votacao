package br.com.votacao.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotoService votoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("1. Deve registrar voto SIM com sucesso")
    void deveRegistrarVotoSimComSucesso() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", "SIM");

        mockMvc.perform(post("/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(votoService).votar(1L, "assoc-1", TipoVoto.SIM);
    }

    @Test
    @DisplayName("2. Deve retornar Erro 400 quando o formato do voto é inválido (Ex: TALVEZ)")
    void deveRetornarErroVotoInvalido() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", "TALVEZ");

        mockMvc.perform(post("/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(votoService);
    }

    @Test
    @DisplayName("3. Deve retornar Erro 400 quando lança BusinessException (Ex: Sessão Fechada)")
    void deveRetornarErroNegocio() throws Exception {
        VotoRequest request = new VotoRequest("assoc-1", "SIM");

        doThrow(new BusinessException("Sessão encerrada para esta pauta."))
                .when(votoService).votar(anyLong(), anyString(), any(TipoVoto.class));

        mockMvc.perform(post("/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Deve retornar Erro 400 quando o JSON está incompleto (Validação Bean Validation)")
    void deveRetornarErroValidacaoCampos() throws Exception {
        VotoRequest request = new VotoRequest(null, "SIM");

        mockMvc.perform(post("/votos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Deve retornar o resultado da pauta com sucesso")
    void deveRetornarResultadoSucesso() throws Exception {
        ResultadoVotacaoResponse response = new ResultadoVotacaoResponse(10L, 2L);

        when(votoService.resultado(1L)).thenReturn(response);

        mockMvc.perform(get("/votos/pauta/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.votosSim").value(10))
                .andExpect(jsonPath("$.votosNao").value(2));
    }
}