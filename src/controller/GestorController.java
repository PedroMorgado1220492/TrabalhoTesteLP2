// Ficheiro: controller/GestorController.java
package controller;

import view.GestorView;
import model.Gestor;
import model.RepositorioDados;
import model.Departamento;
import model.Curso;
import model.UnidadeCurricular;
import model.Docente;
import model.Estudante;
import utils.EmailGenerator;
import utils.PasswordGenerator;
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
            int opcao = view.mostrarMenuPrincipalGestor(); // Sugestão: adicione "Gerir Docentes" na View
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
                    gerirEstudantes();
                    break;
                case 5:
                    gerirDocentes(); // Nova opção para o Gestor criar os docentes!
                    break;
                case 6: // Ajuste o número de "Sair" na sua View conforme necessário
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
                case 1:
                    String sigla = view.pedirInputString("Sigla do Departamento");
                    String nome = view.pedirInputString("Nome do Departamento");

                    Departamento novoDep = new Departamento(sigla, nome);
                    if (repositorio.adicionarDepartamento(novoDep)) {
                        view.mostrarMensagem("Departamento '" + nome + "' guardado com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: Limite de departamentos atingido.");
                    }
                    break;
                case 2: view.mostrarMensagem("Funcionalidade em desenvolvimento."); break;
                case 3:
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
                case 4: aExecutar = false; break;
            }
        }
    }

    // --- LÓGICA DE CURSOS ---
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

                    String siglaCurso = view.pedirInputString("Sigla do Curso");
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

                    Curso novoCurso = new Curso(siglaCurso, nomeCurso, deps[escolhaIndex]);
                    if (repositorio.adicionarCurso(novoCurso)) {
                        view.mostrarMensagem("Curso '" + nomeCurso + "' adicionado com sucesso!");
                    } else {
                        view.mostrarMensagem("Erro: Limite de cursos atingido.");
                    }
                    break;
                case 2: view.mostrarMensagem("Funcionalidade em desenvolvimento."); break;
                case 3:
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
                case 4: aExecutar = false; break;
            }
        }
    }

    // --- LÓGICA DE UCS ---
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

                    String siglaUc = view.pedirInputString("Sigla da UC");
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

                    UnidadeCurricular novaUc = new UnidadeCurricular(siglaUc, nomeUc, anoCurricular, docenteResponsavel);
                    if (repositorio.adicionarUnidadeCurricular(novaUc)) {
                        cursoAssociado.adicionarUnidadeCurricular(novaUc);
                        novaUc.adicionarCurso(cursoAssociado);
                        docenteResponsavel.adicionarUcResponsavel(novaUc);
                        docenteResponsavel.adicionarUcLecionada(novaUc);
                        view.mostrarMensagem("UC '" + nomeUc + "' criada com sucesso!");
                    }
                    break;
                case 2: view.mostrarMensagem("Funcionalidade em desenvolvimento."); break;
                case 3:
                    view.mostrarMensagem("\n--- LISTA DE UCs ---");
                    UnidadeCurricular[] ucs = repositorio.getUcs();
                    for (int i = 0; i < repositorio.getTotalUcs(); i++) {
                        view.mostrarMensagem("- " + ucs[i].getSigla() + " : " + ucs[i].getNome() + " | Docente: " + ucs[i].getDocenteResponsavel().getSigla());
                    }
                    break;
                case 4: aExecutar = false; break;
            }
        }
    }

    // --- LÓGICA DE ESTUDANTES (COM VALIDAÇÕES) ---
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

                    // VALIDAÇÃO: NOME
                    String nome = "";
                    while (true) {
                        nome = view.pedirInputString("Nome do Estudante (Nome e Sobrenome)");
                        if (Validador.isNomeValido(nome)) break;
                        view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras.");
                    }

                    // VALIDAÇÃO: NIF
                    String nif = "";
                    while (true) {
                        nif = view.pedirInputString("NIF (9 dígitos)");
                        if (Validador.isNifValido(nif)) break;
                        view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
                    }

                    String morada = view.pedirInputString("Morada");

                    // VALIDAÇÃO: DATA DE NASCIMENTO
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

                    int numeroMecanografico = 1000 + repositorio.getTotalEstudantes();
                    String passwordGerada = PasswordGenerator.generatePassword();
                    int anoInscricao = repositorio.getAnoAtual();

                    Estudante novoEstudante = gestorAtivo.criarEstudante(
                            numeroMecanografico, passwordGerada, nome, nif, morada, dataNascimento, cursos[escolhaCurso], anoInscricao
                    );

                    if (repositorio.adicionarEstudante(novoEstudante)) {
                        view.mostrarMensagem("Estudante registado com sucesso! Email: " + novoEstudante.getEmail());
                    }
                    break;
                case 2: view.mostrarMensagem("Funcionalidade em desenvolvimento."); break;
                case 3:
                    view.mostrarMensagem("\n--- LISTA DE ESTUDANTES ---");
                    Estudante[] estudantes = repositorio.getEstudantes();
                    for (int i = 0; i < repositorio.getTotalEstudantes(); i++) {
                        String siglaCurso = (estudantes[i].getCurso() != null) ? estudantes[i].getCurso().getSigla() : "N/A";
                        view.mostrarMensagem("- " + estudantes[i].getNumeroMecanografico() + " : " + estudantes[i].getNome() + " | Curso: " + siglaCurso);
                    }
                    break;
                case 4: aExecutar = false; break;
            }
        }
    }

    // --- LÓGICA DE DOCENTES (COM VALIDAÇÕES) ---
    private void gerirDocentes() {
        boolean aExecutar = true;
        while (aExecutar) {
            // Pode usar um view.mostrarMenuDocentes() ou apenas listar opções diretamente
            String escolha = view.pedirInputString("\n--- GERIR DOCENTES ---\n1 - Adicionar Docente\n2 - Recuar\nOpção");

            if (escolha.equals("1")) {
                String sigla = view.pedirInputString("Sigla do Docente (3 letras, ex: ABC)");

                // VALIDAÇÃO: NOME
                String nome = "";
                while (true) {
                    nome = view.pedirInputString("Nome do Docente (Nome e Sobrenome)");
                    if (Validador.isNomeValido(nome)) break;
                    view.mostrarMensagem("Erro: O nome deve conter pelo menos nome e sobrenome.");
                }

                // VALIDAÇÃO: NIF
                String nif = "";
                while (true) {
                    nif = view.pedirInputString("NIF (9 dígitos)");
                    if (Validador.isNifValido(nif)) break;
                    view.mostrarMensagem("Erro: O NIF deve conter exatamente 9 dígitos numéricos.");
                }

                String morada = view.pedirInputString("Morada");

                // VALIDAÇÃO: DATA DE NASCIMENTO
                String dataNascimento = "";
                while (true) {
                    dataNascimento = view.pedirInputString("Data de Nascimento (DD-MM-AAAA)");
                    if (Validador.isDataNascimentoValida(dataNascimento)) break;
                    view.mostrarMensagem("Erro: A data deve respeitar o formato DD-MM-AAAA.");
                }

                String emailGerado = EmailGenerator.gerarEmailDocente(sigla);
                String passwordGerada = PasswordGenerator.generatePassword();

                Docente novoDocente = new Docente(sigla, emailGerado, passwordGerada, nome, nif, morada, dataNascimento);

                if (repositorio.adicionarDocente(novoDocente)) {
                    view.mostrarMensagem("Docente '" + nome + "' registado com sucesso! Email: " + emailGerado);
                } else {
                    view.mostrarMensagem("Erro: Limite de docentes atingido.");
                }

            } else if (escolha.equals("2")) {
                aExecutar = false;
            } else {
                view.mostrarMensagem("Opção inválida.");
            }
        }
    }
}