package br.com.votacao.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CriarPautaRequest {

    @NotBlank(message = "O título da pauta é obrigatório.")
    private String titulo;

    public CriarPautaRequest() {
    }

    public CriarPautaRequest(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String toString() {
        return "CriarPautaRequest{" +
                "titulo='" + titulo + '\'' +
                '}';
    }
}