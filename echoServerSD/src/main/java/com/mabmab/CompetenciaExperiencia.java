package com.mabmab;

import jakarta.persistence.*;

@Entity
@Table(name = "CompetenciaExperiencia")
public class CompetenciaExperiencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidato_email", referencedColumnName = "email", nullable = false)
    private Candidato candidato;

    @ManyToOne
    @JoinColumn(name = "competencia_nome", referencedColumnName = "nome", nullable = false)
    private Competencia competencia;

    @Column(name = "experiencia", nullable = false)
    private int experiencia;

    public CompetenciaExperiencia() {}

    public CompetenciaExperiencia(Candidato candidato, Competencia competencia, int experiencia) {
        this.candidato = candidato;
        this.competencia = competencia;
        this.experiencia = experiencia;
    }

    public Long getId() {
        return id;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }
}
