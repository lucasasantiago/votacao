package br.com.votacao.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VotoRequest {

    @NotBlank(message = "O associado é obrigatório.")
    private String associadoId;

    @NotNull(message = "O voto é obrigatório.")
    private String voto;

    public VotoRequest(String associadoId, String voto) {
        this.associadoId = associadoId;
        this.voto = voto;
    }

    public String getAssociadoId() {
        return associadoId;
    }

    public void setAssociadoId(String associadoId) {
        this.associadoId = associadoId;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    @Override
    public String toString() {
        return "VotoRequest{" +
                "associadoId='" + associadoId + '\'' +
                ", voto='" + voto + '\'' +
                '}';
    }
}
