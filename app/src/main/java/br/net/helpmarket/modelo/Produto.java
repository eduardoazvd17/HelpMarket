package br.net.helpmarket.modelo;

public class Produto {

    private Long codigoBarras;
    private String nome;
    private String urlImagem;

    public Produto(Long codigoBarras, String nome, String urlImagem) {
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.urlImagem = urlImagem;
    }

    public Produto(Long codigoBarras, String nome) {
        this.codigoBarras = codigoBarras;
        this.nome = nome;
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
