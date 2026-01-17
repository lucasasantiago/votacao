package br.com.votacao.dto.response;

public class ResultadoVotacaoResponse {

    private long votosSim;
    private long votosNao;

    public ResultadoVotacaoResponse(long votosSim, long votosNao) {
        this.votosSim = votosSim;
        this.votosNao = votosNao;
    }

    public long getVotosSim() {
        return votosSim;
    }

    public long getVotosNao() {
        return votosNao;
    }
}
