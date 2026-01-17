package br.com.votacao.service;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.domain.entity.Voto;
import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.dto.response.ResultadoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.message.VotoProducer;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.SessaoVotacaoRepository;
import br.com.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;
    private final VotacaoCacheService cacheService;
    private final VotoProducer votoProducer;
    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    public VotoService(VotoRepository votoRepository,
                       SessaoVotacaoRepository sessaoRepository,
                       PautaRepository pautaRepository, VotacaoCacheService cacheService, VotoProducer votoProducer) {
        this.votoRepository = votoRepository;
        this.sessaoRepository = sessaoRepository;
        this.pautaRepository = pautaRepository;
        this.cacheService = cacheService;
        this.votoProducer = votoProducer;
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
        votoProducer.enviarVoto(new VotoMensagem(pautaId, sessaoId, associadoId, voto));

        Pauta pauta = pautaRepository.getReferenceById(pautaId);

        Voto novoVoto = new Voto();
        novoVoto.setAssociadoId(associadoId);
        novoVoto.setPauta(pauta);
        novoVoto.setVoto(voto);

        votoRepository.save(novoVoto);
        log.info("O voto do associado foi registrado com sucesso. pautaId={}, associadoId={}, voto={}", pautaId, associadoId, voto);
    }

    public ResultadoVotacaoResponse resultado(Long pautaId) {
        log.info("Obtendo o resultado da votação. pautaId={}", pautaId);

        long sim = votoRepository.countByPautaIdAndVoto(pautaId, TipoVoto.SIM);
        long nao = votoRepository.countByPautaIdAndVoto(pautaId, TipoVoto.NAO);
        return new ResultadoVotacaoResponse(sim, nao);
    }
}
