package br.com.votacao.domain.enums;

import br.com.votacao.exception.BusinessException;

public enum TipoVoto {
    SIM("Sim"),
    NAO("Não");

    private final String descricao;

    TipoVoto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoVoto buscarPorDescricao(String valor) {
        for (TipoVoto tipo : values()) {
            if (tipo.descricao.equalsIgnoreCase(valor) || tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new BusinessException("Voto inválido. Utilize Sim ou Não.");
    }
}
