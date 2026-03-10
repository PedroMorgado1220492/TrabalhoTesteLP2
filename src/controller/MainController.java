package controller;

import model.*;
import view.*;
import util.CSVService;
import util.PasswordGenerator;
import java.util.Scanner;

public class MainController {

    private SistemaAcademico sistema;
    private MainView mainView;
    private GestorController gestorController;
    private DocenteController docenteController;
    private EstudanteController estudanteController;
    private Scanner sc;

    public MainController(SistemaAcademico sistema) {
        this.sistema = sistema;
        this.mainView = new MainView();
        this.gestorController = new GestorController(sistema);
        this.docenteController = new DocenteController(sistema);
        this.estudanteController = new EstudanteController();
        this.sc = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            int opcao = mainView.mostrarMenu();
            switch (opcao) {
                case 1: // Login
                    loginFlow();
                    break;

                case 2: // Criar estudante sem login
                    System.out.print("Nome do estudante: ");
                    String nome = sc.nextLine();

                    System.out.print("NIF: ");
                    String nif = sc.nextLine();

                    System.out.print("Data nascimento: ");
                    String dataNascimento = sc.nextLine();

                    System.out.print("Morada: ");
                    String morada = sc.nextLine();

                    int numero = sistema.gerarNumeroMecanografico();
                    Curso curso = null; // podes depois pedir o curso ao utilizador

                    gestorController.criarEstudante(numero, nome, nif, dataNascimento, morada, curso);

                    System.out.print("Indique o caminho do CSV para guardar: ");
                    String caminhoExport = sc.nextLine();
                    CSVService.exportarSistema(sistema, caminhoExport);
                    break;

                case 3: // Avançar Ano
                    sistema.avancarAno();
                    System.out.print("Indique o caminho do CSV para guardar: ");
                    caminhoExport = sc.nextLine();
                    CSVService.exportarSistema(sistema, caminhoExport);
                    break;

                case 4: // Importar CSV manual
                    System.out.print("Indique o caminho do ficheiro CSV: ");
                    String caminhoCSV = sc.nextLine();
                    CSVService.importarCSV(sistema, caminhoCSV);
                    break;

                case 5: // Sair
                    System.out.print("Indique o caminho do CSV para guardar antes de sair: ");
                    caminhoExport = sc.nextLine();
                    CSVService.exportarSistema(sistema, caminhoExport);
                    running = false;
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }

        System.out.println("Programa terminado.");
    }

    private void loginFlow() {
        String email = mainView.lerEmail();
        String password = mainView.lerPassword();

        Utilizador user = new AuthController(sistema).login(email, password);
        if (user == null) {
            System.out.println("Login inválido.");
            return;
        }

        if (user instanceof Gestor) {
            System.out.println("Backoffice");
            menuGestor((Gestor) user);

        } else if (user instanceof Docente) {
            Docente d = (Docente) user;
            System.out.println("Docente: " + d.getSigla());
            menuDocente(d);

        } else if (user instanceof Estudante) {
            Estudante e = (Estudante) user;
            System.out.println("Estudante: " + e.getNumeroMecanografico());
            menuEstudante(e);
        }
    }

    private void menuGestor(Gestor gestor) {
        GestorView gv = new GestorView();
        boolean gestorRunning = true;
        while (gestorRunning) {
            int menu = gv.menuGestor();
            switch (menu) {
                case 1: gv.departamentoMenu(gestorController); break;
                case 2: gv.cursoMenu(gestorController); break;
                case 3: gv.ucMenu(gestorController); break;
                case 4: gv.docenteMenu(gestorController); break;
                case 5: gv.estudanteMenu(gestorController); break;
                case 6: gv.alterarPassword(gestor); break;
                case 0: gestorRunning = false; break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void menuDocente(Docente docente) {
        DocenteView dv = new DocenteView();
        boolean docenteRunning = true;
        while (docenteRunning) {
            int menu = dv.menuDocente();
            switch (menu) {
                case 1: dv.adicionarAvaliacao(docente, docenteController, sistema); break;
                case 2: dv.verDados(docente); break;
                case 0: docenteRunning = false; break;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void menuEstudante(Estudante estudante) {
        EstudanteView ev = new EstudanteView();
        boolean estudanteRunning = true;
        while (estudanteRunning) {
            int menu = ev.menuEstudante();
            switch (menu) {
                case 1: ev.verDados(estudante); break;
                case 2: ev.verPercurso(estudante, estudanteController); break;
                case 3: ev.alterarDados(estudante); break;
                case 0: estudanteRunning = false; break;
                default: System.out.println("Opção inválida.");
            }
        }
    }
}