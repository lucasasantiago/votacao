package br.com.votacao.controller;

import br.com.votacao.dto.request.AbrirSessaoRequest;
import br.com.votacao.dto.request.CriarPautaRequest;
import br.com.votacao.dto.response.PautaResponse;
import br.com.votacao.dto.response.SessaoVotacaoResponse;
import br.com.votacao.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PautaService pautaService;

    @Test
    @DisplayName("1. Deve criar uma pauta com sucesso e retornar 201 Created")
    void deveCriarPautaComSucesso() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest();
        request.setTitulo("Pauta de Teste");

        PautaResponse response = new PautaResponse();
        response.setId(1L);
        response.setTitulo("Pauta de Teste");

        when(pautaService.criarPauta(any(String.class))).thenReturn(response);

        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Pauta de Teste"));
    }

    @Test
    @DisplayName("2. Deve abrir uma sessão para uma pauta e retornar 201 Created")
    void deveAbrirSessaoComSucesso() throws Exception {
        Long pautaId = 1L;
        AbrirSessaoRequest request = new AbrirSessaoRequest();
        request.setDuracaoEmMinutos(5);

        SessaoVotacaoResponse response = new SessaoVotacaoResponse();
        response.setId(10L);
        response.setInicio(LocalDateTime.now());
        response.setFim(LocalDateTime.now().plusMinutes(5));

        when(pautaService.abrirSessao(eq(pautaId), any(AbrirSessaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/pautas/{pautaId}/sessao", pautaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("3. Deve retornar 400 Bad Request ao tentar criar pauta sem título")
    void deveRetornar400QuandoTituloForInvalido() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest();

        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}