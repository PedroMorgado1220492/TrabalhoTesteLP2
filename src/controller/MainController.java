package controller;

import model.bll.*;
import model.dal.ExportadorCSV;
import model.dal.ImportadorCSV;
import utils.*;
import view.MainView;
import model.dal.RepositorioDados;
import utils.CancelamentoException;

/**
 * Controlador principal da aplicação (Entry Point do Sistema).
 * Atua como o coordenador geral do padrão MVC. É responsável por exibir o menu inicial (público),
 * processar a autenticação global, gerir o auto-registo de novos alunos e coordenar a transição
 * global de ano letivo (incluindo validações financeiras e académicas delegadas aos respetivos Models).
 */
public class MainController {

    // ---------- ATRIBUTOS ----------
    private MainView view;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor do MainController.
     * @param view        A view principal de interação com o utilizador visitante (MainView).
     * @param repositorio O repositório centralizado de dados (em memória).
     */
    public MainController(MainView view, RepositorioDados repositorio) {
        this.view = view;
        this.repositorio = repositorio;
    }

    /**
     * Inicia o ciclo principal do sistema.
     * Mantém o ecrã de boas-vindas ativo até indicação de encerramento.
     */
    public void iniciarSistema() {
        boolean aExecutar = true;
        view.mostrarBemVindo();

        while (aExecutar) {
            view.mostrarAnoLetivo(repositorio.getAnoAtual());
            int opcao = view.mostrarMenu();

            try {
                switch (opcao) {
                    case 1: processarLogin(); break;
                    case 2: processarRegistoEstudante(); break;
                    case 3: processarTransicaoAno(); break;
                    case 4: processarRecuperacaoPassword(); break;
                    case 0:
                        view.msgEncerramento();
                        aExecutar = false;
                        break;
                    default:
                        view.msgOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento();
            }
        }
    }


    // =========================================================
    // 1. LÓGICA DE LOGIN E SESSÃO
    // =========================================================

    /**
     * Coordena o fluxo de autenticação.
     * Verifica os ficheiros parciais para acesso rápido e, em caso de sucesso,
     * carrega toda a base de dados em memória e redireciona para o respetivo sub-controlador (Role).
     */
    private void processarLogin() {
        view.mostrarCabecalhoLogin();

        String emailLogin = validarDominioEmail();
        String passwordLogin = view.pedirPassword();
        String passEncriptada = utils.Seguranca.encriptar(passwordLogin);

        // Verificação leve (sem carregar toda a base de dados)
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

        // Autenticação forte (agora que os objetos estão em memória)
        Utilizador userLogado = repositorio.autenticar(emailLogin, passEncriptada);

        if (userLogado != null) {
            // Delegação do controlo de estado às classes filhas
            if (userLogado instanceof Estudante && !((Estudante) userLogado).isAtivo() ||
                    userLogado instanceof Docente && !((Docente) userLogado).isAtivo() ||
                    userLogado instanceof Gestor && !((Gestor) userLogado).isAtivo()) {

                view.msgErroInativo();
                return;
            }
            abrirMenuPorRole(tipoUtilizador, userLogado);
        }

        encerrarSessaoESalvar();
    }

    /**
     * Instancia e executa o Controlador apropriado consoante a função (Role) do utilizador.
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
    // 2. REGISTO PÚBLICO DE ESTUDANTE
    // =========================================================

    /**
     * Coordena o auto-registo de um novo aluno através da interface pública.
     */
    private void processarRegistoEstudante() {
        view.msgPrepararRegisto();
        carregarBaseDeDadosCompleta();
        registarEstudanteNoSistema();
        encerrarSessaoESalvar();
    }

    private void registarEstudanteNoSistema() {
        view.mostrarCabecalhoRegisto();

        // Delegação de pesquisa ao Repositório
        Curso[] cursosAtivos = repositorio.obterCursosDisponiveisParaMatricula();

        if (cursosAtivos.length == 0) {
            view.msgSemCursosParaRegisto();
            return;
        }

        // --- RECOLHA DE DADOS ---
        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();
        String emailPessoal = validarEmailPessoal();

        int indexCurso = view.pedirEscolhaCurso(cursosAtivos, cursosAtivos.length);

        if (indexCurso < 0 || indexCurso >= cursosAtivos.length) {
            view.msgErroNumeroInvalido();
            return;
        }

        Curso cursoEscolhido = cursosAtivos[indexCurso];
        view.mostrarRevisaoEstudante(nome, nif, morada, dataNascimento, emailPessoal, cursoEscolhido.getNome());

        // --- PROCESSAMENTO DA MATRÍCULA ---
        if (view.confirmarDados()) {
            int anoAtual = repositorio.getAnoAtual();
            int numMec = repositorio.gerarNumeroMecanografico(anoAtual);

            String email = utils.GeradorEmail.gerarEmailEstudante(numMec);
            String passRaw = utils.GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passRaw);

            // 1. Criar o Estudante (O construtor já o define como ATIVO = FALSE)
            Estudante novo = new Estudante(numMec, email, passEnc, nome, nif, morada, dataNascimento, cursoEscolhido, anoAtual, emailPessoal);

            if (repositorio.adicionarEstudante(novo)) {

                // 2. Verificar quantos alunos existem no curso (incluindo este novo)
                int totalInscritos = repositorio.contarInscritosPrimeiroAno(cursoEscolhido.getSigla(), anoAtual);

                if (totalInscritos >= 5) {
                    ativarAlunosDoCurso(cursoEscolhido);
                    view.msgSucessoRegistoComAtivacao();
                } else {
                    // Passa o total atual para a view mostrar (ex: 3/5)
                    view.msgAvisoAguardandoQuorum(totalInscritos);
                }

                // 3. Notificação e Persistência
                utils.ServicoEmail.enviarEmailBoasVindas(novo, passRaw);
                view.mostrarCredenciaisGeradas(anoAtual, numMec, email, passRaw);

                // Grava o estado (Ativo ou Inativo) no CSV
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);

            } else {
                view.msgErroLimiteEstudantes();
            }
        } else {
            view.msgRegistoCancelado();
        }
    }



    /**
     * Método auxiliar para ativar todos os estudantes pendentes de um curso
     * quando o quórum de 5 é finalmente atingido.
     */
    private void ativarAlunosDoCurso(Curso curso) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            // Se pertence ao curso e ainda está inativo
            if (e != null && e.getCurso().getSigla().equals(curso.getSigla()) && !e.isAtivo()) {
                e.setAtivo(true);
                e.matricularNasUcsIniciais();
                // Opcional: Enviar email extra a avisar que a turma abriu
            }
        }
    }

    // =========================================================
    // 3. TRANSIÇÃO DE ANO LETIVO E VALIDAÇÕES MACRO
    // =========================================================

    /**
     * Executa os processos pesados de fecho e abertura de ano letivo.
     * Inclui validação de viabilidade de cursos (Mínimo 5 inscritos) e geração de diplomas.
     */
    private void processarTransicaoAno() {
        view.mostrarCabecalhoTransicao();
        carregarBaseDeDadosCompleta();

        int proximoAno = repositorio.getAnoAtual() + 1;

        // Fase 1: Auditar se os cursos têm condições de abrir no próximo ano
        validarArranqueDeCursos(proximoAno);

        if (view.pedirConfirmacaoAvanco(proximoAno)) {
            repositorio.avancarAno();

            // Fase 2: Processar formaturas do ano que está a terminar
            gerarCertificadosConcluintes(proximoAno - 1);

            view.msgSucessoAvancoAno(repositorio.getAnoAtual());
            ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.msgCancelamentoAvancoAno(repositorio.getAnoAtual());
        }

    }

    /**
     * Analisa todos os cursos e encerra os que não atingirem a quota mínima de 5 alunos no 1º ano.
     */
    private void validarArranqueDeCursos(int anoAlvo) {
        view.mostrarAvisoValidacaoCursos();
        if (repositorio.getTotalCursos() == 0) return;

        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso curso = repositorio.getCursos()[i];

            if (curso != null && curso.isAtivo()) {

                // Valida a estrutura estrutural do Curso delegada ao Model
                if (!curso.temEstruturaValida()) {
                    view.mostrarCursoCanceladoFaltaUCs(curso.getSigla());
                    curso.setAtivo(false);
                    repositorio.anularMatriculasPrimeiroAno(curso.getSigla(), anoAlvo);
                    continue;
                }

                // Verifica a regra financeira (mínimo de alunos) delegada ao Repositório
                int inscritos = repositorio.contarAlunosNoPrimeiroAno(curso.getSigla());

                if (inscritos > 0 && inscritos < 5) {
                    view.mostrarCursoCancelado(curso.getSigla(), inscritos);
                    repositorio.anularMatriculasPrimeiroAno(curso.getSigla(), anoAlvo);
                    curso.setAtivo(false);
                } else if (inscritos >= 5) {
                    view.mostrarCursoAprovado(curso.getSigla(), inscritos);
                }
            }
        }
        view.mostrarFimValidacao();
    }

    /**
     * Itera por todos os alunos para verificar se concluíram o curso, gerando os respetivos diplomas (PDF/TXT)
     * e inativando as contas.
     */
    private void gerarCertificadosConcluintes(int ano) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];

            if (e != null && e.getCurso() != null && e.isAtivo()) {

                // Delegação de regras académicas ao Model Estudante
                if (e.concluiuCurso()) {
                    String caminhoCertificado = model.bll.Certificado.gerarCertificado(e, ano);

                    if (caminhoCertificado != null && e.getEmailPessoal() != null && !e.getEmailPessoal().isEmpty()) {
                        utils.ServicoEmail.enviarEmailCertificado(e.getEmailPessoal(), e.getNome(), caminhoCertificado);
                    }
                    // A conta é desativada pois o aluno tornou-se diplomado/alumnni
                    e.setAtivo(false);
                }
            }
        }
    }


    // =========================================================
    // 4. RECUPERAÇÃO DA PASSWORD E UTILITÁRIOS
    // =========================================================

    /**
     * Lida com os pedidos públicos de redefinição de palavra-passe.
     */
    private void processarRecuperacaoPassword() {
        view.mostrarCabecalhoLogin();
        String email = view.pedirEmail();
        String nif = view.pedirNifRecuperacao();

        carregarBaseDeDadosCompleta();

        // Delegação de lógica de segurança aos Utilitários
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
    // 5. MÉTODOS AUXILIARES E PRIVADOS
    // =========================================================

    /**
     * Fecha a sessão do utilizador logado, força a exportação do estado atual
     * e apaga a instância em memória por questões de segurança.
     */
    private void encerrarSessaoESalvar() {
        //ExportadorCSV.exportarDados("bd", repositorio);
        this.repositorio = new RepositorioDados(); // Reset à memória
        view.msgSessaoEncerrada();
    }

    /**
     * Invoca os importadores de CSV para reconstruir toda a matriz de objetos em memória RAM.
     */
    private void carregarBaseDeDadosCompleta() {
        repositorio.limpar();
        ImportadorCSV.importarGestores("bd/gestores.csv", repositorio);
        ImportadorCSV.importarDepartamentos("bd/departamentos.csv", repositorio);
        ImportadorCSV.importarCursos("bd/cursos.csv", repositorio);
        ImportadorCSV.importarDocentes("bd/docentes.csv", repositorio);
        ImportadorCSV.importarUCs("bd/ucs.csv", repositorio);
        ImportadorCSV.importarEstudantes("bd/estudantes.csv", repositorio);
        ImportadorCSV.importarAvaliacoes("bd/avaliacoes.csv", repositorio); // <-- apenas uma vez!
    }

    private String validarDominioEmail() {
        while (true) {
            String email = view.pedirEmail();
            if (Validador.isEmailInstitucionalValido(email)) return email;
            view.msgErroEmailDominio();
        }
    }

    private String validarNome() {
        while (true) {
            String nome = view.pedirNome();
            if (Validador.isNomeValido(nome)) return nome;
            view.msgErroNome();
        }
    }

    private String validarNif() {
        String nif;
        while (true) {
            nif = view.pedirNif();

            if (nif.length() != 9) {
                view.mostrarErroNifFormato();
            } else if (repositorio.existeNif(nif)) {
                view.mostrarErroNifDuplicado();
            } else {
                return nif;
            }
        }
    }

    private String validarDataNascimento() {
        while (true) {
            String data = view.pedirDataNascimento();
            if (!Validador.isDataNascimentoValida(data)) {
                view.msgErroData();
            } else if (!Validador.temIdadeMinima(data)) {
                view.msgErroIdadeMinima();
            } else {
                return data;
            }
        }
    }

    private String validarEmailPessoal() {
        while (true) {
            String email = view.pedirEmailPessoal();
            if (Validador.isEmailPessoalValido(email)) {
                return email;
            }
            view.msgErroEmailPessoal();
        }
    }

}