package view;

import model.bll.Curso;
import model.bll.Departamento;
import model.bll.Docente;
import model.bll.Estudante;
import model.bll.Gestor;
import model.bll.UnidadeCurricular;

/**
 * Interface de utilizador (View) destinada ao perfil de Gestor.
 * <p>
 * No padrão MVC, esta classe concentra todos os ecrãs de backoffice, formulários
 * de recolha de dados administrativos e a apresentação de relatórios gerenciais.
 * Atua estritamente na camada de apresentação via linha de comandos (CLI).
 * </p>
 */
public class GestorView {

    /**
     * Construtor por defeito da GestorView.
     */
    public GestorView() {
    }


    // =========================================================
    // 1. MENUS DE NAVEGAÇÃO PRINCIPAL E SUB-MENUS
    // =========================================================

    /**
     * Apresenta o painel de controlo principal do Gestor.
     *
     * @return A opção selecionada.
     */
    public int mostrarMenuPrincipalGestor() {
        System.out.println("\n============= BACKOFFICE - GESTÃO CENTRAL =============");
        System.out.println("1  - Gerir Departamentos");
        System.out.println("2  - Gerir Cursos");
        System.out.println("3  - Gerir Unidades Curriculares");
        System.out.println("4  - Gerir Estudantes");
        System.out.println("5  - Gerir Docentes");
        System.out.println("6  - Gerir Gestores");
        System.out.println("7  - Consultar Relatórios Académicos");
        System.out.println("0  - Sair (Logout)");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de gestão de departamentos.
     */
    public int mostrarMenuDepartamentos() {
        System.out.println("\n--- MÓDULO: DEPARTAMENTOS ---");
        System.out.println("1 - Adicionar Departamento");
        System.out.println("2 - Alterar Departamento");
        System.out.println("3 - Listar Todos os Departamentos");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de gestão de cursos.
     */
    public int mostrarMenuCursos() {
        System.out.println("\n--- MÓDULO: CURSOS ---");
        System.out.println("1 - Adicionar Curso");
        System.out.println("2 - Alterar Curso");
        System.out.println("3 - Listar Cursos por Departamento");
        System.out.println("4 - Alternar Estado (Ativo/Inativo)");
        System.out.println("5 - Consultar Percurso Académico do Curso");
        System.out.println("6 - Atualizar Preço do Curso");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de gestão de Unidades Curriculares.
     */
    public int mostrarMenuUCs() {
        System.out.println("\n--- MÓDULO: UNIDADES CURRICULARES ---");
        System.out.println("1 - Adicionar Unidade Curricular");
        System.out.println("2 - Associar UC Existente a outro Curso");
        System.out.println("3 - Alterar Unidade Curricular");
        System.out.println("4 - Listar Todas as Unidades Curriculares");
        System.out.println("5 - Alternar Estado (Ativo/Inativo)");
        System.out.println("6 - Remover Unidade Curricular de Curso");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de gestão de estudantes.
     */
    public int mostrarMenuEstudantes() {
        System.out.println("\n--- MÓDULO: ESTUDANTES ---");
        System.out.println("1 - Adicionar Estudante");
        System.out.println("2 - Atualizar Ficha do Estudante");
        System.out.println("3 - Listar Todos os Estudantes");
        System.out.println("4 - Alternar Estado (Ativo/Inativo)");
        System.out.println("5 - Listar Alunos com Dívidas");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de gestão de docentes.
     */
    public int mostrarMenuDocentes() {
        System.out.println("\n--- MÓDULO: DOCENTES ---");
        System.out.println("1 - Adicionar Docente");
        System.out.println("2 - Atualizar Ficha do Docente");
        System.out.println("3 - Listar Todos os Docentes");
        System.out.println("4 - Alternar Estado (Ativo/Inativo)");
        System.out.println("5 - Visualizar Ficha Profissional");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de relatórios e KPI.
     */
    public int mostrarMenuRelatorios() {
        System.out.println("\n--- MÓDULO: RELATÓRIOS ---");
        System.out.println("1 - Listar Alunos por Curso");
        System.out.println("2 - Listar Alunos por Unidade Curricular");
        System.out.println("3 - Listar Unidades Curriculares por Curso");
        System.out.println("4 - Consultar Estatísticas");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /**
     * Apresenta o sub‑menu de gestão de gestores (backoffice).
     */
    public int mostrarMenuGestores() {
        System.out.println("\n--- MÓDULO: EQUIPA ADMINISTRATIVA ---");
        System.out.println("1 - Adicionar Gestor");
        System.out.println("2 - Alterar Gestor para Inativo");
        System.out.println("3 - Listar Todos os Gestores");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return utils.Consola.lerOpcaoMenu();
    }


    // =========================================================
    // 2. FORMULÁRIOS DE INPUT ESPECÍFICOS
    // =========================================================

    public String pedirSiglaDepartamento() {
        return utils.Consola.lerString("Sigla do Departamento: ");
    }

    public String pedirNomeDepartamento() {
        return utils.Consola.lerString("Nome do Departamento: ");
    }

    public String pedirSiglaCurso() {
        return utils.Consola.lerString("Sigla do Curso: ");
    }

    public String pedirNomeCurso() {
        return utils.Consola.lerString("Nome do Curso: ");
    }

    public String pedirSiglaUC() {
        return utils.Consola.lerString("Sigla da Unidade Curricular: ");
    }

    public String pedirNomeUC() {
        return utils.Consola.lerString("Nome da UC: ");
    }

    public String pedirAnoCurricularUC() {
        return utils.Consola.lerString("Ano Curricular (1, 2 ou 3): ");
    }

    public int pedirNumAvaliacoesUC() {
        return utils.Consola.lerInt("Número de avaliações planeadas (1-3): ");
    }

    public String pedirSiglaUCPartilhar() {
        return utils.Consola.lerString("Sigla da UC a partilhar: ");
    }

    public String pedirSiglaUCAlterar() {
        return utils.Consola.lerString("Sigla da UC a editar: ");
    }

    public String pedirSiglaUCRemover() {
        return utils.Consola.lerString("Sigla da UC a desassociar: ");
    }

    public String pedirNomePessoa() {
        return utils.Consola.lerString("Nome Completo: ");
    }

    public String pedirNif() {
        return utils.Consola.lerString("NIF (9 dígitos): ");
    }

    public String pedirMorada() {
        return utils.Consola.lerString("Morada: ");
    }

    public String pedirDataNascimento() {
        return utils.Consola.lerString("Data de Nascimento (DD-MM-AAAA): ");
    }

    public String pedirEmailPessoal() {
        return utils.Consola.lerString("Email Pessoal: ");
    }

    public String pedirNumMecEstudanteAlterar() {
        return utils.Consola.lerString("Nº Mecanográfico do Estudante: ");
    }

    public String pedirSiglaDocenteBusca() {
        return utils.Consola.lerString("Sigla do Docente: ");
    }

    public String pedirSiglaCursoBusca() {
        return utils.Consola.lerString("Sigla do Curso: ");
    }

    public String pedirEmailGestor() {
        return utils.Consola.lerString("Email do Gestor: ");
    }

    public String pedirPasswordGestor() {
        return utils.Consola.lerString("Palavra-passe do Gestor a Desativar: ");
    }

    public String pedirNomeGestor() {
        return utils.Consola.lerString("Nome do Novo Gestor: ");
    }

    public String pedirNovoNome(String atual) {
        return utils.Consola.lerString("Novo Nome [Enter p/ manter]: ");
    }

    public String pedirNovoAnoCurricular(int atual) {
        return utils.Consola.lerString("Novo Ano [Enter p/ manter]: ");
    }

    public String pedirNovaMorada(String atual) {
        return utils.Consola.lerString("Nova Morada (Atual: " + atual + ") [Enter p/ manter]: ");
    }

    public String pedirNovoEmailPessoal(String atual) {
        return utils.Consola.lerString("Novo Email Pessoal (Atual: " + atual + ") [Enter p/ manter]: ");
    }

    public double pedirNovoPreco(double atual) {
        return utils.Consola.lerDouble("Novo valor de propina anual (Atual: " + atual + "€): ");
    }

    public String pedirNovoDocenteUC(String atual) {
        return utils.Consola.lerString("Sigla do Novo Docente [Enter p/ manter]: ");
    }

    public String pedirNovoNumAvaliacoes(int atual) {
        return utils.Consola.lerString("Novo nº de Avaliações [Enter p/ manter]: ");
    }

    /**
     * Confirmação para transição global de ano letivo.
     *
     * @param proximoAno Ano para o qual se pretende avançar.
     * @return true se o utilizador confirmar ('S'/'s'), false caso contrário.
     */
    public boolean pedirConfirmacaoAvancoAno(int proximoAno) {
        String input = utils.Consola.lerString("\nConfirmar transição para o ano " + proximoAno + "? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    /**
     * Confirmação de persistência de dados (S/N).
     */
    public boolean confirmarDados() {
        String input = utils.Consola.lerString("\nConfirmar a gravação destes dados? (S/N): ");
        return input.equalsIgnoreCase("S");
    }


    // =========================================================
    // 3. ECRÃS DE REVISÃO E FICHAS INDIVIDUAIS
    // =========================================================

    /**
     * Apresenta um resumo dos dados do departamento a criar/alterar.
     */
    public void mostrarRevisaoDepartamento(String sigla, String nome) {
        System.out.println("\n--- REVISÃO: DEPARTAMENTO ---");
        System.out.println("Sigla: " + sigla + " | Nome: " + nome);
    }

    /**
     * Apresenta um resumo dos dados do curso a criar/alterar.
     */
    public void mostrarRevisaoCurso(String sigla, String nome, String siglaDep) {
        System.out.println("\n--- REVISÃO: CURSO ---");
        System.out.println("Identificador: " + sigla + "\nDesignação: " + nome + "\nDepartamento: " + siglaDep);
    }

    /**
     * Apresenta um resumo dos dados da UC a criar/alterar.
     */
    public void mostrarRevisaoUC(String sigla, String nome, int ano, String nomeDocente, String siglaCurso, int numAv) {
        System.out.println("\n--- REVISÃO: UNIDADE CURRICULAR ---");
        System.out.printf("UC: [%s] %s | Nível: %dº Ano\nResponsável: %s | Curso Origem: %s\nNumero de Avaliações: %d\n",
                sigla, nome, ano, nomeDocente, siglaCurso, numAv);
    }

    /**
     * Apresenta um resumo dos dados do estudante a criar/alterar.
     */
    public void mostrarRevisaoEstudante(String nome, String nif, String morada, String dataNasc, String email, String siglaCurso) {
        System.out.println("\n--- REVISÃO: ESTUDANTE ---");
        System.out.println("Nome: " + nome + " | NIF: " + nif + " | Morada: " + morada);
        System.out.println("Nascimento: " + dataNasc + " | Email: " + email + " | Curso: " + siglaCurso);
    }

    /**
     * Apresenta um resumo dos dados do docente a criar/alterar.
     */
    public void mostrarRevisaoDocente(String nome, String nif, String morada, String dataNasc, String email, String sigla) {
        System.out.println("\n--- REVISÃO: DOCENTE ---");
        System.out.println("Nome: " + nome + " | NIF: " + nif + " | Morada: " + morada);
        System.out.println("Sigla Sistema: " + sigla + " | Email: " + email);
    }

    /**
     * Apresenta um resumo dos dados do gestor a criar.
     */
    public void mostrarRevisaoGestor(String nome, String morada, String emailGerado) {
        System.out.println("\n--- REVISÃO: NOVO ADMINISTRADOR ---");
        System.out.println("Nome: " + nome + " | Morada: " + morada + "\nLogin Institucional: " + emailGerado);
    }

    /**
     * Exibe a ficha profissional de um docente.
     */
    public void mostrarFichaDocente(Docente d) {
        System.out.println("\n--------- FICHA DO DOCENTE ---------");
        System.out.println("Nome          : " + d.getNome());
        System.out.println("Sigla         : " + d.getSigla());
        System.out.println("Email Inst.   : " + d.getEmail());
        System.out.println("Email Pessoal : " + d.getEmailPessoal());
        System.out.println("NIF           : " + d.getNif());
        System.out.println("Estado        : " + (d.isAtivo() ? "COLABORADOR ATIVO" : "INATIVO"));
        System.out.println("Carga Letiva  : " + d.getTotalUcsLecionadas() + " Unidades Curriculares");
        System.out.println("------------------------------------");
    }


    // =========================================================
    // 4. LISTAGENS, RELATÓRIOS E ESTATÍSTICAS
    // =========================================================

    /**
     * Lista todos os departamentos.
     */
    public void mostrarListaDepartamentos(Departamento[] deps, int total) {
        System.out.println("\n========== LISTA DE DEPARTAMENTOS ==========");
        if (total == 0) System.out.println(">> Sem registos.");
        else for (int i = 0; i < total; i++) System.out.printf("- [%s] %s\n", deps[i].getSigla(), deps[i].getNome());
    }

    /**
     * Solicita a escolha de um departamento a partir de uma lista numerada. @return índice (0‑based)
     */
    public int pedirEscolhaDepartamento(Departamento[] deps, int total) {
        System.out.println("\n--- SELECIONAR DEPARTAMENTO ---");
        for (int i = 0; i < total; i++)
            System.out.printf("%d - %s (%s)\n", (i + 1), deps[i].getSigla(), deps[i].getNome());
        return utils.Consola.lerInt("Indique o número: ") - 1;
    }

    /**
     * Lista os cursos agrupados por departamento.
     */
    public void mostrarListaCursos(Departamento[] departamentos, int totalDep, Curso[] cursos, int totalCursos) {
        System.out.println("\n============= CURSOS POR DEPARTAMENTO =============");
        if (totalCursos == 0) {
            System.out.println(">> Não existem cursos registados.");
            return;
        }
        for (int i = 0; i < totalDep; i++) {
            Departamento dep = departamentos[i];
            if (dep != null) {
                System.out.println("\nDEPARTAMENTO: " + dep.getNome());
                boolean temCursos = false;
                for (int j = 0; j < totalCursos; j++) {
                    Curso c = cursos[j];
                    if (c != null && c.getDepartamento().getSigla().equals(dep.getSigla())) {
                        temCursos = true;
                        String estado = c.isAtivo() ? "[ATIVO]" : "[INATIVO]";
                        System.out.println("  > " + estado + " " + c.getSigla() + " - " + c.getNome());
                    }
                }
                if (!temCursos) System.out.println("  (Sem cursos associados)");
            }
        }
    }

    /**
     * Solicita a escolha de um curso a partir de uma lista numerada. @return índice (0‑based)
     */
    public int pedirEscolhaCurso(Curso[] cursos, int total) {
        System.out.println("\n--- SELECIONAR CURSO ---");
        for (int i = 0; i < total; i++)
            System.out.printf("%d - [%s] %s\n", (i + 1), cursos[i].getSigla(), cursos[i].getNome());
        return utils.Consola.lerInt("Indique o número: ") - 1;
    }

    /**
     * Lista todas as Unidades Curriculares (catálogo).
     */
    public void mostrarListaUCs(UnidadeCurricular[] ucs, int total) {
        System.out.println("\n=============== CATÁLOGO DE UNIDADES CURRICULARES ===============");
        if (total == 0) {
            System.out.println(">> Sem registos.");
            return;
        }
        for (int i = 0; i < total; i++) {
            UnidadeCurricular uc = ucs[i];
            if (uc != null) {
                String doc = (uc.getDocenteResponsavel() != null) ? uc.getDocenteResponsavel().getNome() : "Sem regente";
                System.out.printf("[%s] %-25s | Ano: %d | Av: %d | Resp: %s\n",
                        (uc.isAtivo() ? "ATIVO" : "INATIVO"),
                        uc.getNome(),
                        uc.getAnoCurricular(),
                        uc.getNumAvaliacoes(),
                        doc);
            }
        }
    }

    /**
     * Lista todos os docentes (sigla e nome).
     */
    public void mostrarListaDocentes(Docente[] docs, int total) {
        System.out.println("\n========== CORPO DOCENTE ==========");
        if (total == 0) {
            System.out.println(">> Sem registos.");
        } else {
            for (int i = 0; i < total; i++) {
                String estado = docs[i].isAtivo() ? "ATIVO" : "INATIVO";
                System.out.printf("- [%s] %-5s : %s\n", estado, docs[i].getSigla(), docs[i].getNome());
            }
        }
    }

    /**
     * Lista todos os estudantes (número, nome, curso).
     */
    public void mostrarListaEstudantes(Estudante[] ests, int total) {
        System.out.println("\n========== LISTAGEM DE ESTUDANTES ==========");
        if (total == 0) {
            System.out.println(">> Sem registos.");
        } else {
            for (int i = 0; i < total; i++) {
                String estado = ests[i].isAtivo() ? "ATIVO" : "INATIVO";
                String cursoSigla = (ests[i].getCurso() != null) ? ests[i].getCurso().getSigla() : "N/A";
                System.out.printf("- [%s] %-10d : %-25s | Curso: %s\n",
                        estado,
                        ests[i].getNumeroMecanografico(),
                        ests[i].getNome(),
                        cursoSigla);
            }
        }
    }

    /**
     * Exibe os KPIs (média global, melhor aluno, curso com mais alunos).
     */
    public void mostrarEstatisticas(double media, String melhor, String cursoTop) {
        System.out.println("\n=============== KPI - MÉTRICAS INSTITUCIONAIS ===============");
        System.out.printf("Média Global da Instituição : %.2f Valores\n", media);
        System.out.println("Mérito Académico (Melhor Aluno): " + melhor);
        System.out.println("Aderência (Curso com mais inscritos): " + (cursoTop != null ? cursoTop : "N/D"));
        System.out.println("=============================================================");
    }

    /**
     * Lista os estudantes que têm dívidas activas.
     */
    public void mostrarListaDevedores(Estudante[] devs, double[] divs, int total) {
        System.out.println("\n========== ESTUDANTES COM DÍVIDAS ACTIVAS ==========");
        if (total == 0) System.out.println(">> Situação financeira global regularizada.");
        else for (int i = 0; i < total; i++)
            System.out.printf("-> %-8d %-25s | Dívida Total: %.2f€\n", devs[i].getNumeroMecanografico(), devs[i].getNome(), divs[i]);
    }

    /**
     * Mostra a lista de cursos para alteração do preçário (mostra o preço para o ano seguinte).
     *
     * @param cursos   Array de cursos.
     * @param total    Número de cursos.
     * @param anoAtual Ano letivo corrente.
     * @return Índice do curso selecionado (1‑based, como aparece na lista).
     */
    public int mostrarCursosParaPropina(Curso[] cursos, int total, int anoAtual) {
        System.out.println("\n--- ATUALIZAÇÃO DE PREÇÁRIO ---");
        int anoAlvo = anoAtual + 1;
        for (int i = 0; i < total; i++) {
            if (cursos[i] != null) {
                double precoAlvo = model.dal.ImportadorCSV.obterPrecoCurso(cursos[i].getSigla(), anoAlvo);
                System.out.printf("%d - [%s] %s (Preço para %d: %.2f€)\n", (i+1), cursos[i].getSigla(), cursos[i].getNome(), anoAlvo, precoAlvo);
            }
        }
        return utils.Consola.lerInt("Indique o curso a alterar: ");
    }

    /**
     * Apresenta o plano de estudos de um curso, com o número de alunos inscritos por UC.
     */
    public void mostrarPercursoAcademicoCurso(Curso curso, Estudante[] estudantes, int totalEstudantes) {
        System.out.println("\n============= PLANO E CARGA LECTIVA: " + curso.getNome() + " =============");
        if (curso.getTotalUCs() == 0) {
            System.out.println(">> Plano de estudos vazio.");
            return;
        }
        for (int ano = 1; ano <= 3; ano++) {
            System.out.println("\n[ " + ano + "º ANO ]");
            boolean tem = false;
            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];
                if (uc != null && uc.getAnoCurricular() == ano) {
                    tem = true;
                    int insc = 0;
                    for (int j = 0; j < totalEstudantes; j++)
                        if (estudantes[j] != null && estudantes[j].estaInscrito(uc.getSigla())) insc++;
                    System.out.printf("  -> [%s] %-25s | Inscritos: %d\n", uc.getSigla(), uc.getNome(), insc);
                }
            }
            if (!tem) System.out.println("  (Sem UCs registadas)");
        }
    }

    /**
     * Lista todos os gestores (backoffice).
     */
    public void mostrarListaGestores(Gestor[] gests, int total) {
        System.out.println("\n========== EQUIPA DE BACKOFFICE ==========");
        if (total == 0) {
            System.out.println(">> Sem registos.");
        } else {
            System.out.printf("%-25s | %-35s | %s\n", "NOME", "LOGIN", "MORADA");
            System.out.println("---------------------------------------------------------------------------------------");
            for (int i = 0; i < total; i++) {
                if (gests[i] != null) {
                    String estado = gests[i].isAtivo() ? "ATIVO" : "INATIVO";
                    System.out.printf("- [%s] %-20s | %-35s | %s\n",
                            estado,
                            gests[i].getNome(),
                            gests[i].getEmail(),
                            gests[i].getMorada());
                }
            }
        }
    }

    /**
     * Relatório: lista de alunos por curso.
     */
    public void mostrarRelatorioAlunosPorCurso(Curso[] cursos, int totalC, Estudante[] ests, int totalE) {
        System.out.println("\n========== ESTUDANTES POR CURSO ==========");
        for (int i = 0; i < totalC; i++) {
            System.out.println("\nCURSO: " + cursos[i].getNome());
            boolean tem = false;
            for (int j = 0; j < totalE; j++) {
                if (ests[j].getCurso() != null && ests[j].getCurso().getSigla().equals(cursos[i].getSigla())) {
                    tem = true;
                    System.out.println("  -> " + ests[j].getNumeroMecanografico() + " - " + ests[j].getNome());
                }
            }
            if (!tem) System.out.println("  (Vazio)");
        }
    }

    /**
     * Relatório: lista de alunos por Unidade Curricular.
     */
    public void mostrarRelatorioAlunosPorUC(UnidadeCurricular[] ucs, int totalU, Estudante[] ests, int totalE) {
        System.out.println("\n========== ESTUDANTES POR UNIDADE CURRICULAR ==========");
        for (int i = 0; i < totalU; i++) {
            System.out.println("\nUC: " + ucs[i].getNome());
            boolean tem = false;
            for (int j = 0; j < totalE; j++) {
                if (ests[j].estaInscrito(ucs[i].getSigla())) {
                    tem = true;
                    System.out.println("  -> " + ests[j].getNumeroMecanografico() + " - " + ests[j].getNome());
                }
            }
            if (!tem) System.out.println("  (Vazio)");
        }
    }

    /**
     * Relatório: lista de UCs por curso (matriz curricular).
     */
    public void mostrarRelatorioUCsPorCurso(Curso[] cursos, int total) {
        System.out.println("\n========== MATRIZES CURRICULARES ==========");
        for (int i = 0; i < total; i++) {
            System.out.println("\nCURSO: " + cursos[i].getNome());
            if (cursos[i].getTotalUCs() == 0) System.out.println("  (Vazio)");
            else for (int j = 0; j < cursos[i].getTotalUCs(); j++)
                System.out.println("  -> " + cursos[i].getUnidadesCurriculares()[j].getSigla() + " (Ano: " + cursos[i].getUnidadesCurriculares()[j].getAnoCurricular() + ")");
        }
    }

    // =========================================================
    // 5. MENSAGENS DE FEEDBACK E ESTADO
    // =========================================================

    public void mostrarMensagemSaida() {
        System.out.println(">> A terminar sessão administrativa...");
    }

    public void mostrarOpcaoInvalida() {
        System.out.println(">> Erro: Opção inexistente no menu.");
    }

    public void mostrarAvisoSemAlteracao() {
        System.out.println(">> Nenhuma alteração foi guardada.");
    }

    public void mostrarSucessoAtualizacao() {
        System.out.println(">> Alterações realizadas com êxito.");
    }

    public void mostrarInfoEdicao(String nome) {
        System.out.println(">> Modo Edição: " + nome);
    }

    // Erros de limite/validação
    public void mostrarErroSiglaJaExiste(String s) {
        System.out.println(">> Erro: A sigla '" + s + "' já está em uso.");
    }

    public void mostrarErroLimiteDepartamentos() {
        System.out.println(">> Erro: Capacidade máxima de departamentos esgotada.");
    }

    public void mostrarErroLimiteCursos() {
        System.out.println(">> Erro: Capacidade máxima de cursos esgotada.");
    }

    public void mostrarErroLimiteUCs() {
        System.out.println(">> Erro: Capacidade máxima de UCs esgotada.");
    }

    public void mostrarErroLimiteEstudantes() {
        System.out.println(">> Erro: Capacidade máxima de estudantes esgotada.");
    }

    public void mostrarErroLimiteDocentes() {
        System.out.println(">> Erro: Capacidade máxima de docentes esgotada.");
    }

    public void mostrarErroLimiteGestores() {
        System.out.println(">> Erro: Capacidade máxima de gestores esgotada.");
    }

    public void mostrarAvisoSemDepartamentos() {
        System.out.println(">> Aviso: Não existem departamentos para gerir.");
    }

    public void mostrarAvisoSemCursos() {
        System.out.println(">> Aviso: Não existem cursos para gerir.");
    }

    public void mostrarErroUCNaoEncontrada() {
        System.out.println(">> Erro: Unidade Curricular inexistente.");
    }

    public void mostrarErroEstudanteNaoEncontrado() {
        System.out.println(">> Erro: Estudante inexistente.");
    }

    public void mostrarErroDocenteNaoEncontrado() {
        System.out.println(">> Erro: Docente inexistente.");
    }

    public void mostrarErroCursoNaoEncontrado() {
        System.out.println(">> Erro: Curso inexistente.");
    }

    public void mostrarSucessoRegistoDepartamento(String n) {
        System.out.println(">> Sucesso: Departamento '" + n + "' registado.");
    }

    public void mostrarSucessoRegistoCurso(String n) {
        System.out.println(">> Sucesso: Curso '" + n + "' registado.");
    }

    public void mostrarSucessoRegistoUC(String n) {
        System.out.println(">> Sucesso: UC '" + n + "' registada.");
    }

    public void mostrarSucessoRegistoGestor(String e) {
        System.out.println(">> Sucesso: Novo Administrador criado (" + e + ").");
    }

    public void mostrarErroFaltaDepartamento() {
        System.out.println(">> Erro: Acção impossível sem Departamentos registados.");
    }

    public void mostrarErroFaltaCurso() {
        System.out.println(">> Erro: Acção impossível sem Cursos registados.");
    }

    public void mostrarErroFaltaDocenteOuCurso() {
        System.out.println(">> Erro: Requer pelo menos 1 Docente e 1 Curso.");
    }

    public void mostrarErroCursoBloqueado() {
        System.out.println(">> Erro: Curso em funcionamento. Alterações estruturais proibidas.");
    }

    public void mostrarErroAnoNumerico() {
        System.out.println(">> Erro: O ano deve ser um valor inteiro (1, 2 ou 3).");
    }

    public void mostrarErroAnoNumericoMantido() {
        System.out.println(">> Erro: Formato inválido. Valor original mantido.");
    }

    public void mostrarErroLimiteUCsAno(String c, int a) {
        System.out.println(">> Erro: Curso " + c + " excedeu o teto de 5 UCs no " + a + "º ano.");
    }

    public void mostrarErroUCJaNoCurso() {
        System.out.println(">> Erro: Duplicação de UC detectada no curso.");
    }

    public void mostrarSucessoPartilhaUC(String u, String c) {
        System.out.println(">> Sucesso: UC '" + u + "' agora partilhada com '" + c + "'.");
    }

    public void msgErroUCInativa() {
        System.out.println(">> Erro: UC inactiva. Impossível efectuar vínculos.");
    }

    public void mostrarErroNumAvaliacoes() {
        System.out.println(">> Erro: O limite de avaliações deve ser entre 1 e 3.");
    }

    public void mostrarErroNomeInvalido() {
        System.out.println(">> Erro: Introduza nome e apelido (apenas letras).");
    }

    public void mostrarErroNomeInvalidoMantido() {
        System.out.println(">> Erro: Nome inválido. Original mantido.");
    }

    public void mostrarErroNifDuplicado() {
        System.out.println("\n>> Erro: O NIF introduzido já pertence a um utilizador no sistema.");
        System.out.println(">> Não são permitidos registos duplicados.");
    }

    public void mostrarErroNifFormato() {
        System.out.println("\n>> Erro: Formato de NIF inválido. Introduza exatamente 9 dígitos.");
    }

    public void mostrarErroDataInvalida() {
        System.out.println(">> Erro: Formato inválido. Use DD-MM-AAAA.");
    }

    public void mostrarErroIdadeMinima() {
        System.out.println(">> Erro: O estudante deve ter pelo menos 16 anos.");
    }

    public void mostrarErroNumMecNumerico() {
        System.out.println(">> Erro: O número mecanográfico é estritamente numérico.");
    }

    public void mostrarSiglaGerada(String s) {
        System.out.println(">> Atribuição: Sigla institucional gerada: " + s);
    }

    public void mostrarErroEmailInvalido() {
        System.out.println(">> Erro: O email pessoal introduzido é inválido. Deve conter '@' e '.'.");
    }


    // Preçário
    public void mostrarSucessoAlteracaoPreco(String c, double p) {
        System.out.printf(">> Sucesso: Novo preçário para %s: %.2f€\n", c, p);
    }

    public void mostrarErroPrecoInvalido() {
        System.out.println(">> Erro: O valor monetário deve ser superior a zero.");
    }

    public void mostrarHistoricoPrecosCurso(String siglaCurso, double[][] historico) {
        System.out.println("\n--- HISTÓRICO DE PREÇOS DO CURSO " + siglaCurso + " ---");
        if (historico == null || historico.length == 0) {
            System.out.println(">> Não existem preços registados para este curso.");
            return;
        }
        System.out.printf("%-10s %-12s\n", "Ano", "Preço (€)");
        System.out.println("------------------------");
        for (double[] entry : historico) {
            int ano = (int) entry[0];
            double preco = entry[1];
            System.out.printf("%-10d %-12.2f\n", ano, preco);
        }
        System.out.println("------------------------");
    }

    // Docentes, Cursos, UCs (estado)
    public void msgAvisoDocenteComUCs(String s) {
        System.out.println(">> Erro: Docente " + s + " possui regências activas. Impossível desactivar.");
    }

    public void msgAvisoCursoComAlunosAtivos(String s) {
        System.out.println(">> Erro: Curso " + s + " possui matrículas activas. Impossível desactivar.");
    }

    public void msgAvisoUCAssociada(String s) {
        System.out.println(">> Erro: UC " + s + " integra planos de estudo activos. Impossível desactivar.");
    }

    public void msgSucessoEstadoAlterado(String e, boolean a) {
        System.out.println(">> Sucesso: Estado de '" + e + "' alterado para " + (a ? "ACTIVO" : "INACTIVO") + ".");
    }

    // Gestores
    public void mostrarErroNomeGestor() {
        System.out.println(">> Erro: O nome do gestor deve ser uma palavra única.");
    }

    public void mostrarErroCredenciaisGestor() {
        System.out.println(">> Erro: Autenticação falhou.");
    }

    public void mostrarErroDesativarGestorProprio() {
        System.out.println(">> Segurança: Não é permitida a auto-desactivação.");
    }

    public void mostrarSucessoDesativacaoGestor() {
        System.out.println(">> Sucesso: Acesso administrativo revogado.");
    }

    public void mostrarAvisoDesativacaoGestor(String n) {
        System.out.println("\n[CUIDADO] Vai revogar permanentemente o acesso de " + n + ".");
    }

    public void mostrarErroDocenteInativo() {
        System.out.println(">> Erro: Docente inactivo. Requer colaboração activa para regência.");
    }

    public void mostrarErroCursoInativo() {
        System.out.println(">> Erro: Curso inactivo.");
    }

    // Email e credenciais
    public void mostrarStatusEmail(boolean env, String email) {
        if (env) System.out.println(">> Canal Email: Credenciais enviadas para " + email);
        else System.out.println(">> Canal Email: Falha no envio. Contacte o suporte técnico.");
    }

    public void mostrarCredenciaisCriadas(String tipo, String nome, String email, String pass) {
        System.out.println("\n================ REGISTO CONCLUÍDO ================");
        System.out.println("Utilizador : " + nome + " (" + tipo + ")");
        System.out.println("Login      : " + email);
        //System.out.println("Password   : " + pass);
        System.out.println("====================================================");
    }

    // Cancelamentos e avisos genéricos
    public void mostrarCancelamento(String menu) {
        System.out.println("\n>> Cancelado. Retornando ao menu " + menu + "...");
    }

    public void mostrarErroFormatoNumericoGenerico() {
        System.out.println(">> Erro: Requer valor numérico.");
    }

    public void mostrarAvisoGestorDesativado() {
        System.out.println(">> Erro: Conta já se encontra inactiva.");
    }

    public void mostrarUcsAgregadasDocente(UnidadeCurricular[] ucs, int total) {
        System.out.println(">> Regências activas detectadas:");
        for (int i = 0; i < total; i++) if (ucs[i] != null) System.out.println("   - " + ucs[i].getNome());
    }

    // Quórum e matrículas
    public void mostrarAvisoEstudantePendente(int atuais) {
        System.out.println("\nEstudante registado com sucesso.");
        System.out.printf(">> Estado do Curso: %d/5 inscritos (Aguardando Quórum).\n", atuais);
        System.out.println(">> O acesso ao sistema e as matrículas estão suspensas até atingir 5 alunos.");
    }

    public void mostrarSucessoQuorumAtingido(String siglaCurso) {
        System.out.println("\nNumero de 5 alunos atingido!");
        System.out.println(">> A turma do curso " + siglaCurso + " está ATIVADA.");
        System.out.println(">> Todos os estudantes estão matriculados nas UCs iniciais.");

    }
        // Dívidas e reinscrição

        public void mostrarMultaAplicada ( double multa){
            System.out.println(">> Multa de " + String.format("%.2f", multa) + "€ aplicada por dívidas de anos anteriores.");
        }
    }
