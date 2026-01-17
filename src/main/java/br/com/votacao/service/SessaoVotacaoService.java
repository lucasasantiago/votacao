package br.com.votacao.service;

import br.com.votacao.domain.entity.SessaoVotacao;
import br.com.votacao.repository.SessaoVotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoService.class);

    public SessaoVotacaoService(SessaoVotacaoRepository sessaoVotacaoRepository) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    }

    public SessaoVotacao save(SessaoVotacao sessao) {
        log.info("Iniciando a persistência do dado de sessão. sessao={}", sessao);

        return sessaoVotacaoRepository.save(sessao);
    }
}
