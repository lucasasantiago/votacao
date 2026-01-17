package br.com.votacao.service;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.dto.VotoContagem;
import br.com.votacao.dto.request.AbrirSessaoRequest;
import br.com.votacao.dto.response.PautaResponse;
import br.com.votacao.dto.response.SessaoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.mapper.PautaMapper;
import br.com.votacao.mapper.SessaoVotacaoMapper;
import br.com.votacao.repository.PautaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PautaService {
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotacaoCacheService cacheService;
    private final PautaMapper pautaMapper;
    private final SessaoVotacaoMapper sessaoVotacaoMapper;
    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    public PautaService(PautaRepository pautaRepository,
                        SessaoVotacaoService sessaoVotacaoService, VotacaoCacheService cacheService, PautaMapper pautaMapper, SessaoVotacaoMapper sessaoVotacaoMapper) {
        this.pautaRepository = pautaRepository;
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.cacheService = cacheService;
        this.pautaMapper = pautaMapper;
        this.sessaoVotacaoMapper = sessaoVotacaoMapper;
    }

    public PautaResponse criarPauta(String titulo) {
        log.info("Criando a pauta para o título={}", titulo);

        Pauta pauta = new Pauta();
        pauta.setTitulo(titulo);
        Pauta pautaSalva = pautaRepository.save(pauta);
        log.info("Pauta criada para o título={}", titulo);
        return pautaMapper.toResponse(pautaSalva);
    }

    public SessaoVotacaoResponse abrirSessao(Long pautaId, AbrirSessaoRequest abrirSessaoRequest) {
        log.info("Abrindo sessão de votação para pautaId={}", pautaId);

        Pauta pauta = pautaRepository.findPautaComSessaoAtiva(pautaId, LocalDateTime.now())
                .orElseThrow(() -> {
                    log.warn("Pauta não encontrada. pautaId={}", pautaId);
                    return new BusinessException("Pauta não encontrada");
                });

        if (pauta.getSessao() != null) {
            log.warn("Tentativa de abrir sessão já existente. pautaId={}", pautaId);
            throw new BusinessException("Sessão já aberta");
        }

        int duracaoMinutos = abrirSessaoRequest != null ? abrirSessaoRequest.getDuracaoEmMinutos() : 1;
        log.info("Sessão será aberta por {} minuto(s)", duracaoMinutos);

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setInicio(LocalDateTime.now());
        sessao.setFim(LocalDateTime.now().plusMinutes(duracaoMinutos));

        SessaoVotacao sessaoSalva = sessaoVotacaoService.save(sessao);
        log.info("Sessão criada com sucesso para pautaId={}", pautaId);

        cacheService.sinalizarSessaoAberta(pautaId, sessaoSalva.getId(), duracaoMinutos);

        return sessaoVotacaoMapper.toResponse(sessaoSalva);
    }

    public VotoContagem votosContagem(Long pautaId){
        log.info("Iniciando a contagem de votos no Pauta Service para pautaId={}", pautaId);

        return pautaRepository.votosContagem(pautaId);
    }
}
