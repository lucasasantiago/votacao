package br.com.votacao.dto.response;

import java.time.LocalDateTime;

public class SessaoVotacaoResponse {
    private Long id;
    private PautaResponse pauta;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private Boolean isProcessada;
    private Boolean aberta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PautaResponse getPauta() { return pauta; }
    public void setPauta(PautaResponse pauta) { this.pauta = pauta; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
    public Boolean getIsProcessada() { return isProcessada; }
    public void setIsProcessada(Boolean isProcessada) { this.isProcessada = isProcessada; }
    public Boolean getAberta() { return aberta; }
    public void setAberta(Boolean aberta) { this.aberta = aberta; }
}
