package br.net.helpmarket.modelo;

import java.util.Date;

public class Lista {

    private Long id;
    private Usuario usuario;
    private String nome;
    private Double gastoMaximo;
    private Integer quantidadeProdutos;
    private Date dataCriacao;
    private Boolean terminado;

    public Lista(Long id, Usuario usuario, String nome, double gastoMaximo, Integer quantidadeProdutos, Date dataCriacao, Boolean terminado) {
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public Lista(Long id, Usuario usuario, String nome, Integer quantidadeProdutos, Date dataCriacao, Boolean terminado) {
        this.id = id;
        this.usuario = usuario;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public Lista(Usuario usuario, String nome, double gastoMaximo, Date dataCriacao, Boolean terminado) {
        this.usuario = usuario;
        this.nome = nome;
        this.gastoMaximo = gastoMaximo;
        this.dataCriacao = dataCriacao;
        this.terminado = terminado;
    }

    public Lista(Usuario usuario, String nome, Date dataCriacao, Boolean terminado) {
        this.usuario = usuario;
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

    public Integer getQuantidadeProdutos() {
        return quantidadeProdutos;
    }

    public void setQuantidadeProdutos(Integer quantidadeProdutos) {
        this.quantidadeProdutos = quantidadeProdutos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
