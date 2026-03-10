package view;

import java.util.Scanner;
import controller.DocenteController;
import model.*;

public class DocenteView {

    private Scanner scanner = new Scanner(System.in);

    public int menuDocente() {
        System.out.println("\n===== MENU DOCENTE =====");
        System.out.println("1 - Adicionar Avaliação");
        System.out.println("2 - Ver Dados");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return scanner.nextInt();
    }

    public void verDados(Docente d) {
        System.out.println("Sigla: " + d.getSigla());
        System.out.println("Nome: " + d.getNome());
        System.out.println("Email: " + d.getEmail());
    }

    public void adicionarAvaliacao(Docente d, DocenteController dc, SistemaAcademico sistema) {
        UnidadeCurricular[] ucs = dc.listarUCsDocente(d);
        System.out.println("UCs lecionadas:");
        for (int i = 0; i < ucs.length; i++)
            System.out.println((i+1) + " - " + ucs[i].getNome());
        System.out.print("Escolha UC: ");
        int op = scanner.nextInt() - 1;
        if (op < 0 || op >= ucs.length) return;

        UnidadeCurricular uc = ucs[op];
        System.out.print("Número de avaliações (1-3): ");
        int n = scanner.nextInt();
        for (int a = 1; a <= n; a++) {
            System.out.print("Número mecanográfico do estudante: ");
            int num = scanner.nextInt();
            Estudante est = sistema.procurarEstudante(num);
            if (est != null) {
                System.out.print("Nota avaliação " + a + ": ");
                double nota = scanner.nextDouble();
                dc.adicionarNota(est, uc, a, nota);
            } else System.out.println("Estudante não encontrado.");
        }
    }
}