package br.com.votacao.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("1. Deve tratar BusinessException e retornar 400 Bad Request")
    void deveTratarBusinessException() {
        BusinessException ex = new BusinessException("Pauta não encontrada");

        ResponseEntity<Map<String, Object>> response = handler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Pauta não encontrada", Objects.requireNonNull(response.getBody()).get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("2. Deve tratar Exception genérica e retornar 500 Internal Server Error")
    void deveTratarExceptionGenerica() {
        Exception ex = new Exception("Erro de banco");

        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno inesperado", Objects.requireNonNull(response.getBody()).get("error"));
    }

    @Test
    @DisplayName("3. Deve tratar MethodArgumentNotValidException e retornar a mensagem de validação")
    void deveTratarValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        ObjectError error = new ObjectError("request", "O título é obrigatório");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        ResponseEntity<Map<String, Object>> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O título é obrigatório", Objects.requireNonNull(response.getBody()).get("error"));
    }
}