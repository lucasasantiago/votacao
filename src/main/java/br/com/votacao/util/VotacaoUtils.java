package br.com.votacao.util;

import br.com.votacao.domain.enums.ResultadoVotacao;

public class VotacaoUtils {

    private VotacaoUtils() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada.");
    }

    public static ResultadoVotacao calcularResultado(long votosSim, long votosNao) {
        if (votosSim > votosNao) {
            return ResultadoVotacao.APROVADA;
        } else if (votosNao > votosSim) {
            return ResultadoVotacao.REPROVADA;
        } else {
            return ResultadoVotacao.EMPATE;
        }
    }
}
