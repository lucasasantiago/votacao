package br.com.votacao.message;

import br.com.votacao.domain.enums.TipoVoto;

public record VotoMessage(Long pautaId, String associadoId, TipoVoto voto) {}
