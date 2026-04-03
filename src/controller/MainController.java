package controller;

import model.bll.*;
import model.dal.ExportadorCSV;
import model.dal.ImportadorCSV;
import utils.*;
import view.MainView;
import model.dal.RepositorioDados;

public class MainController {

    private MainView view;
    private RepositorioDados repositorio;

    public MainController(MainView view, RepositorioDados repositorio) {
        this.view = view;
        this.repositorio = repositorio;
    }

    public void iniciarSistema() {
        boolean aExecutar = true;
        view.mostrarMensagem("Bem-vindo ao Sistema do ISSMF!");

        while (aExecutar) {
            view.mostrarMensagem("--- Ano Letivo Atual: " + repositorio.getAnoAtual() + " ---");
            int opcao = view.mostrarMenu();

            switch (opcao) {
                case 1:
                    view.mostrarMensagem("\n--- LOGIN DO SISTEMA ---");
                    String emailLogin = view.pedirInputString("Email").trim();
                    String passwordLogin = view.pedirInputString("Password").trim();

                    String tipoUtilizador = ImportadorCSV.verificarLoginRapido("bd/logins.csv", emailLogin, passwordLogin);

                    if (tipoUtilizador == null) {
                        view.mostrarMensagem("Erro: Email ou Password incorretos.");
                        break;
                    }

                    view.mostrarMensagem("Login validado (" + tipoUtilizador + "). A abrir ficheiros necessários...");

                    // Carregamos a base completa para a RAM porque as relações precisam de estar estabelecidas
                    carregarBaseDeDadosCompleta();

                    // Encaminhamento
                    if (tipoUtilizador.equals("GESTOR")) {
                        Utilizador userLogado = repositorio.autenticar(emailLogin, passwordLogin);
                        view.mostrarMensagem("Bem-vindo Gestor!");
                        new GestorController((Gestor) userLogado, repositorio).iniciarMenuGestor();

                    } else if (tipoUtilizador.equals("DOCENTE")) {
                        Utilizador userLogado = repositorio.autenticar(emailLogin, passwordLogin);
                        view.mostrarMensagem("Bem-vindo Docente!");
                        new DocenteController((Docente) userLogado, repositorio).iniciarMenu();

                    } else if (tipoUtilizador.equals("ESTUDANTE")) {
                        Utilizador userLogado = repositorio.autenticar(emailLogin, passwordLogin);
                        view.mostrarMensagem("Bem-vindo Estudante!");
                        new EstudanteController((Estudante) userLogado, repositorio).iniciarMenu();
                    }

                    // CORREÇÃO CRÍTICA: GRAVAR O TRABALHO ANTES DE APAGAR A RAM!
                    ExportadorCSV.exportarDados("bd", repositorio);

                    this.repositorio = new RepositorioDados();
                    view.mostrarMensagem(">> Sessão encerrada. Dados guardados e memória libertada com sucesso.");
                    break;

                case 2:
                    view.mostrarMensagem("A preparar o sistema de registo...");
                    // CORREÇÃO CRÍTICA: Carregar tudo para não reescrever o ficheiro só com 1 aluno!
                    carregarBaseDeDadosCompleta();

                    criarEstudanteSemLogin();

                    ExportadorCSV.exportarDados("bd", repositorio);
                    this.repositorio = new RepositorioDados(); // Limpar a memória no fim
                    break;

                case 3:
                    view.mostrarMensagem("\n--- TRANSIÇÃO DE ANO LETIVO ---");
                    carregarBaseDeDadosCompleta();

                    // --- VALIDAÇÃO AUTOMÁTICA DE 1º ANO ---
                    validarArranqueDeCursos();
                    // --------------------------------------

                    int proximoAno = repositorio.getAnoAtual() + 1;
                    String confirmacao = view.pedirInputString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N)");

                    if (confirmacao.equalsIgnoreCase("S")) {
                        repositorio.avancarAno();
                        view.mostrarMensagem("Sucesso! O sistema avançou para o ano letivo de " + repositorio.getAnoAtual() + ".");
                        ExportadorCSV.exportarDados("bd", repositorio);
                    } else {
                        view.mostrarMensagem("Operação cancelada. Mantemo-nos em " + repositorio.getAnoAtual() + ".");
                    }
                    this.repositorio = new RepositorioDados(); // Limpar a memória
                    break;

                case 4:
                    view.mostrarMensagem("Não é necessário guardar manualmente. O sistema tem Auto-Save inteligente ao sair dos menus!");
                    break;

                case 0:
                    view.mostrarMensagem("A encerrar o sistema...");
                    aExecutar = false;
                    break;

                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    /**
     * Helper Method: Carrega todos os ficheiros de forma estruturada e interligada
     */
    private void carregarBaseDeDadosCompleta() {
        ImportadorCSV.importarGestores("bd/gestores.csv", repositorio);
        ImportadorCSV.importarDepartamentos("bd/departamentos.csv", repositorio);
        ImportadorCSV.importarCursos("bd/cursos.csv", repositorio);
        ImportadorCSV.importarDocentes("bd/docentes.csv", repositorio);
        ImportadorCSV.importarEstudantes("bd/estudantes.csv", repositorio);
        ImportadorCSV.importarUCs("bd/ucs.csv", repositorio);
        ImportadorCSV.importarAvaliacoes("bd/avaliacoes.csv", repositorio);
    }

    private void criarEstudanteSemLogin() {
        // ... (EXATAMENTE o mesmo código do criarEstudanteSemLogin que tu já tens. Não precisas de mexer na lógica de criação) ...
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
            view.mostrarMensagem("Erro: A data deve respeitar estritamente o formato DD-MM-AAAA.");
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
                if (escolhaCurso >= 0 && escolhaCurso < repositorio.getTotalCursos()) break;
                view.mostrarMensagem("Erro: Escolha um número válido.");
            } catch (NumberFormatException e) {
                view.mostrarMensagem("Erro: Apenas números.");
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

        if (novoEstudante.getCurso() != null && novoEstudante.getPercursoAcademico() != null) {
            for (int i = 0; i < cursoEscolhido.getTotalUCs(); i++) {
                UnidadeCurricular uc = cursoEscolhido.getUnidadesCurriculares()[i];
                if (uc.getAnoCurricular() == novoEstudante.getAnoFrequencia()) {
                    novoEstudante.getPercursoAcademico().inscreverEmUc(uc);
                }
            }
        }

        if (repositorio.adicionarEstudante(novoEstudante)) {
            view.mostrarMensagem("\nEstudante registado com sucesso no ano " + anoInscricao + "!");
            view.mostrarMensagem("Nº Mec: " + numeroMecanografico + " | Email: " + emailGerado + " | Pass: " + passwordGerada);
        } else {
            view.mostrarMensagem("Erro: Limite máximo de estudantes atingido.");
        }
    }

    /**
     * Valida automaticamente se os cursos têm o mínimo de 5 alunos no 1º ano.
     * Cursos com menos de 5 alunos no 1º ano são cancelados e os alunos removidos.
     */
    private void validarArranqueDeCursos() {
        view.mostrarMensagem("\n>> A verificar o número mínimo de alunos (5) para as turmas de 1º ano...");

        if (repositorio.getTotalCursos() == 0) return;

        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso curso = repositorio.getCursos()[i];
            int inscritosPrimeiroAno = 0;

            // Contar os alunos do 1º ano deste curso
            for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
                Estudante e = repositorio.getEstudantes()[j];
                if (e != null && e.getCurso() != null) {
                    if (e.getCurso().getSigla().equals(curso.getSigla()) && e.getAnoFrequencia() == 1) {
                        inscritosPrimeiroAno++;
                    }
                }
            }

            // Aplicar a Regra
            if (inscritosPrimeiroAno > 0 && inscritosPrimeiroAno < 5) {
                view.mostrarMensagem("   [AVISO] " + curso.getSigla() + " cancelado no 1º ano! Apenas " + inscritosPrimeiroAno + " inscritos.");
                anularMatriculasPrimeiroAno(curso.getSigla());
            } else if (inscritosPrimeiroAno >= 5) {
                view.mostrarMensagem("   [OK] " + curso.getSigla() + " aprovado para o 1º ano (" + inscritosPrimeiroAno + " inscritos).");
            }
        }
        view.mostrarMensagem(">> Validação concluída!\n");
    }

    /**
     * Remove os alunos do 1º ano de um curso que foi cancelado.
     */
    private void anularMatriculasPrimeiroAno(String siglaCurso) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(siglaCurso) && e.getAnoFrequencia() == 1) {
                repositorio.removerEstudante(e.getNumeroMecanografico());
                i--; // Importante: recuar o índice porque o array encolheu após a remoção
            }
        }
    }
}