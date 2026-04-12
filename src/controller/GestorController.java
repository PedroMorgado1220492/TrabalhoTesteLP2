package controller;

import view.GestorView;
import model.bll.Gestor;
import model.dal.RepositorioDados;
import model.bll.Departamento;
import model.bll.Curso;
import model.bll.UnidadeCurricular;
import model.bll.Docente;
import model.bll.Estudante;
import utils.Validador;

public class GestorController {

    private GestorView view;
    private Gestor gestorAtivo;
    private RepositorioDados repositorio;

    public GestorController(Gestor gestorAtivo, RepositorioDados repositorio) {
        this.view = new GestorView();
        this.gestorAtivo = gestorAtivo;
        this.repositorio = repositorio;
    }

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

    private void adicionarDepartamento() {
        String sigla;
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
        } else view.mostrarErroLimiteDepartamentos();
    }

    private void alterarDepartamento() {
        String siglaDep = view.pedirSiglaDepartamentoAlterar();
        Departamento depEditar = encontrarDepartamento(siglaDep);

        if (depEditar != null) {
            String novoNomeDep = view.pedirNovoNome(depEditar.getNome());
            if (!novoNomeDep.isEmpty()) {
                depEditar.setNome(novoNomeDep);
                view.mostrarSucessoAtualizacao();
            } else view.mostrarAvisoSemAlteracao();
        } else view.mostrarErroDepartamentoNaoEncontrado();
    }

    // =========================================================
    // 2. GESTÃO DE CURSOS
    // =========================================================

    private void gerirCursos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuCursos();
            switch (opcao) {
                case 1: adicionarCurso(); break;
                case 2: alterarCurso(); break;
                case 3: view.mostrarListaCursos(repositorio.getCursos(), repositorio.getTotalCursos()); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
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
        int escolhaIndex = view.pedirEscolhaDepartamento(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos());

        if (escolhaIndex < 0 || escolhaIndex >= repositorio.getTotalDepartamentos()) {
            view.mostrarOpcaoInvalida();
            return;
        }

        Curso novoCurso = gestorAtivo.criarCurso(siglaCurso, nomeCurso, repositorio.getDepartamentos()[escolhaIndex]);
        if (repositorio.adicionarCurso(novoCurso)) {
            view.mostrarSucessoRegistoCurso(nomeCurso);
        } else view.mostrarErroLimiteCursos();
    }

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
                } else view.mostrarAvisoSemAlteracao();
            }
        } else view.mostrarErroCursoNaoEncontrado();
    }

    // =========================================================
    // 3. GESTÃO DE UCs
    // =========================================================

    private void gerirUCs() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuUCs();
            switch (opcao) {
                case 1: criarUC(); break;
                case 2: partilharUC(); break;
                case 3: alterarUC(); break;
                case 4: view.mostrarListaUCs(repositorio.getUcs(), repositorio.getTotalUcs()); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    private void criarUC() {
        if (repositorio.getTotalDocentes() == 0 || repositorio.getTotalCursos() == 0) {
            view.mostrarErroFaltaDocenteOuCurso(); return;
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
            view.mostrarErroAnoNumerico(); return;
        }

        int escolhaDocente = view.pedirEscolhaDocente(repositorio.getDocentes(), repositorio.getTotalDocentes());
        if (escolhaDocente < 0 || escolhaDocente >= repositorio.getTotalDocentes()) {
            view.mostrarOpcaoInvalida(); return;
        }
        Docente docenteResponsavel = repositorio.getDocentes()[escolhaDocente];

        int escolhaCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida(); return;
        }
        Curso cursoAssociado = repositorio.getCursos()[escolhaCurso];

        if (!cursoAssociado.podeAdicionarUcNoAno(anoCurricular)) {
            view.mostrarErroLimiteUCsAno(cursoAssociado.getSigla(), anoCurricular);
            return;
        }

        UnidadeCurricular novaUc = gestorAtivo.criarUnidadeCurricular(siglaUc, nomeUc, anoCurricular, docenteResponsavel);
        if (repositorio.adicionarUnidadeCurricular(novaUc)) {
            vincularUC(novaUc, cursoAssociado, docenteResponsavel);
            view.mostrarSucessoRegistoUC(nomeUc);
        } else view.mostrarErroLimiteUCs();
    }

    private void partilharUC() {
        String siglaPartilha = view.pedirSiglaUCPartilhar();
        UnidadeCurricular ucPartilhar = encontrarUC(siglaPartilha);
        if (ucPartilhar == null) { view.mostrarErroUCNaoEncontrada(); return; }

        int indexCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (indexCurso < 0 || indexCurso >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida(); return;
        }
        Curso cursoAlvo = repositorio.getCursos()[indexCurso];

        if (isUCNoCurso(ucPartilhar, cursoAlvo)) {
            view.mostrarErroUCJaNoCurso(); return;
        }
        if (!cursoAlvo.podeAdicionarUcNoAno(ucPartilhar.getAnoCurricular())) {
            view.mostrarErroLimiteUCsAno(cursoAlvo.getSigla(), ucPartilhar.getAnoCurricular());
            return;
        }

        cursoAlvo.adicionarUnidadeCurricular(ucPartilhar);
        ucPartilhar.adicionarCurso(cursoAlvo);
        view.mostrarSucessoPartilhaUC(ucPartilhar.getNome(), cursoAlvo.getSigla());
    }

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
        } else view.mostrarErroUCNaoEncontrada();
    }

    // =========================================================
    // 4. GESTÃO DE UTILIZADORES (ESTUDANTES E DOCENTES)
    // =========================================================

    private void gerirEstudantes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuEstudantes();
            switch (opcao) {
                case 1: adicionarEstudante(); break;
                case 2: alterarEstudante(); break;
                case 3: view.mostrarListaEstudantes(repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    private void adicionarEstudante() {
        if (repositorio.getTotalCursos() == 0) { view.mostrarErroFaltaCurso(); return; }

        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();

        int escolhaCurso = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());
        if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
            view.mostrarOpcaoInvalida(); return;
        }

        int anoInscricao = repositorio.getAnoAtual();
        int numeroMecanografico = repositorio.gerarNumeroMecanografico(anoInscricao);

        Estudante novoEstudante = gestorAtivo.criarEstudante(
                numeroMecanografico, nome, nif, morada, dataNascimento, repositorio.getCursos()[escolhaCurso], anoInscricao
        );

        String passGeradaEst = novoEstudante.getPassword();
        novoEstudante.setPassword(utils.Seguranca.encriptar(passGeradaEst));

        if (repositorio.adicionarEstudante(novoEstudante)) {
            view.mostrarCredenciaisCriadas("ESTUDANTE", novoEstudante.getNome(), novoEstudante.getEmail(), passGeradaEst);
        } else view.mostrarErroLimiteEstudantes();
    }

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
            } else view.mostrarErroEstudanteNaoEncontrado();
        } catch (NumberFormatException e) {
            view.mostrarErroNumMecNumerico();
        }
    }

    private void gerirDocentes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDocentes();
            switch (opcao) {
                case 1: adicionarDocente(); break;
                case 2: alterarDocente(); break;
                case 3: view.mostrarListaDocentes(repositorio.getDocentes(), repositorio.getTotalDocentes()); break;
                case 0: aExecutar = false; break;
                default: view.mostrarOpcaoInvalida();
            }
        }
    }

    private void adicionarDocente() {
        String nome = validarNome();
        String siglaGerada = gerarSiglaDocente(nome);
        view.mostrarSiglaGerada(siglaGerada);
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();

        Docente novoDocente = gestorAtivo.criarDocente(siglaGerada, nome, nif, morada, dataNascimento);
        String passGeradaDoc = novoDocente.getPassword();
        novoDocente.setPassword(utils.Seguranca.encriptar(passGeradaDoc));

        if (repositorio.adicionarDocente(novoDocente)) {
            view.mostrarCredenciaisCriadas("DOCENTE", novoDocente.getNome(), novoDocente.getEmail(), passGeradaDoc);
        } else view.mostrarErroLimiteDocentes();
    }

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
        } else view.mostrarErroDocenteNaoEncontrado();
    }

    // =========================================================
    // 5. OPERAÇÕES DE SISTEMA, RELATÓRIOS E AUXILIARES
    // =========================================================

    private void avancarAnoLetivo() {
        view.mostrarAvisoTransicaoAno();
        int proximoAno = repositorio.getAnoAtual() + 1;
        if (view.pedirConfirmacaoAvancoAno(proximoAno)) {
            repositorio.avancarAno();
            view.mostrarSucessoAvancoAno(repositorio.getAnoAtual());
        } else view.mostrarCancelamentoAvancoAno(repositorio.getAnoAtual());
    }

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

    private void verEstatisticas() {
        double mediaGlobal = utils.Estatisticas.calcularMediaGlobalInstituicao(repositorio);
        String melhorAluno = utils.Estatisticas.identificarMelhorAluno(repositorio);
        Curso cursoTop = utils.Estatisticas.obterCursoComMaisAlunos(repositorio);
        view.mostrarEstatisticas(mediaGlobal, melhorAluno, cursoTop != null ? cursoTop.getNome() : null);
    }

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

    private void alterarPrecoCurso() {
        int escolha = view.mostrarCursosParaPropina(repositorio.getCursos(), repositorio.getTotalCursos());
        if (escolha >= 1 && escolha <= repositorio.getTotalCursos()) {
            Curso cursoEscolhido = repositorio.getCursos()[escolha - 1];
            double novoPreco = view.pedirNovoPreco();
            if (novoPreco > 0) {
                cursoEscolhido.setValorPropinaAnual(novoPreco);
                view.mostrarSucessoAlteracaoPreco(cursoEscolhido.getSigla(), novoPreco);
                model.dal.ExportadorCSV.exportarDados("bd", repositorio);
            } else view.mostrarErroPrecoInvalido();
        } else view.mostrarOpcaoInvalida();
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

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