// Ficheiro: view/GestorView.java
package view;

import java.util.Scanner;

public class GestorView {
    private Scanner scanner;

    public GestorView() {
        this.scanner = new Scanner(System.in);
    }

    public int mostrarMenuPrincipalGestor() {
        System.out.println("\n=== BACKOFFICE - GESTOR ===");
        System.out.println("1 - Gerir Departamentos");
        System.out.println("2 - Gerir Cursos");
        System.out.println("3 - Gerir Unidades Curriculares");
        System.out.println("4 - Gerir Estudantes");
        System.out.println("5 - Voltar / Logout");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        return opcao;
    }

    public int mostrarMenuDepartamentos() {
        System.out.println("\n--- GERIR DEPARTAMENTOS ---");
        System.out.println("1 - Adicionar Departamento");
        System.out.println("2 - Alterar Departamento");
        System.out.println("3 - Listar Departamentos");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();
        return opcao;
    }

    public int mostrarMenuCursos() {
        System.out.println("\n--- GERIR CURSOS ---");
        System.out.println("1 - Adicionar Curso");
        System.out.println("2 - Alterar Curso");
        System.out.println("3 - Listar Cursos");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();
        return opcao;
    }

    public int mostrarMenuUCs() {
        System.out.println("\n--- GERIR UNIDADES CURRICULARES ---");
        System.out.println("1 - Adicionar Unidade Curricular");
        System.out.println("2 - Alterar Unidade Curricular");
        System.out.println("3 - Listar Unidades Curriculares");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer
        return opcao;
    }

    public int mostrarMenuEstudantes() {
        System.out.println("\n--- GERIR ESTUDANTES ---");
        System.out.println("1 - Adicionar Estudante");
        System.out.println("2 - Alterar Estudante");
        System.out.println("3 - Listar Estudantes");
        System.out.println("4 - Recuar");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer
        return opcao;
    }

    public String pedirInputString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine();
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }
}