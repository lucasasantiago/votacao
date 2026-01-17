package br.com.votacao.message;

public class ResultadoMessage {

    private Long pautaId;
    private String resultado;

    public ResultadoMessage() {
    }

    private ResultadoMessage(Builder builder) {
        this.pautaId = builder.pautaId;
        this.resultado = builder.resultado;
    }

    public Long getPautaId() { return pautaId; }
    public String getResultado() { return resultado; }

    public static class Builder {
        private Long pautaId;
        private String resultado;

        public Builder pautaId(Long pautaId) {
            this.pautaId = pautaId;
            return this;
        }

        public Builder resultado(String resultado) {
            this.resultado = resultado;
            return this;
        }

        public ResultadoMessage build() {
            return new ResultadoMessage(this);
        }
    }
}
