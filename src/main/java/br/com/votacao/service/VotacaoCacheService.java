package br.com.votacao.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VotacaoCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public VotacaoCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void sinalizarSessaoAberta(Long pautaId, int minutos) {
        String chave = gerarChavePauta(pautaId);
        redisTemplate.opsForValue().set(chave, "true", minutos, TimeUnit.MINUTES);
    }

    public boolean isSessaoAberta(Long pautaId) {
        String chave = gerarChavePauta(pautaId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(chave));
    }

    public Long obterSessaoAtiva(Long pautaId) {
        String chave = gerarChavePauta(pautaId);
        Object valor = redisTemplate.opsForValue().get(chave);
        return valor != null ? Long.valueOf(valor.toString()) : null;
    }

    private String gerarChavePauta(Long pautaId) {
        return "pauta:" + pautaId + ":sessao:ativa";
    }

    public boolean jaVotou(Long pautaId, String associadoId) {
        String chave = gerarChaveVoto(pautaId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(chave, associadoId));
    }

    public void registrarVotoNoCache(Long pautaId, String associadoId) {
        String chave = gerarChaveVoto(pautaId);
        redisTemplate.opsForSet().add(chave, associadoId);
        redisTemplate.expire(chave, 24, TimeUnit.HOURS);
    }

    private String gerarChaveVoto(Long pautaId) {
        return "pauta:" + pautaId + ":votos_registrados";
    }
}
