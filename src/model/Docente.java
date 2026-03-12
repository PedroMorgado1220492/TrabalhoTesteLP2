// Ficheiro: model/Docente.java
package model;

public class Docente extends Utilizador {

    private String sigla; // Sigla de 3 letras

    // Arrays tradicionais em vez de ArrayList
    private UnidadeCurricular[] ucsLecionadas;
    private int totalUcsLecionadas;

    private UnidadeCurricular[] ucsResponsavel;
    private int totalUcsResponsavel;

    public Docente(String sigla, String email, String password, String nome, String nif, String morada, String dataNascimento) {
        super(email, password, nome, nif, morada, dataNascimento);
        this.sigla = sigla;

        // Inicializamos os arrays com um limite (ex: 20 UCs máximo por docente)
        this.ucsLecionadas = new UnidadeCurricular[20];
        this.totalUcsLecionadas = 0;

        this.ucsResponsavel = new UnidadeCurricular[20];
        this.totalUcsResponsavel = 0;
    }

    // ---------- GETTERS ----------
    public String getSigla() { return sigla; }
    public UnidadeCurricular[] getUcsLecionadas() { return ucsLecionadas; }
    public int getTotalUcsLecionadas() { return totalUcsLecionadas; }
    public UnidadeCurricular[] getUcsResponsavel() { return ucsResponsavel; }
    public int getTotalUcsResponsavel() { return totalUcsResponsavel; }

    // ---------- SETTERS ----------
    public void setSigla(String sigla) { this.sigla = sigla; }

    // ---------- MÉTODOS PARA ADICIONAR AOS ARRAYS ----------
    public boolean adicionarUcLecionada(UnidadeCurricular uc) {
        if (totalUcsLecionadas < ucsLecionadas.length) {
            ucsLecionadas[totalUcsLecionadas] = uc;
            totalUcsLecionadas++;
            return true;
        }
        return false; // Array cheio
    }

    public boolean adicionarUcResponsavel(UnidadeCurricular uc) {
        if (totalUcsResponsavel < ucsResponsavel.length) {
            ucsResponsavel[totalUcsResponsavel] = uc;
            totalUcsResponsavel++;
            return true;
        }
        return false; // Array cheio
    }
}