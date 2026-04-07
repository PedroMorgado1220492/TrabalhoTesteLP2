package view;

import java.util.Scanner;

public class GestorView {

    // ---------- ATRIBUTOS ----------
    private Scanner scanner;

    // ---------- CONSTRUTOR ----------
    public GestorView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MÉTODOS DE APRESENTAÇÃO E LEITURA ----------

    /**
     * Apresenta o menu principal do painel de administração (Backoffice).
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuPrincipalGestor() {
        System.out.println("\n=== BACKOFFICE - GESTOR ===");
        System.out.println("1 - Gerir Departamentos");
        System.out.println("2 - Gerir Cursos");
        System.out.println("3 - Gerir Unidades Curriculares");
        System.out.println("4 - Gerir Estudantes");
        System.out.println("5 - Gerir Docentes");
        System.out.println("6 - Avançar Ano Letivo");
        System.out.println("7 - Listagens e Relatórios");
        System.out.println("8 - Ver Alunos com Dívidas");
        System.out.println("9 - Alterar Preço de Cursos");
        System.out.println("0 - Sair / Logout");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Departamentos.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuDepartamentos() {
        System.out.println("\n--- GERIR DEPARTAMENTOS ---");
        System.out.println("1 - Adicionar Departamento");
        System.out.println("2 - Alterar Departamento");
        System.out.println("3 - Listar Departamentos");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Cursos.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuCursos() {
        System.out.println("\n--- GERIR CURSOS ---");
        System.out.println("1 - Adicionar Curso");
        System.out.println("2 - Alterar Curso");
        System.out.println("3 - Listar Cursos");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Unidades Curriculares.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuUCs() {
        System.out.println("\n--- GERIR UNIDADES CURRICULARES ---");
        System.out.println("1 - Criar Nova Unidade Curricular");
        System.out.println("2 - Associar UC Existente a outro Curso");
        System.out.println("3 - Alterar Unidade Curricular");
        System.out.println("4 - Listar Unidades Curriculares");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }


    }

    /**
     * Apresenta o submenu de gestão de Estudantes.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuEstudantes() {
        System.out.println("\n--- GERIR ESTUDANTES ---");
        System.out.println("1 - Adicionar Estudante");
        System.out.println("2 - Alterar Estudante");
        System.out.println("3 - Listar Estudantes");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de gestão de Docentes.
     * @return A opção numérica selecionada.
     */
    public int mostrarMenuDocentes() {
        System.out.println("\n--- GERIR DOCENTES ---");
        System.out.println("1 - Adicionar Docente");
        System.out.println("2 - Alterar Docente");
        System.out.println("3 - Listar Docentes");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Apresenta o submenu de visualização de Relatórios e Estatísticas.
     * @return A opção numérica selecionada.
     */

    public int mostrarMenuRelatorios() {
        System.out.println("\n--- RELATÓRIOS E ESTATÍSTICAS ---");
        System.out.println("1 - Alunos agrupados por Curso");
        System.out.println("2 - Alunos agrupados por UC");
        System.out.println("3 - UCs agrupadas por Curso");
        System.out.println("4 - Cursos agrupados por Departamento");
        System.out.println("5 - Ver Estatísticas Globais da Faculdade");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }



    /**
     * Pede ao gestor que insira texto através da consola.
     * @param mensagem Texto de pedido de informação.
     * @return A string inserida.
     */
    public String pedirInputString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine();
    }

    /**
     * Apresenta uma mensagem de sistema/feedback ao gestor.
     * @param mensagem Texto a apresentar.
     */
    public void mostrarMensagem(String mensagem) {
        System.out.println(">> " + mensagem);
    }

    /**
     * Mostra a lista final de alunos em dívida construída pelo controlador.
     */
    public void mostrarListaDevedores(String[] devedores, int total) {
        System.out.println("\n--- LISTA DE DEVEDORES (PROPINAS) ---");
        if (total == 0) {
            System.out.println("Nenhum aluno tem propinas em atraso.");
        } else {
            for (int i = 0; i < total; i++) {
                System.out.println(devedores[i]);
            }
        }
    }

    /**
     * Imprime a lista de cursos e pede para escolher um para alterar a propina.
     */
    public int mostrarCursosParaPropina(model.bll.Curso[] cursos, int totalCursos) {
        System.out.println("\n--- ATUALIZAR PREÇO DO CURSO (PROPINAS) ---");
        System.out.println("Aviso: Esta alteração afetará APENAS os novos alunos que se inscreverem a partir de agora.");
        System.out.println("Os alunos antigos manterão o valor (Direitos Adquiridos).\n");

        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null) {
                System.out.println((i + 1) + " - " + cursos[i].getNome() + " (Preço Atual: " + cursos[i].getValorPropinaAnual() + "€)");
            }
        }

        System.out.print("Escolha o número do curso: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public double pedirNovoPreco() {
        System.out.print("Introduza a nova propina anual (€): ");
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    /**
     * Apresenta o cartão de credenciais do novo utilizador no momento da criação.
     */
    public void mostrarCredenciaisCriadas(String tipo, String nome, String email, String password) {
        System.out.println("\n--- NOVO " + tipo.toUpperCase() + " REGISTADO COM SUCESSO! ---");
        System.out.println("Nome: " + nome);
        System.out.println("Email de Acesso: " + email);
        System.out.println("Password Provisória: " + password);
        System.out.println("----------------------------------------------\n");
    }
}