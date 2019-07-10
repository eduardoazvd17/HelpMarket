package br.net.helpmarket.modelo;

public class ListaCompras {

    private Long id;
    private Lista lista;
    private Produto produto;
    private String nomePersonalizado;
    private Integer quantidade;
    private Double preco;

    public ListaCompras(Long id, Lista lista, Produto produto, String nomePersonalizado, Integer quantidade, Double preco) {
        this.id = id;
        this.lista = lista;
        this.produto = produto;
        this.nomePersonalizado = nomePersonalizado;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public ListaCompras(Lista lista, Produto produto, String nomePersonalizado, Integer quantidade, Double preco) {
        this.lista = lista;
        this.produto = produto;
        this.nomePersonalizado = nomePersonalizado;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lista getLista() {
        return lista;
    }

    public void setLista(Lista lista) {
        this.lista = lista;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
