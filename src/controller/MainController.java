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

    /**
     * Processa a tentativa de autenticação de um utilizador.
     * Implementa um mecanismo de "Login Rápido" (lendo apenas logins.csv) para otimizar desempenho,
     * carregando a base de dados completa apenas se as credenciais forem válidas.
     */
    private void processarLogin() {
        view.mostrarCabecalhoLogin();

        String emailLogin = validarDominioEmail();
        String passwordLogin = view.pedirPassword();
        String passEncriptada = utils.Seguranca.encriptar(passwordLogin);

        // Verifica rapidamente as credenciais sem carregar toda a base de dados
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

        // Se a validação for bem sucedida, carrega a BD completa para a RAM
        carregarBaseDeDadosCompleta();

        Utilizador userLogado = repositorio.autenticar(emailLogin, passEncriptada);
        if (userLogado != null) {
            // Bloqueia o acesso a utilizadores inativos/suspensos
            if (userLogado instanceof Estudante && !((Estudante) userLogado).isAtivo()) {
                view.msgErroInativo();
                return;
            }
            if (userLogado instanceof Docente && !((Docente) userLogado).isAtivo()) {
                view.msgErroInativo();
                return;
            }
            // Encaminha para o controlador específico consoante o perfil
            abrirMenuPorRole(tipoUtilizador, userLogado);
        }

        // Ao fechar a sessão, guarda eventuais alterações no disco
        encerrarSessaoESalvar();
    }

    /**
     * Valida se o email introduzido pertence ao domínio da instituição de ensino.
     *
     * @return O email validado.
     */
    private String validarDominioEmail() {
        while (true) {
            String email = view.pedirEmail();
            if (email.toLowerCase().endsWith("@issmf.ipp.pt")) {
                return email;
            }
            view.msgErroEmailDominio();
        }
    }

    /**
     * Instancia e executa o controlador apropriado com base no cargo do utilizador logado.
     *
     * @param tipo A string que define o perfil (GESTOR, DOCENTE, ESTUDANTE).
     * @param user O objeto de utilizador polimórfico carregado do repositório.
     */
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

    /**
     * Processa a entrada para o fluxo de registo público de novos estudantes.
     */
    private void processarRegistoEstudante() {
        view.msgPrepararRegisto();
        carregarBaseDeDadosCompleta();

        registarEstudanteNoSistema();

        encerrarSessaoESalvar();
    }



    // =========================================================
    // 3. TRANSIÇÃO DE ANO LETIVO E GESTÃO GLOBAL (CASE 3)
    // =========================================================

    /**
     * Executa os processos de fecho de ano letivo, incluindo validações de abertura de curso,
     * progressão no tempo e a geração dos certificados de conclusão.
     */
    private void processarTransicaoAno() {
        view.mostrarCabecalhoTransicao();
        carregarBaseDeDadosCompleta();

        // Verifica a viabilidade de funcionamento dos cursos no arranque
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

        this.repositorio = new RepositorioDados(); // Limpar a memória no final do processo
    }

    // =========================================================
    // 4. RECUPERAÇÃO DA PASSWORD (CASE 4)
    // =========================================================

    /**
     * Inicia o fluxo de recuperação de password para qualquer utilizador do sistema.
     */
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
            // Se falhou, pode ter sido NIF errado OU erro de rede no email
            view.msgErroDadosIncorretosOuFalhaEmail();
        }

        this.repositorio = new RepositorioDados();
    }

    // =========================================================
    // MÉTODOS AUXILIARES E PRIVADOS (REGRAS E DADOS)
    // =========================================================

    /**
     * Exporta as alterações em memória para disco (ficheiros CSV) e limpa o repositório,
     * garantindo o isolamento dos dados e prevenindo fugas de memória entre sessões.
     */
    private void encerrarSessaoESalvar() {
        ExportadorCSV.exportarDados("bd", repositorio);
        this.repositorio = new RepositorioDados();
        view.msgSessaoEncerrada();
    }

    /**
     * Centraliza a chamada a todos os importadores para carregar o modelo de domínio completo.
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

    /**
     * Garante que o estudante apenas se inscreve num curso que esteja atualmente ativo no sistema.
     *
     * @return O curso válido escolhido pelo utilizador.
     */
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

    /**
     * Efetiva a criação e o registo do estudante na base de dados, gerando credenciais seguras.
     */
    private void registarEstudanteNoSistema() {
        view.mostrarCabecalhoRegisto();

        // Validação se existem cursos
        if (repositorio.getTotalCursos() == 0) {
            view.msgSemCursosParaRegisto();
            return;
        }

        // Recolha de Dados
        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();
        String emailPessoal = view.pedirEmailPessoal();

        int indexCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (indexCurso < 0 || indexCurso >= repositorio.getTotalCursos()) {
            view.msgErroNumeroInvalido();
            return;
        }

        Curso cursoEscolhido = repositorio.getCursos()[indexCurso];

        // Revisão de dados na View
        view.mostrarRevisaoEstudante(nome, nif, morada, dataNascimento, emailPessoal, cursoEscolhido.getNome());

        // Confirmação
        if (view.confirmarDados()) {

            // Lógica de Geração de Credenciais
            int anoAtual = repositorio.getAnoAtual();
            int numMec = repositorio.gerarNumeroMecanografico(anoAtual);
            String email = utils.GeradorEmail.gerarEmailEstudante(numMec);
            String passRaw = utils.GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passRaw);

            Estudante novo = new Estudante(numMec, email, passEnc, nome, nif, morada, dataNascimento, cursoEscolhido, anoAtual, emailPessoal);

            if (repositorio.adicionarEstudante(novo)) {
                // Enviar o email e capturar o resultado
                boolean emailEnviado = utils.ServicoEmail.enviarEmailBoasVindas(novo, passRaw);

                if (emailEnviado) {
                    view.msgSucessoEnvioEmail(novo.getEmailPessoal());
                } else {
                    view.msgErroEnvioEmail();
                }

                view.mostrarCredenciaisGeradas(anoAtual, numMec, email, passRaw);

                // Salva o novo aluno nos CSVs
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.msgErroLimiteEstudantes();
            }
        } else {
            view.msgRegistoCancelado();
        }
    }

    /**
     * Inscreve um recém-matriculado nas Unidades Curriculares correspondentes ao seu primeiro ano de frequência.
     */
    private void vincularUcsIniciais(Estudante est, Curso curso) {
        for (int i = 0; i < curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
            if (uc.getAnoCurricular() == est.getAnoFrequencia()) {
                est.getPercursoAcademico().inscreverEmUc(uc);
            }
        }
    }

    /**
     * Valida a condição de "Numerus Clausus Mínimo" para o arranque do 1º ano de cada curso.
     * Cursos com menos de 5 alunos no 1º ano são cancelados e os alunos desmatriculados.
     */
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

    /**
     * Contabiliza o número de alunos matriculados no primeiro ano de um dado curso.
     */
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

    /**
     * Remove do sistema os alunos de 1º ano matriculados num curso que não reuniu as condições de arranque.
     */
    private void anularMatriculasPrimeiroAno(String siglaCurso) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.getCurso() != null &&
                    e.getCurso().getSigla().equals(siglaCurso) && e.getAnoFrequencia() == 1) {
                repositorio.removerEstudante(e.getNumeroMecanografico());
                // Decrementa o índice para ajustar à contração do array após remoção
                i--;
            }
        }
    }

    /**
     * Emite certificados e diplomas para todos os alunos que tenham reunido condições de conclusão.
     */
    private void gerarCertificadosConcluintes(int ano) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            if (e != null && e.getCurso() != null) {
                if (verificarConclusaoCurso(e)) {
                    model.bll.Certificado.gerarCertificado(e, ano);
                }
            }
        }
    }

    /**
     * Verifica se um aluno obteve aproveitamento (média > 9.5) em todas as Unidades Curriculares do seu curso.
     *
     * @param e O estudante a avaliar.
     * @return true se todas as UCs do plano de estudos constarem no histórico com nota positiva.
     */
    private boolean verificarConclusaoCurso(Estudante e) {
        Curso c = e.getCurso();
        if (c.getTotalUCs() == 0) return false;

        int ucsAprovadas = 0;
        for (int i = 0; i < c.getTotalUCs(); i++) {
            UnidadeCurricular ucCurso = c.getUnidadesCurriculares()[i];

            // Procura a UC atual no histórico de avaliações do aluno
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
}