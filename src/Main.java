import model.*;
import controller.*;
import view.*;
import util.CSVService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        SistemaAcademico sistema = new SistemaAcademico();

        MainView mainView = new MainView();
        AuthController authController = new AuthController(sistema);
        GestorController gestorController = new GestorController(sistema);
        DocenteController docenteController = new DocenteController(sistema);
        EstudanteController estudanteController = new EstudanteController();

        Scanner sc = new Scanner(System.in);

        boolean running = true;

        while (running) {

            int opcao = mainView.mostrarMenu();

            switch (opcao) {

                case 1: // Login

                    String email = mainView.lerEmail();
                    String password = mainView.lerPassword();

                    Utilizador user = authController.login(email, password);

                    if (user == null) {
                        System.out.println("Login inválido.");
                        break;
                    }

                    if (user instanceof Gestor) {
                        menuGestor((Gestor) user, gestorController, sc);
                    }
                    else if (user instanceof Docente) {
                        menuDocente((Docente) user, docenteController, sistema, sc);
                    }
                    else if (user instanceof Estudante) {
                        menuEstudante((Estudante) user, estudanteController, sc);
                    }

                    break;

                case 2: // Criar estudante

                    System.out.print("Nome do estudante: ");
                    String nome = sc.nextLine();

                    System.out.print("NIF: ");
                    String nif = sc.nextLine();

                    System.out.print("Data de nascimento: ");
                    String dataNascimento = sc.nextLine();

                    System.out.print("Morada: ");
                    String morada = sc.nextLine();

                    int numero = sistema.gerarNumeroMecanografico();

                    // Escolha do curso
                    System.out.println("Cursos disponíveis:");
                    Curso[] cursos = sistema.getCursos();
                    for (int i = 0; i < sistema.getTotalCursos(); i++) {
                        System.out.println((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
                    }
                    System.out.print("Escolha o curso (número): ");
                    int cursoIndex = Integer.parseInt(sc.nextLine()) - 1;

                    Curso cursoEscolhido = null;
                    if (cursoIndex >= 0 && cursoIndex < sistema.getTotalCursos()) {
                        cursoEscolhido = cursos[cursoIndex];
                    } else {
                        System.out.println("Curso inválido, estudante ficará sem curso por enquanto.");
                    }

                    gestorController.criarEstudante(numero, nome, nif, dataNascimento, morada, cursoEscolhido);

                    System.out.println("Estudante criado com sucesso.");
                    break;

                case 3: // Avançar ano

                    sistema.avancarAno();
                    System.out.println("Ano letivo avançado.");

                    break;

                case 4: // Importar CSV

                    System.out.print("Indique o caminho do ficheiro CSV: ");
                    String caminhoCSV = sc.nextLine();

                    CSVService.importarCSV(sistema, caminhoCSV);

                    break;

                case 5: // Sair

                    System.out.print("Indique o caminho do CSV para guardar: ");
                    String caminhoExport = sc.nextLine();

                    CSVService.exportarSistema(sistema, caminhoExport);

                    running = false;

                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }

        System.out.println("Programa terminado.");
    }

    private static void menuGestor(Gestor gestor, GestorController gestorController, Scanner sc) {

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

                case 0:
                    gestorRunning = false;
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuDocente(Docente docente,
                                    DocenteController docenteController,
                                    SistemaAcademico sistema,
                                    Scanner sc) {

        DocenteView dv = new DocenteView();

        boolean docenteRunning = true;

        while (docenteRunning) {

            int menu = dv.menuDocente();

            switch (menu) {

                case 1:
                    dv.adicionarAvaliacao(docente, docenteController, sistema);
                    break;

                case 2:
                    dv.verDados(docente);
                    break;

                case 0:
                    docenteRunning = false;
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuEstudante(Estudante estudante,
                                      EstudanteController estudanteController,
                                      Scanner sc) {

        EstudanteView ev = new EstudanteView();

        boolean estudanteRunning = true;

        while (estudanteRunning) {

            int menu = ev.menuEstudante();

            switch (menu) {

                case 1:
                    ev.verDados(estudante);
                    break;

                case 2:
                    ev.verPercurso(estudante, estudanteController);
                    break;

                case 3:
                    ev.alterarDados(estudante);
                    break;

                case 0:
                    estudanteRunning = false;
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}