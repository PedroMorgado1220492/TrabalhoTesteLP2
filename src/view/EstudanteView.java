package view;

import java.util.Scanner;
import controller.EstudanteController;
import model.Estudante;

public class EstudanteView {

    private Scanner scanner = new Scanner(System.in);

    public int menuEstudante() {
        System.out.println("\n===== MENU ESTUDANTE =====");
        System.out.println("1 - Ver Dados");
        System.out.println("2 - Ver Percurso Académico");
        System.out.println("3 - Alterar Dados");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return scanner.nextInt();
    }

    public void verDados(Estudante e) {
        EstudanteController ec = new EstudanteController();
        ec.verDados(e);
    }

    public void verPercurso(Estudante e, EstudanteController ec) {
        ec.verPercurso(e);
    }

    public void alterarDados(Estudante e) {
        scanner.nextLine();
        System.out.print("Novo nome: ");
        e.setNome(scanner.nextLine());
        System.out.println("Dados atualizados.");
    }
}