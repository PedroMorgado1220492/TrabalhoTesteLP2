// Ficheiro: view/DocenteView.java
package view;

import java.util.Scanner;

public class DocenteView {
    private Scanner scanner;

    public DocenteView() {
        this.scanner = new Scanner(System.in);
    }

    public int mostrarMenuPrincipal() {
        System.out.println("\n=== ÁREA DO DOCENTE ===");
        System.out.println("1 - Ver Dados Pessoais");
        System.out.println("2 - Atualizar Dados");
        System.out.println("3 - Adicionar Avaliação a Aluno");
        System.out.println("4 - Sair / Logout");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();
        return opcao;
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }
}