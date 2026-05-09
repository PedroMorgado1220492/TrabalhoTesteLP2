package controller;

import model.bll.*;
import model.dal.ExportadorCSV;
import model.dal.ImportadorCSV;
import utils.*;
import view.MainView;
import model.dal.RepositorioDados;
import model.bll.Relatorio;
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
            view.mostrarAnoLetivo(repositorio.getAnoAtual(), repositorio.isAnoIniciado());
            int opcao = view.mostrarMenu();

            try {
                switch (opcao) {
                    case 1: processarLogin(); break;
                    case 2: processarRegistoEstudante(); break;
                    case 3: processarRecuperacaoPassword(); break;
                    case 4: processarIniciarAnoLetivo(); break;
                    case 5: processarTransicaoAno(); break;
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
            if (userLogado instanceof Estudante && !((Estudante) userLogado).isAtivo() ||
                    userLogado instanceof Docente && !((Docente) userLogado).isAtivo() ||
                    userLogado instanceof Gestor && !((Gestor) userLogado).isAtivo()) {

                view.msgErroInativo();
                return;
            }

            String identificador = "";
            if (userLogado instanceof Estudante) {
                identificador = String.valueOf(((Estudante) userLogado).getNumeroMecanografico());
            } else if (userLogado instanceof Docente) {
                identificador = ((Docente) userLogado).getSigla();
            } else if (userLogado instanceof Gestor) {
                identificador = ((Gestor) userLogado).getMorada();
            }

            view.msgBemVindoUsuario(userLogado.getNome(), identificador, tipoUtilizador);

            abrirMenuPorRole(tipoUtilizador, userLogado);
        }

        encerrarSessaoESalvar();
    }

    /**
     * Instancia e executa o Controlador apropriado consoante a função (Role) do utilizador.
     */
    private void abrirMenuPorRole(String tipo, Utilizador user) {
        if (tipo.equals("GESTOR")) {

            new GestorController((Gestor) user, repositorio).iniciarMenuGestor();
        } else if (tipo.equals("DOCENTE")) {

            new DocenteController((Docente) user, repositorio).iniciarMenu();
        } else if (tipo.equals("ESTUDANTE")) {

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

        if (repositorio.isAnoIniciado()) {
            view.msgAnoIniciadoInscricoesFechadas();
            return;
        }

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
    // 3. RECUPERAÇÃO DA PASSWORD E UTILITÁRIOS
    // =========================================================

    /**
     * Lida com os pedidos públicos de redefinição de palavra-passe.
     */
    private void processarRecuperacaoPassword() {
        view.mostrarCabecalhoLogin();
        String email = view.pedirEmail();

        carregarBaseDeDadosCompleta();

        // Verificar se o email pertence a um Gestor
        Utilizador user = repositorio.procurarUtilizadorPorEmail(email);
        if (user instanceof Gestor) {
            view.msgGestorNaoPodeRecuperar();
            this.repositorio = new RepositorioDados();
            return;
        }

        // Delegação de lógica de segurança aos Utilitários
        String nif = view.pedirNifRecuperacao();
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
    // 4. INICIAÇÃO DE ANO LETIVO
    // =========================================================

    /**
     * Inicia o ano letivo, realizando todas as verificações necessárias antes do início das aulas.
     *
     * <p>Este método executa as seguintes validações:</p>
     * <ul>
     *   <li>Verifica se o ano letivo já foi iniciado (caso afirmativo, termina a execução).</li>
     *   <li>Desativa cursos que não possuem estrutura curricular mínima (pelo menos uma UC ativa por ano).</li>
     *   <li>Identifica Unidades Curriculares que ainda não têm o número de avaliações definido.</li>
     *   <li>Gera um relatório detalhado em ficheiro .txt com o resultado das verificações.</li>
     *   <li>Define o estado do sistema como "ano iniciado" apenas se todas as UCs estiverem configuradas.</li>
     * </ul>
     *
     * <p>Se existirem UCs sem número de avaliações definido, o ano letivo NÃO é iniciado e
     * o relatório lista as UCs em falta com os respetivos docentes responsáveis.</p>
     *
     * <p>Se algum curso for desativado por falta de estrutura curricular, essa informação
     * é incluída no relatório.</p>
     *
     * <p>O relatório é guardado na raiz do projeto com o nome:
     * <code>relatorio_inicio_ano_[ANO].txt</code></p>
     *
     * <p>Após a execução, os dados são persistidos nos ficheiros CSV.</p>
     *
     * @see model.bll.Relatorio#gerarRelatorioInicioAno(RepositorioDados)
     * @see model.bll.Relatorio#salvarRelatorio(String, String)
     * @see RepositorioDados#setAnoIniciado(boolean)
     * @see RepositorioDados#isAnoIniciado()
     */
    private void processarIniciarAnoLetivo() {
        carregarBaseDeDadosCompleta();

        if (repositorio.isAnoIniciado()) {
            view.msgAnoJaIniciado();
            encerrarSessaoESalvar();
            return;
        }

        // Pedir confirmação simples
        if (!view.pedirConfirmacaoInicioAno(repositorio.getAnoAtual())) {
            view.mostrarCancelamento();
            encerrarSessaoESalvar();
            return;
        }

        // Gerar relatório
        Relatorio.ResultadoValidacao resultado = Relatorio.gerarRelatorioInicioAno(repositorio);

        // Imprimir relatório na consola
        Relatorio.imprimirRelatorio(resultado.getRelatorioConteudo());

        if (!resultado.isTodasUcsDefinidas()) {
            view.msgExistemUcsSemAvaliacoes();
        } else {
            repositorio.setAnoIniciado(true);
            view.msgInicioAnoSucesso(repositorio.getAnoAtual());
        }

        if (resultado.isAlgumCursoDesativado()) {
            view.msgCursosDesativados();
        }

        // Salvar relatório
        String fileName = "relatorio_inicio_ano_" + repositorio.getAnoAtual() + ".txt";
        boolean sucesso = Relatorio.salvarRelatorio(resultado.getRelatorioConteudo(), fileName);
        if (sucesso) {
            view.msgRelatorioGerado("relatorios/" + fileName);
        } else {
            view.msgErroRelatorio();
        }

        model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        encerrarSessaoESalvar();
    }

    // =========================================================
    // 5. TRANSIÇÃO DE ANO LETIVO
    // =========================================================

    /**
     * Executa os processos pesados de fecho e abertura de ano letivo.
     * Inclui validação de viabilidade de cursos (Mínimo 5 inscritos) e geração de diplomas.
     */
    private void processarTransicaoAno() {
        view.mostrarCabecalhoTransicao();
        carregarBaseDeDadosCompleta();

        int proximoAno = repositorio.getAnoAtual() + 1;

        // Verificar se o ano letivo foi iniciado
        if (!repositorio.isAnoIniciado()) {
            view.msgTransicaoBloqueada();
            return;
        }

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

        if (repositorio.getTotalCursos() == 0) return;

        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso curso = repositorio.getCursos()[i];

            if (curso != null && curso.isAtivo()) {

                // Valida a estrutura estrutural do Curso delegada ao Model
                if (!curso.temEstruturaValida()) {
                    view.mostrarCursoCanceladoFaltaUCs(curso.getSigla());
                    repositorio.anularMatriculasPrimeiroAno(curso.getSigla(), anoAlvo);
                    continue;
                }

                // Verifica a regra financeira (mínimo de alunos) delegada ao Repositório
                int inscritos = repositorio.contarAlunosNoPrimeiroAno(curso.getSigla());

                if (inscritos > 0 && inscritos < 5) {
                    repositorio.anularMatriculasPrimeiroAno(curso.getSigla(), anoAlvo);
                } else if (inscritos >= 5) {
                }
            }
        }
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
    // 6. MÉTODOS AUXILIARES E PRIVADOS
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
        ImportadorCSV.importarAvaliacoes("bd/avaliacoes.csv", repositorio);
    }

    /**
     * Valida o domínio do email institucional.
     * O email deve terminar com @issmf.ipp.pt.
     *
     * @return Email institucional válido.
     */
    private String validarDominioEmail() {
        while (true) {
            String email = view.pedirEmail();
            if (Validador.isEmailInstitucionalValido(email)) return email;
            view.msgErroEmailDominio();
        }
    }

    /**
     * Valida o nome completo do estudante.
     * O nome deve ter pelo menos duas palavras e conter apenas letras.
     *
     * @return Nome completo válido.
     */
    private String validarNome() {
        while (true) {
            String nome = view.pedirNome();
            if (Validador.isNomeValido(nome)) return nome;
            view.msgErroNome();
        }
    }

    /**
     * Valida o NIF (Número de Identificação Fiscal).
     * O NIF deve ter exatamente 9 dígitos e ser único no sistema.
     *
     * @return NIF válido.
     */
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

    /**
     * Valida a data de nascimento.
     * A data deve ter o formato DD-MM-AAAA, ser uma data real (ex: 30-02 inválido)
     * e o estudante deve ter pelo menos 16 anos.
     *
     * @return Data de nascimento válida.
     */
    private String validarDataNascimento() {
        while (true) {
            String data = view.pedirDataNascimento();
            if (!Validador.isDataFormatoValido(data)) {
                view.msgErroDataFormato();
            } else if (!Validador.isDataReal(data)) {
                view.msgErroDataInexistente();
            } else if (!Validador.temIdadeMinima(data)) {
                view.msgErroIdadeMinima();
            } else {
                return data;
            }
        }
    }

    /**
     * Valida o email pessoal.
     * O email deve conter '@' e '.' (formato básico).
     *
     * @return Email pessoal válido.
     */
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