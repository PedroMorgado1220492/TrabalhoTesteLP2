package view;

import model.bll.Curso;
import model.bll.Departamento;
import model.bll.Docente;
import model.bll.Estudante;
import model.bll.UnidadeCurricular;

import java.util.Scanner;

/**
 * Interface gráfica de linha de comandos (CLI) destinada ao perfil Gestor.
 * Concentra todos os menus de backoffice, inputs e outputs relacionados com a administração da instituição.
 */
public class GestorView {

    private Scanner scanner;

    public GestorView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MENUS PRINCIPAIS ----------

    public int mostrarMenuPrincipalGestor() {
        System.out.println("\n=== BACKOFFICE - GESTOR ===");
        System.out.println("1 - Gerir Departamentos");
        System.out.println("2 - Gerir Cursos");
        System.out.println("3 - Gerir Unidades Curriculares");
        System.out.println("4 - Gerir Estudantes");
        System.out.println("5 - Gerir Docentes");
        System.out.println("6 - Avançar Ano Letivo");
        System.out.println("7 - Listagens e Relatórios");
        System.out.println("8 - Ver Alunos com Dívidas");
        System.out.println("9 - Alterar Preço de Cursos");
        System.out.println("0 - Sair / Logout");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public int mostrarMenuDepartamentos() {
        System.out.println("\n--- GERIR DEPARTAMENTOS ---");
        System.out.println("1 - Adicionar Departamento");
        System.out.println("2 - Alterar Departamento");
        System.out.println("3 - Listar Departamentos");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public int mostrarMenuCursos() {
        System.out.println("\n--- GERIR CURSOS ---");
        System.out.println("1 - Adicionar Curso");
        System.out.println("2 - Alterar Curso");
        System.out.println("3 - Listar Cursos");
        System.out.println("4 - Ativar/Desativar Curso");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public int mostrarMenuUCs() {
        System.out.println("\n--- GERIR UNIDADES CURRICULARES ---");
        System.out.println("1 - Criar Nova Unidade Curricular");
        System.out.println("2 - Associar UC Existente a outro Curso");
        System.out.println("3 - Alterar Unidade Curricular");
        System.out.println("4 - Listar Unidades Curriculares");
        System.out.println("5 - Ativar/Desativar UC");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public int mostrarMenuEstudantes() {
        System.out.println("\n--- GERIR ESTUDANTES ---");
        System.out.println("1 - Adicionar Estudante");
        System.out.println("2 - Alterar Estudante");
        System.out.println("3 - Listar Estudantes");
        System.out.println("4 - Ativar/Desativar Estudante");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public int mostrarMenuDocentes() {
        System.out.println("\n--- GERIR DOCENTES ---");
        System.out.println("1 - Adicionar Docente");
        System.out.println("2 - Alterar Docente");
        System.out.println("3 - Listar Docentes");
        System.out.println("4 - Ativar/Desativar Docente");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    public int mostrarMenuRelatorios() {
        System.out.println("\n--- RELATÓRIOS E ESTATÍSTICAS ---");
        System.out.println("1 - Alunos agrupados por Curso");
        System.out.println("2 - Alunos agrupados por UC");
        System.out.println("3 - UCs agrupadas por Curso");
        System.out.println("4 - Cursos agrupados por Departamento");
        System.out.println("5 - Ver Estatísticas Globais da Faculdade");
        System.out.println("0 - Recuar");
        System.out.print("Opção: ");
        return lerOpcaoInteira();
    }

    // ---------- INPUTS BASE ----------

    private String pedirString(String mensagem) {
        System.out.print(mensagem + ": ");
        return scanner.nextLine().trim();
    }

    private int lerOpcaoInteira() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------- PEDIDOS DE INPUT ESPECÍFICOS ----------

    public String pedirSiglaDepartamento() { return pedirString("Sigla do Departamento"); }
    public String pedirNomeDepartamento() { return pedirString("Nome do Departamento"); }
    public String pedirSiglaDepartamentoAlterar() { return pedirString("Introduza a Sigla do Departamento a alterar"); }

    public String pedirSiglaCurso() { return pedirString("Sigla do Curso"); }
    public String pedirNomeCurso() { return pedirString("Nome do Curso"); }
    public String pedirSiglaCursoAlterar() { return pedirString("Introduza a Sigla do Curso a alterar"); }

    public String pedirSiglaUC() { return pedirString("Sigla da Unidade Curricular"); }
    public String pedirNomeUC() { return pedirString("Nome da UC"); }
    public String pedirAnoCurricularUC() { return pedirString("Ano Curricular (1, 2 ou 3)"); }
    public String pedirSiglaUCPartilhar() { return pedirString("Introduza a Sigla da UC existente que quer partilhar"); }
    public String pedirSiglaUCAlterar() { return pedirString("Introduza a Sigla da UC a alterar"); }

    public String pedirNomePessoa() { return pedirString("Nome (Nome e Sobrenome)"); }
    public String pedirNif() { return pedirString("NIF (9 dígitos)"); }
    public String pedirMorada() { return pedirString("Morada"); }
    public String pedirDataNascimento() { return pedirString("Data de Nascimento (DD-MM-AAAA)"); }
    public String pedirNumMecEstudanteAlterar() { return pedirString("Introduza o Nº Mecanográfico do Estudante a alterar"); }
    public String pedirSiglaDocenteAlterar() { return pedirString("Introduza a Sigla do Docente a alterar"); }

    public String pedirNovoNome(String atual) { return pedirString("Novo Nome (deixe em branco para manter '" + atual + "')"); }
    public String pedirNovoAnoCurricular(int atual) { return pedirString("Novo Ano Curricular (deixe em branco para manter '" + atual + "')"); }
    public String pedirNovaMorada(String atual) { return pedirString("Nova Morada (deixe em branco para manter)"); }

    public boolean pedirConfirmacaoAvancoAno(int proximoAno) {
        System.out.print("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    public String pedirEmailPessoal() { return pedirString("Email Pessoal"); }

    public double pedirNovoPreco() {
        System.out.print("Introduza a nova propina anual (€): ");
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    // ---------- FEEDBACK AO UTILIZADOR (Mensagens de Erro e Sucesso) ----------

    public void mostrarMensagemSaida() { System.out.println(">> A sair do Backoffice..."); }
    public void mostrarOpcaoInvalida() { System.out.println(">> Erro: Opção inválida."); }
    public void mostrarAvisoSemAlteracao() { System.out.println(">> Aviso: Nenhuma alteração efetuada."); }
    public void mostrarSucessoAtualizacao() { System.out.println(">> Sucesso: Ficha/Entidade atualizada com sucesso!"); }
    public void mostrarInfoEdicao(String nome) { System.out.println(">> A editar: " + nome); }

    public void mostrarErroSiglaJaExiste(String sigla) { System.out.println(">> Erro: Já existe um registo com a sigla '" + sigla + "'."); }
    public void mostrarErroNifJaExiste(String nif) { System.out.println(">> Erro: Já existe um utilizador com o NIF '" + nif + "'."); }

    public void mostrarErroLimiteDepartamentos() { System.out.println(">> Erro: Limite máximo de departamentos atingido."); }
    public void mostrarErroLimiteCursos() { System.out.println(">> Erro: Limite máximo de cursos atingido."); }
    public void mostrarErroLimiteUCs() { System.out.println(">> Erro: Limite máximo de UCs atingido."); }
    public void mostrarErroLimiteEstudantes() { System.out.println(">> Erro: Limite máximo de estudantes atingido."); }
    public void mostrarErroLimiteDocentes() { System.out.println(">> Erro: Limite máximo de docentes atingido."); }

    public void mostrarErroDepartamentoNaoEncontrado() { System.out.println(">> Erro: Departamento não encontrado."); }
    public void mostrarErroCursoNaoEncontrado() { System.out.println(">> Erro: Curso não encontrado."); }
    public void mostrarErroUCNaoEncontrada() { System.out.println(">> Erro: Unidade Curricular não encontrada."); }
    public void mostrarErroEstudanteNaoEncontrado() { System.out.println(">> Erro: Estudante não encontrado."); }
    public void mostrarErroDocenteNaoEncontrado() { System.out.println(">> Erro: Docente não encontrado."); }

    public void mostrarSucessoRegistoDepartamento(String nome) { System.out.println(">> Sucesso: Departamento '" + nome + "' guardado com sucesso!"); }
    public void mostrarSucessoRegistoCurso(String nome) { System.out.println(">> Sucesso: Curso '" + nome + "' adicionado com sucesso!"); }
    public void mostrarSucessoRegistoUC(String nome) { System.out.println(">> Sucesso: UC '" + nome + "' criada com sucesso!"); }

    public void mostrarErroFaltaDepartamento() { System.out.println(">> Atenção: Crie um Departamento primeiro."); }
    public void mostrarErroFaltaCurso() { System.out.println(">> Atenção: Crie um Curso primeiro."); }
    public void mostrarErroFaltaDocenteOuCurso() { System.out.println(">> Atenção: Precisa de ter pelo menos 1 Docente e 1 Curso registados."); }

    public void mostrarErroCursoBloqueado() { System.out.println(">> Erro: O curso já tem estudantes ou professores alocados. O sistema proíbe a sua alteração!"); }
    public void mostrarErroAnoNumerico() { System.out.println(">> Erro: O Ano Curricular deve ser um número."); }
    public void mostrarErroAnoNumericoMantido() { System.out.println(">> Erro: O Ano Curricular deve ser um número. Mantido o original."); }
    public void mostrarErroLimiteUCsAno(String curso, int ano) { System.out.println(">> Erro: O curso " + curso + " já atingiu o máximo de 5 UCs no " + ano + "º ano!"); }
    public void mostrarErroUCJaNoCurso() { System.out.println(">> Erro: Esta UC já pertence a este Curso."); }
    public void mostrarSucessoPartilhaUC(String nomeUc, String curso) { System.out.println(">> Sucesso: A UC de " + nomeUc + " foi partilhada com " + curso + "."); }
    public void msgErroUCInativa() { System.out.println(">> Erro: Esta Unidade Curricular encontra-se INATIVA e não pode ser associada a cursos."); }

    public void mostrarErroNomeInvalido() { System.out.println(">> Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras."); }
    public void mostrarErroNomeInvalidoMantido() { System.out.println(">> Erro: Nome inválido. Mantido o original."); }
    public void mostrarErroNifInvalido() { System.out.println(">> Erro: O NIF deve conter exatamente 9 dígitos numéricos."); }
    public void mostrarErroDataInvalida() { System.out.println(">> Erro: A data deve respeitar o formato DD-MM-AAAA."); }
    public void mostrarErroNumMecNumerico() { System.out.println(">> Erro: O número mecanográfico deve conter apenas números."); }
    public void mostrarSiglaGerada(String sigla) { System.out.println(">> Info: Sigla gerada automaticamente pelo sistema: " + sigla); }

    public void mostrarSucessoAvancoAno(int ano) { System.out.println(">> Sucesso! O sistema avançou. Bem-vindo ao ano letivo de " + ano + "."); }
    public void mostrarCancelamentoAvancoAno(int ano) { System.out.println(">> Operação cancelada. Mantemo-nos em " + ano + "."); }
    public void mostrarSucessoAlteracaoPreco(String curso, double preco) { System.out.println(">> Sucesso! O curso " + curso + " custa agora " + preco + "€/ano."); }
    public void mostrarErroPrecoInvalido() { System.out.println(">> Erro: Tem de introduzir um valor superior a 0."); }

    public void msgAvisoDocenteComUCs(String sigla) { System.out.println(">> Erro: O Docente " + sigla + " tem UCs associadas e não pode ser desativado."); }
    public void msgAvisoCursoComAlunosAtivos(String sigla) { System.out.println(">> Erro: O Curso " + sigla + " tem Estudantes Ativos e não pode ser desativado."); }
    public void msgAvisoUCAssociada(String sigla) { System.out.println(">> Erro: A UC " + sigla + " está associada a cursos e não pode ser desativada."); }
    public void msgSucessoEstadoAlterado(String entidade, boolean ativo) {
        String estado = ativo ? "ATIVADO" : "DESATIVADO";
        System.out.println(">> Sucesso: O " + entidade + " encontra-se agora " + estado + ".");
    }

    public void mostrarAvisoTransicaoAno() {
        System.out.println("\n--- TRANSIÇÃO DE ANO LETIVO ---");
        System.out.println("Atenção: Esta ação irá avaliar todos os alunos, subir o ano de frequência");
        System.out.println("dos que tiverem aprovação (>= 60%) e arquivar todas as notas.");
    }

    /**
     * Feedback visual e de estado sobre o envio de credenciais.
     */
    public void mostrarStatusEmail(boolean enviado, String email) {
        if (enviado) {
            System.out.println(">> Notificação enviada para: " + email);
        } else {
            System.out.println(">> Alerta: Falha técnica no envio da notificação para " + email + ". O registo foi concluído, mas o utilizador não recebeu o email automático.");
        }
    }

    public void mostrarCredenciaisCriadas(String tipo, String nome, String email, String password) {
        System.out.println("\n--- NOVO " + tipo.toUpperCase() + " REGISTADO COM SUCESSO! ---");
        System.out.println("Nome: " + nome);
        System.out.println("Email de Acesso: " + email);
        System.out.println("Password Provisória: " + password);
        System.out.println("----------------------------------------------\n");
    }

    // ---------- MÉTODOS DE LISTAGEM E ESTATÍSTICAS ----------

    public void mostrarListaDepartamentos(Departamento[] deps, int total) {
        System.out.println("\n--- LISTA DE DEPARTAMENTOS ---");
        if (total == 0) System.out.println("Não existem departamentos registados.");
        else for (int i = 0; i < total; i++) System.out.println("- " + deps[i].getSigla() + " : " + deps[i].getNome());
    }

    public int pedirEscolhaDepartamento(Departamento[] deps, int total) {
        System.out.println("\n--- Escolha o Departamento ---");
        for (int i = 0; i < total; i++) System.out.println((i + 1) + " - " + deps[i].getSigla() + " (" + deps[i].getNome() + ")");
        System.out.print("Número do Departamento: ");
        return lerOpcaoInteira() - 1;
    }

    public void mostrarListaCursos(Curso[] cursos, int total) {
        System.out.println("\n--- LISTA DE CURSOS ---");
        if (total == 0) System.out.println("Não existem cursos registados.");
        else for (int i = 0; i < total; i++) {
            String status = cursos[i].isAtivo() ? "[ATIVO]" : "[INATIVO]";
            System.out.println("- " + status + " " + cursos[i].getSigla() + " - " + cursos[i].getNome());
        }
    }

    public int pedirEscolhaCurso(Curso[] cursos, int total) {
        System.out.println("\n--- Escolha o Curso ---");
        for (int i = 0; i < total; i++) System.out.println((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
        System.out.print("Número do Curso: ");
        return lerOpcaoInteira() - 1;
    }

    public void mostrarListaUCs(UnidadeCurricular[] ucs, int total) {
        System.out.println("\n--- LISTA DE UCs ---");
        if (total == 0) System.out.println("Não existem UCs registadas.");
        else for (int i = 0; i < total; i++) {
            String status = ucs[i].isAtivo() ? "[ATIVO]" : "[INATIVO]";
            System.out.println("- " + status + " " + ucs[i].getSigla() + " : " + ucs[i].getNome());
        }
    }

    public void mostrarListaDocentes(Docente[] docentes, int total) {
        System.out.println("\n--- LISTA DE DOCENTES ---");
        if (total == 0) System.out.println("Não existem docentes registados.");
        else for (int i = 0; i < total; i++) {
            String status = docentes[i].isAtivo() ? "[ATIVO]" : "[INATIVO]";
            System.out.println("- " + status + " " + docentes[i].getSigla() + " : " + docentes[i].getNome());
        }
    }

    public int pedirEscolhaDocente(Docente[] docentes, int total) {
        System.out.println("\n--- Escolha o Docente Responsável ---");
        for (int i = 0; i < total; i++) System.out.println((i + 1) + " - " + docentes[i].getNome() + " (" + docentes[i].getSigla() + ")");
        System.out.print("Número do Docente: ");
        return lerOpcaoInteira() - 1;
    }

    public void mostrarListaEstudantes(Estudante[] estudantes, int total) {
        System.out.println("\n--- LISTA DE ESTUDANTES ---");
        if (total == 0) System.out.println("Não existem estudantes registados.");
        else {
            for (int i = 0; i < total; i++) {
                String siglaCurso = (estudantes[i].getCurso() != null) ? estudantes[i].getCurso().getSigla() : "N/A";
                String status = estudantes[i].isAtivo() ? "[ATIVO]" : "[INATIVO]";
                System.out.println("- " + status + " " + estudantes[i].getNumeroMecanografico() + " : " + estudantes[i].getNome() + " | Curso: " + siglaCurso);
            }
        }
    }

    public void mostrarUcsAgregadasDocente(UnidadeCurricular[] ucs, int total) {
        System.out.println(">> UCs associadas ao docente:");
        for (int i = 0; i < total; i++) {
            if (ucs[i] != null) {
                System.out.println("  -> " + ucs[i].getSigla() + " - " + ucs[i].getNome());
            }
        }
    }

    public void mostrarRelatorioAlunosPorCurso(Curso[] cursos, int totalCursos, Estudante[] estudantes, int totalEstudantes) {
        System.out.println("\n--- ALUNOS POR CURSO ---");
        for (int i = 0; i < totalCursos; i++) {
            Curso c = cursos[i];
            System.out.println("\n[" + c.getSigla() + "] " + c.getNome() + ":");
            boolean temAlunos = false;
            for (int j = 0; j < totalEstudantes; j++) {
                Estudante e = estudantes[j];
                if (e.getCurso() != null && e.getCurso().getSigla().equals(c.getSigla())) {
                    System.out.println("  -> " + e.getNumeroMecanografico() + " - " + e.getNome());
                    temAlunos = true;
                }
            }
            if (!temAlunos) System.out.println("  (Nenhum aluno inscrito)");
        }
    }

    public void mostrarRelatorioAlunosPorUC(UnidadeCurricular[] ucs, int totalUcs, Estudante[] estudantes, int totalEstudantes) {
        System.out.println("\n--- ALUNOS POR UNIDADE CURRICULAR ---");
        for (int i = 0; i < totalUcs; i++) {
            UnidadeCurricular uc = ucs[i];
            System.out.println("\n[" + uc.getSigla() + "] " + uc.getNome() + ":");
            boolean temAlunos = false;
            for (int j = 0; j < totalEstudantes; j++) {
                Estudante e = estudantes[j];
                if (e.estaInscrito(uc.getSigla())) {
                    System.out.println("  -> " + e.getNumeroMecanografico() + " - " + e.getNome());
                    temAlunos = true;
                }
            }
            if (!temAlunos) System.out.println("  (Nenhum aluno inscrito)");
        }
    }

    public void mostrarRelatorioUCsPorCurso(Curso[] cursos, int totalCursos) {
        System.out.println("\n--- UNIDADES CURRICULARES POR CURSO ---");
        for (int i = 0; i < totalCursos; i++) {
            Curso c = cursos[i];
            System.out.println("\n[" + c.getSigla() + "] " + c.getNome() + ":");
            if (c.getTotalUCs() == 0) {
                System.out.println("  (Nenhuma UC registada neste curso)");
            } else {
                for (int j = 0; j < c.getTotalUCs(); j++) {
                    UnidadeCurricular uc = c.getUnidadesCurriculares()[j];
                    System.out.println("  -> " + uc.getSigla() + " - " + uc.getNome() + " (Ano: " + uc.getAnoCurricular() + "º)");
                }
            }
        }
    }

    public void mostrarRelatorioCursosPorDepartamento(Departamento[] deps, int totalDeps) {
        System.out.println("\n--- CURSOS POR DEPARTAMENTO ---");
        for (int i = 0; i < totalDeps; i++) {
            Departamento d = deps[i];
            System.out.println("\nDEPARTAMENTO: " + d.getNome());
            if (d.getTotalCursos() == 0) {
                System.out.println("  (Sem Cursos registados)");
            } else {
                for (int j = 0; j < d.getTotalCursos(); j++) {
                    System.out.println("  -> " + d.getCursos()[j].getSigla() + " - " + d.getCursos()[j].getNome());
                }
            }
        }
    }

    public void mostrarEstatisticas(double mediaGlobal, String melhorAluno, String nomeCursoTop) {
        System.out.println("\n--- ESTATÍSTICAS GLOBAIS DO ISSMF ---");
        System.out.println("Média Global da Instituição: " + mediaGlobal + " valores.");
        System.out.println("Melhor Aluno(a): " + melhorAluno);
        if (nomeCursoTop != null) {
            System.out.println("Curso mais popular: " + nomeCursoTop);
        } else {
            System.out.println("Curso mais popular: Dados insuficientes.");
        }
    }

    // ---------- PROPINAS E DEVEDORES ----------

    public void mostrarListaDevedores(Estudante[] devedores, double[] dividas, int total) {
        System.out.println("\n--- LISTA DE DEVEDORES (PROPINAS) ---");
        if (total == 0) System.out.println("Nenhum aluno tem propinas em atraso.");
        else {
            for (int i = 0; i < total; i++) {
                System.out.println("-> " + devedores[i].getNumeroMecanografico() + " - " + devedores[i].getNome() + " | Dívida: " + dividas[i] + "€");
            }
        }
    }

    public int mostrarCursosParaPropina(Curso[] cursos, int totalCursos) {
        System.out.println("\n--- ATUALIZAR PREÇO DO CURSO (PROPINAS) ---");
        System.out.println("Aviso: Esta alteração afetará APENAS os novos alunos que se inscreverem a partir de agora.");
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null) System.out.println((i + 1) + " - " + cursos[i].getNome() + " (Preço Atual: " + cursos[i].getValorPropinaAnual() + "€)");
        }
        System.out.print("Escolha o número do curso: ");
        return lerOpcaoInteira();
    }
}