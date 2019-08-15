package br.net.helpmarket.modelo;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Objects;

public class Compra implements Serializable {

    private String id;
    private Lista lista;
    private Produto produto;
    private String nomePersonalizado;
    private Integer quantidade;
    private Double preco;
    private Boolean comprado;

    public Compra() {

    }

    public Compra(String id, Lista lista, Produto produto, String nomePersonalizado, Integer quantidade, Double preco, Boolean comprado) {
        this.id = id;
        this.lista = lista;
        this.produto = produto;
        this.nomePersonalizado = nomePersonalizado;
        this.quantidade = quantidade;
        this.preco = preco;
        this.comprado = comprado;
    }

    public Compra(Lista lista, Produto produto, String nomePersonalizado, Integer quantidade, Double preco, Boolean comprado) {
        this.lista = lista;
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
        Compra compra = (Compra) o;
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
}
