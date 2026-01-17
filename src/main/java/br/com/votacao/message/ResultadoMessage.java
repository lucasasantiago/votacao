package br.com.votacao.message;

public record ResultadoMessage(Long pautaId, Long votosSim, Long votosNao, String resultadoVotacao){ }
