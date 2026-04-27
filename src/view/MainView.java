package view;

import javax.swing.*;
import java.io.Console;
import java.util.Scanner;

/**
 * Interface de utilizador principal (View) do sistema ISSMF.
 * <p>
 * No padrão MVC, esta classe gere o ponto de entrada visual da aplicação.
 * É responsável pela interação no terminal (CLI) para utilizadores não autenticados,
 * implementando mecanismos seguros para recolha de passwords e formulários de registo.
 * </p>
 */
public class MainView {

    /**
     * Construtor por defeito da MainView.
     */
    public MainView() { }

    // =========================================================
    // 1. MENUS PRINCIPAIS E CABEÇALHOS
    // =========================================================

    /**
     * Apresenta o cabeçalho de boas-vindas do sistema.
     */
    public void mostrarBemVindo() {
        System.out.println("\n******************************************");
        System.out.println("* SISTEMA ACADÉMICO ISSMF v1.0      *");
        System.out.println("******************************************");
    }

    /**
     * Exibe o ano letivo corrente.
     * @param ano Ano atual (ex: 2026)
     */
    public void mostrarAnoLetivo(int ano) {
        System.out.println("\n>> CICLO CORRENTE: " + ano);
    }

    /**
     * Apresenta o menu principal público.
     * @return A opção selecionada pelo utilizador.
     */
    public int mostrarMenu() {
        System.out.println("\n========= MENU PRINCIPAL =========");
        System.out.println("1 - Efetuar Login");
        System.out.println("2 - Auto-Inscrição Estudante");
        System.out.println("3 - Transitar Ano Letívo");
        System.out.println("4 - Recuperar Palavra-passe");
        System.out.println("0 - Encerrar Sistema");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    /** Cabeçalho para o formulário de login. */
    public void mostrarCabecalhoLogin() {
        System.out.println("\n--- AUTENTICAÇÃO DE UTILIZADOR ---");
    }

    /** Cabeçalho para o formulário de auto‑registo. */
    public void mostrarCabecalhoRegisto() {
        System.out.println("\n--- FORMULÁRIO DE CANDIDATURA ---");
    }

    /** Cabeçalho para o processo de transição de ano letivo. */
    public void mostrarCabecalhoTransicao() {
        System.out.println("\n--- PROCESSAMENTO ESTRUTURAL DE ANO ---");
    }

    // =========================================================
    // 2. INPUTS DE DADOS E SEGURANÇA (LOGINS)
    // =========================================================

    /**
     * Solicita o email institucional.
     * <p>Utiliza a consola se disponível, caso contrário abre uma caixa de diálogo gráfica.</p>
     * @return Email introduzido.
     * @throws utils.CancelamentoException Se o utilizador cancelar a operação ("/" ou cancelar no diálogo).
     */
    public String pedirEmail() throws utils.CancelamentoException {
        Console console = System.console();
        if (console != null) {
            System.out.print("Email Institucional: ");
            String input = new Scanner(System.in).nextLine().trim();
            if (input.equals("/")) throw new utils.CancelamentoException();
            return input;
        } else {
            JFrame frameAux = new JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);
            String email = JOptionPane.showInputDialog(frameAux,
                    "Introduza o Email Institucional:",
                    "Autenticação ISSMF",
                    JOptionPane.PLAIN_MESSAGE);
            frameAux.dispose();
            if (email == null) throw new utils.CancelamentoException();
            return email.trim();
        }
    }

    /**
     * Lê a palavra‑passe de forma segura (sem eco no terminal ou com campo protegido em modo gráfico).
     * @return Password introduzida.
     * @throws utils.CancelamentoException Se o utilizador cancelar.
     */
    public String pedirPassword() throws utils.CancelamentoException {
        Console console = System.console();
        if (console != null) {
            char[] passwordArray = console.readPassword("Password: ");
            if (passwordArray == null) throw new utils.CancelamentoException();
            return new String(passwordArray).trim();
        } else {
            JFrame frameAux = new JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);
            JPasswordField pf = new JPasswordField();
            int opcao = JOptionPane.showConfirmDialog(frameAux, pf,
                    "Introduza a Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            frameAux.dispose();
            if (opcao != JOptionPane.OK_OPTION) throw new utils.CancelamentoException();
            return new String(pf.getPassword()).trim();
        }
    }

    // =========================================================
    // 3. FORMULÁRIOS DE REGISTO E REVISÃO
    // =========================================================

    /** Solicita o nome completo. */
    public String pedirNome() {
        return utils.Consola.lerString("Nome e Sobrenome: ");
    }

    /** Solicita o NIF (9 dígitos). */
    public String pedirNif() {
        return utils.Consola.lerString("NIF (9 dígitos): ");
    }

    /**
     * Solicita o NIF especificamente para a recuperação de palavra‑passe.
     * Mantém a consistência visual com o pedido de email.
     * @return NIF introduzido.
     * @throws utils.CancelamentoException Se o utilizador cancelar.
     */
    public String pedirNifRecuperacao() throws utils.CancelamentoException {
        Console console = System.console();
        if (console != null) {
            String input = utils.Consola.lerString("NIF (9 dígitos): ").trim();
            if (input.equals("/")) throw new utils.CancelamentoException();
            return input;
        } else {
            JFrame frameAux = new JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);
            String nif = JOptionPane.showInputDialog(frameAux,
                    "Introduza o seu NIF (9 dígitos) para confirmar a identidade:",
                    "Recuperação de Palavra-passe",
                    JOptionPane.PLAIN_MESSAGE);
            frameAux.dispose();
            if (nif == null) throw new utils.CancelamentoException();
            return nif.trim();
        }
    }

    /** Solicita a morada. */
    public String pedirMorada() {
        return utils.Consola.lerString("Morada: ");
    }

    /** Solicita a data de nascimento (formato DD-MM-AAAA). */
    public String pedirDataNascimento() {
        return utils.Consola.lerString("Data Nascimento (DD-MM-AAAA): ");
    }

    /** Solicita o email pessoal. */
    public String pedirEmailPessoal() {
        return utils.Consola.lerString("Email Pessoal: ");
    }

    /**
     * Lista os cursos disponíveis para matrícula e solicita a escolha.
     * @param cursos Array de cursos disponíveis.
     * @param total Número de cursos.
     * @return Índice do curso selecionado (0‑based) ou -1 se inválido.
     */
    public int pedirEscolhaCurso(model.bll.Curso[] cursos, int total) {
        System.out.println("\n--- CURSOS DISPONÍVEIS ---");
        for (int i = 0; i < total; i++) {
            if (cursos[i] != null) {
                String estado = cursos[i].isAtivo() ? "" : "[INDISPONÍVEL] ";
                System.out.printf("%d - %s%s (%s)\n", (i + 1), estado, cursos[i].getNome(), cursos[i].getSigla());
            }
        }
        return utils.Consola.lerInt("Selecione o número do curso: ") - 1;
    }

    /**
     * Mostra um resumo dos dados do candidato para confirmação antes do registo.
     */
    public void mostrarRevisaoEstudante(String nome, String nif, String morada, String dataNasc, String emailP, String curso) {
        System.out.println("\n--- CONFIRMAÇÃO DE CANDIDATURA ---");
        System.out.printf("Candidato: %s | NIF: %s\nResidência: %s\nContacto: %s | Curso: %s\n", nome, nif, morada, emailP, curso);
    }

    /** Solicita confirmação de dados (S/N). */
    public boolean confirmarDados() {
        String input = utils.Consola.lerString("\nConfirma a veracidade dos dados? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    /**
     * Exibe as credenciais geradas para o novo estudante.
     * @param ano Ano de matrícula.
     * @param numMec Número mecanográfico.
     * @param email Email institucional.
     * @param pass Palavra‑passe provisória (não é exibida por razões de segurança).
     */
    public void mostrarCredenciaisGeradas(int ano, int numMec, String email, String pass) {
        System.out.println("\n**************************************************");
        System.out.println("   MATRÍCULA EFETUADA COM SUCESSO! (" + ano + ")");
        System.out.println("   Número Mecanográfico : " + numMec);
        System.out.println("   Email Institucional  : " + email);
        System.out.println("**************************************************");
    }

    /**
     * Mensagem exibida quando o aluno se regista, mas o curso ainda não atingiu o quórum de 5 alunos.
     * @param atuais Número atual de inscritos no 1º ano do curso.
     */
    public void msgAvisoAguardandoQuorum(int atuais) {
        System.out.println("\nCandidatura registada com sucesso!");
        System.out.println(">> O curso ainda não atingiu o numero mínimo de 5 alunos.");
        System.out.printf(">> Estado atual: %d/5 inscritos.\n", atuais);
    }

    /**
     * Mensagem exibida quando o 5º aluno se inscreve e a turma é ativada.
     */
    public void msgSucessoRegistoComAtivacao() {
        System.out.println("\nInscrição confirmada e numero de alunos atingido!");
        System.out.println(">> A turma está aberta.");
        System.out.println(">> Todos os alunos deste curso estão matriculados.");
    }

    /**
     * Solicita confirmação para a transição global de ano letivo.
     * @param proximoAno Ano para o qual se pretende avançar.
     * @return true se o utilizador confirmar ('S'/'s'), false caso contrário.
     */
    public boolean pedirConfirmacaoAvanco(int proximoAno) {
        String input = utils.Consola.lerString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    // =========================================================
    // 4. FEEDBACK E MENSAGENS DE SISTEMA
    // =========================================================

    public void msgPrepararRegisto() {
        System.out.println(">> A carregar formulários de candidatura...");
    }
    public void msgErroLogin() {
        System.out.println(">> Erro: Credenciais inválidas.");
    }
    public void msgValidacaoSucesso(String tipo) {
        System.out.println(">> Acesso autorizado [" + tipo + "]. A carregar perfil...");
    }

    public void msgBemVindoUsuario(String nome, String identificador, String tipo) {
        System.out.println(">> Bem-vindo, " + nome + " (" + identificador + ") - " + tipo + "!");
    }

    public void msgSessaoEncerrada() {
        System.out.println(">> Sessão terminada com segurança. Dados salvos.");
    }
    public void msgSemCursosParaRegisto() {
        System.out.println(">> Aviso: Não existem cursos com vagas ou ativos de momento.");
    }
    public void msgErroNome() {
        System.out.println(">> Erro: Formato de nome inválido (Use Nome e Apelido).");
    }
    public void mostrarErroNifDuplicado() {
        System.out.println("\n>>Erro: O NIF introduzido já pertence a um utilizador no sistema.");
        System.out.println(">> Erro: Não são permitidos registos duplicados.");
    }
    public void msgErroEmailPessoal() {
        System.out.println(">> Erro: Email pessoal inválido. Deve conter '@' e '.'.");
    }
    public void mostrarErroNifFormato() {
        System.out.println("\n>>Erro: Formato de NIF inválido. Introduza exatamente 9 dígitos.");
    }
    public void msgErroData() {
        System.out.println(">> Erro: Use o formato DD-MM-AAAA.");
    }
    public void msgErroIdadeMinima() {
        System.out.println(">> Erro: O estudante deve ter pelo menos 16 anos.");
    }
    public void msgErroNumeroInvalido() {
        System.out.println(">> Erro: Seleção fora do intervalo permitido.");
    }
    public void msgErroLimiteEstudantes() {
        System.out.println(">> Erro: Capacidade máxima do sistema atingida.");
    }
    public void msgOpcaoInvalida() {
        System.out.println(">> Erro: Opção de menu inválida.");
    }
    public void msgErroEmailDominio() {
        System.out.println(">> Erro: Use o domínio institucional (@issmf.ipp.pt).");
    }
    public void msgErroInativo() {
        System.out.println(">> Erro: Conta suspensa ou inativa. Contacte a administração.");
    }
    public void msgRegistoCancelado() {
        System.out.println(">> Operação abortada. Nenhum dado foi registado.");
    }
    public void msgEncerramento() {
        System.out.println(">> A encerrar ISSMF. Até à próxima!");
    }

    // --- Processos Globais (Ano Letivo) ---
    public void msgSucessoAvancoAno(int ano) {
        System.out.println(">> Ano letivo " + ano + " iniciado.");
    }
    public void msgCancelamentoAvancoAno(int ano) {
        System.out.println(">> Transição cancelada. Mantemos o ano " + ano + ".");
    }
    public void mostrarAvisoValidacaoCursos() {
        System.out.println("\n>> A auditar viabilidade de cursos (Mín. 5 alunos)...");
    }
    public void mostrarFimValidacao() {
        System.out.println(">> Auditoria estrutural concluída.");
    }
    public void mostrarCursoAprovado(String sigla, int n) {
        System.out.println("   Valido " + sigla + ": " + n + " inscritos.");
    }
    public void mostrarCursoCancelado(String sigla, int n) {
        System.out.println("   Curso cancelado " + sigla + ": Quota insuficiente (" + n + ").");
    }
    public void mostrarCursoCanceladoFaltaUCs(String sigla) {
        System.out.println("   Erro: " + sigla + ": Sem estrutura curricular mínima.");
    }
    public void mostrarCancelamento() {
        System.out.println("\n>> Operação cancelada pelo utilizador.");
    }

    // --- Recuperação de palavra‑passe ---
    public void msgSucessoRecuperacao() {
        System.out.println(">> Sucesso: Verifique a nova senha no seu email pessoal.");
    }
    public void msgErroDadosIncorretosOuFalhaEmail() {
        System.out.println(">> Erro: Utilizador e NIF não coorrespondem.");
    }
    public void msgErroArquivoNaoEncontrado(String c) {
        System.err.println(">> Erro: Ficheiro " + c + " não localizado.");
    }
}