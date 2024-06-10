package com.mabmab;

import jakarta.persistence.*;

@Entity
@Table(name = "Candidato")
public class Candidato {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "senha", length = 50)
    private String senha;

    public Candidato() {

    }
    public Candidato(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Long getId() {
        return id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
