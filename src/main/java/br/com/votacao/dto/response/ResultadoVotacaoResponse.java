package br.com.votacao.dto.response;

import br.com.votacao.domain.enums.ResultadoVotacao;

public record ResultadoVotacaoResponse (long pautaId, long votosSim, long votosNao, ResultadoVotacao resultadoVotacao){}
