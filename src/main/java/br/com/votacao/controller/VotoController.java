package br.com.votacao.controller;

import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.dto.request.VotoRequest;
import br.com.votacao.dto.response.ResultadoVotacaoResponse;
import br.com.votacao.exception.BusinessException;
import br.com.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votos")
public class VotoController {

    private final VotoService votoService;
    private static final Logger log = LoggerFactory.getLogger(VotoController.class);

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/{pautaId}")
    public ResponseEntity<Void> votar(
            @PathVariable Long pautaId,
            @RequestBody @Valid VotoRequest request) {
        log.info("Recebida requisição para votar. VotoRequest={}", request);

        TipoVoto tipoVoto;
        try {
            tipoVoto = TipoVoto.valueOf(request.getVoto().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Voto inválido. Utilize SIM ou NAO.");
        }

        votoService.votar(pautaId, request.getAssociadoId(), tipoVoto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pauta/{pautaId}/resultado")
    public ResponseEntity<ResultadoVotacaoResponse> resultado(@PathVariable Long pautaId) {
        log.info("Recebida requisição para obter o resultado para a pautaId={}", pautaId);

        return ResponseEntity.ok(votoService.resultado(pautaId));
    }
}
