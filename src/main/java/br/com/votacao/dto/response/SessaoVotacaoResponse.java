package br.com.votacao.dto.response;

import java.time.LocalDateTime;

public class SessaoVotacaoResponse {
    private Long id;
    private PautaResponse pauta;
    private LocalDateTime inicio;
    private LocalDateTime fim;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PautaResponse getPauta() { return pauta; }
    public void setPauta(PautaResponse pauta) { this.pauta = pauta; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
}
