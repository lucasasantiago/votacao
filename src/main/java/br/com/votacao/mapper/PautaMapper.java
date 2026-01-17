package br.com.votacao.mapper;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.dto.response.PautaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PautaMapper {

    PautaResponse toResponse(Pauta pauta);
}
