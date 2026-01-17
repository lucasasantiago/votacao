package br.com.votacao.dto.response;

import java.time.LocalDateTime;

public record VotoResponse(String mensagem, LocalDateTime timestamp) {}
