package view;

import controller.GestorController;
import model.*;

import java.util.Scanner;

public class GestorView {

    private Scanner sc = new Scanner(System.in);

    public int menuGestor() {
        System.out.println("\n--- Menu Gestor ---");
        System.out.println("1. Departamento");
        System.out.println("2. Cursos");
        System.out.println("3. Unidades Curriculares");
        System.out.println("4. Docentes");
        System.out.println("5. Estudantes");
        System.out.println("6. Alterar Password");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
        return Integer.parseInt(sc.nextLine());
    }

    // -----------------------------
    // DEPARTAMENTO
    // -----------------------------
    public void departamentoMenu(GestorController controller) {
        System.out.println("\n--- Menu Departamento ---");
        System.out.println("1. Criar Departamento");
        System.out.println("2. Listar Departamentos");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        int op = Integer.parseInt(sc.nextLine());

        switch (op) {
            case 1:
                System.out.print("Sigla do departamento: ");
                String sigla = sc.nextLine();
                System.out.print("Nome do departamento: ");
                String nome = sc.nextLine();
                System.out.print("Docente responsável (opcional, deixar vazio se nenhum): ");
                String docSig = sc.nextLine();
                Docente docente = docSig.isEmpty() ? null : controller.listarDocentes()[0];
                controller.criarDepartamento(sigla, nome, docente);
                break;
            case 2:
                Departamento[] deps = controller.listarDepartamentos();
                for (Departamento d : deps) {
                    if (d != null) System.out.println(d);
                }
                break;
            case 0:
                return;
        }
    }

    // -----------------------------
    // CURSOS
    // -----------------------------
    public void cursoMenu(GestorController controller) {
        System.out.println("\n--- Menu Cursos ---");
        System.out.println("1. Criar Curso");
        System.out.println("2. Listar Cursos");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        int op = Integer.parseInt(sc.nextLine());

        switch (op) {
            case 1:
                System.out.print("Sigla do curso: ");
                String sigla = sc.nextLine();
                System.out.print("Nome do curso: ");
                String nome = sc.nextLine();
                System.out.print("Departamento (sigla, opcional): ");
                String depSig = sc.nextLine();
                Departamento dep = depSig.isEmpty() ? null : controller.listarDepartamentos()[0];
                System.out.print("Docente responsável (opcional): ");
                String docSig = sc.nextLine();
                Docente docente = docSig.isEmpty() ? null : controller.listarDocentes()[0];
                controller.criarCurso(sigla, nome, dep, docente);
                break;
            case 2:
                Curso[] cursos = controller.listarCursos();
                for (Curso c : cursos) {
                    if (c != null) System.out.println(c);
                }
                break;
            case 0:
                return;
        }
    }

    // -----------------------------
    // UNIDADES CURRICULARES
    // -----------------------------
    public void ucMenu(GestorController controller) {
        System.out.println("\n--- Menu Unidades Curriculares ---");
        System.out.println("1. Criar UC");
        System.out.println("2. Listar UCs");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        int op = Integer.parseInt(sc.nextLine());

        switch (op) {
            case 1:
                System.out.print("Sigla da UC: ");
                String sigla = sc.nextLine();
                System.out.print("Nome da UC: ");
                String nome = sc.nextLine();
                System.out.print("Ano Curricular: ");
                int ano = Integer.parseInt(sc.nextLine());
                System.out.print("Curso (opcional): ");
                String cursoSig = sc.nextLine();
                Curso curso = cursoSig.isEmpty() ? null : controller.listarCursos()[0];
                System.out.print("Docente responsável (opcional): ");
                String docSig = sc.nextLine();
                Docente docente = docSig.isEmpty() ? null : controller.listarDocentes()[0];
                controller.criarUC(sigla, nome, ano, curso, docente);
                break;
            case 2:
                UnidadeCurricular[] ucs = controller.listarUCs();
                for (UnidadeCurricular uc : ucs) {
                    if (uc != null) System.out.println(uc);
                }
                break;
            case 0:
                return;
        }
    }

    // -----------------------------
    // DOCENTES
    // -----------------------------
    public void docenteMenu(GestorController controller) {
        System.out.println("\n--- Menu Docentes ---");
        System.out.println("1. Criar Docente");
        System.out.println("2. Listar Docentes");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        int op = Integer.parseInt(sc.nextLine());

        switch (op) {
            case 1:
                System.out.print("Sigla do docente: ");
                String sigla = sc.nextLine();
                System.out.print("Nome: ");
                String nome = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("Password: ");
                String password = sc.nextLine();
                controller.criarDocente(sigla, nome, email, password);
                break;
            case 2:
                Docente[] docentes = controller.listarDocentes();
                for (Docente d : docentes) {
                    if (d != null) System.out.println(d);
                }
                break;
            case 0:
                return;
        }
    }

    // -----------------------------
    // ESTUDANTES
    // -----------------------------
    public void estudanteMenu(GestorController controller) {
        System.out.println("\n--- Menu Estudantes ---");
        System.out.println("1. Criar Estudante");
        System.out.println("2. Listar Estudantes");
        System.out.println("0. Voltar");
        System.out.print("Escolha uma opção: ");
        int op = Integer.parseInt(sc.nextLine());

        switch (op) {
            case 1:
                System.out.print("Número: ");
                int numero = Integer.parseInt(sc.nextLine());
                System.out.print("Nome: ");
                String nome = sc.nextLine();
                System.out.print("NIF: ");
                String nif = sc.nextLine();
                System.out.print("Morada: ");
                String morada = sc.nextLine();
                System.out.print("Data Nascimento (dd/MM/yyyy): ");
                String data = sc.nextLine();
                System.out.print("Curso (opcional): ");
                String cursoSig = sc.nextLine();
                Curso curso = cursoSig.isEmpty() ? null : controller.listarCursos()[0];

                // Novos parâmetros obrigatórios
                controller.criarEstudante(numero, nome, nif, morada, data, curso);
                break;
            case 2:
                Estudante[] estudantes = controller.listarEstudantes();
                for (Estudante e : estudantes) {
                    if (e != null) System.out.println(e);
                }
                break;
            case 0:
                return;
        }
    }

    // -----------------------------
    // ALTERAR PASSWORD
    // -----------------------------
    public void alterarPassword(Gestor gestor) {
        System.out.print("Nova password: ");
        String novaPass = sc.nextLine();
        gestor.alterarPassword(novaPass);
        System.out.println("Password alterada.");
    }
}