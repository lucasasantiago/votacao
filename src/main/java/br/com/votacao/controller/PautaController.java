package br.com.votacao.controller;

import br.com.votacao.dto.request.AbrirSessaoRequest;
import br.com.votacao.dto.request.CriarPautaRequest;
import br.com.votacao.service.PautaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pautas")
public class PautaController {

    private final PautaService pautaService;

    private static final Logger log = LoggerFactory.getLogger(PautaController.class);

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping
    public ResponseEntity<Void> criarPauta(@RequestBody @Valid CriarPautaRequest request) {
        log.info("Recebida requisição para criar pauta. Título={}", request.getTitulo());
        pautaService.criarPauta(request.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{pautaId}/sessao")
    public ResponseEntity<Void> abrirSessao(
            @PathVariable Long pautaId,
            @RequestBody(required = false) AbrirSessaoRequest request) {

        log.info("Recebida requisição para abrir a sessão. AbrirSessaoRequest={}", request);

        Integer duracao = request != null ? request.getDuracaoEmMinutos() : null;
        pautaService.abrirSessao(pautaId, duracao);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
