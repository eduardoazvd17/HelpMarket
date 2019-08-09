package br.net.helpmarket.modelo;

import java.io.Serializable;
import java.util.Objects;

public class CompraDB implements Serializable {

    private String id;
    private String idUsuario;
    private String idLista;
    private Produto produto;
    private String nomePersonalizado;
    private Integer quantidade;
    private Double preco;
    private Boolean comprado;

    public CompraDB() {

    }

    public CompraDB(String id, String idUsuario, String idLista, Produto produto, String nomePersonalizado, Integer quantidade, Double preco, Boolean comprado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idLista = idLista;
        this.produto = produto;
        this.nomePersonalizado = nomePersonalizado;
        this.quantidade = quantidade;
        this.preco = preco;
        this.comprado = comprado;
    }

    public CompraDB(String idUsuario, String idLista, Produto produto, String nomePersonalizado, Integer quantidade, Double preco, Boolean comprado) {
        this.idUsuario = idUsuario;
        this.idLista = idLista;
        this.produto = produto;
        this.nomePersonalizado = nomePersonalizado;
        this.quantidade = quantidade;
        this.preco = preco;
        this.comprado = comprado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompraDB compra = (CompraDB) o;
        return Objects.equals(id, compra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNomePersonalizado() {
        return nomePersonalizado;
    }

    public void setNomePersonalizado(String nomePersonalizado) {
        this.nomePersonalizado = nomePersonalizado;
    }

    public Boolean getComprado() {
        return comprado;
    }

    public void setComprado(Boolean comprado) {
        this.comprado = comprado;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdLista() {
        return idLista;
    }

    public void setIdLista(String idLista) {
        this.idLista = idLista;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
