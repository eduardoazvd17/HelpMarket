package br.net.helpmarket.modelo;

import java.util.Date;

public class Lista {

    private Long id;
    private String nome;
    private Double gastoMaximo;
    private Date dataCriacao;
    private Boolean terminado;

    public Lista(Long id, String nome, double gastoMaximo, Date dataCriacao, Boolean terminado) {
        this.id = id;
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
    }

    public Lista(Long id, String nome, Date dataCriacao, Boolean terminado) {
        this.id = id;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
    }

    public Lista(String nome, double gastoMaximo, Date dataCriacao, Boolean terminado) {
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
    }

    public Lista(String nome, Date dataCriacao, Boolean terminado) {
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
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

    public Boolean getTerminado() {
        return terminado;
    }

    public void setTerminado(Boolean terminado) {
        this.terminado = terminado;
    }
}
