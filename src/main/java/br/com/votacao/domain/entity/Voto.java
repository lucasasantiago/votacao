package br.com.votacao.domain.entity;

import br.com.votacao.domain.enums.TipoVoto;
import jakarta.persistence.*;

@Entity
@Table(
        name = "votos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pauta_id", "associado_id"})
)
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "associado_id", nullable = false)
    private String associadoId;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVoto voto;

    public Long getId() {
        return id;
    }

    public String getAssociadoId() {
        return associadoId;
    }

    public void setAssociadoId(String associadoId) {
        this.associadoId = associadoId;
    }

    public Pauta getPauta() {
        return pauta;
    }

    public void setPauta(Pauta pauta) {
        this.pauta = pauta;
    }

    public TipoVoto getVoto() {
        return voto;
    }

    public void setVoto(TipoVoto voto) {
        this.voto = voto;
    }
}
