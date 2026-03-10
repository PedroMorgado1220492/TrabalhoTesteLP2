package model;

public class Avaliacao {

    private Estudante estudante;
    private UnidadeCurricular uc;
    private int ano;
    private double[] notas = new double[3];
    private int totalAvaliacoes = 0;

    public Avaliacao(Estudante estudante, UnidadeCurricular uc, int ano) {
        this.estudante = estudante;
        this.uc = uc;
        this.ano = ano;
    }

    public void adicionarNota(double nota) {
        if (totalAvaliacoes < 3) {
            notas[totalAvaliacoes] = nota;
            totalAvaliacoes++;
        }
    }

    public double calcularMedia() {
        if (totalAvaliacoes == 0) return 0;
        double soma = 0;
        for (int i = 0; i < totalAvaliacoes; i++) soma += notas[i];
        return soma / totalAvaliacoes;
    }

    public boolean aprovado() {
        return calcularMedia() >= 10;
    }

    public Estudante getEstudante() { return estudante; }
    public UnidadeCurricular getUc() { return uc; }
    public int getAno() { return ano; }
}