package br.com.votacao.service;

import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.dto.VotoContagem;
import br.com.votacao.dto.response.ResultadoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.message.VotoMessage;
import br.com.votacao.message.producer.VotoProducer;
import br.com.votacao.util.VotacaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VotoService {

    private final VotacaoCacheService cacheService;
    private final VotoProducer votoProducer;
    private final PautaService pautaService;
    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    public VotoService(VotacaoCacheService cacheService, VotoProducer votoProducer, PautaService pautaService) {
        this.cacheService = cacheService;
        this.votoProducer = votoProducer;
        this.pautaService = pautaService;
    }

    public void votar(Long pautaId, String associadoId, TipoVoto voto) {
        log.info("Iniciando a votação. pautaId={}, associadoId={}, voto={}", pautaId, associadoId, voto);

        Long sessaoId = cacheService.obterSessaoAtiva(pautaId);

        if (sessaoId == null) {
            log.info("Não existe sessão aberta para esta pauta. pautaId={}, associadoId={}", pautaId, associadoId);
            throw new BusinessException("Não existe sessão aberta para esta pauta.");
        }

        if (cacheService.jaVotou(pautaId, associadoId)) {
            log.info("O associado já votou. pautaId={}, associadoId={}", pautaId, associadoId);
            throw new BusinessException("Associado já votou");
        }

        cacheService.registrarVotoNoCache(pautaId, associadoId);
        votoProducer.enviarVoto(new VotoMessage(pautaId, sessaoId, associadoId, voto));

        log.info("O voto do associado foi registrado com sucesso. pautaId={}, associadoId={}, voto={}", pautaId, associadoId, voto);
    }

    public ResultadoVotacaoResponse resultado(Long pautaId) {
        log.info("Obtendo o resultado da votação. pautaId={}", pautaId);

        VotoContagem votosContagem = pautaService.votosContagem(pautaId);
        return new ResultadoVotacaoResponse(pautaId, votosContagem.votosSim(), votosContagem.votosNao(),
                VotacaoUtils.calcularResultado(votosContagem.votosSim(), votosContagem.votosNao()));
    }
}
