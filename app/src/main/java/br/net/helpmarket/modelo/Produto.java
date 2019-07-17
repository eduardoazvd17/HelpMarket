package br.net.helpmarket.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Produto implements Serializable {

    private Long codigoBarras;
    private String nome;
    private String urlImagem;

    public Produto(Long codigoBarras, String nome, String urlImagem) {
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.urlImagem = urlImagem;
    }

    public void verificarPreenchimento() {
        if (null == urlImagem || urlImagem.isEmpty()) {
            urlImagem = "https://cdn.iset.io/assets/51664/produtos/1192/produto-sem-imagem-1000x1000.jpg";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(codigoBarras, produto.codigoBarras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoBarras);
    }

    public Long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(Long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}
