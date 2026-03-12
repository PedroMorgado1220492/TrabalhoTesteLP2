// Ficheiro: controller/GestorController.java
package controller;

import view.GestorView;
import model.Gestor;
import model.RepositorioDados;
import model.Departamento;
import model.Curso;
import model.Docente;
import model.UnidadeCurricular;
import model.Estudante;
import utils.EmailGenerator;
import utils.PasswordGenerator;

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
                case 1:
                    gerirDepartamentos();
                    break;
                case 2:
                    gerirCursos();
                    break;
                case 3:
                    gerirUCs();
                    break;
                case 4:
                    gerirEstudantes(); // Substitua a mensagem antiga por esta chamada
                    break;
                case 5:
                    view.mostrarMensagem("A sair do Backoffice...");
                    aExecutar = false;
                    break;
                default:
                    view.mostrarMensagem("Opção inválida.");
            }
        }
    }

    // --- LÓGICA DE DEPARTAMENTOS ---
    private void gerirDepartamentos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuDepartamentos();
            switch (opcao) {
                case 1: // Adicionar Departamento
                    String sigla = view.pedirInputString("Sigla do Departamento");
                    String nome = view.pedirInputString("Nome do Departamento");

                    Departamento novoDep = new Departamento(sigla, nome);
                    if (repositorio.adicionarDepartamento(novoDep)) {
                        view.mostrarMensagem("Departamento '" + nome + "' guardado com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: Limite de departamentos atingido.");
                    }
                    break;
                case 2: // Alterar
                    view.mostrarMensagem("Funcionalidade de alterar a ser desenvolvida.");
                    break;
                case 3: // Listar
                    view.mostrarMensagem("--- LISTA DE DEPARTAMENTOS ---");
                    if (repositorio.getTotalDepartamentos() == 0) {
                        view.mostrarMensagem("Não existem departamentos registados.");
                    } else {
                        Departamento[] deps = repositorio.getDepartamentos();
                        for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
                            view.mostrarMensagem("- " + deps[i].getSigla() + " : " + deps[i].getNome());
                        }
                    }
                    break;
                case 4: // Recuar
                    aExecutar = false;
                    break;
            }
        }
    }

    // --- LÓGICA DE CURSOS ---
    private void gerirCursos() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuCursos();
            switch (opcao) {
                case 1: // Adicionar Curso
                    if (repositorio.getTotalDepartamentos() == 0) {
                        view.mostrarMensagem("Atenção: Precisa de criar um Departamento antes de criar um Curso.");
                        break; // Sai do case e volta ao menu
                    }

                    String siglaCurso = view.pedirInputString("Sigla do Curso");
                    String nomeCurso = view.pedirInputString("Nome do Curso");

                    // 1. Listar os departamentos disponíveis
                    view.mostrarMensagem("--- Escolha o Departamento ---");
                    Departamento[] deps = repositorio.getDepartamentos();
                    for (int i = 0; i < repositorio.getTotalDepartamentos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + deps[i].getSigla() + " (" + deps[i].getNome() + ")");
                    }

                    // 2. Ler a escolha do utilizador
                    String escolhaString = view.pedirInputString("Digite o número do Departamento");
                    int escolhaIndex = Integer.parseInt(escolhaString) - 1; // Subtraímos 1 para voltar ao índice do array (0, 1, 2...)

                    // 3. Validar a escolha
                    if (escolhaIndex < 0 || escolhaIndex >= repositorio.getTotalDepartamentos()) {
                        view.mostrarMensagem("Erro: Departamento inválido. Operação cancelada.");
                        break;
                    }

                    // 4. Associar e guardar o curso
                    Departamento depAssociado = deps[escolhaIndex];

                    Curso novoCurso = new Curso(siglaCurso, nomeCurso, depAssociado);
                    if (repositorio.adicionarCurso(novoCurso)) {
                        view.mostrarMensagem("Curso '" + nomeCurso + "' adicionado com sucesso ao departamento " + depAssociado.getSigla() + "!");
                    } else {
                        view.mostrarMensagem("Erro: Limite de cursos atingido no sistema.");
                    }
                    break;
                case 2: // Alterar
                    view.mostrarMensagem("Funcionalidade de alterar a ser desenvolvida.");
                    break;
                case 3: // Listar
                    view.mostrarMensagem("--- LISTA DE CURSOS ---");
                    if (repositorio.getTotalCursos() == 0) {
                        view.mostrarMensagem("Não existem cursos registados.");
                    } else {
                        Curso[] cursos = repositorio.getCursos();
                        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                            view.mostrarMensagem("- " + cursos[i].getSigla() + " (" + cursos[i].getDepartamento().getSigla() + ")");
                        }
                    }
                    break;
                case 4: // Recuar
                    aExecutar = false;
                    break;
            }
        }
    }

    // --- LÓGICA DE UNIDADES CURRICULARES ---
    private void gerirUCs() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuUCs();
            switch (opcao) {
                case 1: // Adicionar UC
                    // Validação Inicial: Só podemos criar uma UC se houver Docentes e Cursos para a associar!
                    if (repositorio.getTotalDocentes() == 0 || repositorio.getTotalCursos() == 0) {
                        view.mostrarMensagem("Atenção: Precisa de ter pelo menos 1 Docente e 1 Curso registados no sistema para criar uma UC.");
                        break;
                    }

                    String siglaUc = view.pedirInputString("Sigla da UC");
                    String nomeUc = view.pedirInputString("Nome da UC");

                    // Pedimos o ano curricular e convertemos de String para int
                    String anoString = view.pedirInputString("Ano Curricular (1, 2 ou 3)");
                    int anoCurricular = Integer.parseInt(anoString);

                    // --- 1º Associação: Escolher o Docente Responsável ---
                    view.mostrarMensagem("\n--- Escolha o Docente Responsável ---");
                    Docente[] docentes = repositorio.getDocentes();
                    for (int i = 0; i < repositorio.getTotalDocentes(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + docentes[i].getNome() + " (" + docentes[i].getSigla() + ")");
                    }
                    int escolhaDocente = Integer.parseInt(view.pedirInputString("Número do Docente")) - 1;

                    // Validação simples da escolha
                    if (escolhaDocente < 0 || escolhaDocente >= repositorio.getTotalDocentes()) {
                        view.mostrarMensagem("Docente inválido. Operação cancelada.");
                        break;
                    }
                    Docente docenteResponsavel = docentes[escolhaDocente];

                    // --- 2º Associação: Escolher o Curso ---
                    view.mostrarMensagem("\n--- Escolha o Curso ao qual a UC pertence ---");
                    Curso[] cursos = repositorio.getCursos();
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
                    }
                    int escolhaCurso = Integer.parseInt(view.pedirInputString("Número do Curso")) - 1;

                    if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
                        view.mostrarMensagem("Curso inválido. Operação cancelada.");
                        break;
                    }
                    Curso cursoAssociado = cursos[escolhaCurso];

                    // --- Instanciação e Gravação ---
                    UnidadeCurricular novaUc = new UnidadeCurricular(siglaUc, nomeUc, anoCurricular, docenteResponsavel);

                    if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                        // Liga as pontas todas nos respetivos Arrays!
                        cursoAssociado.adicionarUnidadeCurricular(novaUc);
                        novaUc.adicionarCurso(cursoAssociado);

                        docenteResponsavel.adicionarUcResponsavel(novaUc);
                        docenteResponsavel.adicionarUcLecionada(novaUc); // Assumimos que o responsável também a leciona

                        view.mostrarMensagem("Sucesso! A UC '" + nomeUc + "' foi criada, entregue ao docente " + docenteResponsavel.getSigla() + " e associada ao curso " + cursoAssociado.getSigla() + ".");
                    } else {
                        view.mostrarMensagem("Erro: Limite de UCs atingido no sistema.");
                    }
                    break;

                case 2: // Alterar
                    view.mostrarMensagem("Funcionalidade de alterar a ser desenvolvida.");
                    break;

                case 3: // Listar UCs
                    view.mostrarMensagem("\n--- LISTA DE UNIDADES CURRICULARES ---");
                    if (repositorio.getTotalUcs() == 0) {
                        view.mostrarMensagem("Não existem UCs registadas.");
                    } else {
                        UnidadeCurricular[] ucs = repositorio.getUcs();
                        for (int i = 0; i < repositorio.getTotalUcs(); i++) {
                            view.mostrarMensagem("- " + ucs[i].getSigla() + " : " + ucs[i].getNome() +
                                    " | Ano: " + ucs[i].getAnoCurricular() +
                                    " | Docente: " + ucs[i].getDocenteResponsavel().getSigla());
                        }
                    }
                    break;

                case 4: // Recuar
                    aExecutar = false;
                    break;
            }
        }
    }

    // --- LÓGICA DE ESTUDANTES ---
    private void gerirEstudantes() {
        boolean aExecutar = true;
        while (aExecutar) {
            int opcao = view.mostrarMenuEstudantes();
            switch (opcao) {
                case 1: // Adicionar Estudante
                    // Validação: O aluno precisa de ser inscrito num curso, logo tem de existir pelo menos um!
                    if (repositorio.getTotalCursos() == 0) {
                        view.mostrarMensagem("Atenção: Precisa de criar pelo menos um Curso antes de registar Estudantes.");
                        break;
                    }

                    String nome = view.pedirInputString("Nome do Estudante");
                    String nif = view.pedirInputString("NIF");
                    String morada = view.pedirInputString("Morada");
                    String dataNascimento = view.pedirInputString("Data de Nascimento (DD-MM-AAAA)");

                    // --- Associação: Escolher o Curso ---
                    view.mostrarMensagem("\n--- Escolha o Curso a que o Aluno se vai inscrever ---");
                    Curso[] cursos = repositorio.getCursos();
                    for (int i = 0; i < repositorio.getTotalCursos(); i++) {
                        view.mostrarMensagem((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
                    }
                    int escolhaCurso = Integer.parseInt(view.pedirInputString("Número do Curso")) - 1;

                    if (escolhaCurso < 0 || escolhaCurso >= repositorio.getTotalCursos()) {
                        view.mostrarMensagem("Curso inválido. Operação cancelada.");
                        break;
                    }
                    Curso cursoAssociado = cursos[escolhaCurso];

                    // --- Gerar dados automáticos ---
                    int numeroMecanografico = 1000 + repositorio.getTotalEstudantes();
                    String passwordGerada = PasswordGenerator.generatePassword();
                    int anoInscricao = repositorio.getAnoAtual();

                    // Usamos o método do gestorAtivo que criámos no modelo!
                    Estudante novoEstudante = gestorAtivo.criarEstudante(
                            numeroMecanografico,
                            passwordGerada,
                            nome,
                            nif,
                            morada,
                            dataNascimento,
                            cursoAssociado, // Agora já passamos o curso real em vez de null!
                            anoInscricao
                    );

                    // --- Gravar na Base de Dados ---
                    if (repositorio.adicionarEstudante(novoEstudante)) {
                        view.mostrarMensagem("Sucesso! Estudante registado no ano letivo " + anoInscricao + ".");
                        view.mostrarMensagem("Nº Mecanográfico: " + numeroMecanografico);
                        view.mostrarMensagem("Email: " + novoEstudante.getEmail());
                        view.mostrarMensagem("Password: " + passwordGerada);
                        view.mostrarMensagem("Inscrito no Curso: " + cursoAssociado.getSigla());
                    } else {
                        view.mostrarMensagem("Erro: Limite de estudantes atingido no sistema.");
                    }
                    break;

                case 2: // Alterar
                    view.mostrarMensagem("Funcionalidade de alterar a ser desenvolvida.");
                    break;

                case 3: // Listar Estudantes
                    view.mostrarMensagem("\n--- LISTA DE ESTUDANTES ---");
                    if (repositorio.getTotalEstudantes() == 0) {
                        view.mostrarMensagem("Não existem estudantes registados.");
                    } else {
                        Estudante[] estudantes = repositorio.getEstudantes();
                        for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
                            String siglaCurso = (estudantes[i].getCurso() != null) ? estudantes[i].getCurso().getSigla() : "N/A";
                            view.mostrarMensagem("- " + estudantes[i].getNumeroMecanografico() + " : " + estudantes[i].getNome() +
                                    " | Curso: " + siglaCurso +
                                    " | 1ª Inscrição: " + estudantes[i].getAnoPrimeiraInscricao());
                        }
                    }
                    break;

                case 4: // Recuar
                    aExecutar = false;
                    break;
            }
        }
    }
}