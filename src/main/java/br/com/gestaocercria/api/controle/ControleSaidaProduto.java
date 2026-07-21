package br.com.gestaocercria.api.controle;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import br.com.gestaocercria.api.entidade.EntradaProduto;
import br.com.gestaocercria.api.entidade.SaidaProduto;
import br.com.gestaocercria.api.repositorio.RepositorioEntradaProduto;
import br.com.gestaocercria.api.repositorio.RepositorioSaidaProduto;
import lombok.NonNull;

@RestController
@RequestMapping("/controle-produto/saida")
public class ControleSaidaProduto {
    private final RepositorioSaidaProduto acao;
    private final RepositorioEntradaProduto entradaRepo;

    public ControleSaidaProduto(RepositorioSaidaProduto acao, RepositorioEntradaProduto entradaRepo){
        this.acao = acao;
        this.entradaRepo = entradaRepo;
    }

    @PostMapping("/cadastro")
    public SaidaProduto cadastrar(@RequestBody @NonNull SaidaProduto s) {

        Double restante = s.getQuantidade();

        List<EntradaProduto> lotes = entradaRepo.findByProdutoIdAndQuantidadeAtualGreaterThanOrderByDataEntradaAsc(
                s.getProduto().getId(), 0.0);

        System.out.println("Lotes encontrados: " + lotes.size());
        
        for (EntradaProduto lote : lotes) {

            if (restante <= 0)
                break;

            if (lote.getQuantidadeAtual() <= restante) {
                restante -= lote.getQuantidadeAtual();
                lote.setQuantidadeAtual(0.0);

            } else {
                lote.setQuantidadeAtual(
                    lote.getQuantidadeAtual() - restante
                );
                restante = 0.0;
            }

            entradaRepo.save(lote);
        }

        if(restante > 0){
            throw new RuntimeException("Estoque insuficiente.");
        }

        return acao.save(s);
    }

    @GetMapping("/listagem")
    public Iterable<SaidaProduto> selecionar() {
        return acao.findAll();
    }
}
