// Ficheiro: controller/MainController.java
package controller;

import view.MainView;
import model.RepositorioDados;
import model.Estudante;
import model.Utilizador;
import model.Gestor;
import model.Docente;
import utils.ImportadorCSV;
import utils.EmailGenerator;
import utils.PasswordGenerator;

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
                    // Pede ao utilizador para escrever o caminho do ficheiro
                    view.mostrarMensagem("\n--- IMPORTAR BASE DE DADOS ---");
                    view.mostrarMensagem("Dica: Se o ficheiro estiver na mesma pasta, digite apenas 'dados.csv'.");
                    view.mostrarMensagem("Se estiver noutra pasta, digite o caminho completo (ex: C:\\Users\\utilizador\\Desktop\\dados.csv).");

                    String caminhoFicheiro = view.pedirInputString("Caminho do ficheiro CSV");

                    view.mostrarMensagem("A iniciar importação do ficheiro: " + caminhoFicheiro);

                    // Chama o importador com o caminho que o utilizador escreveu
                    ImportadorCSV.importarDados(caminhoFicheiro, repositorio);

                    // Mostra o resumo final
                    view.mostrarMensagem("--- Resumo da Base de Dados ---");
                    view.mostrarMensagem("Estudantes guardados: " + repositorio.getTotalEstudantes());
                    view.mostrarMensagem("Docentes guardados: " + repositorio.getTotalDocentes());
                    break;
                case 5:
                    view.mostrarMensagem("A encerrar o sistema ISSMF. Até logo!");
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

        // 1. Pede dados básicos
        String nome = view.pedirInputString("Nome");
        String nif = view.pedirInputString("NIF");
        String morada = view.pedirInputString("Morada");
        String dataNascimento = view.pedirInputString("Data de Nascimento (DD-MM-AAAA)");

        // 2. Gera os dados automáticos (Número Mecanográfico, Email e Password)
        // O contadorMecanografico pode começar em 1000 e somar os estudantes que já existem
        int numeroMecanografico = 1000 + repositorio.getTotalEstudantes();

        String emailGerado = EmailGenerator.gerarEmailEstudante(numeroMecanografico);
        String passwordGerada = PasswordGenerator.generatePassword();

        // O ano de inscrição vai buscar a variável dinâmica que criámos!
        int anoInscricao = repositorio.getAnoAtual();

        // 3. Instancia o objeto Estudante
        Estudante novoEstudante = new Estudante(
                numeroMecanografico,
                emailGerado,
                passwordGerada,
                nome,
                nif,
                morada,
                dataNascimento,
                null, // Curso a null nesta fase
                anoInscricao // Usa o ano dinâmico do sistema!
        );

        // 4. Guarda no repositório com array tradicional
        boolean sucesso = repositorio.adicionarEstudante(novoEstudante);

        // 5. Dá o feedback final
        if (sucesso) {
            view.mostrarMensagem("Estudante registado com sucesso no ano letivo " + anoInscricao + "!");
            view.mostrarMensagem("Nº Mecanográfico: " + numeroMecanografico);
            view.mostrarMensagem("Email: " + emailGerado);
            view.mostrarMensagem("Password: " + passwordGerada);
        } else {
            view.mostrarMensagem("Erro: O sistema atingiu o limite máximo de estudantes.");
        }
    }
}