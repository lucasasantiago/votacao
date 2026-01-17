package br.com.votacao.mapper;

import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.dto.response.SessaoVotacaoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessaoVotacaoMapper {

    SessaoVotacaoResponse toResponse(SessaoVotacao pauta);
}
