package view;

import java.util.Scanner;

public class MainView {

    private Scanner scanner = new Scanner(System.in);

    public int mostrarMenu() {
        System.out.println();
        System.out.println("===== SISTEMA ISSMF =====");
        System.out.println("1 - Login");
        System.out.println("2 - Criar Estudante");
        System.out.println("3 - Avançar Ano");
        System.out.println("4 - Importar CSV");
        System.out.println("5 - Sair");
        System.out.print("Opção: ");
        return scanner.nextInt();
    }

    public String lerEmail() {
        System.out.print("Email: ");
        return new Scanner(System.in).nextLine();
    }

    public String lerPassword() {
        System.out.print("Password: ");
        return new Scanner(System.in).nextLine();
    }

    public String lerNome() {
        System.out.print("Nome: ");
        return new Scanner(System.in).nextLine();
    }

    public String lerCSV() {
        System.out.print("Nome do ficheiro CSV: ");
        return new Scanner(System.in).nextLine();
    }
}