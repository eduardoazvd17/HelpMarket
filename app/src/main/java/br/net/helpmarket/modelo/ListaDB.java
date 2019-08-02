package br.net.helpmarket.modelo;

import java.io.Serializable;
import java.util.Objects;

public class ListaDB implements Serializable {

    private String id;
    private String idUsuario;
    private String nome;
    private Double gastoMaximo;
    private Integer quantidadeProdutos;
    private String dataCriacao;
    private Boolean terminado;

    public ListaDB() {

    }

    public ListaDB(String id, String idUsuario, String nome, Double gastoMaximo, Integer quantidadeProdutos, String dataCriacao, Boolean terminado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public ListaDB(String idUsuario, String nome, Double gastoMaximo, Integer quantidadeProdutos, String dataCriacao, Boolean terminado) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.quantidadeProdutos = quantidadeProdutos;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaDB lista = (ListaDB) o;
        return Objects.equals(id, lista.id);
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getGastoMaximo() {
        return gastoMaximo;
    }

    public void setGastoMaximo(Double gastoMaximo) {
        this.gastoMaximo = gastoMaximo;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Boolean getTerminado() {
        return terminado;
    }

    public void setTerminado(Boolean terminado) {
        this.terminado = terminado;
    }

    public Integer getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public void setQuantidadeProdutos(Integer quantidadeProdutos) {
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String id_usuario) {
        this.idUsuario = idUsuario;
    }

}
