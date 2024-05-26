package com.mabmab;

import jakarta.persistence.*;

@Entity
@Table(name = "Empresa")
public class Empresa {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "razaoSocial", length = 100)
    private String razaoSocial;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "cnpj", length = 20)
    private String cnpj;

    @Column(name = "senha", length = 50)
    private String senha;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "ramo", length = 100)
    private String ramo;

    public Empresa() {
    }

    public Empresa(String razaoSocial, String email, String cnpj, String senha, String descricao, String ramo) {
        this.razaoSocial = razaoSocial;
        this.email = email;
        this.cnpj = cnpj;
        this.senha = senha;
        this.descricao = descricao;
        this.ramo = ramo;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }
}
