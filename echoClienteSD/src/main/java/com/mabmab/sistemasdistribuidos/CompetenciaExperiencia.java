package com.mabmab.sistemasdistribuidos;

public class CompetenciaExperiencia {

    private String competencia;
    private int experiencia;

    public CompetenciaExperiencia(String competencia, int experiencia) {
        this.competencia = competencia;
        this.experiencia = experiencia;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }
}
