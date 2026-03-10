package model;

public class PercursoAcademico {

    private static final int MAX = 200;

    private Estudante estudante;

    private UnidadeCurricular[] ucsInscritas = new UnidadeCurricular[MAX];
    private int totalUCsInscritas = 0;

    private UnidadeCurricular[] ucsAprovadas = new UnidadeCurricular[MAX];
    private double[] notas = new double[MAX];
    private int totalUCsAprovadas = 0;

    private Avaliacao[] avaliacoes = new Avaliacao[MAX];
    private int totalAvaliacoes = 0;

    private int anoCurricular;

    public PercursoAcademico(Estudante estudante) {
        this.estudante = estudante;
        this.anoCurricular = 1;
    }

    public UnidadeCurricular[] getUcsAprovadas() {
        return ucsAprovadas;
    }

    public double[] getNotas() {
        return notas;
    }

    public int getTotalUCsAprovadas() {
        return totalUCsAprovadas;
    }

    public int getAnoCurricular() {
        return anoCurricular;
    }

    public void setAnoCurricular(int anoCurricular) {
        this.anoCurricular = anoCurricular;
    }

    public Avaliacao getAvaliacao(UnidadeCurricular uc) {
        for (int i = 0; i < totalAvaliacoes; i++) {
            if (avaliacoes[i].getUc() == uc) return avaliacoes[i];
        }
        return null;
    }

    public void adicionarAvaliacao(Avaliacao a) {
        registrarAvaliacao(a);
    }

    public void inscreverUC(UnidadeCurricular uc) {
        if (totalUCsInscritas < MAX) {
            ucsInscritas[totalUCsInscritas++] = uc;
        }
    }

    public void registrarAvaliacao(Avaliacao a) {
        if (totalAvaliacoes < MAX) {
            avaliacoes[totalAvaliacoes++] = a;
            if (a.aprovado()) {
                aprovarUC(a.getUc(), a.calcularMedia());
            }
        }
    }

    private void aprovarUC(UnidadeCurricular uc, double nota) {
        if (totalUCsAprovadas < MAX) {
            ucsAprovadas[totalUCsAprovadas] = uc;
            notas[totalUCsAprovadas] = nota;
            totalUCsAprovadas++;
        }
    }

    public void verPercurso() {
        System.out.println("===== Percurso Académico =====");
        System.out.println("Ano Curricular: " + anoCurricular);
        System.out.println("UCs Inscritas:");
        for (int i = 0; i < totalUCsInscritas; i++) {
            System.out.println("- " + ucsInscritas[i].getNome() + " | Ano: " + ucsInscritas[i].getAnoCurricular());
        }
        System.out.println("UCs Aprovadas:");
        double somaNotas = 0;
        for (int i = 0; i < totalUCsAprovadas; i++) {
            System.out.println("- " + ucsAprovadas[i].getNome() + " | Nota: " + notas[i]);
            somaNotas += notas[i];
        }
        double media = totalUCsAprovadas > 0 ? somaNotas / totalUCsAprovadas : 0;
        System.out.printf("Média Geral: %.2f\n", media);
    }

    public UnidadeCurricular[] getUcsInscritas() { return ucsInscritas; }
    public int getTotalUCsInscritas() { return totalUCsInscritas; }
}