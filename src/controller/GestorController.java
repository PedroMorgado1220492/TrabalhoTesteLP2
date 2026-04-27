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
import model.bll.Propina;
import utils.Seguranca;


/**
 * Controlador central responsável pela gestão de topo de todas as entidades do sistema.
 * Atua como o principal orquestrador (Polícia Sinaleiro) no padrão MVC para os utilizadores Gestores.
 * A sua responsabilidade é captar as intenções na GestorView, validar regras simples de fluxo,
 * interrogar a base de dados (RepositorioDados) e delegar as lógicas de negócio pesadas
 * para os respetivos Models (Curso, Estudante, UnidadeCurricular, etc.).
 */
public class GestorController {

    // ---------- ATRIBUTOS ----------
    private GestorView view;
    private Gestor gestorAtivo;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor do GestorController.
     * Inicializa a View específica do gestor e recebe a referência da sessão e da base de dados.
     *
     * @param gestorAtivo O gestor que está atualmente autenticado no sistema.
     * @param repositorio A referência global para a base de dados em memória.
     */
    public GestorController(Gestor gestorAtivo, RepositorioDados repositorio) {
        this.view = new GestorView();
        this.gestorAtivo = gestorAtivo;
        this.repositorio = repositorio;
    }


    // =========================================================
    // 1. FLUXO PRINCIPAL
    // =========================================================

    /**
     * Inicia o ciclo principal do menu do Backoffice (Gestor).
     * Reencaminha a navegação para os respetivos sub-menus de gestão.
     */
    public void iniciarMenuGestor() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuPrincipalGestor();
            try {
                switch (opcao) {
                    case 1: gerirDepartamentos(); break;
                    case 2: gerirCursos(); break;
                    case 3: gerirUCs(); break;
                    case 4: gerirEstudantes(); break;
                    case 5: gerirDocentes(); break;
                    case 6: gerirGestores(); break;
                    case 7: gerirRelatorios(); break;
                    case 0:
                        view.mostrarMensagemSaida();
                        aExecutar = false;
                        break;
                    default:
                        view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("do Gestor");
            }
        }
    }


    // =========================================================
    // 2. GESTÃO DE DEPARTAMENTOS
    // =========================================================

    /**
     * Gere o sub-menu de operações sobre os Departamentos.
     */
    private void gerirDepartamentos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDepartamentos();
            try {
                switch (opcao) {
                    case 1: adicionarDepartamento(); break;
                    case 2: alterarDepartamento(); break;
                    case 3: view.mostrarListaDepartamentos(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos()); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de Departamentos");
            }
        }
    }

    private void adicionarDepartamento() {
        String sigla;
        while (true) {
            sigla = view.pedirSiglaDepartamento();
            // Delega a verificação de unicidade ao repositório
            if (repositorio.existeSiglaDepartamento(sigla)) {
                view.mostrarErroSiglaJaExiste(sigla);
            } else break;
        }

        String nome = view.pedirNomeDepartamento();
        view.mostrarRevisaoDepartamento(sigla, nome);

        if (view.confirmarDados()) {
            Departamento novoDep = gestorAtivo.criarDepartamento(sigla, nome);
            if (repositorio.adicionarDepartamento(novoDep)) {
                view.mostrarSucessoRegistoDepartamento(nome);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroLimiteDepartamentos();
            }
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }

    private void alterarDepartamento() {
        if (repositorio.getTotalDepartamentos() == 0) {
            view.mostrarAvisoSemDepartamentos();
            return;
        }

        int escolha;
        while (true) {
            escolha = view.pedirEscolhaDepartamento(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos());
            if (escolha >= 0 && escolha < repositorio.getTotalDepartamentos()) {
                break;
            }
            view.mostrarOpcaoInvalida();
        }

        Departamento depEditar = repositorio.getDepartamentos()[escolha];
        view.mostrarInfoEdicao(depEditar.getNome());

        String novoNomeDep = view.pedirNovoNome(depEditar.getNome());
        if (!novoNomeDep.trim().isEmpty()) {
            depEditar.setNome(novoNomeDep);
            view.mostrarSucessoAtualizacao();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }


    // =========================================================
    // 3. GESTÃO DE CURSOS E PROPINAS
    // =========================================================

    /**
     * Gere o sub-menu de operações sobre Cursos.
     */
    private void gerirCursos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuCursos();
            try {
                switch (opcao) {
                    case 1: adicionarCurso(); break;
                    case 2: alterarCurso(); break;
                    case 3: view.mostrarListaCursos(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos(), repositorio.getCursos(), repositorio.getTotalCursos()); break;
                    case 4: alternarEstadoCurso(); break;
                    case 5: verPercursoAcademicoCurso(); break;
                    case 6: alterarPrecoCurso(); break;
                    case 7: view.mostrarRelatorioAlunosPorCurso(repositorio.getCursos(), repositorio.getTotalCursos(), repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de Cursos");
            }
        }
    }

    private void adicionarCurso() {
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
        int escolhaIndex;
        while (true) {
            escolhaIndex = view.pedirEscolhaDepartamento(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos());
            if (escolhaIndex >= 0 && escolhaIndex < repositorio.getTotalDepartamentos()) break;
            view.mostrarOpcaoInvalida();
        }
        Departamento dep = repositorio.getDepartamentos()[escolhaIndex];

        double precoInicial = view.pedirNovoPreco(1000.0); // valor sugerido 1000€
        if (precoInicial <= 0) {
            view.mostrarErroPrecoInvalido();
            return;
        }

        view.mostrarRevisaoCurso(siglaCurso, nomeCurso, dep.getSigla());

        if (view.confirmarDados()) {
            Curso novoCurso = gestorAtivo.criarCurso(siglaCurso, nomeCurso, dep);
            novoCurso.setValorPropinaAnual(precoInicial); // guarda no objeto

            if (repositorio.adicionarCurso(novoCurso)) {
                // Guarda o preço no CSV para o ano atual
                int anoAtual = repositorio.getAnoAtual();
                String[] linhas = model.dal.ImportadorCSV.lerTodasLinhasPrecos();
                String[] novasLinhas = new String[linhas.length + 1];
                System.arraycopy(linhas, 0, novasLinhas, 0, linhas.length);
                novasLinhas[novasLinhas.length - 1] = anoAtual + ";" + siglaCurso.toUpperCase() + ";" + precoInicial;
                model.dal.ExportadorCSV.escreverFicheiroPrecos(novasLinhas);
                view.mostrarSucessoRegistoCurso(nomeCurso);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroLimiteCursos();
            }
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }

    private void alterarCurso() {
        if (repositorio.getTotalCursos() == 0) {
            view.mostrarAvisoSemCursos();
            return;
        }

        int escolha;
        while (true) {
            escolha = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
            if (escolha >= 0 && escolha < repositorio.getTotalCursos()) {
                break;
            }
            view.mostrarOpcaoInvalida();
        }

        Curso cursoEditar = repositorio.getCursos()[escolha];

        // Regra de negócio (bloqueio de edição) delegada ao Model Curso
        if (cursoEditar.isBloqueado(repositorio.getEstudantes(), repositorio.getTotalEstudantes())) {
            view.mostrarErroCursoBloqueado();
        } else {
            view.mostrarInfoEdicao(cursoEditar.getNome());

            String novoNomeCurso = view.pedirNovoNome(cursoEditar.getNome());
            if (!novoNomeCurso.trim().isEmpty()) {
                cursoEditar.setNome(novoNomeCurso);
                view.mostrarSucessoAtualizacao();
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarAvisoSemAlteracao();
            }
        }
    }

    private void alternarEstadoCurso() {
        if (repositorio.getTotalCursos() == 0) {
            view.mostrarAvisoSemCursos();
            return;
        }

        int escolha;
        while (true) {
            escolha = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
            if (escolha >= 0 && escolha < repositorio.getTotalCursos()) {
                break;
            }
            view.mostrarOpcaoInvalida();
        }

        Curso curso = repositorio.getCursos()[escolha];

        // Se o curso está inativo e vai ser ativado, verificar se todas as UCs estão ativas
        if (!curso.isAtivo()) {
            if (!curso.todasUcsAtivas()) {
                view.msgCursoNaoAtivarPorUCsInativas();
                return;
            }
        }

        // Regras existentes para desativação (alunos ativos)
        if (curso.isAtivo() && !curso.podeSerDesativado(repositorio.getEstudantes(), repositorio.getTotalEstudantes())) {
            view.msgAvisoCursoComAlunosAtivos(curso.getSigla());
            return;
        }

        curso.setAtivo(!curso.isAtivo());
        view.msgSucessoEstadoAlterado("Curso", curso.isAtivo());
        model.dal.ExportadorCSV.exportarDados("bd", repositorio);
    }

    private void verPercursoAcademicoCurso() {
        if (repositorio.getTotalCursos() == 0) {
            view.mostrarAvisoSemCursos();
            return;
        }
        System.out.println("\n--- SELECIONAR CURSO ---");
        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso c = repositorio.getCursos()[i];
            if (c != null) System.out.printf("%d - [%s] %s\n", (i+1), c.getSigla(), c.getNome());
        }
        int escolha = utils.Consola.lerInt("Indique o número: ") - 1;
        if (escolha < 0 || escolha >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida();
            return;
        }
        Curso curso = repositorio.getCursos()[escolha];
        view.mostrarPercursoAcademicoCurso(curso, repositorio.getEstudantes(), repositorio.getTotalEstudantes());
    }

    /**
     * Altera o valor anual da propina de um Curso.
     */
    private void alterarPrecoCurso() {
        int escolha = view.mostrarCursosParaPropina(repositorio.getCursos(), repositorio.getTotalCursos(), repositorio.getAnoAtual());
        if (escolha == -1) return;
        if (escolha >= 1 && escolha <= repositorio.getTotalCursos()) {
            Curso cursoEscolhido = repositorio.getCursos()[escolha - 1];
            // Mostrar histórico (leitura)
            double[][] historico = model.dal.ImportadorCSV.obterHistoricoPrecos(cursoEscolhido.getSigla());
            view.mostrarHistoricoPrecosCurso(cursoEscolhido.getSigla(), historico);

            int anoAlvo = repositorio.getAnoAtual() + 1;
            double precoAntigo = model.dal.ImportadorCSV.obterPrecoCurso(cursoEscolhido.getSigla(), anoAlvo);
            double novoPreco = view.pedirNovoPreco(precoAntigo);
            if (novoPreco > 0) {
                // Ler todas as linhas do ficheiro de preços (sem cabeçalho)
                String[] linhas = model.dal.ImportadorCSV.lerTodasLinhasPrecos();
                boolean atualizado = false;
                for (int i = 0; i < linhas.length; i++) {
                    String[] p = linhas[i].split(";");
                    if (p.length >= 3 && p[1].equalsIgnoreCase(cursoEscolhido.getSigla()) && Integer.parseInt(p[0]) == anoAlvo) {
                        linhas[i] = anoAlvo + ";" + cursoEscolhido.getSigla().toUpperCase() + ";" + novoPreco;
                        atualizado = true;
                        break;
                    }
                }
                if (!atualizado) {
                    // Adicionar nova linha
                    String[] novasLinhas = new String[linhas.length + 1];
                    System.arraycopy(linhas, 0, novasLinhas, 0, linhas.length);
                    novasLinhas[novasLinhas.length - 1] = anoAlvo + ";" + cursoEscolhido.getSigla().toUpperCase() + ";" + novoPreco;
                    linhas = novasLinhas;
                }
                // Escrever de volta
                model.dal.ExportadorCSV.escreverFicheiroPrecos(linhas);
                // Atualizar o valor base do curso (para uso futuro)
                cursoEscolhido.setValorPropinaAnual(novoPreco);
                view.mostrarSucessoAlteracaoPreco(cursoEscolhido.getSigla(), novoPreco);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroPrecoInvalido();
            }
        }
    }


    // =========================================================
    // 4. GESTÃO DE UNIDADES CURRICULARES (UCs)
    // =========================================================

    /**
     * Gere o sub-menu de operações de Unidades Curriculares.
     */
    private void gerirUCs() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuUCs();
            try {
                switch (opcao) {
                    case 1: criarUC(); break;
                    case 2: partilharUC(); break;
                    case 3: alterarUC(); break;
                    case 4: view.mostrarListaUCs(repositorio.getUcs(), repositorio.getTotalUcs()); break;
                    case 5: alternarEstadoUC(); break;
                    case 6: removerUcDeCurso(); break;
                    case 7: view.mostrarRelatorioAlunosPorUC(repositorio.getUcs(), repositorio.getTotalUcs(), repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                    case 8: view.mostrarRelatorioUCsPorCurso(repositorio.getCursos(), repositorio.getTotalCursos()); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de UCs");
            }
        }
    }

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

        Docente docenteResponsavel = null;
        while (true) {
            String siglaDoc = view.pedirSiglaDocenteBusca();
            docenteResponsavel = repositorio.obterDocentePorSigla(siglaDoc);

            if (docenteResponsavel == null) {
                view.mostrarErroDocenteNaoEncontrado();
            } else if (!docenteResponsavel.isAtivo()) {
                view.mostrarErroDocenteInativo();
            } else {
                break;
            }
        }

        Curso cursoAssociado = null;
        while (true) {
            String siglaCurso = view.pedirSiglaCursoBusca();
            cursoAssociado = repositorio.obterCursoPorSigla(siglaCurso);

            if (cursoAssociado == null) {
                view.mostrarErroCursoNaoEncontrado();
            } else if (!cursoAssociado.isAtivo()) {
                view.mostrarErroCursoInativo();
            } else {
                break;
            }
        }

        // Validação da carga letiva do curso delegada ao Model
        if (!cursoAssociado.podeAdicionarUcNoAno(anoCurricular)) {
            view.mostrarErroLimiteUCsAno(cursoAssociado.getSigla(), anoCurricular);
            return;
        }

        int numAvaliacoes;
        while (true) {
            numAvaliacoes = view.pedirNumAvaliacoesUC();
            if (numAvaliacoes >= 1 && numAvaliacoes <= 3) {
                break;
            }
            view.mostrarErroNumAvaliacoes();
        }

        view.mostrarRevisaoUC(siglaUc, nomeUc, anoCurricular, docenteResponsavel.getNome(), cursoAssociado.getSigla(), numAvaliacoes);

        if (view.confirmarDados()) {
            UnidadeCurricular novaUc = gestorAtivo.criarUnidadeCurricular(siglaUc, nomeUc, anoCurricular, docenteResponsavel, numAvaliacoes);

            if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                // A própria UC encarrega-se de amarrar as referências cruzadas
                novaUc.estabelecerVinculosIniciais(cursoAssociado);
                view.mostrarSucessoRegistoUC(nomeUc);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroLimiteUCs();
            }
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }

    private void partilharUC() {
        String siglaPartilha = view.pedirSiglaUCPartilhar();
        UnidadeCurricular ucPartilhar = repositorio.obterUCPorSigla(siglaPartilha);

        if (ucPartilhar == null) {
            view.mostrarErroUCNaoEncontrada();
            return;
        }

        if (!ucPartilhar.isAtivo()) {
            view.msgErroUCInativa();
            return;
        }

        Curso cursoAlvo = null;
        while (true) {
            String siglaCurso = view.pedirSiglaCursoBusca();
            cursoAlvo = repositorio.obterCursoPorSigla(siglaCurso);

            if (cursoAlvo == null) {
                view.mostrarErroCursoNaoEncontrado();
            } else if (!cursoAlvo.isAtivo()) {
                view.mostrarErroCursoInativo();
            } else {
                break;
            }
        }

        if (cursoAlvo.temUnidadeCurricular(ucPartilhar.getSigla())) {
            view.mostrarErroUCJaNoCurso();
            return;
        }

        if (!cursoAlvo.podeAdicionarUcNoAno(ucPartilhar.getAnoCurricular())) {
            view.mostrarErroLimiteUCsAno(cursoAlvo.getSigla(), ucPartilhar.getAnoCurricular());
            return;
        }

        cursoAlvo.adicionarUnidadeCurricular(ucPartilhar);
        ucPartilhar.adicionarCurso(cursoAlvo);
        view.mostrarSucessoPartilhaUC(ucPartilhar.getNome(), cursoAlvo.getSigla());
        model.dal.ExportadorCSV.exportarDados("bd", repositorio);
    }

    private void removerUcDeCurso() {
        Curso curso = null;
        while (true) {
            String siglaCurso = view.pedirSiglaCursoBusca();
            curso = repositorio.obterCursoPorSigla(siglaCurso);
            if (curso == null) view.mostrarErroCursoNaoEncontrado();
            else break;
        }

        view.mostrarRelatorioUCsPorCurso(new Curso[]{curso}, 1);

        if (curso.getTotalUCs() == 0) return;

        String sigla = view.pedirSiglaUCRemover();

        if (curso.removerUnidadeCurricular(sigla)) {
            UnidadeCurricular uc = repositorio.obterUCPorSigla(sigla);
            if (uc != null) {
                uc.removerCurso(curso.getSigla());
            }
            view.mostrarSucessoAtualizacao();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarErroUCNaoEncontrada();
        }
    }

    private void alterarUC() {
        String siglaBusca = view.pedirSiglaUCAlterar();
        UnidadeCurricular ucEditar = repositorio.obterUCPorSigla(siglaBusca);

        if (ucEditar != null) {
            view.mostrarInfoEdicao(ucEditar.getNome());

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

            String siglaNovoDoc = view.pedirNovoDocenteUC(ucEditar.getDocenteResponsavel().getSigla());
            if (!siglaNovoDoc.trim().isEmpty()) {
                Docente novoDoc = repositorio.obterDocentePorSigla(siglaNovoDoc);
                if (novoDoc == null) {
                    view.mostrarErroDocenteNaoEncontrado();
                } else if (!novoDoc.isAtivo()) {
                    view.mostrarErroDocenteInativo();
                } else {
                    // Delega a gestão da troca de ponteiros ao Model da UC
                    ucEditar.trocarDocenteResponsavel(novoDoc);
                }
            }

            String novoNumAvStr = view.pedirNovoNumAvaliacoes(ucEditar.getNumAvaliacoes());
            if (!novoNumAvStr.trim().isEmpty()) {
                try {
                    int novoNum = Integer.parseInt(novoNumAvStr);
                    if (novoNum >= 1 && novoNum <= 3) {
                        ucEditar.setNumAvaliacoes(novoNum);
                    } else {
                        view.mostrarErroNumAvaliacoes();
                    }
                } catch (NumberFormatException e) {
                    view.mostrarErroFormatoNumericoGenerico();
                }
            }

            view.mostrarSucessoAtualizacao();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarErroUCNaoEncontrada();
        }
    }

    private void alternarEstadoUC() {
        String sigla = view.pedirSiglaUCAlterar();
        UnidadeCurricular uc = repositorio.obterUCPorSigla(sigla);

        if (uc != null) {
            // Delegação ao Model para verificar se pode ser desativada
            if (uc.isAtivo() && !uc.podeSerDesativada()) {
                view.msgAvisoUCAssociada(uc.getSigla());
                return;
            }

            uc.setAtivo(!uc.isAtivo());
            view.msgSucessoEstadoAlterado("Unidade Curricular", uc.isAtivo());
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarErroUCNaoEncontrada();
        }
    }


    // =========================================================
    // 5. GESTÃO DE ESTUDANTES E DOCENTES
    // =========================================================

    /**
     * Gere o sub-menu de operações relativas a Estudantes.
     */
    private void gerirEstudantes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuEstudantes();
            try {
                switch (opcao) {
                    case 1: adicionarEstudante(); break;
                    case 2: alterarEstudante(); break;
                    case 3: view.mostrarListaEstudantes(repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                    case 4: alternarEstadoEstudante(); break;
                    case 5: listarAlunosComDividas(); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de Estudantes");
            }
        }
    }

    private void adicionarEstudante() {
        Curso[] cursosAtivos = repositorio.obterCursosDisponiveisParaMatricula();

        if (cursosAtivos.length == 0) {
            view.mostrarErroFaltaCurso();
            return;
        }

        String nome = pedirNomeValido();
        String nif = pedirNifValido();
        String morada = view.pedirMorada();
        String dataNascimento = pedirDataNascimentoValida();
        String emailPessoal = validarEmailPessoalNoGestor();

        int escolhaCurso = view.pedirEscolhaCurso(cursosAtivos, cursosAtivos.length);
        if (escolhaCurso < 0 || escolhaCurso >= cursosAtivos.length) {
            view.mostrarOpcaoInvalida();
            return;
        }

        Curso cursoSelecionado = cursosAtivos[escolhaCurso];
        view.mostrarRevisaoEstudante(nome, nif, morada, dataNascimento, emailPessoal, cursoSelecionado.getSigla());

        if (view.confirmarDados()) {
            int anoInscricao = repositorio.getAnoAtual();
            int numeroMecanografico = repositorio.gerarNumeroMecanografico(anoInscricao);

            String emailAcesso = GeradorEmail.gerarEmailEstudante(numeroMecanografico);
            String passGeradaEst = GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passGeradaEst);

            Estudante novoEstudante = new Estudante(
                    numeroMecanografico, emailAcesso, passEnc, nome, nif, morada, dataNascimento, cursoSelecionado, anoInscricao, emailPessoal
            );

            if (repositorio.adicionarEstudante(novoEstudante)) {

                int totalNoCurso = repositorio.contarInscritosPrimeiroAno(cursoSelecionado.getSigla(), anoInscricao);

                if (totalNoCurso >= 5) {
                    ativarEstudantesPendentes(cursoSelecionado);
                    // Notifica a View que o quórum foi atingido
                    view.mostrarSucessoQuorumAtingido(cursoSelecionado.getSigla());
                } else {
                    // Notifica a View que o aluno está em espera
                    view.mostrarAvisoEstudantePendente(totalNoCurso);
                }

                boolean emailEnviado = ServicoEmail.enviarEmailBoasVindas(novoEstudante, passGeradaEst);
                view.mostrarStatusEmail(emailEnviado, novoEstudante.getEmailPessoal());
                view.mostrarCredenciaisCriadas("ESTUDANTE", novoEstudante.getNome(), novoEstudante.getEmail(), passGeradaEst);

                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroLimiteEstudantes();
            }
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }

    private void ativarEstudantesPendentes(Curso curso) {
        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.getCurso().getSigla().equals(curso.getSigla()) && !e.isAtivo()) {
                e.setAtivo(true);
                e.matricularNasUcsIniciais();
            }
        }
    }

    private void alterarEstudante() {
        try {
            int numMec = Integer.parseInt(view.pedirNumMecEstudanteAlterar());
            Estudante estEditar = repositorio.obterEstudantePorNumMec(numMec);

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
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroEstudanteNaoEncontrado();
            }
        } catch (NumberFormatException e) {
            view.mostrarErroNumMecNumerico();
        }
    }

    private void alternarEstadoEstudante() {
        try {
            int numMec = Integer.parseInt(view.pedirNumMecEstudanteAlterar());
            Estudante est = repositorio.obterEstudantePorNumMec(numMec);
            if (est != null) {
                // Se for ativar (está inativo)
                if (!est.isAtivo()) {
                    int anoAtual = repositorio.getAnoAtual();
                    // Verificar dívidas de anos anteriores
                    if (Propina.temDividasAteAno(est, anoAtual - 1, anoAtual)) {
                        double multa = 100.0;
                        String data = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        Propina.adicionarMulta(numMec, anoAtual, multa, data);
                        view.mostrarMultaAplicada(multa);
                    }
                }
                est.setAtivo(!est.isAtivo());
                view.msgSucessoEstadoAlterado("Estudante", est.isAtivo());
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroEstudanteNaoEncontrado();
            }
        } catch (NumberFormatException e) {
            view.mostrarErroNumMecNumerico();
        }
    }

    /**
     * Gere o sub-menu de operações relativas a Docentes.
     */
    private void gerirDocentes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDocentes();
            try {
                switch (opcao) {
                    case 1: adicionarDocente(); break;
                    case 2: alterarDocente(); break;
                    case 3: view.mostrarListaDocentes(repositorio.getDocentes(), repositorio.getTotalDocentes()); break;
                    case 4: alternarEstadoDocente(); break;
                    case 5: verFichaDocente(); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de Docentes");
            }
        }
    }

    private void adicionarDocente() {
        String nome = pedirNomeValido();
        String siglaGerada = repositorio.gerarSiglaDocente(nome);
        view.mostrarSiglaGerada(siglaGerada);

        String nif = pedirNifValido();
        String morada = view.pedirMorada();
        String dataNascimento = pedirDataNascimentoValida();
        String emailPessoal = view.pedirEmailPessoal();

        view.mostrarRevisaoDocente(nome, nif, morada, dataNascimento, emailPessoal, siglaGerada);

        if (view.confirmarDados()) {
            String emailAcesso = utils.GeradorEmail.gerarEmailDocente(siglaGerada);
            String passGeradaDoc = GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passGeradaDoc);

            Docente novoDocente = new Docente(siglaGerada, emailAcesso, passEnc, nome, nif, morada, dataNascimento, emailPessoal);

            if (repositorio.adicionarDocente(novoDocente)) {
                boolean emailEnviado = ServicoEmail.enviarEmailBoasVindas(novoDocente, passGeradaDoc);
                view.mostrarStatusEmail(emailEnviado, novoDocente.getEmailPessoal());

                view.mostrarCredenciaisCriadas("DOCENTE", novoDocente.getNome(), novoDocente.getEmail(), passGeradaDoc);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroLimiteDocentes();
            }
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }

    private void alterarDocente() {
        String siglaDoc = view.pedirSiglaDocenteBusca();
        Docente docEditar = repositorio.obterDocentePorSigla(siglaDoc);

        if (docEditar != null) {
            view.mostrarInfoEdicao(docEditar.getNome());

            String novoNomeDoc = view.pedirNovoNome(docEditar.getNome());
            if (!novoNomeDoc.trim().isEmpty()) {
                if (Validador.isNomeValido(novoNomeDoc)) docEditar.setNome(novoNomeDoc);
                else view.mostrarErroNomeInvalidoMantido();
            }

            String novaMoradaDoc = view.pedirNovaMorada(docEditar.getMorada());
            if (!novaMoradaDoc.trim().isEmpty()) docEditar.setMorada(novaMoradaDoc);

            String novoEmail = view.pedirNovoEmailPessoal(docEditar.getEmailPessoal());
            if (!novoEmail.trim().isEmpty()) {
                docEditar.setEmailPessoal(novoEmail);
            }

            view.mostrarSucessoAtualizacao();
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarErroDocenteNaoEncontrado();
        }
    }

    private void alternarEstadoDocente() {
        String siglaDoc = view.pedirSiglaDocenteBusca();
        Docente doc = repositorio.obterDocentePorSigla(siglaDoc);

        if (doc != null) {
            // Delegação ao Model para verificar se tem UCs a seu cargo e impedir desativação
            if (doc.isAtivo() && !doc.podeSerDesativado()) {
                view.msgAvisoDocenteComUCs(doc.getSigla());
                view.mostrarUcsAgregadasDocente(doc.getUcsLecionadas(), doc.getTotalUcsLecionadas());
                return;
            }

            doc.setAtivo(!doc.isAtivo());
            view.msgSucessoEstadoAlterado("Docente", doc.isAtivo());
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarErroDocenteNaoEncontrado();
        }
    }

    private void verFichaDocente() {
        String siglaDoc = view.pedirSiglaDocenteBusca();
        Docente docSelecionado = repositorio.obterDocentePorSigla(siglaDoc);

        if (docSelecionado != null) {
            view.mostrarFichaDocente(docSelecionado);
        } else {
            view.mostrarErroDocenteNaoEncontrado();
        }
    }


    // =========================================================
    // 6. OPERAÇÕES DE SISTEMA, ESTATÍSTICAS E RELATÓRIOS
    // =========================================================

    /**
     * Gere o sub-menu de consulta de relatórios gerenciais e estatísticas globais.
     */
    private void gerirRelatorios() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuRelatorios();
            try {
                switch (opcao) {
                    case 1: verEstatisticas(); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de Relatórios");
            }
        }
    }

    /**
     * Calcula e apresenta os KPIs da instituição (Médias, Melhor Aluno, Curso com mais aderência).
     */
    private void verEstatisticas() {
        double mediaGlobal = utils.Estatisticas.calcularMediaGlobalInstituicao(repositorio);
        String melhorAluno = utils.Estatisticas.identificarMelhorAluno(repositorio);
        Curso cursoTop = utils.Estatisticas.obterCursoComMaisAlunos(repositorio);

        view.mostrarEstatisticas(mediaGlobal, melhorAluno, cursoTop != null ? cursoTop.getNome() : null);
    }

    /**
     * Percorre o repositório de alunos e invoca a model para listar os alunos com propinas em atraso.
     */
    private void listarAlunosComDividas() {
        Estudante[] devedores = new Estudante[repositorio.getTotalEstudantes()];
        double[] dividas = new double[repositorio.getTotalEstudantes()];
        int totalDevedores = 0;
        int anoAtual = repositorio.getAnoAtual();

        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
            Estudante e = repositorio.getEstudantes()[i];
            if (e != null && e.isAtivo() && Propina.temDividas(e, anoAtual)) {
                double dividaTotal = Propina.calcularDividaTotal(e, anoAtual);
                if (dividaTotal > 0.0) {
                    devedores[totalDevedores] = e;
                    dividas[totalDevedores] = dividaTotal;
                    totalDevedores++;
                }
            }
        }
        view.mostrarListaDevedores(devedores, dividas, totalDevedores);
    }


    // =========================================================
    // 7. GESTÃO DE BACKOFFICE (GESTORES)
    // =========================================================

    /**
     * Gere o sub-menu dedicado à criação e gestão de contas da equipa de Backoffice (outros Gestores).
     */
    private void gerirGestores() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuGestores();
            try {
                switch (opcao) {
                    case 1: adicionarGestor(); break;
                    case 2: desativarGestor(); break;
                    case 3: view.mostrarListaGestores(repositorio.getGestores(), repositorio.getTotalGestores()); break;
                    case 4: alterarPassword(); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                view.mostrarCancelamento("de Gestores");
            }
        }
    }

    private void adicionarGestor() {
        String nome = pedirNomeGestorValido();
        String morada = view.pedirMorada();

        String emailGerado = utils.GeradorEmail.gerarEmailGestor(nome);

        view.mostrarRevisaoGestor(nome, morada, emailGerado);

        if (view.confirmarDados()) {
            String passRaw = utils.GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passRaw);

            Gestor novoGestor = new Gestor(emailGerado, passEnc, nome, morada);

            if (repositorio.adicionarGestor(novoGestor)) {
                view.mostrarSucessoRegistoGestor(emailGerado);

                // Dispara o email com as credenciais do novo gestor
                String destinoHardcoded = "1220492@isep.ipp.pt";
                boolean emailEnviado = utils.ServicoEmail.enviarEmailNovoGestor(emailGerado, passRaw, destinoHardcoded);
                view.mostrarStatusEmail(emailEnviado, destinoHardcoded);

                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarErroLimiteGestores();
            }
        } else {
            view.mostrarAvisoSemAlteracao();
        }
    }

    private void desativarGestor() {
        String email = view.pedirEmailGestor();

        // Regra de segurança: Um gestor não se pode desativar a si próprio no sistema
        if (email.equalsIgnoreCase(gestorAtivo.getEmail())) {
            view.mostrarErroDesativarGestorProprio();
            return;
        }

        String pass = view.pedirPasswordGestor();
        String passEnc = utils.Seguranca.encriptar(pass);

        model.bll.Utilizador alvo = repositorio.autenticar(email, passEnc);

        if (alvo != null && alvo instanceof Gestor) {
            Gestor gestorAlvo = (Gestor) alvo;

            if (!gestorAlvo.isAtivo()) {
                view.mostrarAvisoGestorDesativado();
                return;
            }

            view.mostrarAvisoDesativacaoGestor(gestorAlvo.getNome());

            if (view.confirmarDados()) {
                gestorAlvo.setAtivo(false);
                view.mostrarSucessoDesativacaoGestor();
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.mostrarAvisoSemAlteracao();
            }
        } else {
            view.mostrarErroCredenciaisGestor();
        }
    }

    private void alterarPassword() {
        String passAtualRaw = view.pedirPassAtual();
        String passAtualEnc = Seguranca.encriptar(passAtualRaw);

        if (gestorAtivo.verificarPassword(passAtualEnc)) {
            String novaPassRaw = view.pedirNovaPass();
            String confirmacaoRaw = view.pedirConfirmacaoPass();

            if (!novaPassRaw.isEmpty() && novaPassRaw.equals(confirmacaoRaw)) {
                gestorAtivo.setPassword(Seguranca.encriptar(novaPassRaw));
                view.msgSucesso();
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else {
                view.msgErroPassNaoCoincidem();
            }
        } else {
            view.msgErroPassIncorreta();
        }
    }


    // =========================================================
    // 8. MÉTODOS AUXILIARES DE FLUXO E VALIDAÇÃO CONTÍNUA
    // =========================================================

    /**
     * Garante a introdução de um Nome válido mediante as Regras de Expressão Regular do Validador.
     * Retém o fluxo na View enquanto o utilizador falhar.
     * @return String contendo um Nome válido.
     */
    private String pedirNomeValido() {
        while (true) {
            String nome = view.pedirNomePessoa();
            if (Validador.isNomeValido(nome)) return nome;
            view.mostrarErroNomeInvalido();
        }
    }

    /**
     * Garante a introdução de um NIF válido e Único no sistema.
     * @return String contendo um NIF em formato PT com 9 dígitos.
     */
    private String pedirNifValido() {
        String nif;
        while (true) {
            nif = view.pedirNif();

            if (nif.length() != 9) {
                view.mostrarErroNifFormato();
            }
            else if (repositorio.existeNif(nif)) {
                view.mostrarErroNifDuplicado();
            }
            else {
                return nif;
            }
        }
    }

    /**
     * Garante a introdução de uma Data de Nascimento no formato dd/mm/aaaa.
     * @return String formatada como data válida.
     */
    private String pedirDataNascimentoValida() {
        while (true) {
            String data = view.pedirDataNascimento();
            if (!Validador.isDataNascimentoValida(data)) {
                view.mostrarErroDataInvalida();
            } else if (!Validador.temIdadeMinima(data)) {
                view.mostrarErroIdadeMinima();
            } else {
                return data;
            }
        }
    }

    /**
     * Garante a introdução de um Nome de Gestor válido (normalmente com menos restrições que os Alunos).
     * @return String contendo o nome do gestor.
     */
    private String pedirNomeGestorValido() {
        while (true) {
            String nome = view.pedirNomeGestor();
            if (Validador.isNomeGestorValido(nome)) {
                return nome.trim();
            }
            view.mostrarErroNomeGestor();
        }
    }

    /**
     * Valida o email pessoal introduzido pelo gestor, recorrendo ao Validador.
     */
    private String validarEmailPessoalNoGestor() {
        while (true) {
            String email = view.pedirEmailPessoal();
            if (Validador.isEmailPessoalValido(email)) {
                return email;
            }
            view.mostrarErroEmailInvalido();
        }
    }

}