package br.com.gestaocercria.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import br.com.gestaocercria.api.entidade.Produto;

public class ProdutoTest {

    @Test
    void deveTestarGettersESetters() {
        Produto produto = new Produto();

        produto.setId(1);
        produto.setNome("Arroz");
        produto.setCategoria("Alimento");
        produto.setUnidadeMedida("Kg");
        produto.setRendimento("10");

        assertEquals(1, produto.getId());
        assertEquals("Arroz", produto.getNome());
        assertEquals("Alimento", produto.getCategoria());
        assertEquals("Kg", produto.getUnidadeMedida());
        assertEquals("10", produto.getRendimento());
    }

    @Test
    void deveCriarProdutoComValoresNulos() {
        Produto produto = new Produto();

        assertEquals(0, produto.getId());
        assertNull(produto.getNome());
        assertNull(produto.getCategoria());
        assertNull(produto.getUnidadeMedida());
        assertNull(produto.getRendimento());
    }

    @Test
    void deveAlterarValoresDoProduto() {
        Produto produto = new Produto();

        produto.setNome("Feijão");
        assertEquals("Feijão", produto.getNome());

        produto.setNome("Macarrão");
        assertEquals("Macarrão", produto.getNome());
    }

    @Test
    void deveAceitarStringsVazias() {
        Produto produto = new Produto();

        produto.setNome("");
        produto.setCategoria("");
        produto.setUnidadeMedida("");
        produto.setRendimento("");

        assertEquals("", produto.getNome());
        assertEquals("", produto.getCategoria());
        assertEquals("", produto.getUnidadeMedida());
        assertEquals("", produto.getRendimento());
    }
}