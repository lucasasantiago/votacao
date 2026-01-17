package br.com.votacao.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "pautas")
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @OneToOne(mappedBy = "pauta", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("sessao")
    private SessaoVotacao sessao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public SessaoVotacao getSessao() {
        return sessao;
    }

    public void setSessao(SessaoVotacao sessao) {
        this.sessao = sessao;
    }
}
