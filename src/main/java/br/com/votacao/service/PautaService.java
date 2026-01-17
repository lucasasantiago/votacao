package br.com.votacao.service;

import br.com.votacao.domain.entity.Pauta;
import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.repository.PautaRepository;
import br.com.votacao.repository.SessaoVotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PautaService {
    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final VotacaoCacheService cacheService;
    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    public PautaService(PautaRepository pautaRepository,
                        SessaoVotacaoRepository sessaoRepository, VotacaoCacheService cacheService) {
        this.pautaRepository = pautaRepository;
        this.sessaoRepository = sessaoRepository;
        this.cacheService = cacheService;
    }

    public Pauta criarPauta(String titulo) {
        log.info("Criando a pauta para o título={}", titulo);

        Pauta pauta = new Pauta();
        pauta.setTitulo(titulo);
        Pauta pautaSalva = pautaRepository.save(pauta);
        log.info("Pauta criada para o título={}", titulo);
        return pautaSalva;
    }

    public void abrirSessao(Long pautaId, Integer minutos) {
        log.info("Abrindo sessão de votação para pautaId={}", pautaId);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> {
                    log.warn("Pauta não encontrada. pautaId={}", pautaId);
                    return new BusinessException("Pauta não encontrada");
                });

        if (pauta.getSessao() != null) {
            log.warn("Tentativa de abrir sessão já existente. pautaId={}", pautaId);
            throw new BusinessException("Sessão já aberta");
        }

        int duracaoMinutos = minutos != null ? minutos : 1;
        log.info("Sessão será aberta por {} minuto(s)", duracaoMinutos);

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setPauta(pauta);
        sessao.setInicio(LocalDateTime.now());
        sessao.setFim(LocalDateTime.now().plusMinutes(duracaoMinutos));

        SessaoVotacao sessaoSalva = sessaoRepository.save(sessao);
        log.info("Sessão criada com sucesso para pautaId={}", pautaId);

        cacheService.sinalizarSessaoAberta(pautaId, sessaoSalva.getId(), duracaoMinutos);
    }
}
