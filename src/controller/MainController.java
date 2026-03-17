package controller;

import utils.*;
import view.MainView;
import model.RepositorioDados;
import model.Estudante;
import model.Utilizador;
import model.Gestor;
import model.Docente;
import model.Curso;

public class MainController {

    // ---------- ATRIBUTOS ----------
    private MainView view;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------
    public MainController() {
        this.view = new MainView();
        this.repositorio = new RepositorioDados();
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Ponto de entrada principal da aplicação.
     * Gere o menu inicial (Login, Registo, Avanço de Ano, Importação e Exportação)
     * e encaminha o utilizador para os respetivos sub-controladores consoante o seu perfil.
     */
    public void iniciarSistema() {
        boolean aExecutar = true;
        view.mostrarMensagem("Bem-vindo ao Sistema do ISSMF!");

        while (aExecutar) {
            view.mostrarMensagem("--- Ano Letivo Atual: " + repositorio.getAnoAtual() + " ---");
            int opcao = view.mostrarMenu();

            switch (opcao) {
                case 1:
                    view.mostrarMensagem("\n--- LOGIN DO SISTEMA ---");
                    String emailLogin = view.pedirInputString("Email");
                    String passwordLogin = view.pedirInputString("Password");

                    Utilizador utilizadorLogado = repositorio.autenticar(emailLogin, passwordLogin);

                    if (utilizadorLogado == null) {
                        view.mostrarMensagem("Erro: Email ou Password incorretos.");
                    } else {
                        view.mostrarMensagem("Login efetuado com sucesso!");

                        if (utilizadorLogado instanceof Gestor) {
                            view.mostrarMensagem("Bem-vindo Gestor: Backoffice");
                            GestorController gc = new GestorController((Gestor) utilizadorLogado, repositorio);
                            gc.iniciarMenuGestor();

                        } else if (utilizadorLogado instanceof Docente) {
                            Docente docenteLogado = (Docente) utilizadorLogado;
                            view.mostrarMensagem("Bem-vindo Docente: " + docenteLogado.getSigla());
                            DocenteController dc = new DocenteController(docenteLogado, repositorio);
                            dc.iniciarMenu();

                        } else if (utilizadorLogado instanceof Estudante) {
                            Estudante estudanteLogado = (Estudante) utilizadorLogado;
                            view.mostrarMensagem("Bem-vindo Estudante: " + estudanteLogado.getNumeroMecanografico());
                            EstudanteController ec = new EstudanteController(estudanteLogado, repositorio);
                            ec.iniciarMenu();
                        }
                    }
                    break;
                case 2:
                    criarEstudanteSemLogin();
                    break;
                case 3:
                    view.mostrarMensagem("\n--- TRANSIÇÃO DE ANO LETIVO ---");

                    int proximoAno = repositorio.getAnoAtual() + 1;
                    String confirmacao = view.pedirInputString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N)");

                    if (confirmacao.equalsIgnoreCase("S")) {
                        repositorio.avancarAno();
                        view.mostrarMensagem("Sucesso! O sistema avançou para o ano letivo de " + repositorio.getAnoAtual() + ".");
                    } else {
                        view.mostrarMensagem("Operação cancelada. Mantemo-nos em " + repositorio.getAnoAtual() + ".");
                    }
                    break;
                case 4:
                    view.mostrarMensagem("\n--- IMPORTAR BASE DE DADOS ---");
                    view.mostrarMensagem("Dica: Se o ficheiro estiver na mesma pasta, digite apenas 'dados.csv'.");
                    view.mostrarMensagem("Se estiver noutra pasta, digite o caminho completo.");
                    String caminhoFicheiro = "";

                    while (true) {
                        caminhoFicheiro = view.pedirInputString("Caminho do ficheiro CSV (ou digite 'sair' para cancelar)");

                        if (caminhoFicheiro.equalsIgnoreCase("sair")) {
                            view.mostrarMensagem("Importação cancelada.");
                            break;
                        }
                        caminhoFicheiro = "bd/" + caminhoFicheiro;
                        java.io.File ficheiro = new java.io.File(caminhoFicheiro);
                        if (ficheiro.exists() && !ficheiro.isDirectory()) {
                            break;
                        } else {
                            view.mostrarMensagem("Erro: Ficheiro não encontrado! Verifique o caminho e tente novamente.");
                        }
                    }

                    if (!caminhoFicheiro.equalsIgnoreCase("sair")) {
                        view.mostrarMensagem("A iniciar importação do ficheiro: " + caminhoFicheiro);
                        ImportadorCSV.importarDados(caminhoFicheiro, repositorio);

                        view.mostrarMensagem("--- Resumo da Base de Dados ---");
                        view.mostrarMensagem("Departamentos: " + repositorio.getTotalDepartamentos());
                        view.mostrarMensagem("Cursos:        " + repositorio.getTotalCursos());
                        view.mostrarMensagem("Docentes:      " + repositorio.getTotalDocentes());
                        view.mostrarMensagem("Estudantes:    " + repositorio.getTotalEstudantes());
                        view.mostrarMensagem("UCs:           " + repositorio.getTotalUcs());
                    }
                    break;
                case 5:
                    view.mostrarMensagem("A guardar dados e a encerrar o sistema...");
                    ExportadorCSV.exportarDados("bd/dados.csv", repositorio);
                    break;
                case 0:
                    view.mostrarMensagem("A encerrar o sistema sem guardar dados...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    /**
     * Lógica isolada para permitir o registo de um novo estudante no sistema
     * a partir do ecrã público, sem necessidade de autenticação.
     */
    private void criarEstudanteSemLogin() {
        view.mostrarMensagem("\n--- NOVO REGISTO DE ESTUDANTE ---");

        if (repositorio.getTotalCursos() == 0) {
            view.mostrarMensagem("Atenção: De momento não existem cursos disponíveis no sistema para inscrição. Tente mais tarde.");
            return;
        }

        String nome = "";
        while (true) {
            nome = view.pedirInputString("Nome (Nome e Sobrenome)");
            if (Validador.isNomeValido(nome)) break;
            view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras.");
        }

        String nif = "";
        while (true) {
            nif = view.pedirInputString("NIF (9 dígitos)");
            if (Validador.isNifValido(nif)) break;
            view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
        }

        String morada = view.pedirInputString("Morada");

        String dataNascimento = "";
        while (true) {
            dataNascimento = view.pedirInputString("Data de Nascimento (DD-MM-AAAA)");
            if (Validador.isDataNascimentoValida(dataNascimento)) break;
            view.mostrarMensagem("Erro: A data deve respeitar estritamente o formato DD-MM-AAAA (ex: 15-04-2002).");
        }

        view.mostrarMensagem("\n--- Escolha o Curso ---");
        Curso[] cursos = repositorio.getCursos();
        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            view.mostrarMensagem((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
        }

        int escolhaCurso = -1;
        while (true) {
            try {
                String input = view.pedirInputString("Número do Curso");
                escolhaCurso = Integer.parseInt(input) - 1;
                if (escolhaCurso >= 0 && escolhaCurso < repositorio.getTotalCursos()) {
                    break;
                }
                view.mostrarMensagem("Erro: Escolha um número de curso válido da lista.");
            } catch (NumberFormatException e) {
                view.mostrarMensagem("Erro: Por favor, introduza apenas números.");
            }
        }

        Curso cursoEscolhido = cursos[escolhaCurso];

        int anoInscricao = repositorio.getAnoAtual();
        int numeroMecanografico = repositorio.gerarNumeroMecanografico(anoInscricao);
        String emailGerado = EmailGenerator.gerarEmailEstudante(numeroMecanografico);
        String passwordGerada = PasswordGenerator.generatePassword();

        Estudante novoEstudante = new Estudante(
                numeroMecanografico, emailGerado, passwordGerada, nome,
                nif, morada, dataNascimento, cursoEscolhido, anoInscricao
        );

        // --- Auto-Matrícula ---
        if (novoEstudante.getCurso() != null && novoEstudante.getPercursoAcademico() != null) {
            Curso cursoDoAluno = novoEstudante.getCurso();

            for (int i = 0; i < cursoDoAluno.getTotalUCs(); i++) {
                model.UnidadeCurricular uc = cursoDoAluno.getUnidadesCurriculares()[i];

                if (uc.getAnoCurricular() == novoEstudante.getAnoFrequencia()) {
                    novoEstudante.getPercursoAcademico().inscreverEmUc(uc);
                }
            }
        }

        boolean sucesso = repositorio.adicionarEstudante(novoEstudante);


        if (sucesso) {
            view.mostrarMensagem("\nEstudante registado com sucesso no ano letivo " + anoInscricao + "!");
            view.mostrarMensagem("Nº Mecanográfico: " + numeroMecanografico);
            view.mostrarMensagem("Email: " + emailGerado);
            view.mostrarMensagem("Password: " + passwordGerada);
            view.mostrarMensagem("Inscrito no Curso: " + cursoEscolhido.getNome());
        } else {
            view.mostrarMensagem("Erro: O sistema atingiu o limite máximo de estudantes.");
        }
    }
}