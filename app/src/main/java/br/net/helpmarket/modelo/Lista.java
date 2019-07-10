package br.net.helpmarket.modelo;

import java.util.Date;

public class Lista {

    private Long id;
    private String nome;
    private Double gastoMaximo;
    private Date dataCriacao;

    public Lista(Long id, String nome, double gastoMaximo, Date dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
    }

    public Lista(Long id, String nome, Date dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
    }

    public Lista(String nome, double gastoMaximo, Date dataCriacao) {
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
    }

    public Lista(String nome, Date dataCriacao) {
        this.nome = nome;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
