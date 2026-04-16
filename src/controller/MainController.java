package controller;

import model.bll.*;
import model.dal.ExportadorCSV;
import model.dal.ImportadorCSV;
import utils.*;
import view.MainView;
import model.dal.RepositorioDados;

/**
 * Controlador principal da aplicação (Entry Point).
 * Responsável por gerir o menu inicial (público), processos de autenticação,
 * registo de novos alunos, transições de ano letivo e a persistência base dos dados.
 */
public class MainController {

    private MainView view;
    private RepositorioDados repositorio;

    /**
     * Construtor do MainController.
     *
     * @param view        A view principal de interação com o utilizador (MainView).
     * @param repositorio O repositório centralizado de dados.
     */
    public MainController(MainView view, RepositorioDados repositorio) {
        this.view = view;
        this.repositorio = repositorio;
    }

    /**
     * Inicia o ciclo principal do sistema (Ponto de Entrada).
     * Gere o menu público e encaminha a execução para os fluxos correspondentes (Login, Registo, etc.).
     */
    public void iniciarSistema() {
        boolean aExecutar = true;
        view.mostrarBemVindo();

        while (aExecutar) {
            view.mostrarAnoLetivo(repositorio.getAnoAtual());
            int opcao = view.mostrarMenu();

            try {
                switch (opcao) {
                    case 1:
                        processarLogin();
                        break;
                    case 2:
                        processarRegistoEstudante();
                        break;
                    case 3:
                        processarTransicaoAno();
                        break;
                    case 4:
                        processarRecuperacaoPassword();
                        break;
                    case 0:
                        view.msgEncerramento();
                        aExecutar = false;
                        break;
                    default:
                        view.msgOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu principal...");
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
            if (userLogado instanceof Gestor && !((Gestor) userLogado).isAtivo()) {
                view.msgErroInativo(); return;
            }
            abrirMenuPorRole(tipoUtilizador, userLogado);
        }

        encerrarSessaoESalvar();
    }

    private String validarDominioEmail() {
        while (true) {
            String email = view.pedirEmail();
            if (Validador.isEmailInstitucionalValido(email)) {
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
        registarEstudanteNoSistema();
        encerrarSessaoESalvar();
    }

    // =========================================================
    // 3. TRANSIÇÃO DE ANO LETIVO E GESTÃO GLOBAL (CASE 3)
    // =========================================================

    private void processarTransicaoAno() {
        view.mostrarCabecalhoTransicao();
        carregarBaseDeDadosCompleta();

        int proximoAno = repositorio.getAnoAtual() + 1;
        validarArranqueDeCursos(proximoAno);

        if (view.pedirConfirmacaoAvanco(proximoAno)) {
            repositorio.avancarAno();
            gerarCertificadosConcluintes(proximoAno - 1);
            view.msgSucessoAvancoAno(repositorio.getAnoAtual());
            ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgCancelamentoAvancoAno(repositorio.getAnoAtual());
        }

        this.repositorio = new RepositorioDados();
    }

    // =========================================================
    // 4. RECUPERAÇÃO DA PASSWORD (CASE 4)
    // =========================================================

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
            view.msgErroDadosIncorretosOuFalhaEmail();
        }

        this.repositorio = new RepositorioDados();
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
        ImportadorCSV.importarUCs("bd/ucs.csv", repositorio);
        ImportadorCSV.importarEstudantes("bd/estudantes.csv", repositorio);
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

    private void registarEstudanteNoSistema() {
        view.mostrarCabecalhoRegisto();

        Curso[] cursosAtivos = new Curso[repositorio.getTotalCursos()];
        int totalAtivos = 0;

        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso c = repositorio.getCursos()[i];
            if (c != null && c.isAtivo() && c.temEstruturaValida()) {
                cursosAtivos[totalAtivos] = c;
                totalAtivos++;
            }
        }

        if (totalAtivos == 0) {
            view.msgSemCursosParaRegisto();
            return;
        }

        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();
        String emailPessoal = view.pedirEmailPessoal();

        int indexCurso = view.pedirEscolhaCurso(cursosAtivos, totalAtivos);

        if (indexCurso < 0 || indexCurso >= totalAtivos) {
            view.msgErroNumeroInvalido();
            return;
        }

        Curso cursoEscolhido = cursosAtivos[indexCurso];
        view.mostrarRevisaoEstudante(nome, nif, morada, dataNascimento, emailPessoal, cursoEscolhido.getNome());

        if (view.confirmarDados()) {
            int anoAtual = repositorio.getAnoAtual();
            int numMec = repositorio.gerarNumeroMecanografico(anoAtual);
            String email = utils.GeradorEmail.gerarEmailEstudante(numMec);
            String passRaw = utils.GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passRaw);

            Estudante novo = new Estudante(numMec, email, passEnc, nome, nif, morada, dataNascimento, cursoEscolhido, anoAtual, emailPessoal);

            if (repositorio.adicionarEstudante(novo)) {
                vincularUcsIniciais(novo, cursoEscolhido);
                boolean emailEnviado = utils.ServicoEmail.enviarEmailBoasVindas(novo, passRaw);

                if (emailEnviado) {
                    view.msgSucessoEnvioEmail(novo.getEmailPessoal());
                } else {
                    view.msgErroEnvioEmail();
                }

                view.mostrarCredenciaisGeradas(anoAtual, numMec, email, passRaw);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.msgErroLimiteEstudantes();
            }
        } else {
            view.msgRegistoCancelado();
        }
    }

    private void vincularUcsIniciais(Estudante est, Curso curso) {
        for (int i = 0; i < curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
            if (uc != null && uc.getAnoCurricular() == est.getAnoFrequencia()) {
                est.getPercursoAcademico().inscreverEmUc(uc);
            }
        }
    }

    private void validarArranqueDeCursos(int anoAlvo) {
        view.mostrarAvisoValidacaoCursos();
        if (repositorio.getTotalCursos() == 0) return;

        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso curso = repositorio.getCursos()[i];

            // Valida a estrutura de UCs do Curso
            if (curso != null && curso.isAtivo()) {
                if (!curso.temEstruturaValida()) {
                    view.mostrarCursoCanceladoFaltaUCs(curso.getSigla());
                    curso.setAtivo(false);
                    anularMatriculasPrimeiroAno(curso.getSigla(), anoAlvo);
                    continue;
                }

                int inscritos = contarInscritosPrimeiroAno(curso, anoAlvo);

                if (inscritos > 0 && inscritos < 5) {
                    view.mostrarCursoCancelado(curso.getSigla(), inscritos);
                    anularMatriculasPrimeiroAno(curso.getSigla(), anoAlvo);
                    curso.setAtivo(false);
                } else if (inscritos >= 5) {
                    view.mostrarCursoAprovado(curso.getSigla(), inscritos);
                }
            }
        }
        view.mostrarFimValidacao();
    }

    private int contarInscritosPrimeiroAno(Curso curso, int anoAlvo) {
        int conta = 0;
        for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
            Estudante e = repositorio.getEstudantes()[j];
            if (e != null && e.getCurso() != null &&
                    e.getCurso().getSigla().equals(curso.getSigla()) && e.getAnoPrimeiraInscricao() == anoAlvo) {
                conta++;
            }
        }
        return conta;
    }

    private void anularMatriculasPrimeiroAno(String siglaCurso, int anoAlvo) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.getCurso() != null &&
                    e.getCurso().getSigla().equals(siglaCurso) && e.getAnoPrimeiraInscricao() == anoAlvo) {
                repositorio.removerEstudante(e.getNumeroMecanografico());
                i--;
            }
        }
    }

    private void gerarCertificadosConcluintes(int ano) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            if (e != null && e.getCurso() != null && e.isAtivo()) {
                if (verificarConclusaoCurso(e)) {
                    String caminhoCertificado = model.bll.Certificado.gerarCertificado(e, ano);
                    if (caminhoCertificado != null && e.getEmailPessoal() != null && !e.getEmailPessoal().isEmpty()) {
                        utils.ServicoEmail.enviarEmailCertificado(e.getEmailPessoal(), e.getNome(), caminhoCertificado);
                    }
                    e.setAtivo(false);
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
            for (int j = 0; j < e.getTotalHistorico(); j++) {
                Avaliacao av = e.getHistoricoAvaliacoes()[j];
                if (av != null && av.getUc() != null && av.getUc().getSigla().equals(ucCurso.getSigla()) && av.calcularMedia() >= 9.5) {
                    ucsAprovadas++;
                    break;
                }
            }
        }
        return ucsAprovadas == c.getTotalUCs();
    }
}