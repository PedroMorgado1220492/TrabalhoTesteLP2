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

    public GestorController(Gestor gestorAtivo, RepositorioDados repositorio) {
        this.view = new GestorView();
        this.gestorAtivo = gestorAtivo;
        this.repositorio = repositorio;
    }

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
                    case 6: avancarAnoLetivo(); break;
                    case 7: gerirRelatorios(); break;
                    case 8: listarAlunosComDividas(); break;
                    case 9: alterarPrecoCurso(); break;
                    case 10: gerirGestores(); break;
                    case 0:
                        view.mostrarMensagemSaida();
                        aExecutar = false;
                        break;
                    default:
                        view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu do Gestor...");
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
            try {
                switch (opcao) {
                    case 1: adicionarDepartamento(); break;
                    case 2: alterarDepartamento(); break;
                    case 3: view.mostrarListaDepartamentos(repositorio.getDepartamentos(), repositorio.getTotalDepartamentos()); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Departamentos...");
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
        // Verifica se existem departamentos antes de tentar listar
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
    // 2. GESTÃO DE CURSOS
    // =========================================================

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
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Cursos...");
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

            if (escolhaIndex >= 0 && escolhaIndex < repositorio.getTotalDepartamentos()) {
                break; // Opção correta, sai do ciclo
            }
            view.mostrarOpcaoInvalida();
        }

        Departamento dep = repositorio.getDepartamentos()[escolhaIndex];
        view.mostrarRevisaoCurso(siglaCurso, nomeCurso, dep.getSigla());

        if (view.confirmarDados()) {
            Curso novoCurso = gestorAtivo.criarCurso(siglaCurso, nomeCurso, dep);
            if (repositorio.adicionarCurso(novoCurso)) {
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
        // Verifica se existem cursos antes de tentar listar
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

        // Impede alterar cursos bloqueados
        if (verificarCursoBloqueado(cursoEditar)) {
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
        // Verifica se existem cursos antes de tentar listar
        if (repositorio.getTotalCursos() == 0) {
            view.mostrarAvisoSemCursos();
            return;
        }

        int escolha;
        // Ciclo de validação de escolha
        while (true) {
            escolha = view.pedirEscolhaCurso(repositorio.getCursos(), repositorio.getTotalCursos());

            if (escolha >= 0 && escolha < repositorio.getTotalCursos()) {
                break;
            }
            view.mostrarOpcaoInvalida();
        }

        Curso curso = repositorio.getCursos()[escolha];

        // Não permite desativar se tiver alunos ativos
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
        model.dal.ExportadorCSV.exportarDados("bd", repositorio);
    }

    private void verPercursoAcademicoCurso() {
        String siglaCurso = view.pedirSiglaCursoBusca();
        Curso curso = encontrarCurso(siglaCurso);

        if (curso != null) {
            // Manda a View imprimir os dados, enviando a lista de estudantes para ela poder contar os inscritos!
            view.mostrarPercursoAcademicoCurso(curso, repositorio.getEstudantes(), repositorio.getTotalEstudantes());
        } else {
            view.mostrarErroCursoNaoEncontrado();
        }
    }
    // =========================================================
    // 3. GESTÃO DE UCs
    // =========================================================

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
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de UCs...");
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

        // 1 e 2: Pesquisa por Sigla e valida se o Docente está Ativo
        Docente docenteResponsavel = null;
        while (true) {
            String siglaDoc = view.pedirSiglaDocenteBusca();
            docenteResponsavel = encontrarDocente(siglaDoc);

            if (docenteResponsavel == null) {
                view.mostrarErroDocenteNaoEncontrado();
            } else if (!docenteResponsavel.isAtivo()) {
                view.mostrarErroDocenteInativo(); // Impede associação de Inativos!
            } else {
                break;
            }
        }

        // 3: Pesquisa de Curso por Sigla
        Curso cursoAssociado = null;
        while (true) {
            String siglaCurso = view.pedirSiglaCursoBusca();
            cursoAssociado = encontrarCurso(siglaCurso);

            if (cursoAssociado == null) {
                view.mostrarErroCursoNaoEncontrado();
            } else if (!cursoAssociado.isAtivo()) {
                view.mostrarErroCursoInativo();
            } else {
                break;
            }
        }

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
                vincularUC(novaUc, cursoAssociado, docenteResponsavel);
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
        UnidadeCurricular ucPartilhar = encontrarUC(siglaPartilha);

        if (ucPartilhar == null) {
            view.mostrarErroUCNaoEncontrada();
            return;
        }

        if (!ucPartilhar.isAtivo()) {
            view.msgErroUCInativa();
            return;
        }

        // Substituído para pesquisa direta por Sigla do Curso
        Curso cursoAlvo = null;
        while (true) {
            String siglaCurso = view.pedirSiglaCursoBusca();
            cursoAlvo = encontrarCurso(siglaCurso);

            if (cursoAlvo == null) {
                view.mostrarErroCursoNaoEncontrado();
            } else if (!cursoAlvo.isAtivo()) {
                view.mostrarErroCursoInativo();
            } else {
                break;
            }
        }

        if (isUCNoCurso(ucPartilhar, cursoAlvo)) {
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
            curso = encontrarCurso(siglaCurso);
            if (curso == null) view.mostrarErroCursoNaoEncontrado();
            else break;
        }

        view.mostrarRelatorioUCsPorCurso(new Curso[]{curso}, 1);

        // Se o curso não tem UCs, não faz sentido pedir a sigla para remover
        if (curso.getTotalUCs() == 0) return;

        String sigla = view.pedirSiglaUCRemover();

        // 1º Remove a UC da lista do Curso
        if (curso.removerUnidadeCurricular(sigla)) {
            // 2º Remove o Curso da lista da UC (Bidirecional!)
            UnidadeCurricular uc = encontrarUC(sigla);
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
        UnidadeCurricular ucEditar = encontrarUC(siglaBusca);

        if (ucEditar != null) {
            view.mostrarInfoEdicao(ucEditar.getNome());

            // 1. Alterar Nome
            String novoNomeUc = view.pedirNovoNome(ucEditar.getNome());
            if (!novoNomeUc.trim().isEmpty()) ucEditar.setNome(novoNomeUc);

            // 2. Alterar Ano Curricular
            String novoAnoStr = view.pedirNovoAnoCurricular(ucEditar.getAnoCurricular());
            if (!novoAnoStr.trim().isEmpty()) {
                try {
                    ucEditar.setAnoCurricular(Integer.parseInt(novoAnoStr));
                } catch (NumberFormatException e) {
                    view.mostrarErroAnoNumericoMantido();
                }
            }

            // 3. Alterar Docente Responsável
            String siglaNovoDoc = view.pedirNovoDocenteUC(ucEditar.getDocenteResponsavel().getSigla());
            if (!siglaNovoDoc.trim().isEmpty()) {
                Docente novoDoc = encontrarDocente(siglaNovoDoc);
                if (novoDoc == null) {
                    view.mostrarErroDocenteNaoEncontrado();
                } else if (!novoDoc.isAtivo()) {
                    view.mostrarErroDocenteInativo();
                } else {
                    // Remove a UC do docente antigo e adiciona ao novo
                    ucEditar.getDocenteResponsavel().removerUcResponsavel(ucEditar.getSigla());
                    ucEditar.setDocenteResponsavel(novoDoc);
                    novoDoc.adicionarUcResponsavel(ucEditar);
                }
            }

            // 4. Alterar Número de Avaliações (Nova Funcionalidade)
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
                    System.out.println(">> Erro: Formato numérico inválido.");
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
        UnidadeCurricular uc = encontrarUC(sigla);

        if (uc != null) {
            // Se a UC está ativa e vamos tentar desativar, temos de garantir que NÃO TEM cursos associados
            if (uc.isAtivo()) {
                boolean temCursos = false;
                for (int i = 0; i < uc.getCursos().length; i++) {
                    if (uc.getCursos()[i] != null) {
                        temCursos = true;
                        break;
                    }
                }

                // Se ainda tiver cursos, recusa a inativação
                if (temCursos) {
                    view.msgAvisoUCAssociada(uc.getSigla());
                    return;
                }
            }

            uc.setAtivo(!uc.isAtivo());
            view.msgSucessoEstadoAlterado("Unidade Curricular", uc.isAtivo());
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarErroUCNaoEncontrada();
        }
    }

    // =========================================================
    // 4. GESTÃO DE UTILIZADORES (ESTUDANTES E DOCENTES)
    // =========================================================

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
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Estudantes...");
            }
        }
    }

    private void adicionarEstudante() {
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
            view.mostrarErroFaltaCurso();
            return;
        }

        String nome = validarNome();
        String nif = validarNif();
        String morada = view.pedirMorada();
        String dataNascimento = validarDataNascimento();
        String emailPessoal = view.pedirEmailPessoal();

        int escolhaCurso = view.pedirEscolhaCurso(cursosAtivos, totalAtivos);

        if (escolhaCurso < 0 || escolhaCurso >= totalAtivos) {
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
                vincularUcsIniciais(novoEstudante, cursoSelecionado);

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

    private void vincularUcsIniciais(Estudante est, Curso curso) {
        for (int i = 0; i < curso.getTotalUCs(); i++) {
            UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
            if (uc != null && uc.getAnoCurricular() == est.getAnoFrequencia()) {
                est.getPercursoAcademico().inscreverEmUc(uc);
            }
        }
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
            Estudante est = encontrarEstudante(numMec);
            if (est != null) {
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
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Docentes...");
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
        String emailPessoal = view.pedirEmailPessoal();

        view.mostrarRevisaoDocente(nome, nif, morada, dataNascimento, emailPessoal, siglaGerada);

        if (view.confirmarDados()) {
            String emailAcesso = siglaGerada.toLowerCase() + "@issmf.ipp.pt";
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
        Docente docEditar = encontrarDocente(siglaDoc);

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
        Docente doc = encontrarDocente(siglaDoc);

        if (doc != null) {
            // Regra de negócio: não deixa desativar se tiver UCs associadas
            if (doc.isAtivo() && doc.getTotalUcsLecionadas() > 0) {
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
        Docente docSelecionado = encontrarDocente(siglaDoc);

        if (docSelecionado != null) {
            view.mostrarFichaDocente(docSelecionado);
        } else {
            view.mostrarErroDocenteNaoEncontrado();
        }
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
            model.dal.ExportadorCSV.exportarDados("bd", repositorio);
        } else {
            view.mostrarCancelamentoAvancoAno(repositorio.getAnoAtual());
        }
    }

    private void gerirRelatorios() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuRelatorios();
            try {
                switch (opcao) {
                    case 1: view.mostrarRelatorioAlunosPorCurso(repositorio.getCursos(), repositorio.getTotalCursos(), repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                    case 2: view.mostrarRelatorioAlunosPorUC(repositorio.getUcs(), repositorio.getTotalUcs(), repositorio.getEstudantes(), repositorio.getTotalEstudantes()); break;
                    case 3: view.mostrarRelatorioUCsPorCurso(repositorio.getCursos(), repositorio.getTotalCursos()); break;
                    case 4: verEstatisticas(); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Relatórios...");
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
                double dividaTotal = 0.0;

                // Percorre todos os anos do estudante (desde o ingresso até ao ano atual) e soma as dívidas
                for (int ano = e.getAnoPrimeiraInscricao(); ano <= repositorio.getAnoAtual(); ano++) {
                    model.bll.Propina p = e.getPropinaDoAno(ano);
                    if (p != null) {
                        dividaTotal += p.getValorEmDivida();
                    }
                }

                // Só adiciona o aluno ao relatório se a dívida real for superior a 0 euros
                if (dividaTotal > 0.0) {
                    devedores[totalDevedores] = e;
                    dividas[totalDevedores] = dividaTotal;
                    totalDevedores++;
                }
            }
        }

        // Envia as arrays filtradas para a View
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
            } else {
                view.mostrarErroPrecoInvalido();
            }
        } else {
            view.mostrarOpcaoInvalida();
        }
    }

    // =========================================================
    // 6. GESTÃO DE GESTORES
    // =========================================================

    private void gerirGestores() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuGestores();
            try {
                switch (opcao) {
                    case 1: adicionarGestor(); break;
                    case 2: desativarGestor(); break;
                    case 3: view.mostrarListaGestores(repositorio.getGestores(), repositorio.getTotalGestores()); break;
                    case 0: aExecutar = false; break;
                    default: view.mostrarOpcaoInvalida();
                }
            } catch (utils.CancelamentoException e) {
                System.out.println("\n>> Operação cancelada. A regressar ao menu de Gestores...");
            }
        }
    }

    private void adicionarGestor() {
        String nome = validarNomeGestor();
        String morada = view.pedirMorada();

        String emailGerado = "backoffice_" + nome.toLowerCase() + "@issmf.ipp.pt";

        view.mostrarRevisaoGestor(nome, morada, emailGerado);

        if (view.confirmarDados()) {
            String passRaw = utils.GeradorPassword.generatePassword();
            String passEnc = utils.Seguranca.encriptar(passRaw);

            Gestor novoGestor = new Gestor(emailGerado, passEnc, nome, morada);

            if (repositorio.adicionarGestor(novoGestor)) {
                view.mostrarSucessoRegistoGestor(emailGerado);

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

        if (email.equalsIgnoreCase(gestorAtivo.getEmail())) {
            view.mostrarErroDesativarGestorProprio();
            return;
        }

        String pass = view.pedirPasswordGestor();
        String passEnc = utils.Seguranca.encriptar(pass);

        model.bll.Utilizador alvo = repositorio.autenticar(email, passEnc);

        if (alvo != null && alvo instanceof Gestor) {
            Gestor gestorAlvo = (Gestor) alvo;

            // Impede desativar alguém que já está desativado
            if (!gestorAlvo.isAtivo()) {
                System.out.println(">> Aviso: Esta conta de Gestor já se encontra desativada.");
                return;
            }

            view.mostrarAvisoDesativacaoGestor(gestorAlvo.getNome());

            if (view.confirmarDados()) {
                // Apenas muda o estado, sem apagar do Repositório ---
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

    // --- MÉTODOS AUXILIARES ---

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

    private String validarNomeGestor() {
        while (true) {
            String nome = view.pedirNomeGestor();
            if (Validador.isNomeGestorValido(nome)) {
                return nome.trim();
            }

            view.mostrarErroNomeGestor();
        }
    }
}