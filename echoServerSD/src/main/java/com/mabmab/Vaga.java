package com.mabmab;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Vaga")
public class Vaga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_email", nullable = false)
    private Empresa empresa;

    @Column(name = "nome", length = 100)
    private String nome;

    @Column(name = "faixa_salarial")
    private double faixaSalarial;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @ManyToMany
    @JoinTable(
            name = "vaga_competencia",
            joinColumns = @JoinColumn(name = "vaga_id"),
            inverseJoinColumns = @JoinColumn(name = "competencia_id")
    )
    private List<Competencia> competencias;

    @Column(name = "estado", length = 20)
    private String estado;

    // Construtores, getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getFaixaSalarial() {
        return faixaSalarial;
    }

    public void setFaixaSalarial(double faixaSalarial) {
        this.faixaSalarial = faixaSalarial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Competencia> getCompetencias() {
        return competencias;
    }

    public void setCompetencias(List<Competencia> competencias) {
        this.competencias = competencias;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void adicionarCompetencia(Competencia competencia) {
        competencias.add(competencia);
    }

    public void removerCompetencia(Competencia competencia) {
        competencias.remove(competencia);
    }

}
