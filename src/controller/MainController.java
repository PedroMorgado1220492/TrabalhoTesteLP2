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

    /**
     * Ciclo principal do sistema. Gere o menu inicial e delega ações.
     */
    public void iniciarSistema() {
        boolean aExecutar = true;
        view.mostrarBemVindo();

        while (aExecutar) {
            view.mostrarAnoLetivo(repositorio.getAnoAtual());
            int opcao = view.mostrarMenu();

            switch (opcao) {
                case 1: processarLogin(); break;
                case 2: processarRegistoEstudante(); break;
                case 3: processarTransicaoAno(); break;
                case 4: processarRecuperacaoPassword(); break;
                case 0:
                    view.msgEncerramento();
                    aExecutar = false;
                    break;
                default: view.msgOpcaoInvalida();
            }
        }
    }

    // =========================================================
    // 1. LÓGICA DE LOGIN E SESSÃO (CASE 1)
    // =========================================================

    private void processarLogin() {
        view.mostrarCabecalhoLogin();

        String emailLogin = validarDominioEmail();
        String passwordLogin = view.pedirPassword();
        String passEncriptada = utils.Seguranca.encriptar(passwordLogin);

        String tipoUtilizador = model.dal.ImportadorCSV.verificarLoginRapido("bd/logins.csv", emailLogin, passEncriptada);

        if (tipoUtilizador == null) {
            java.io.File ficheiro = new java.io.File("bd/logins.csv");
            if (!ficheiro.exists()) {
                view.msgErroArquivoNaoEncontrado("bd/logins.csv");
            }
            view.msgErroLogin();
            return;
        }

        view.msgValidacaoSucesso(tipoUtilizador);
        carregarBaseDeDadosCompleta();

        Utilizador userLogado = repositorio.autenticar(emailLogin, passEncriptada);
        if (userLogado != null) {
            if (userLogado instanceof Estudante && !((Estudante) userLogado).isAtivo()) {
                view.msgErroInativo();
                return;
            }
            if (userLogado instanceof Docente && !((Docente) userLogado).isAtivo()) {
                view.msgErroInativo();
                return;
            }
            abrirMenuPorRole(tipoUtilizador, userLogado);
        }

        encerrarSessaoESalvar();
    }

    private String validarDominioEmail() {
        while (true) {
            String email = view.pedirEmail();
            if (email.toLowerCase().endsWith("@issmf.ipp.pt")) {
                return email;
            }
            view.msgErroEmailDominio();
        }
    }

    private void abrirMenuPorRole(String tipo, Utilizador user) {
        if (tipo.equals("GESTOR")) {
            view.msgBemVindoRole("Gestor");
            new GestorController((Gestor) user, repositorio).iniciarMenuGestor();
        } else if (tipo.equals("DOCENTE")) {
            view.msgBemVindoRole("Docente");
            new DocenteController((Docente) user, repositorio).iniciarMenu();
        } else if (tipo.equals("ESTUDANTE")) {
            view.msgBemVindoRole("Estudante");
            new EstudanteController((Estudante) user, repositorio).iniciarMenu();
        }
    }

    // =========================================================
    // 2. REGISTO DE ESTUDANTE (CASE 2)
    // =========================================================

    private void processarRegistoEstudante() {
        view.msgPrepararRegisto();
        carregarBaseDeDadosCompleta();

        executarFluxoRegisto();

        encerrarSessaoESalvar();
    }

    private void executarFluxoRegisto() {
        view.mostrarCabecalhoRegisto();

        if (repositorio.getTotalCursos() == 0) {
            view.msgSemCursosParaRegisto();
            return;
        }

        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNasc = validarDataNascimento();
        Curso curso = escolherCursoValido();

        if (curso == null) return;

        registarEstudanteNoSistema(nome, nif, morada, dataNasc, curso);
    }

    // =========================================================
    // 3. TRANSIÇÃO DE ANO LETIVO (CASE 3)
    // =========================================================

    private void processarTransicaoAno() {
        view.mostrarCabecalhoTransicao();
        carregarBaseDeDadosCompleta();
        validarArranqueDeCursos();

        int proximoAno = repositorio.getAnoAtual() + 1;
        if (view.pedirConfirmacaoAvanco(proximoAno)) {
            repositorio.avancarAno();
            gerarCertificadosConcluintes(repositorio.getAnoAtual());
            view.msgSucessoAvancoAno(repositorio.getAnoAtual());
            ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgCancelamentoAvancoAno(repositorio.getAnoAtual());
        }

        this.repositorio = new RepositorioDados(); // Limpar memória
    }

    // =========================================================
    // MÉTODOS AUXILIARES E PRIVADOS (REGRAS E DADOS)
    // =========================================================

    private void encerrarSessaoESalvar() {
        ExportadorCSV.exportarDados("bd", repositorio);
        this.repositorio = new RepositorioDados();
        view.msgSessaoEncerrada();
    }

    private void carregarBaseDeDadosCompleta() {
        ImportadorCSV.importarGestores("bd/gestores.csv", repositorio);
        ImportadorCSV.importarDepartamentos("bd/departamentos.csv", repositorio);
        ImportadorCSV.importarCursos("bd/cursos.csv", repositorio);
        ImportadorCSV.importarDocentes("bd/docentes.csv", repositorio);
        ImportadorCSV.importarEstudantes("bd/estudantes.csv", repositorio);
        ImportadorCSV.importarUCs("bd/ucs.csv", repositorio);
        ImportadorCSV.importarAvaliacoes("bd/avaliacoes.csv", repositorio);
    }

    private String validarNome() {
        while (true) {
            String nome = view.pedirNome();
            if (Validador.isNomeValido(nome)) return nome;
            view.msgErroNome();
        }
    }

    private String validarNif() {
        while (true) {
            String nif = view.pedirNif();
            if (Validador.isNifValido(nif)) return nif;
            view.msgErroNif();
        }
    }

    private String validarDataNascimento() {
        while (true) {
            String data = view.pedirDataNascimento();
            if (Validador.isDataNascimentoValida(data)) return data;
            view.msgErroData();
        }
    }

    private Curso escolherCursoValido() {
        while (true) {
            int idx = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
            if (idx >= 0 && idx < repositorio.getTotalCursos()) {
                Curso c = repositorio.getCursos()[idx];

                if (c.isAtivo()) {
                    return c;
                } else {
                    view.msgErroCursoInativo();
                }
            } else {
                view.msgErroNumeroInvalido();
            }
        }
    }

    private void registarEstudanteNoSistema(String nome, String nif, String morada, String data, Curso curso) {
        int anoInscricao = repositorio.getAnoAtual();
        int numMec = repositorio.gerarNumeroMecanografico(anoInscricao);
        String email = utils.EmailGenerator.gerarEmailEstudante(numMec);
        String passRaw = utils.PasswordGenerator.generatePassword();
        String passEnc = utils.Seguranca.encriptar(passRaw);
        String emailPessoal = view.pedirEmailPessoal();

        Estudante novo = new Estudante(numMec, email, passEnc, nome, nif, morada, data, curso, anoInscricao, emailPessoal);

        if (novo.getCurso() != null && novo.getPercursoAcademico() != null) {
            vincularUcsIniciais(novo, curso);
        }

        if (repositorio.adicionarEstudante(novo)) {
            ServicoEmail.enviarEmailBoasVindas(novo, passRaw);
            view.mostrarCredenciaisGeradas(anoInscricao, numMec, email, passRaw);
        } else {
            view.msgErroLimiteEstudantes();
        }
    }

    private void vincularUcsIniciais(Estudante est, Curso curso) {
        for (int i = 0; i < curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
            if (uc.getAnoCurricular() == est.getAnoFrequencia()) {
                est.getPercursoAcademico().inscreverEmUc(uc);
            }
        }
    }

    private void validarArranqueDeCursos() {
        view.mostrarAvisoValidacaoCursos();
        if (repositorio.getTotalCursos() == 0) return;

        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso curso = repositorio.getCursos()[i];
            int inscritos = contarInscritosPrimeiroAno(curso);

            if (inscritos > 0 && inscritos < 5) {
                view.mostrarCursoCancelado(curso.getSigla(), inscritos);
                anularMatriculasPrimeiroAno(curso.getSigla());
            } else if (inscritos >= 5) {
                view.mostrarCursoAprovado(curso.getSigla(), inscritos);
            }
        }
        view.mostrarFimValidacao();
    }

    private int contarInscritosPrimeiroAno(Curso curso) {
        int conta = 0;
        for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
            Estudante e = repositorio.getEstudantes()[j];
            if (e != null && e.getCurso() != null &&
                    e.getCurso().getSigla().equals(curso.getSigla()) && e.getAnoFrequencia() == 1) {
                conta++;
            }
        }
        return conta;
    }

    private void anularMatriculasPrimeiroAno(String siglaCurso) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.getCurso() != null &&
                    e.getCurso().getSigla().equals(siglaCurso) && e.getAnoFrequencia() == 1) {
                repositorio.removerEstudante(e.getNumeroMecanografico());
                i--;
            }
        }
    }

    private void gerarCertificadosConcluintes(int ano) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            if (e != null && e.getCurso() != null) {
                if (verificarConclusaoCurso(e)) {
                    // Chama o método estático da classe Certificado
                    model.bll.Certificado.gerarCertificado(e, ano);
                }
            }
        }
    }

    private boolean verificarConclusaoCurso(Estudante e) {
        Curso c = e.getCurso();
        if (c.getTotalUCs() == 0) return false;
        int ucsAprovadas = 0;
        for (int i = 0; i < c.getTotalUCs(); i++) {
            UnidadeCurricular ucCurso = c.getUnidadesCurriculares()[i];
            // Procurar esta UC no histórico de avaliações do aluno
            for (int j = 0; j < e.getTotalHistorico(); j++) {
                Avaliacao av = e.getHistoricoAvaliacoes()[j];
                if (av != null && av.getUc().getSigla().equals(ucCurso.getSigla()) && av.calcularMedia() >= 9.5) {
                    ucsAprovadas++;
                    break;
                }
            }
        }
        return ucsAprovadas == c.getTotalUCs();
    }

    private void processarRecuperacaoPassword() {
        view.mostrarCabecalhoLogin();
        String email = view.pedirEmail();
        String nif = view.pedirNif();

        carregarBaseDeDadosCompleta();

        boolean sucesso = utils.Seguranca.recuperarPassword(email, nif, repositorio);

        if (sucesso) {
            view.msgSucessoRecuperacao();
            ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgErroDadosIncorretos();
        }

        this.repositorio = new RepositorioDados();
    }


}