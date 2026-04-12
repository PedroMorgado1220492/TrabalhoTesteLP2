package controller;

import utils.GeradorEmail;
import utils.GeradorPassword;
import utils.ServicoEmail;
import view.GestorView;
import model.bll.Gestor;
import model.dal.RepositorioDados;
import model.bll.Departamento;
import model.bll.Curso;
import model.bll.UnidadeCurricular;
import model.bll.Docente;
import model.bll.Estudante;
import utils.Validador;

/**
 * Controlador central responsável pela gestão de todas as entidades do sistema.
 * Atua como intermediário entre a interface do Gestor (GestorView) e a camada de dados (RepositorioDados),
 * garantindo a aplicação das regras de negócio institucionais (limites, validações e dependências).
 */
public class GestorController {

    private GestorView view;
    private Gestor gestorAtivo;
    private RepositorioDados repositorio;

    /**
     * Construtor do controlador do Gestor.
     *
     * @param gestorAtivo A instância do gestor (administrador) que iniciou a sessão.
     * @param repositorio O repositório centralizado onde todos os dados estão armazenados.
     */
    public GestorController(Gestor gestorAtivo, RepositorioDados repositorio) {
        this.view = new GestorView();
        this.gestorAtivo = gestorAtivo;
        this.repositorio = repositorio;
    }

    /**
     * Inicia o ciclo principal de execução do painel de administração do Gestor.
     */
    public void iniciarMenuGestor() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuPrincipalGestor();
            switch (opcao) {
                case 1: gerirDepartamentos(); break;
                case 2: gerirCursos(); break;
                case 3: gerirUCs(); break;
                case 4: gerirEstudantes(); break;
                case 5: gerirDocentes(); break;
                case 6: avancarAnoLetivo(); break;
                case 7: gerirRelatorios(); break;
                case 8: listarAlunosComDividas(); break;
                case 9: alterarPrecoCurso(); break;
                case 0:
                    view.mostrarMensagemSaida();
                    aExecutar = false;
                    break;
                default:
                    view.mostrarOpcaoInvalida();
            }
        }
    }

    // =========================================================
    // 1. GESTÃO DE DEPARTAMENTOS
    // =========================================================

    /**
     * Apresenta o submenu dedicado à gestão de Departamentos (Adicionar, Editar e Listar).
     */
    private void gerirDepartamentos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDepartamentos();
            switch (opcao) {
                case 1: adicionarDepartamento(); break;
                case 2: alterarDepartamento(); break;
                case 3: view.mostrarListaDepartamentos(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos()); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    /**
     * Processa o registo de um novo Departamento.
     * Exige uma sigla única no sistema para garantir a integridade dos dados.
     */
    private void adicionarDepartamento() {
        String sigla;
        // Validação contínua até que seja inserida uma sigla não existente
        while (true) {
            sigla = view.pedirSiglaDepartamento();
            if (repositorio.existeSiglaDepartamento(sigla)) {
                view.mostrarErroSiglaJaExiste(sigla);
            } else break;
        }
        String nome = view.pedirNomeDepartamento();

        Departamento novoDep = gestorAtivo.criarDepartamento(sigla, nome);
        if (repositorio.adicionarDepartamento(novoDep)) {
            view.mostrarSucessoRegistoDepartamento(nome);
        } else {
            view.mostrarErroLimiteDepartamentos();
        }
    }

    /**
     * Processa a alteração do nome de um Departamento existente.
     * A sigla atua como chave primária e não pode ser modificada.
     */
    private void alterarDepartamento() {
        String siglaDep = view.pedirSiglaDepartamentoAlterar();
        Departamento depEditar = encontrarDepartamento(siglaDep);

        if (depEditar != null) {
            String novoNomeDep = view.pedirNovoNome(depEditar.getNome());
            // Permite manter o nome atual se for submetido um valor vazio
            if (!novoNomeDep.isEmpty()) {
                depEditar.setNome(novoNomeDep);
                view.mostrarSucessoAtualizacao();
            } else {
                view.mostrarAvisoSemAlteracao();
            }
        } else {
            view.mostrarErroDepartamentoNaoEncontrado();
        }
    }

    // =========================================================
    // 2. GESTÃO DE CURSOS
    // =========================================================

    /**
     * Apresenta o submenu dedicado à gestão de Cursos.
     */
    private void gerirCursos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuCursos();
            switch (opcao) {
                case 1: adicionarCurso(); break;
                case 2: alterarCurso(); break;
                case 3: view.mostrarListaCursos(repositorio.getCursos(), repositorio.getTotalCursos()); break;
                case 4: alternarEstadoCurso(); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    /**
     * Cria um novo Curso, associando-o obrigatoriamente a um Departamento existente.
     */
    private void adicionarCurso() {
        // Bloqueia a criação se a infraestrutura base (Departamentos) não existir
        if (repositorio.getTotalDepartamentos() == 0) {
            view.mostrarErroFaltaDepartamento();
            return;
        }

        String siglaCurso;
        while (true) {
            siglaCurso = view.pedirSiglaCurso();
            if (repositorio.existeSiglaCurso(siglaCurso)) {
                view.mostrarErroSiglaJaExiste(siglaCurso);
            } else break;
        }

        String nomeCurso = view.pedirNomeCurso();
        int escolhaIndex = view.pedirEscolhaDepartamento(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos());

        if (escolhaIndex < 0 || escolhaIndex >= repositorio.getTotalDepartamentos()) {
            view.mostrarOpcaoInvalida();
            return;
        }

        Curso novoCurso = gestorAtivo.criarCurso(siglaCurso, nomeCurso, repositorio.getDepartamentos()[escolhaIndex]);
        if (repositorio.adicionarCurso(novoCurso)) {
            view.mostrarSucessoRegistoCurso(nomeCurso);
        } else {
            view.mostrarErroLimiteCursos();
        }
    }

    /**
     * Permite editar os dados de um Curso.
     * Cursos que já possuam estudantes inscritos ou UCs agregadas ficam bloqueados a edições estruturais para prevenir inconsistências.
     */
    private void alterarCurso() {
        String siglaBusca = view.pedirSiglaCursoAlterar();
        Curso cursoEditar = encontrarCurso(siglaBusca);

        if (cursoEditar != null) {
            if (verificarCursoBloqueado(cursoEditar)) {
                view.mostrarErroCursoBloqueado();
            } else {
                String novoNomeCurso = view.pedirNovoNome(cursoEditar.getNome());
                if (!novoNomeCurso.isEmpty()) {
                    cursoEditar.setNome(novoNomeCurso);
                    view.mostrarSucessoAtualizacao();
                } else {
                    view.mostrarAvisoSemAlteracao();
                }
            }
        } else {
            view.mostrarErroCursoNaoEncontrado();
        }
    }

    /**
     * Alterna o estado de um Curso entre Ativo e Inativo.
     * Cursos inativos não permitem novas matrículas. Não é possível inativar cursos com estudantes no ativo.
     */
    private void alternarEstadoCurso() {
        String sigla = view.pedirSiglaCursoAlterar();
        Curso curso = encontrarCurso(sigla);

        if (curso != null) {
            // Regra de Negócio: Impede a inativação se existirem alunos a frequentar o curso
            if (curso.isAtivo()) {
                boolean temAtivos = false;
                for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
                    Estudante e = repositorio.getEstudantes()[i];
                    if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(curso.getSigla()) && e.isAtivo()) {
                        temAtivos = true;
                        break;
                    }
                }
                if (temAtivos) {
                    view.msgAvisoCursoComAlunosAtivos(curso.getSigla());
                    return;
                }
            }

            curso.setAtivo(!curso.isAtivo());
            view.msgSucessoEstadoAlterado("Curso", curso.isAtivo());
        } else {
            view.mostrarErroCursoNaoEncontrado();
        }
    }

    // =========================================================
    // 3. GESTÃO DE UCs
    // =========================================================

    /**
     * Apresenta o submenu dedicado à gestão de Unidades Curriculares.
     */
    private void gerirUCs() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuUCs();
            switch (opcao) {
                case 1: criarUC(); break;
                case 2: partilharUC(); break;
                case 3: alterarUC(); break;
                case 4: view.mostrarListaUCs(repositorio.getUcs(), repositorio.getTotalUcs()); break;
                case 5: alternarEstadoUC(); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    /**
     * Cria uma nova Unidade Curricular.
     * Requer a atribuição obrigatória de um Docente Responsável e o vínculo a um Curso existente.
     */
    private void criarUC() {
        if (repositorio.getTotalDocentes() == 0 || repositorio.getTotalCursos() == 0) {
            view.mostrarErroFaltaDocenteOuCurso();
            return;
        }

        String siglaUc;
        while (true) {
            siglaUc = view.pedirSiglaUC();
            if (repositorio.existeSiglaUC(siglaUc)) {
                view.mostrarErroSiglaJaExiste(siglaUc);
            } else break;
        }

        String nomeUc = view.pedirNomeUC();
        int anoCurricular;

        try {
            anoCurricular = Integer.parseInt(view.pedirAnoCurricularUC());
        } catch(NumberFormatException e) {
            view.mostrarErroAnoNumerico();
            return;
        }

        // Seleção do docente coordenador
        int escolhaDocente = view.pedirEscolhaDocente(repositorio.getDocentes(), repositorio.getTotalDocentes());
        if (escolhaDocente < 0 || escolhaDocente >= repositorio.getTotalDocentes()) {
            view.mostrarOpcaoInvalida();
            return;
        }
        Docente docenteResponsavel = repositorio.getDocentes()[escolhaDocente];

        // Seleção do curso base
        int escolhaCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida();
            return;
        }
        Curso cursoAssociado = repositorio.getCursos()[escolhaCurso];

        // Regra de negócio: Valida se o curso não excedeu o limite de UCs por ano
        if (!cursoAssociado.podeAdicionarUcNoAno(anoCurricular)) {
            view.mostrarErroLimiteUCsAno(cursoAssociado.getSigla(), anoCurricular);
            return;
        }

        UnidadeCurricular novaUc = gestorAtivo.criarUnidadeCurricular(siglaUc, nomeUc, anoCurricular, docenteResponsavel);
        if (repositorio.adicionarUnidadeCurricular(novaUc)) {
            vincularUC(novaUc, cursoAssociado, docenteResponsavel);
            view.mostrarSucessoRegistoUC(nomeUc);
        } else {
            view.mostrarErroLimiteUCs();
        }
    }

    /**
     * Permite que uma mesma UC pertença a múltiplos cursos (ex: UC de Matemática partilhada).
     */
    private void partilharUC() {
        String siglaPartilha = view.pedirSiglaUCPartilhar();
        UnidadeCurricular ucPartilhar = encontrarUC(siglaPartilha);

        if (ucPartilhar == null) {
            view.mostrarErroUCNaoEncontrada();
            return;
        }

        if (!ucPartilhar.isAtivo()) {
            view.msgErroUCInativa();
            return;
        }

        int indexCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (indexCurso < 0 || indexCurso >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida();
            return;
        }
        Curso cursoAlvo = repositorio.getCursos()[indexCurso];

        // Previne partilhas redundantes
        if (isUCNoCurso(ucPartilhar, cursoAlvo)) {
            view.mostrarErroUCJaNoCurso();
            return;
        }

        // Verifica o limite de carga curricular do curso alvo
        if (!cursoAlvo.podeAdicionarUcNoAno(ucPartilhar.getAnoCurricular())) {
            view.mostrarErroLimiteUCsAno(cursoAlvo.getSigla(), ucPartilhar.getAnoCurricular());
            return;
        }

        cursoAlvo.adicionarUnidadeCurricular(ucPartilhar);
        ucPartilhar.adicionarCurso(cursoAlvo);
        view.mostrarSucessoPartilhaUC(ucPartilhar.getNome(), cursoAlvo.getSigla());
    }

    /**
     * Permite atualizar as características fundamentais (Nome e Ano Curricular) de uma UC.
     */
    private void alterarUC() {
        String siglaBusca = view.pedirSiglaUCAlterar();
        UnidadeCurricular ucEditar = encontrarUC(siglaBusca);

        if (ucEditar != null) {
            String novoNomeUc = view.pedirNovoNome(ucEditar.getNome());
            if (!novoNomeUc.trim().isEmpty()) ucEditar.setNome(novoNomeUc);

            String novoAnoStr = view.pedirNovoAnoCurricular(ucEditar.getAnoCurricular());
            if (!novoAnoStr.trim().isEmpty()) {
                try {
                    ucEditar.setAnoCurricular(Integer.parseInt(novoAnoStr));
                } catch (NumberFormatException e) {
                    view.mostrarErroAnoNumericoMantido();
                }
            }
            view.mostrarSucessoAtualizacao();
        } else {
            view.mostrarErroUCNaoEncontrada();
        }
    }

    /**
     * Alterna o estado de ativação de uma UC.
     * UCs que compõem a matriz de algum Curso não podem ser inativadas para não corromper planos de estudo.
     */
    private void alternarEstadoUC() {
        String sigla = view.pedirSiglaUCAlterar();
        UnidadeCurricular uc = encontrarUC(sigla);

        if (uc != null) {
            if (uc.isAtivo()) {
                boolean associada = false;
                for (int i = 0; i < uc.getCursos().length; i++) {
                    if (uc.getCursos()[i] != null) {
                        associada = true;
                        break;
                    }
                }
                if (associada) {
                    view.msgAvisoUCAssociada(uc.getSigla());
                    return;
                }
            }
            uc.setAtivo(!uc.isAtivo());
            view.msgSucessoEstadoAlterado("Unidade Curricular", uc.isAtivo());
        } else {
            view.mostrarErroUCNaoEncontrada();
        }
    }

    // =========================================================
    // 4. GESTÃO DE UTILIZADORES (ESTUDANTES E DOCENTES)
    // =========================================================

    /**
     * Apresenta o submenu dedicado à gestão de Estudantes.
     */
    private void gerirEstudantes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuEstudantes();
            switch (opcao) {
                case 1: adicionarEstudante(); break;
                case 2: alterarEstudante(); break;
                case 3: view.mostrarListaEstudantes(repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                case 4: alternarEstadoEstudante(); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    /**
     * Regista um novo Estudante no sistema.
     * O sistema gera automaticamente as credenciais (Nº Mecanográfico sequencial, Email institucional e Password)
     * e notifica o utilizador via email.
     */
    private void adicionarEstudante() {
        if (repositorio.getTotalCursos() == 0) {
            view.mostrarErroFaltaCurso();
            return;
        }

        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();
        String emailPessoal = view.pedirEmailPessoal();

        int escolhaCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida();
            return;
        }

        // Geração automática de credenciais baseadas no ano de inscrição
        int anoInscricao = repositorio.getAnoAtual();
        int numeroMecanografico = repositorio.gerarNumeroMecanografico(anoInscricao);
        String emailAcesso = GeradorEmail.gerarEmailEstudante(numeroMecanografico);
        String passGeradaEst = GeradorPassword.generatePassword();
        String passEnc = utils.Seguranca.encriptar(passGeradaEst);

        Estudante novoEstudante = new Estudante(
                numeroMecanografico, emailAcesso, passEnc, nome, nif, morada, dataNascimento, repositorio.getCursos()[escolhaCurso], anoInscricao, emailPessoal
        );

        if (repositorio.adicionarEstudante(novoEstudante)) {
            // Integração com sistema de envio de credenciais simulado
            boolean emailEnviado = ServicoEmail.enviarEmailBoasVindas(novoEstudante, passGeradaEst);
            view.mostrarStatusEmail(emailEnviado, novoEstudante.getEmailPessoal());
            view.mostrarCredenciaisCriadas("ESTUDANTE", novoEstudante.getNome(), novoEstudante.getEmail(), passGeradaEst);
        } else {
            view.mostrarErroLimiteEstudantes();
        }
    }

    /**
     * Permite ao Gestor corrigir os dados demográficos de um estudante.
     */
    private void alterarEstudante() {
        try {
            int numMec = Integer.parseInt(view.pedirNumMecEstudanteAlterar());
            Estudante estEditar = encontrarEstudante(numMec);

            if (estEditar != null) {
                view.mostrarInfoEdicao(estEditar.getNome());

                String novoNomeEst = view.pedirNovoNome(estEditar.getNome());
                if (!novoNomeEst.trim().isEmpty()) {
                    if (Validador.isNomeValido(novoNomeEst)) estEditar.setNome(novoNomeEst);
                    else view.mostrarErroNomeInvalidoMantido();
                }

                String novaMorada = view.pedirNovaMorada(estEditar.getMorada());
                if (!novaMorada.trim().isEmpty()) estEditar.setMorada(novaMorada);

                view.mostrarSucessoAtualizacao();
            } else {
                view.mostrarErroEstudanteNaoEncontrado();
            }
        } catch (NumberFormatException e) {
            view.mostrarErroNumMecNumerico();
        }
    }

    /**
     * Inativa (suspende) ou Reativa o perfil de um Estudante.
     */
    private void alternarEstadoEstudante() {
        try {
            int numMec = Integer.parseInt(view.pedirNumMecEstudanteAlterar());
            Estudante est = encontrarEstudante(numMec);
            if (est != null) {
                est.setAtivo(!est.isAtivo());
                view.msgSucessoEstadoAlterado("Estudante", est.isAtivo());
            } else {
                view.mostrarErroEstudanteNaoEncontrado();
            }
        } catch (NumberFormatException e) {
            view.mostrarErroNumMecNumerico();
        }
    }

    /**
     * Apresenta o submenu dedicado à gestão do Corpo Docente.
     */
    private void gerirDocentes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDocentes();
            switch (opcao) {
                case 1: adicionarDocente(); break;
                case 2: alterarDocente(); break;
                case 3: view.mostrarListaDocentes(repositorio.getDocentes(), repositorio.getTotalDocentes()); break;
                case 4: alternarEstadoDocente(); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    /**
     * Contrata/Regista um novo Docente.
     * Gera uma sigla única baseada no nome inserido, bem como o email institucional e password.
     */
    private void adicionarDocente() {
        String nome = validarNome();
        String siglaGerada = gerarSiglaDocente(nome);
        view.mostrarSiglaGerada(siglaGerada);

        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();
        String emailPessoal = view.pedirEmailPessoal();

        String emailAcesso = siglaGerada.toLowerCase() + "@issmf.ipp.pt";
        String passGeradaDoc = GeradorPassword.generatePassword();
        String passEnc = utils.Seguranca.encriptar(passGeradaDoc);

        Docente novoDocente = new Docente(siglaGerada, emailAcesso, passEnc, nome, nif, morada, dataNascimento, emailPessoal);

        if (repositorio.adicionarDocente(novoDocente)) {
            boolean emailEnviado = ServicoEmail.enviarEmailBoasVindas(novoDocente, passGeradaDoc);
            view.mostrarStatusEmail(emailEnviado, novoDocente.getEmailPessoal());

            view.mostrarCredenciaisCriadas("DOCENTE", novoDocente.getNome(), novoDocente.getEmail(), passGeradaDoc);
        } else {
            view.mostrarErroLimiteDocentes();
        }
    }

    /**
     * Permite corrigir dados do Docente.
     */
    private void alterarDocente() {
        String siglaDoc = view.pedirSiglaDocenteAlterar();
        Docente docEditar = encontrarDocente(siglaDoc);

        if (docEditar != null) {
            String novoNomeDoc = view.pedirNovoNome(docEditar.getNome());
            if (!novoNomeDoc.trim().isEmpty()) {
                if (Validador.isNomeValido(novoNomeDoc)) docEditar.setNome(novoNomeDoc);
                else view.mostrarErroNomeInvalidoMantido();
            }

            String novaMoradaDoc = view.pedirNovaMorada(docEditar.getMorada());
            if (!novaMoradaDoc.trim().isEmpty()) docEditar.setMorada(novaMoradaDoc);

            view.mostrarSucessoAtualizacao();
        } else {
            view.mostrarErroDocenteNaoEncontrado();
        }
    }

    /**
     * Suspende funções de um Docente.
     * Impede a inativação se o docente for o titular responsável por alguma UC.
     */
    private void alternarEstadoDocente() {
        String siglaDoc = view.pedirSiglaDocenteAlterar();
        Docente doc = encontrarDocente(siglaDoc);

        if (doc != null) {
            if (doc.isAtivo() && doc.getTotalUcsLecionadas() > 0) {
                view.msgAvisoDocenteComUCs(doc.getSigla());
                view.mostrarUcsAgregadasDocente(doc.getUcsLecionadas(), doc.getTotalUcsLecionadas());
                return;
            }
            doc.setAtivo(!doc.isAtivo());
            view.msgSucessoEstadoAlterado("Docente", doc.isAtivo());
        } else {
            view.mostrarErroDocenteNaoEncontrado();
        }
    }

    // =========================================================
    // 5. OPERAÇÕES DE SISTEMA, RELATÓRIOS E AUXILIARES
    // =========================================================

    /**
     * Executa a transição global para o próximo Ano Letivo.
     * Despoleta o fecho de pautas, processamento de transições e geração de novas propinas no repositório.
     */
    private void avancarAnoLetivo() {
        view.mostrarAvisoTransicaoAno();
        int proximoAno = repositorio.getAnoAtual() + 1;

        if (view.pedirConfirmacaoAvancoAno(proximoAno)) {
            repositorio.avancarAno();
            view.mostrarSucessoAvancoAno(repositorio.getAnoAtual());
        } else {
            view.mostrarCancelamentoAvancoAno(repositorio.getAnoAtual());
        }
    }

    /**
     * Apresenta relatórios institucionais estáticos.
     */
    private void gerirRelatorios() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuRelatorios();
            switch (opcao) {
                case 1: view.mostrarRelatorioAlunosPorCurso(repositorio.getCursos(), repositorio.getTotalCursos(), repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                case 2: view.mostrarRelatorioAlunosPorUC(repositorio.getUcs(), repositorio.getTotalUcs(), repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                case 3: view.mostrarRelatorioUCsPorCurso(repositorio.getCursos(), repositorio.getTotalCursos()); break;
                case 4: view.mostrarRelatorioCursosPorDepartamento(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos()); break;
                case 5: verEstatisticas(); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    /**
     * Calcula e apresenta métricas dinâmicas da instituição com recurso à classe utilitária de Estatísticas.
     */
    private void verEstatisticas() {
        double mediaGlobal = utils.Estatisticas.calcularMediaGlobalInstituicao(repositorio);
        String melhorAluno = utils.Estatisticas.identificarMelhorAluno(repositorio);
        Curso cursoTop = utils.Estatisticas.obterCursoComMaisAlunos(repositorio);
        view.mostrarEstatisticas(mediaGlobal, melhorAluno, cursoTop != null ? cursoTop.getNome() : null);
    }

    /**
     * Procura e lista todos os estudantes que apresentem propinas em atraso no ano letivo corrente.
     */
    private void listarAlunosComDividas() {
        Estudante[] devedores = new Estudante[repositorio.getTotalEstudantes()];
        double[] dividas = new double[repositorio.getTotalEstudantes()];
        int totalDevedores = 0;

        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.temDividas()) {
                model.bll.Propina propinaAtual = e.getPropinaDoAno(repositorio.getAnoAtual());
                devedores[totalDevedores] = e;
                dividas[totalDevedores] = (propinaAtual != null) ? propinaAtual.getValorEmDivida() : 0.0;
                totalDevedores++;
            }
        }
        view.mostrarListaDevedores(devedores, dividas, totalDevedores);
    }

    /**
     * Altera a propina anual base de um curso.
     * Afeta apenas as matrículas em anos letivos subsequentes à alteração.
     */
    private void alterarPrecoCurso() {
        int escolha = view.mostrarCursosParaPropina(repositorio.getCursos(), repositorio.getTotalCursos());
        if (escolha >= 1 && escolha <= repositorio.getTotalCursos()) {
            Curso cursoEscolhido = repositorio.getCursos()[escolha - 1];
            double novoPreco = view.pedirNovoPreco();

            if (novoPreco > 0) {
                cursoEscolhido.setValorPropinaAnual(novoPreco);
                view.mostrarSucessoAlteracaoPreco(cursoEscolhido.getSigla(), novoPreco);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroPrecoInvalido();
            }
        } else {
            view.mostrarOpcaoInvalida();
        }
    }

    // --- MÉTODOS AUXILIARES PRIVADOS (Mecanismos de Busca e Validação) ---

    private Departamento encontrarDepartamento(String sigla) {
        for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
            if (repositorio.getDepartamentos()[i].getSigla().equalsIgnoreCase(sigla)) return repositorio.getDepartamentos()[i];
        }
        return null;
    }

    private Curso encontrarCurso(String sigla) {
        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            if (repositorio.getCursos()[i].getSigla().equalsIgnoreCase(sigla)) return repositorio.getCursos()[i];
        }
        return null;
    }

    private UnidadeCurricular encontrarUC(String sigla) {
        for (int i = 0; i < repositorio.getTotalUcs(); i++) {
            if (repositorio.getUcs()[i].getSigla().equalsIgnoreCase(sigla)) return repositorio.getUcs()[i];
        }
        return null;
    }

    private Estudante encontrarEstudante(int numMec) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            if (repositorio.getEstudantes()[i].getNumeroMecanografico() == numMec) return repositorio.getEstudantes()[i];
        }
        return null;
    }

    private Docente encontrarDocente(String sigla) {
        for (int i = 0; i < repositorio.getTotalDocentes(); i++) {
            if (repositorio.getDocentes()[i].getSigla().equalsIgnoreCase(sigla)) return repositorio.getDocentes()[i];
        }
        return null;
    }

    private boolean verificarCursoBloqueado(Curso c) {
        if (c.getTotalUCs() > 0) return true;
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(c.getSigla())) return true;
        }
        return false;
    }

    private void vincularUC(UnidadeCurricular uc, Curso curso, Docente docente) {
        curso.adicionarUnidadeCurricular(uc);
        uc.adicionarCurso(curso);
        docente.adicionarUcResponsavel(uc);
        docente.adicionarUcLecionada(uc);
    }

    private boolean isUCNoCurso(UnidadeCurricular uc, Curso c) {
        for (int i = 0; i < c.getTotalUCs(); i++) {
            if (c.getUnidadesCurriculares()[i].getSigla().equalsIgnoreCase(uc.getSigla())) return true;
        }
        return false;
    }

    private String validarNome() {
        while (true) {
            String nome = view.pedirNomePessoa();
            if (Validador.isNomeValido(nome)) return nome;
            view.mostrarErroNomeInvalido();
        }
    }

    private String validarNif() {
        while (true) {
            String nif = view.pedirNif();
            if (!Validador.isNifValido(nif)) view.mostrarErroNifInvalido();
            else if (repositorio.existeNif(nif)) view.mostrarErroNifJaExiste(nif);
            else return nif;
        }
    }

    private String validarDataNascimento() {
        while (true) {
            String data = view.pedirDataNascimento();
            if (Validador.isDataNascimentoValida(data)) return data;
            view.mostrarErroDataInvalida();
        }
    }

    private String gerarSiglaDocente(String nome) {
        char primeiraLetra = nome.trim().toUpperCase().charAt(0);
        String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (true) {
            char let1 = alfabeto.charAt((int)(Math.random() * alfabeto.length()));
            char let2 = alfabeto.charAt((int)(Math.random() * alfabeto.length()));
            String sigla = "" + primeiraLetra + let1 + let2;
            if (!repositorio.existeSiglaDocente(sigla)) return sigla;
        }
    }
}