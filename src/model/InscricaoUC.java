package model;

public class InscricaoUC {

    private Docente docente;
    private UnidadeCurricular uc;
    private String papel; // Ex: Regente, Responsável
    private double nota;

    public InscricaoUC(Docente docente, UnidadeCurricular uc, String papel) {
        this.docente = docente;
        this.uc = uc;
        this.papel = papel;
        this.nota = -1; // -1 significa ainda sem nota
    }

    public Docente getDocente() {
        return docente;
    }

    public UnidadeCurricular getUc() {
        return uc;
    }

    public String getPapel() {
        return papel;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public double getNota() {
        return nota;
    }

    public boolean estaAprovada() {
        return nota >= 10.0;
    }
}