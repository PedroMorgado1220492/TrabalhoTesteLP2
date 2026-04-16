package view;

import javax.swing.*;
import java.io.Console;
import java.util.Scanner;

/**
 * Interface de utilizador principal (View) do sistema ISSMF.
 * No padrão MVC, esta classe gere o ponto de entrada visual da aplicação.
 * É responsável pela interação no terminal (CLI) para utilizadores não autenticados,
 * implementando mecanismos seguros para recolha de passwords e formulários de registo.
 */
public class MainView {

    /**
     * Construtor por defeito da MainView.
     */
    public MainView() { }


    // =========================================================
    // 1. MENUS PRINCIPAIS E CABEÇALHOS
    // =========================================================

    public void mostrarBemVindo() {
        System.out.println("\n******************************************");
        System.out.println("* SISTEMA ACADÉMICO ISSMF v1.0      *");
        System.out.println("******************************************");
    }

    public void mostrarAnoLetivo(int ano) {
        System.out.println("\n>> CICLO CORRENTE: " + ano + " / " + (ano + 1));
    }

    /**
     * Apresenta o menu de entrada público do sistema.
     * @return A opção selecionada.
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

    public void mostrarCabecalhoLogin() { System.out.println("\n--- AUTENTICAÇÃO DE UTILIZADOR ---"); }
    public void mostrarCabecalhoRegisto() { System.out.println("\n--- FORMULÁRIO DE CANDIDATURA ---"); }
    public void mostrarCabecalhoTransicao() { System.out.println("\n--- PROCESSAMENTO ESTRUTURAL DE ANO ---"); }


    // =========================================================
    // 2. INPUTS DE DADOS E SEGURANÇA (LOGINS)
    // =========================================================

    /**
     * Solicita o email institucional. Suporta modo texto puro ou popup gráfico
     * se detetar execução via IDE (onde o System.console() é nulo).
     */
    public String pedirEmail() throws utils.CancelamentoException {
        java.io.Console console = System.console();

        if (console != null) {
            System.out.print("Email Institucional: ");
            String input = new java.util.Scanner(System.in).nextLine().trim();
            if (input.equals("/")) throw new utils.CancelamentoException();
            return input;
        } else {
            javax.swing.JFrame frameAux = new javax.swing.JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);

            String email = javax.swing.JOptionPane.showInputDialog(
                    frameAux,
                    "Introduza o Email Institucional:",
                    "Autenticação ISSMF",
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

            frameAux.dispose();

            if (email == null) {
                throw new utils.CancelamentoException();
            }

            return email.trim();
        }
    }
    /**
     * Lê a password de forma segura.
     * Se correr num terminal real, oculta os caracteres.
     * Se correr num IDE, abre um JPasswordField protegido.
     */
    public String pedirPassword() throws utils.CancelamentoException {
        java.io.Console console = System.console();

        if (console != null) {
            char[] passwordArray = console.readPassword("Password: ");
            if (passwordArray == null) throw new utils.CancelamentoException();
            return new String(passwordArray).trim();
        } else {
            javax.swing.JFrame frameAux = new javax.swing.JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);

            javax.swing.JPasswordField pf = new javax.swing.JPasswordField();

            int opcao = javax.swing.JOptionPane.showConfirmDialog(
                    frameAux,
                    pf,
                    "Introduza a Password",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

            frameAux.dispose();

            if (opcao != javax.swing.JOptionPane.OK_OPTION) {
                throw new utils.CancelamentoException();
            }

            return new String(pf.getPassword()).trim();
        }
    }


    // =========================================================
    // 3. FORMULÁRIOS DE REGISTO E REVISÃO
    // =========================================================

    public String pedirNome() { return utils.Consola.lerString("Nome e Sobrenome: "); }
    public String pedirNif() { return utils.Consola.lerString("NIF (9 dígitos): "); }

    /**
     * Solicita o NIF especificamente para o fluxo de recuperação de password.
     * Mantém a consistência visual (janela pop-up) com o pedido de Email.
     */
    public String pedirNifRecuperacao() throws utils.CancelamentoException {
        java.io.Console console = System.console();

        if (console != null) {
            String input = utils.Consola.lerString("NIF (9 dígitos): ").trim();
            if (input.equals("/")) throw new utils.CancelamentoException();
            return input;
        } else {
            javax.swing.JFrame frameAux = new javax.swing.JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);

            String nif = javax.swing.JOptionPane.showInputDialog(
                    frameAux,
                    "Introduza o seu NIF (9 dígitos) para confirmar a identidade:",
                    "Recuperação de Palavra-passe",
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

            frameAux.dispose();

            // Se o utilizador clicar em Cancelar, aborta o processo
            if (nif == null) {
                throw new utils.CancelamentoException();
            }

            return nif.trim();
        }
    }
    public String pedirMorada() { return utils.Consola.lerString("Morada: "); }
    public String pedirDataNascimento() { return utils.Consola.lerString("Data Nascimento (DD-MM-AAAA): "); }
    public String pedirEmailPessoal() { return utils.Consola.lerString("Email Pessoal: "); }

    /**
     * Lista os cursos para o candidato escolher.
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

    public void mostrarRevisaoEstudante(String nome, String nif, String morada, String dataNasc, String emailP, String curso) {
        System.out.println("\n--- CONFIRMAÇÃO DE CANDIDATURA ---");
        System.out.printf("Candidato: %s | NIF: %s\nResidência: %s\nContacto: %s | Curso: %s\n", nome, nif, morada, emailP, curso);
    }

    public boolean confirmarDados() {
        String input = utils.Consola.lerString("\nConfirma a veracidade dos dados? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    public void mostrarCredenciaisGeradas(int ano, int numMec, String email, String pass) {
        System.out.println("\n**************************************************");
        System.out.println("   MATRÍCULA EFETUADA COM SUCESSO! (" + ano + ")");
        System.out.println("   Número Mecanográfico : " + numMec);
        System.out.println("   Email Institucional  : " + email);
        System.out.println("   Password Provisória  : " + pass);
        System.out.println("**************************************************");
    }

    /**
     * Solicita a confirmação para a transição global de ano letivo.
     * @param proximoAno O ano para o qual o sistema irá avançar.
     * @return true se o utilizador confirmar com 'S'.
     */
    public boolean pedirConfirmacaoAvanco(int proximoAno) {
        String input = utils.Consola.lerString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N): ");
        return input.equalsIgnoreCase("S");
    }
    // =========================================================
    // 4. FEEDBACK E MENSAGENS DE SISTEMA
    // =========================================================

    public void msgPrepararRegisto() { System.out.println(">> A carregar formulários de candidatura..."); }
    public void msgErroLogin() { System.out.println(">> Erro: Credenciais inválidas."); }
    public void msgValidacaoSucesso(String tipo) { System.out.println(">> Acesso autorizado [" + tipo + "]. A carregar perfil..."); }
    public void msgBemVindoRole(String role) { System.out.println(">> Bem-vindo, " + role + "."); }
    public void msgSessaoEncerrada() { System.out.println(">> Sessão terminada com segurança. Dados salvos."); }
    public void msgSemCursosParaRegisto() { System.out.println(">> Aviso: Não existem cursos com vagas ou ativos de momento."); }
    public void msgErroNome() { System.out.println(">> Erro: Formato de nome inválido (Use Nome e Apelido)."); }
    public void msgErroNif() { System.out.println(">> Erro: NIF inválido (Requer 9 dígitos)."); }
    public void msgErroData() { System.out.println(">> Erro: Use o formato DD-MM-AAAA."); }
    public void msgErroNumeroInvalido() { System.out.println(">> Erro: Seleção fora do intervalo permitido."); }
    public void msgErroLimiteEstudantes() { System.out.println(">> Erro: Capacidade máxima do sistema atingida."); }
    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção de menu inválida."); }
    public void msgErroEmailDominio() { System.out.println(">> Erro: Use o domínio institucional (@issmf.ipp.pt)."); }
    public void msgErroInativo() { System.out.println(">> Erro: Conta suspensa ou inativa. Contacte a administração."); }
    public void msgRegistoCancelado() { System.out.println(">> Operação abortada. Nenhum dado foi registado."); }
    public void msgEncerramento() { System.out.println(">> A encerrar ISSMF. Até à próxima!"); }

    // --- Processos Globais (Ano Letivo) ---

    public void msgSucessoAvancoAno(int ano) { System.out.println(">> SUCESSO: Ano letivo " + ano + " iniciado."); }
    public void msgCancelamentoAvancoAno(int ano) { System.out.println(">> INFO: Transição cancelada. Mantemos o ano " + ano + "."); }
    public void mostrarAvisoValidacaoCursos() { System.out.println("\n>> A auditar viabilidade de cursos (Mín. 5 alunos)..."); }
    public void mostrarFimValidacao() { System.out.println(">> Auditoria estrutural concluída."); }
    public void mostrarCursoAprovado(String sigla, int n) { System.out.println("   [OK] " + sigla + ": " + n + " inscritos."); }
    public void mostrarCursoCancelado(String sigla, int n) { System.out.println("   [CANCELADO] " + sigla + ": Quota insuficiente (" + n + ")."); }
    public void mostrarCursoCanceladoFaltaUCs(String sigla) { System.out.println("   [ERRO] " + sigla + ": Sem estrutura curricular mínima."); }
    public void mostrarCancelamento() { System.out.println("\n>> Operação cancelada pelo utilizador."); }

    // --- Recuperação ---

    public void msgSucessoRecuperacao() { System.out.println(">> Sucesso: Verifique a nova senha no seu email pessoal."); }
    public void msgErroDadosIncorretosOuFalhaEmail() { System.out.println(">> Erro: Dados não conferem ou falha no servidor de email."); }
    public void msgSucessoEnvioEmail(String email) { System.out.println(">> Notificação enviada para: " + email); }
    public void msgErroEnvioEmail() { System.out.println(">> Aviso: Falha no disparo do email automático."); }
    public void msgErroArquivoNaoEncontrado(String c) { System.err.println(">> ERRO CRÍTICO: Ficheiro " + c + " não localizado."); }
}