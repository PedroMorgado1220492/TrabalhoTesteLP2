package controller;

import view.GestorView;
import model.Gestor;
import model.RepositorioDados;
import model.Departamento;
import model.Curso;
import model.UnidadeCurricular;
import model.Docente;
import model.Estudante;
import utils.Validador;

public class GestorController {

    // ---------- ATRIBUTOS ----------
    private GestorView view;
    private Gestor gestorAtivo;
    private RepositorioDados repositorio;

    // ---------- CONSTRUTOR ----------
    public GestorController(Gestor gestorAtivo, RepositorioDados repositorio) {
        this.view = new GestorView();
        this.gestorAtivo = gestorAtivo;
        this.repositorio = repositorio;
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Inicia o ciclo principal do Backoffice do Gestor, permitindo gerir
     * as entidades académicas do sistema (Departamentos, Cursos, UCs, Utilizadores)
     * e avançar o ano letivo.
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
                case 8:
                    view.mostrarMensagem("A sair do Backoffice...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    /**
     * Gere o processo interativo para confirmar a progressão do ano letivo
     * de todos os estudantes registados no repositório. Arquiva as notas atuais
     * e calcula as transições de ano baseado no aproveitamento.
     */
    private void avancarAnoLetivo() {
        view.mostrarMensagem("\n--- TRANSIÇÃO DE ANO LETIVO ---");
        view.mostrarMensagem("Atenção: Esta ação irá avaliar todos os alunos, subir o ano de frequência");
        view.mostrarMensagem("dos que tiverem aprovação (>= 60%) e arquivar todas as notas.");

        int proximoAno = repositorio.getAnoAtual() + 1;
        String confirmacao = view.pedirInputString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N)");

        if (confirmacao.equalsIgnoreCase("S")) {
            repositorio.avancarAno();
            view.mostrarMensagem("Sucesso! Bem-vindo ao ano letivo de " + repositorio.getAnoAtual() + ".");
        } else {
            view.mostrarMensagem("Operação cancelada. Mantemo-nos em " + repositorio.getAnoAtual() + ".");
        }
    }

    /**
     * Submenu de operações CRUD para Departamentos.
     * Permite a criação, alteração e listagem dos departamentos da instituição.
     */
    private void gerirDepartamentos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDepartamentos();
            switch (opcao) {
                case 1:
                    String sigla = "";
                    while (true) {
                        sigla = view.pedirInputString("Sigla do Departamento");
                        if (repositorio.existeSiglaDepartamento(sigla)) {
                            view.mostrarMensagem("Erro: Já existe um departamento com a sigla " + sigla + ".");
                        } else {
                            break;
                        }
                    }
                    String nome = view.pedirInputString("Nome do Departamento");

                    // Delegação da criação ao Modelo (Gestor)
                    Departamento novoDep = gestorAtivo.criarDepartamento(sigla, nome);
                    if (repositorio.adicionarDepartamento(novoDep)) {
                        view.mostrarMensagem("Departamento '" + nome + "' guardado com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: Limite de departamentos atingido.");
                    }
                    break;
                case 2: // --- ALTERAR DEPARTAMENTO ---
                    String siglaDep = view.pedirInputString("Introduza a Sigla do Departamento a alterar");
                    Departamento depEditar = null;

                    for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
                        if (repositorio.getDepartamentos()[i].getSigla().equalsIgnoreCase(siglaDep)) {
                            depEditar = repositorio.getDepartamentos()[i];
                            break;
                        }
                    }

                    if (depEditar != null) {
                        String novoNomeDep = view.pedirInputString("Novo Nome (deixe em branco para manter '" + depEditar.getNome() + "')");
                        if (!novoNomeDep.trim().isEmpty()) {
                            depEditar.setNome(novoNomeDep);
                            view.mostrarMensagem("Departamento atualizado com sucesso!");
                        } else {
                            view.mostrarMensagem("Nenhuma alteração efetuada.");
                        }
                    } else {
                        view.mostrarMensagem("Erro: Departamento não encontrado.");
                    }
                    break;
                case 3:
                    view.mostrarMensagem("\n--- LISTA DE DEPARTAMENTOS ---");
                    if (repositorio.getTotalDepartamentos() == 0) {
                        view.mostrarMensagem("Não existem departamentos registados.");
                    } else {
                        Departamento[] deps = repositorio.getDepartamentos();
                        for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
                            view.mostrarMensagem("- " + deps[i].getSigla() + " : " + deps[i].getNome());
                        }
                    }
                    break;
                case 4: aExecutar = false; break;
            }
        }
    }

    /**
     * Submenu de operações CRUD para Cursos.
     * Exige a existência prévia de Departamentos. Permite a criação, alteração e listagem.
     */
    private void gerirCursos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuCursos();
            switch (opcao) {
                case 1:
                    if (repositorio.getTotalDepartamentos() == 0) {
                        view.mostrarMensagem("Atenção: Crie um Departamento primeiro.");
                        break;
                    }

                    String siglaCurso = "";
                    while (true) {
                        siglaCurso = view.pedirInputString("Sigla do Curso");
                        if (repositorio.existeSiglaCurso(siglaCurso)) {
                            view.mostrarMensagem("Erro: Já existe um curso com a sigla " + siglaCurso + ".");
                        } else {
                            break;
                        }
                    }
                    String nomeCurso = view.pedirInputString("Nome do Curso");

                    view.mostrarMensagem("--- Escolha o Departamento ---");
                    Departamento[] deps = repositorio.getDepartamentos();
                    for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + deps[i].getSigla() + " (" + deps[i].getNome() + ")");
                    }
                    int escolhaIndex = Integer.parseInt(view.pedirInputString("Número do Departamento")) - 1;

                    if (escolhaIndex < 0 || escolhaIndex >= repositorio.getTotalDepartamentos()) {
                        view.mostrarMensagem("Erro: Departamento inválido.");
                        break;
                    }

                    // Delegação da criação ao Modelo (Gestor)
                    Curso novoCurso = gestorAtivo.criarCurso(siglaCurso, nomeCurso, deps[escolhaIndex]);
                    if (repositorio.adicionarCurso(novoCurso)) {
                        view.mostrarMensagem("Curso '" + nomeCurso + "' adicionado com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: Limite de cursos atingido.");
                    }
                    break;
                case 2: // --- ALTERAR CURSO ---
                    String siglaBusca = view.pedirInputString("Introduza a Sigla do Curso a alterar");
                    Curso cursoEditar = null;

                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        if (repositorio.getCursos()[i].getSigla().equalsIgnoreCase(siglaBusca)) {
                            cursoEditar = repositorio.getCursos()[i];
                            break;
                        }
                    }

                    if (cursoEditar != null) {
                        // Verificar se tem UCs, Professores ou Estudantes alocados
                        boolean bloqueado = false;
                        if (cursoEditar.getTotalUCs() > 0) {
                            bloqueado = true;
                        }
                        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
                            Estudante e = repositorio.getEstudantes()[i];
                            if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(cursoEditar.getSigla())) {
                                bloqueado = true;
                                break;
                            }
                        }

                        if (bloqueado) {
                            view.mostrarMensagem("Erro: O curso já tem estudantes ou professores alocados. O sistema proíbe a sua alteração!");
                        } else {
                            String novoNomeCurso = view.pedirInputString("Novo Nome (deixe em branco para manter '" + cursoEditar.getNome() + "')");
                            if (!novoNomeCurso.trim().isEmpty()) {
                                cursoEditar.setNome(novoNomeCurso);
                                view.mostrarMensagem("Curso atualizado com sucesso!");
                            } else {
                                view.mostrarMensagem("Nenhuma alteração efetuada.");
                            }
                        }
                    } else {
                        view.mostrarMensagem("Erro: Curso não encontrado.");
                    }
                    break;
                case 3:
                    view.mostrarMensagem("\n--- LISTA DE CURSOS ---");
                    if (repositorio.getTotalCursos() == 0) {
                        view.mostrarMensagem("Não existem cursos registados.");
                    } else {
                        Curso[] cursos = repositorio.getCursos();
                        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                            view.mostrarMensagem("- " + cursos[i].getSigla() + " - " + cursos[i].getNome() + " (" + cursos[i].getDepartamento().getSigla() + ")");
                        }
                    }
                    break;
                case 4: aExecutar = false; break;
            }
        }
    }

    /**
     * Submenu de operações CRUD para Unidades Curriculares.
     * Exige a existência prévia de Docentes e Cursos para estabelecer as relações.
     */
    private void gerirUCs() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuUCs();
            switch (opcao) {
                case 1:
                    if (repositorio.getTotalDocentes() == 0 || repositorio.getTotalCursos() == 0) {
                        view.mostrarMensagem("Atenção: Precisa de ter pelo menos 1 Docente e 1 Curso.");
                        break;
                    }

                    String siglaUc = "";
                    while (true) {
                        siglaUc = view.pedirInputString("Sigla da Unidade Curricular");
                        if (repositorio.existeSiglaUC(siglaUc)) {
                            view.mostrarMensagem("Erro: Já existe uma Unidade Curricular com a sigla " + siglaUc + ".");
                        } else {
                            break;
                        }
                    }
                    String nomeUc = view.pedirInputString("Nome da UC");
                    int anoCurricular = Integer.parseInt(view.pedirInputString("Ano Curricular (1, 2 ou 3)"));

                    view.mostrarMensagem("\n--- Escolha o Docente Responsável ---");
                    Docente[] docentes = repositorio.getDocentes();
                    for (int i = 0; i < repositorio.getTotalDocentes(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + docentes[i].getNome() + " (" + docentes[i].getSigla() + ")");
                    }
                    int escolhaDocente = Integer.parseInt(view.pedirInputString("Número do Docente")) - 1;

                    if (escolhaDocente < 0 || escolhaDocente >= repositorio.getTotalDocentes()) {
                        view.mostrarMensagem("Docente inválido."); break;
                    }
                    Docente docenteResponsavel = docentes[escolhaDocente];

                    view.mostrarMensagem("\n--- Escolha o Curso ---");
                    Curso[] cursos = repositorio.getCursos();
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
                    }
                    int escolhaCurso = Integer.parseInt(view.pedirInputString("Número do Curso")) - 1;

                    if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
                        view.mostrarMensagem("Curso inválido."); break;
                    }
                    Curso cursoAssociado = cursos[escolhaCurso];

                    // Limita a quantidade de UCs a 5 UCs por ano
                    if (!cursoAssociado.podeAdicionarUcNoAno(anoCurricular)) {
                        view.mostrarMensagem("Erro: O curso " + cursoAssociado.getSigla() + " já atingiu o máximo de 5 UCs no " + anoCurricular + "º ano!");
                        break; // Aborta a criação e volta ao menu
                    }

                    // Delegação da criação ao Modelo (Gestor)
                    UnidadeCurricular novaUc = gestorAtivo.criarUnidadeCurricular(siglaUc, nomeUc, anoCurricular, docenteResponsavel);
                    if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                        cursoAssociado.adicionarUnidadeCurricular(novaUc);
                        novaUc.adicionarCurso(cursoAssociado);
                        docenteResponsavel.adicionarUcResponsavel(novaUc);
                        docenteResponsavel.adicionarUcLecionada(novaUc);
                        view.mostrarMensagem("UC '" + nomeUc + "' criada com sucesso!");
                    }
                    break;

                case 2: // --- ASSOCIAR UC EXISTENTE A OUTRO CURSO ---
                    String siglaPartilha = view.pedirInputString("Introduza a Sigla da UC existente que quer partilhar");
                    UnidadeCurricular ucPartilhar = null;
                    for (int i = 0; i < repositorio.getTotalUcs(); i++) {
                        if (repositorio.getUcs()[i].getSigla().equalsIgnoreCase(siglaPartilha)) {
                            ucPartilhar = repositorio.getUcs()[i];
                            break;
                        }
                    }

                    if (ucPartilhar == null) {
                        view.mostrarMensagem("Erro: UC não encontrada.");
                        break;
                    }

                    view.mostrarMensagem("\n--- Escolha o novo Curso para associar à UC ---");
                    Curso[] cursosPartilha = repositorio.getCursos();
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + cursosPartilha[i].getNome() + " (" + cursosPartilha[i].getSigla() + ")");
                    }
                    int indexCurso = Integer.parseInt(view.pedirInputString("Número do Curso")) - 1;
                    if (indexCurso < 0 || indexCurso >= repositorio.getTotalCursos()) {
                        view.mostrarMensagem("Curso inválido."); break;
                    }
                    Curso cursoAlvo = cursosPartilha[indexCurso];

                    // Verifica se UC já existe no Curso
                    boolean jaExiste = false;
                    for(int i=0; i < cursoAlvo.getTotalUCs(); i++) {
                        if (cursoAlvo.getUnidadesCurriculares()[i].getSigla().equalsIgnoreCase(ucPartilhar.getSigla())) {
                            jaExiste = true; break;
                        }
                    }
                    if (jaExiste) {
                        view.mostrarMensagem("Erro: Esta UC já pertence a este Curso.");
                        break;
                    }

                    // Verifica regra das 5 UCs
                    if (!cursoAlvo.podeAdicionarUcNoAno(ucPartilhar.getAnoCurricular())) {
                        view.mostrarMensagem("Erro: O Curso " + cursoAlvo.getSigla() + " já atingiu o máximo de 5 UCs no " + ucPartilhar.getAnoCurricular() + "º ano!");
                        break;
                    }

                    cursoAlvo.adicionarUnidadeCurricular(ucPartilhar);
                    ucPartilhar.adicionarCurso(cursoAlvo);
                    view.mostrarMensagem("Sucesso! A Unidade Curricular de " + ucPartilhar.getNome() + " foi partilhada com " + cursoAlvo.getSigla() + ".");
                    break;

                case 3: // --- ALTERAR UC ---
                    String siglaBusca = view.pedirInputString("Introduza a Sigla da UC a alterar");
                    UnidadeCurricular ucEditar = null;

                    for (int i = 0; i < repositorio.getTotalUcs(); i++) {
                        if (repositorio.getUcs()[i].getSigla().equalsIgnoreCase(siglaBusca)) {
                            ucEditar = repositorio.getUcs()[i];
                            break;
                        }
                    }

                    if (ucEditar != null) {
                        String novoNomeUc = view.pedirInputString("Novo Nome (deixe em branco para manter '" + ucEditar.getNome() + "')");
                        if (!novoNomeUc.trim().isEmpty()) {
                            ucEditar.setNome(novoNomeUc);
                        }

                        String novoAnoStr = view.pedirInputString("Novo Ano Curricular (deixe em branco para manter '" + ucEditar.getAnoCurricular() + "')");
                        if (!novoAnoStr.trim().isEmpty()) {
                            try {
                                ucEditar.setAnoCurricular(Integer.parseInt(novoAnoStr));
                            } catch (NumberFormatException e) {
                                view.mostrarMensagem("Erro: O Ano Curricular deve ser um número. Mantido o original.");
                            }
                        }
                        view.mostrarMensagem("Unidade Curricular atualizada com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: UC não encontrada.");
                    }
                    break;
                case 4:
                    view.mostrarMensagem("\n--- LISTA DE UCs ---");
                    UnidadeCurricular[] ucs = repositorio.getUcs();
                    for (int i = 0; i < repositorio.getTotalUcs(); i++) {
                        view.mostrarMensagem("- " + ucs[i].getSigla() + " : " + ucs[i].getNome() + " | Docente: " + ucs[i].getDocenteResponsavel().getSigla());
                    }
                    break;
                case 5: aExecutar = false; break;
            }
        }
    }

    /**
     * Submenu para a gestão administrativa de Estudantes no Backoffice.
     * Permite registar manualmente um aluno e alterar a sua ficha.
     */
    private void gerirEstudantes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuEstudantes();
            switch (opcao) {
                case 1:
                    if (repositorio.getTotalCursos() == 0) {
                        view.mostrarMensagem("Atenção: Precisa de criar um Curso primeiro.");
                        break;
                    }

                    String nome = "";
                    while (true) {
                        nome = view.pedirInputString("Nome do Estudante (Nome e Sobrenome)");
                        if (Validador.isNomeValido(nome)) break;
                        view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras.");
                    }

                    String nif = "";
                    while (true) {
                        nif = view.pedirInputString("NIF (9 dígitos)");
                        if (!Validador.isNifValido(nif)) {
                            view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
                        } else if (repositorio.existeNif(nif)) {
                            view.mostrarMensagem("Erro: Já existe um utilizador registado com o NIF " + nif + ".");
                        } else {
                            break; // É válido e é único!
                        }
                    }

                    String morada = view.pedirInputString("Morada");

                    String dataNascimento = "";
                    while (true) {
                        dataNascimento = view.pedirInputString("Data de Nascimento (DD-MM-AAAA)");
                        if (Validador.isDataNascimentoValida(dataNascimento)) break;
                        view.mostrarMensagem("Erro: A data deve respeitar o formato DD-MM-AAAA.");
                    }

                    view.mostrarMensagem("\n--- Escolha o Curso ---");
                    Curso[] cursos = repositorio.getCursos();
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + cursos[i].getNome());
                    }
                    int escolhaCurso = Integer.parseInt(view.pedirInputString("Número do Curso")) - 1;
                    if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
                        view.mostrarMensagem("Curso inválido."); break;
                    }

                    int anoInscricao = repositorio.getAnoAtual();
                    int numeroMecanografico = repositorio.gerarNumeroMecanografico(anoInscricao);

                    // Delegação da criação ao Modelo (Gestor). O Gestor gera a password lá dentro.
                    Estudante novoEstudante = gestorAtivo.criarEstudante(
                            numeroMecanografico, nome, nif, morada, dataNascimento, cursos[escolhaCurso], anoInscricao
                    );

                    if (repositorio.adicionarEstudante(novoEstudante)) {
                        view.mostrarMensagem("Estudante registado com sucesso! Email: " + novoEstudante.getEmail());
                    }
                    break;
                case 2: // --- ALTERAR ESTUDANTE ---
                    try {
                        int numMec = Integer.parseInt(view.pedirInputString("Introduza o Nº Mecanográfico do Estudante a alterar"));
                        Estudante estEditar = null;

                        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
                            if (repositorio.getEstudantes()[i].getNumeroMecanografico() == numMec) {
                                estEditar = repositorio.getEstudantes()[i];
                                break;
                            }
                        }

                        if (estEditar != null) {
                            view.mostrarMensagem("A editar estudante: " + estEditar.getNome());

                            String novoNomeEst = view.pedirInputString("Novo Nome (deixe em branco para manter)");
                            if (!novoNomeEst.trim().isEmpty()) {
                                if (Validador.isNomeValido(novoNomeEst)) {
                                    estEditar.setNome(novoNomeEst);
                                } else {
                                    view.mostrarMensagem("Erro: Nome inválido. Mantido o original.");
                                }
                            }

                            String novaMorada = view.pedirInputString("Nova Morada (deixe em branco para manter)");
                            if (!novaMorada.trim().isEmpty()) {
                                estEditar.setMorada(novaMorada);
                            }

                            view.mostrarMensagem("Ficha de estudante atualizada com sucesso!");
                        } else {
                            view.mostrarMensagem("Erro: Estudante não encontrado.");
                        }
                    } catch (NumberFormatException e) {
                        view.mostrarMensagem("Erro: O número mecanográfico deve conter apenas números.");
                    }
                    break;
                case 3:
                    view.mostrarMensagem("\n--- LISTA DE ESTUDANTES ---");
                    Estudante[] estudantes = repositorio.getEstudantes();
                    for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {

                        String siglaCurso;
                        if (estudantes[i].getCurso() != null) {
                            siglaCurso = estudantes[i].getCurso().getSigla();
                        } else {
                            siglaCurso = "N/A";
                        }

                        view.mostrarMensagem("- " + estudantes[i].getNumeroMecanografico() + " : " + estudantes[i].getNome() + " | Curso: " + siglaCurso);
                    }
                    break;
                case 4: aExecutar = false; break;
            }
        }
    }

    /**
     * Submenu para a gestão administrativa de Docentes no Backoffice.
     * Permite adicionar, alterar a ficha pessoal e listar os docentes ativos.
     */
    private void gerirDocentes() {
        boolean aExecutar = true;
        while (aExecutar) {
            String escolha = view.pedirInputString("\n--- GERIR DOCENTES ---\n1 - Adicionar Docente\n2 - Alterar Docente\n3 - Listar Docentes\n4 - Recuar\nOpção");

            switch (escolha) {
                case "1":
                    String nome = "";
                    while (true) {
                        nome = view.pedirInputString("Nome do Docente (Nome e Sobrenome)");
                        if (Validador.isNomeValido(nome)) break;
                        view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome.");
                    }

                    // Geração Automática da Sigla
                    String siglaGerada = "";
                    char primeiraLetra = nome.trim().toUpperCase().charAt(0);
                    String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                    while (true) {
                        // Escolhe 2 letras aleatórias do alfabeto
                        char let1 = alfabeto.charAt((int)(Math.random() * alfabeto.length()));
                        char let2 = alfabeto.charAt((int)(Math.random() * alfabeto.length()));

                        siglaGerada = "" + primeiraLetra + let1 + let2;

                        // Garante que o sistema não gera uma sigla que já pertença a outro professor
                        if (!repositorio.existeSiglaDocente(siglaGerada)) {
                            break;
                        }
                    }
                    view.mostrarMensagem("Sigla gerada automaticamente pelo sistema: " + siglaGerada);
                    // -----------------------------------------------

                    String nif = "";
                    while (true) {
                        nif = view.pedirInputString("NIF (9 dígitos)");
                        if (!Validador.isNifValido(nif)) {
                            view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
                        } else if (repositorio.existeNif(nif)) {
                            view.mostrarMensagem("Erro: Já existe um utilizador registado com o NIF " + nif + ".");
                        } else {
                            break;
                        }
                    }

                    String morada = view.pedirInputString("Morada");

                    String dataNascimento = "";
                    while (true) {
                        dataNascimento = view.pedirInputString("Data de Nascimento (DD-MM-AAAA)");
                        if (Validador.isDataNascimentoValida(dataNascimento)) break;
                        view.mostrarMensagem("Erro: A data deve respeitar o formato DD-MM-AAAA.");
                    }

                    Docente novoDocente = gestorAtivo.criarDocente(siglaGerada, nome, nif, morada, dataNascimento);

                    if (repositorio.adicionarDocente(novoDocente)) {
                        view.mostrarMensagem("Docente '" + nome + "' registado com sucesso! Email: " + novoDocente.getEmail());
                    } else {
                        view.mostrarMensagem("Erro: Limite de docentes atingido.");
                    }
                    break;

                case "2": // --- ALTERAR DOCENTE ---
                    String siglaDoc = view.pedirInputString("Introduza a Sigla do Docente a alterar");
                    Docente docEditar = null;

                    for (int i = 0; i < repositorio.getTotalDocentes(); i++) {
                        if (repositorio.getDocentes()[i].getSigla().equalsIgnoreCase(siglaDoc)) {
                            docEditar = repositorio.getDocentes()[i];
                            break;
                        }
                    }

                    if (docEditar != null) {
                        String novoNomeDoc = view.pedirInputString("Novo Nome (deixe em branco para manter)");
                        if (!novoNomeDoc.trim().isEmpty()) {
                            if (Validador.isNomeValido(novoNomeDoc)) {
                                docEditar.setNome(novoNomeDoc);
                            } else {
                                view.mostrarMensagem("Erro: Nome inválido. Mantido o original.");
                            }
                        }

                        String novaMoradaDoc = view.pedirInputString("Nova Morada (deixe em branco para manter)");
                        if (!novaMoradaDoc.trim().isEmpty()) {
                            docEditar.setMorada(novaMoradaDoc);
                        }

                        view.mostrarMensagem("Ficha do docente atualizada com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: Docente não encontrado.");
                    }
                    break;
                case "3": // --- LISTAR DOCENTES ---
                    view.mostrarMensagem("\n--- LISTA DE DOCENTES ---");
                    if (repositorio.getTotalDocentes() == 0) {
                        view.mostrarMensagem("Não existem docentes registados.");
                    } else {
                        Docente[] docentes = repositorio.getDocentes();
                        for (int i = 0; i < repositorio.getTotalDocentes(); i++) {
                            view.mostrarMensagem("- " + docentes[i].getSigla() + " : " + docentes[i].getNome());
                        }
                    }
                    break;
                case "4":
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    /**
     * Submenu de Relatórios: Permite extrair informações cruzadas e hierárquicas
     * do Repositório (Alunos por Curso, UCs por Curso, etc).
     */
    private void gerirRelatorios() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuRelatorios();
            switch (opcao) {
                case 1: // Alunos por Curso
                    view.mostrarMensagem("\n--- ALUNOS POR CURSO ---");
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        Curso c = repositorio.getCursos()[i];
                        view.mostrarMensagem("\n[" + c.getSigla() + "] " + c.getNome() + ":");
                        boolean temAlunos = false;
                        for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
                            Estudante e = repositorio.getEstudantes()[j];
                            if (e.getCurso() != null && e.getCurso().getSigla().equals(c.getSigla())) {
                                view.mostrarMensagem("  -> " + e.getNumeroMecanografico() + " - " + e.getNome());
                                temAlunos = true;
                            }
                        }
                        if (!temAlunos) view.mostrarMensagem("  (Nenhum aluno inscrito)");
                    }
                    break;

                case 2: // Alunos por UC
                    view.mostrarMensagem("\n--- ALUNOS POR UNIDADE CURRICULAR ---");
                    for (int i = 0; i < repositorio.getTotalUcs(); i++) {
                        UnidadeCurricular uc = repositorio.getUcs()[i];
                        view.mostrarMensagem("\n[" + uc.getSigla() + "] " + uc.getNome() + ":");

                        Estudante[] alunosInscritos = repositorio.obterEstudantesPorUC(uc.getSigla());

                        if (alunosInscritos.length == 0) {
                            view.mostrarMensagem("  (Nenhum aluno inscrito)");
                        } else {
                            for (int j = 0; j < alunosInscritos.length; j++) {
                                Estudante e = alunosInscritos[j];
                                view.mostrarMensagem("  -> " + e.getNumeroMecanografico() + " - " + e.getNome());
                            }
                        }
                    }
                    break;

                case 3: // UCs por Curso
                    view.mostrarMensagem("\n--- UNIDADES CURRICULARES POR CURSO ---");
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        Curso c = repositorio.getCursos()[i];
                        view.mostrarMensagem("\n[" + c.getSigla() + "] " + c.getNome() + ":");

                        if (c.getTotalUCs() == 0) {
                            view.mostrarMensagem("  (Nenhuma UC registada neste curso)");
                        } else {
                            for (int j = 0; j < c.getTotalUCs(); j++) {
                                UnidadeCurricular uc = c.getUnidadesCurriculares()[j];
                                view.mostrarMensagem("  -> " + uc.getSigla() + " - " + uc.getNome() + " (Ano: " + uc.getAnoCurricular() + "º)");
                            }
                        }
                    }
                    break;

                case 4: // Cursos por Departamento
                    view.mostrarMensagem("\n--- CURSOS POR DEPARTAMENTO ---");
                    for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
                        Departamento d = repositorio.getDepartamentos()[i];
                        view.mostrarMensagem("\nDEPARTAMENTO: " + d.getNome());
                        if (d.getTotalCursos() == 0) {
                            view.mostrarMensagem("  (Sem Cursos registados)");
                        } else {
                            for (int j = 0; j < d.getTotalCursos(); j++) {
                                view.mostrarMensagem("  -> " + d.getCursos()[j].getSigla() + " - " + d.getCursos()[j].getNome());
                            }
                        }
                    }
                    break;

                case 5: // Estatísticas
                    view.mostrarMensagem("\n--- ESTATÍSTICAS GLOBAIS DO ISSMF ---");

                    double mediaGlobal = utils.Estatisticas.calcularMediaGlobalInstituicao(repositorio);
                    view.mostrarMensagem("Média Global da Instituição: " + mediaGlobal + " valores.");

                    String melhorAluno = utils.Estatisticas.identificarMelhorAluno(repositorio);
                    view.mostrarMensagem("Melhor Aluno(a): " + melhorAluno);

                    Curso cursoTop = utils.Estatisticas.obterCursoComMaisAlunos(repositorio);
                    if (cursoTop != null) {
                        view.mostrarMensagem("Curso mais popular: " + cursoTop.getNome());
                    } else {
                        view.mostrarMensagem("Curso mais popular: Dados insuficientes.");
                    }
                    break;

                case 6:
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }
}