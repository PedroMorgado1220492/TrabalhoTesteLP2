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

    private MainView view;
    private RepositorioDados repositorio;

    public MainController() {
        this.view = new MainView();
        this.repositorio = new RepositorioDados();
    }

    public void iniciarSistema() {
        boolean aExecutar = true;
        view.mostrarMensagem("Bem-vindo ao Sistema do ISSMF!");

        while (aExecutar) {
            // Mostra o ano atual no menu para o utilizador saber em que ano está
            view.mostrarMensagem("--- Ano Letivo Atual: " + repositorio.getAnoAtual() + " ---");
            int opcao = view.mostrarMenu();

            switch (opcao) {
                case 1:
                    view.mostrarMensagem("\n--- LOGIN DO SISTEMA ---");
                    String emailLogin = view.pedirInputString("Email");
                    String passwordLogin = view.pedirInputString("Password");

                    // Pede à base de dados para autenticar
                    Utilizador utilizadorLogado = repositorio.autenticar(emailLogin, passwordLogin);

                    if (utilizadorLogado == null) {
                        view.mostrarMensagem("Erro: Email ou Password incorretos.");
                    } else {
                        view.mostrarMensagem("Login efetuado com sucesso!");

                        // Verifica que tipo de utilizador é (Polimorfismo!)
                        if (utilizadorLogado instanceof Gestor) {
                            view.mostrarMensagem("Bem-vindo Gestor: Backoffice");
                            GestorController gc = new GestorController((Gestor) utilizadorLogado, repositorio);
                            gc.iniciarMenuGestor();

                        } else if (utilizadorLogado instanceof Docente) {
                            Docente docenteLogado = (Docente) utilizadorLogado;
                            view.mostrarMensagem("Bem-vindo Docente: " + docenteLogado.getSigla());

                            // Liga o controlador do Docente!
                            DocenteController dc = new DocenteController(docenteLogado, repositorio);
                            dc.iniciarMenu();

                        } else if (utilizadorLogado instanceof Estudante) {
                            Estudante estudanteLogado = (Estudante) utilizadorLogado;
                            view.mostrarMensagem("Bem-vindo Estudante: " + estudanteLogado.getNumeroMecanografico());

                            // Liga o controlador do Estudante!
                            EstudanteController ec = new EstudanteController(estudanteLogado, repositorio);
                            ec.iniciarMenu();
                        }
                    }
                    break;
                case 2:
                    // --- OPÇÃO 2: CRIAR ESTUDANTE SEM LOGIN ---
                    criarEstudanteSemLogin();
                    break;
                case 3:
                    // --- OPÇÃO 3: AVANÇAR ANO ---
                    repositorio.avancarAno();
                    view.mostrarMensagem("Sucesso! O sistema avançou para o ano letivo de " + repositorio.getAnoAtual());
                    break;
                case 4:
                    view.mostrarMensagem("\n--- IMPORTAR BASE DE DADOS ---");
                    view.mostrarMensagem("Dica: Se o ficheiro estiver na mesma pasta, digite apenas 'dados.csv'.");
                    view.mostrarMensagem("Se estiver noutra pasta, digite o caminho completo.");

                    String caminhoFicheiro = "";

                    // Ciclo para continuar a pedir até o ficheiro existir
                    while (true) {
                        caminhoFicheiro = view.pedirInputString("Caminho do ficheiro CSV (ou digite 'sair' para cancelar)");

                        // Permite ao utilizador desistir se não encontrar o ficheiro
                        if (caminhoFicheiro.equalsIgnoreCase("sair")) {
                            view.mostrarMensagem("Importação cancelada.");
                            break;
                        }

                        // Verifica fisicamente se o ficheiro existe no computador
                        java.io.File ficheiro = new java.io.File(caminhoFicheiro);
                        if (ficheiro.exists() && !ficheiro.isDirectory()) {
                            // Ficheiro existe! Sai do ciclo para continuar a importação
                            break;
                        } else {
                            view.mostrarMensagem("Erro: Ficheiro não encontrado! Verifique o caminho e tente novamente.");
                        }
                    }

                    // Se não cancelou, avança com a importação
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

                case 5: // Sair
                    view.mostrarMensagem("A guardar dados e a encerrar o sistema...");
                    // Pode usar o mesmo caminho que usou na importação
                    ExportadorCSV.exportarDados("dados.csv", repositorio);
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    // --- Lógica isolada para manter o Switch limpo ---
    private void criarEstudanteSemLogin() {
        view.mostrarMensagem("\n--- NOVO REGISTO DE ESTUDANTE ---");

        // 0. Validação de Cursos (Não pode inscrever se não houver cursos)
        if (repositorio.getTotalCursos() == 0) {
            view.mostrarMensagem("Atenção: De momento não existem cursos disponíveis no sistema para inscrição. Tente mais tarde.");
            return; // Sai do método e volta ao menu principal
        }

        // 1. Pede dados básicos (Já com as nossas super validações!)
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

        // 2. Escolher o Curso
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
                    break; // Escolha válida, sai do ciclo
                }
                view.mostrarMensagem("Erro: Escolha um número de curso válido da lista.");
            } catch (NumberFormatException e) {
                view.mostrarMensagem("Erro: Por favor, introduza apenas números.");
            }
        }
        Curso cursoEscolhido = cursos[escolhaCurso];

        // 3. Gera os dados automáticos (Número Mecanográfico, Email e Password)
        int numeroMecanografico = 1000 + repositorio.getTotalEstudantes();
        String emailGerado = EmailGenerator.gerarEmailEstudante(numeroMecanografico);
        String passwordGerada = PasswordGenerator.generatePassword();
        int anoInscricao = repositorio.getAnoAtual();

        // 4. Instancia o objeto Estudante com o CURSO ESCOLHIDO
        Estudante novoEstudante = new Estudante(
                numeroMecanografico,
                emailGerado,
                passwordGerada,
                nome,
                nif,
                morada,
                dataNascimento,
                cursoEscolhido, // Passamos o curso escolhido aqui!
                anoInscricao
        );

        // 5. Guarda no repositório com array tradicional
        boolean sucesso = repositorio.adicionarEstudante(novoEstudante);

        // 6. Dá o feedback final
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