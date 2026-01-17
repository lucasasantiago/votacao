package br.com.votacao.controller;

import br.com.votacao.domain.enums.TipoVoto;
import br.com.votacao.dto.request.VotoRequest;
import br.com.votacao.dto.response.ResultadoVotacaoResponse;
import br.com.votacao.dto.response.VotoResponse;
import br.com.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/votos")
public class VotoController {

    private final VotoService votoService;
    private static final Logger log = LoggerFactory.getLogger(VotoController.class);

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/pautas/{pautaId}")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable Long pautaId,
            @RequestBody @Valid VotoRequest request) {
        log.info("Recebida requisição para votar. VotoRequest={}", request);

        TipoVoto tipoVoto = TipoVoto.buscarPorDescricao(request.getVoto());
        votoService.votar(pautaId, request.getAssociadoId(), tipoVoto);
        VotoResponse response = new VotoResponse("Voto recebido com sucesso e enviado para processamento.", LocalDateTime.now());

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/pautas/{pautaId}/resultado")
    public ResponseEntity<ResultadoVotacaoResponse> resultado(@PathVariable Long pautaId) {
        log.info("Recebida requisição para obter o resultado para a pautaId={}", pautaId);

        return ResponseEntity.ok(votoService.resultado(pautaId));
    }
}
