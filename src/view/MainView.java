package view;

import utils.Consola;

import javax.swing.*;
import java.io.Console;
import java.util.Scanner;

/**
 * Interface de utilizador principal (View) do sistema ISSMF.
 */
public class MainView {

    public MainView() { }

    // =========================================================
    // 1. MENUS PRINCIPAIS E CABEÇALHOS
    // =========================================================

    public void mostrarBemVindo() {
        System.out.println("******************************************");
        System.out.println("* SISTEMA ACADÉMICO ISSMF v1.0      *");
        System.out.println("******************************************");
    }

    public void mostrarAnoLetivo(int ano, boolean anoIniciado) {
        String estado = anoIniciado ? "INICIADO" : "POR INICIAR";
        System.out.println("\n>> CICLO CORRENTE: " + ano + "\n>> ESTADO: " + estado);
    }

    public int mostrarMenu() {
        System.out.println("\n========= MENU PRINCIPAL =========");
        System.out.println("1 - Efetuar Login");
        System.out.println("2 - Auto-Inscrição Estudante");
        System.out.println("3 - Recuperar Palavra-Passe");
        System.out.println("4 - Iniciar Ano Letivo");
        System.out.println("5 - Transitar Ano Letivo");
        System.out.println("0 - Encerrar Sistema");
        System.out.print("Escolha uma opção: ");
        return utils.Consola.lerOpcaoMenu();
    }

    public static void mostrarNota() {
        System.out.println("\n>>> Diretoria de trabalho: " + System.getProperty("user.dir"));
        System.out.println(">>> Usar '/' para cancelar operações.");
    }

    public void mostrarCabecalhoLogin() {
        System.out.println("\n--- AUTENTICAÇÃO DE UTILIZADOR ---");
    }

    public void mostrarCabecalhoRegisto() {
        System.out.println("\n--- FORMULÁRIO DE CANDIDATURA ---");
    }

    public void mostrarCabecalhoTransicao() {
        System.out.println("\n--- PROCESSAMENTO ESTRUTURAL DE ANO ---");
    }
    // =========================================================
    // 2. INPUTS DE DADOS E SEGURANÇA (LOGINS)
    // =========================================================

    public String pedirEmail() throws utils.CancelamentoException {
        Console console = System.console();
        if (console != null) {
            return Consola.lerString("Email Institucional: ");
        } else {
            JFrame frameAux = new JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);
            String email = JOptionPane.showInputDialog(frameAux, "Introduza o Email Institucional:", "Autenticação ISSMF", JOptionPane.PLAIN_MESSAGE);
            frameAux.dispose();
            if (email == null) throw new utils.CancelamentoException();
            return email.trim();
        }
    }
    public String pedirPassword() throws utils.CancelamentoException {
        Console console = System.console();
        if (console != null) {
            return Consola.lerPassword("Password");
        } else {
            JFrame frameAux = new JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);
            JPasswordField pf = new JPasswordField();
            int opcao = JOptionPane.showConfirmDialog(frameAux, pf, "Introduza a Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            frameAux.dispose();
            if (opcao != JOptionPane.OK_OPTION) throw new utils.CancelamentoException();
            return new String(pf.getPassword()).trim();
        }
    }

    // =========================================================
    // 3. FORMULÁRIOS DE REGISTO E REVISÃO
    // =========================================================

    public String pedirNome() { return utils.Consola.lerString("Nome e Sobrenome: "); }
    public String pedirNif() { return utils.Consola.lerString("NIF (9 dígitos): "); }
    public String pedirNifRecuperacao() throws utils.CancelamentoException {
        Console console = System.console();
        if (System.console() != null) {
            return Consola.lerString("NIF (9 dígitos)");
        } else {
            JFrame frameAux = new JFrame();
            frameAux.setAlwaysOnTop(true);
            frameAux.setLocationRelativeTo(null);
            String nif = JOptionPane.showInputDialog(frameAux, "Introduza o seu NIF (9 dígitos) para confirmar a identidade:", "Recuperação de Palavra-passe", JOptionPane.PLAIN_MESSAGE);
            frameAux.dispose();
            if (nif == null) throw new utils.CancelamentoException();
            return nif.trim();
        }
    }
    public String pedirMorada() { return utils.Consola.lerString("Morada: "); }
    public String pedirDataNascimento() { return utils.Consola.lerString("Data Nascimento (DD-MM-AAAA): "); }
    public String pedirEmailPessoal() { return utils.Consola.lerString("Email Pessoal: "); }
    public int pedirEscolhaCurso(model.bll.Curso[] cursos, int total) {
        System.out.println("\n--- CURSOS DISPONÍVEIS ---");
        for (int i = 0; i < total; i++) {
            if (cursos[i] != null) {
                System.out.printf("%d - %s%s (%s)\n", (i + 1), cursos[i].isAtivo() ? "" : "[INDISPONÍVEL] ", cursos[i].getNome(), cursos[i].getSigla());
            }
        }
        return utils.Consola.lerInt("Selecione o número do curso: ") - 1;
    }
    public void mostrarRevisaoEstudante(String nome, String nif, String morada, String dataNasc, String emailP, String curso) {
        System.out.println("\n--- CONFIRMAÇÃO DE CANDIDATURA ---");
        System.out.printf("Candidato: %s | NIF: %s\nResidência: %s\nContacto: %s | Curso: %s\n", nome, nif, morada, emailP, curso);
    }
    public boolean confirmarDados() { return utils.Consola.lerString("\nConfirma a veracidade dos dados? (S/N): ").equalsIgnoreCase("S"); }
    public void mostrarCredenciaisGeradas(int ano, int numMec, String email, String pass) {
        System.out.println("\n**************************************************");
        System.out.println("   MATRÍCULA EFETUADA COM SUCESSO! (" + ano + ")");
        System.out.println("   Número Mecanográfico : " + numMec);
        System.out.println("   Email Institucional  : " + email);
        System.out.println("**************************************************");
    }
    public void msgAvisoAguardandoQuorum(int atuais) {
        System.out.println("\nCandidatura registada com sucesso!");
        System.out.println(">> O curso ainda não atingiu o numero mínimo de 5 alunos.");
        System.out.printf(">> Estado atual: %d/5 inscritos.\n", atuais);
    }
    public void msgSucessoRegistoComAtivacao() {
        System.out.println("\nInscrição confirmada e numero de alunos atingido!");
        System.out.println(">> A turma está aberta.");
        System.out.println(">> Todos os alunos deste curso estão matriculados.");
    }
    public boolean pedirConfirmacaoAvanco(int proximoAno) { return utils.Consola.lerString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N): ").equalsIgnoreCase("S"); }
    public boolean pedirConfirmacaoInicioAno(int ano) { String input = Consola.lerString("Deseja mesmo iniciar o ano letivo " + ano + "? (S/N): "); return input.equalsIgnoreCase("S"); }

    public void msgTransicaoBloqueadaPorAvaliacoesEmFalta(String[] faltas) {
        System.out.println(">> NÃO É POSSÍVEL TRANSITAR DE ANO.");
        System.out.println(">> Existem alunos com avaliações em falta nas seguintes UCs:");
        System.out.println("\n--- AVALIAÇÕES EM FALTA ---");
        for (String falta : faltas) {
            String[] partes = falta.split(";");
            if (partes.length >= 6) {
                System.out.printf("  - Aluno %s - %s | UC: %s - %s | Tem %s de %s avaliações\n",
                        partes[0], partes[1], partes[2], partes[3], partes[4], partes[5]);
            }
        }
        System.out.println();
    }

    // =========================================================
    // 4. FEEDBACK E MENSAGENS DE SISTEMA
    // =========================================================

    public void msgPrepararRegisto() { System.out.println(">> A carregar formulários de candidatura..."); }
    public void msgErroLogin() { System.out.println(">> Erro: Credenciais inválidas."); }
    public void msgValidacaoSucesso(String tipo) { System.out.println(">> Acesso autorizado [" + tipo + "]. A carregar perfil..."); }
    public void msgBemVindoUsuario(String nome, String identificador, String tipo) { System.out.println(">> Bem-vindo, " + nome + " (" + identificador + ") - " + tipo + "!"); }
    public void msgSessaoEncerrada() { System.out.println(">> Sessão terminada com segurança. Dados salvos."); }
    public void msgSemCursosParaRegisto() { System.out.println(">> Aviso: Não existem cursos com vagas ou ativos de momento."); }
    public void msgErroNome() { System.out.println(">> Erro: Formato de nome inválido (Use Nome e Apelido)."); }
    public void mostrarErroNifDuplicado() { System.out.println("\n>>Erro: O NIF introduzido já pertence a um utilizador no sistema.\n>> Erro: Não são permitidos registos duplicados."); }
    public void msgErroEmailPessoal() { System.out.println(">> Erro: Email pessoal inválido. Deve conter '@' e '.'."); }
    public void mostrarErroNifFormato() { System.out.println("\n>>Erro: Formato de NIF inválido. Introduza exatamente 9 dígitos."); }
    public void msgErroIdadeMinima() { System.out.println(">> Erro: O estudante deve ter pelo menos 16 anos."); }
    public void msgErroNumeroInvalido() { System.out.println(">> Erro: Seleção fora do intervalo permitido."); }
    public void msgErroLimiteEstudantes() { System.out.println(">> Erro: Capacidade máxima do sistema atingida."); }
    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção de menu inválida."); }
    public void msgErroEmailDominio() { System.out.println(">> Erro: Use o domínio institucional (@issmf.ipp.pt)."); }
    public void msgErroInativo() { System.out.println(">> Erro: Conta suspensa ou inativa. Contacte a administração."); }
    public void msgRegistoCancelado() { System.out.println(">> Operação abortada. Nenhum dado foi registado."); }
    public void msgEncerramento() { System.out.println(">> A encerrar ISSMF. Até à próxima!"); }
    public void msgSucessoAvancoAno(int ano) { System.out.println(">> Ano letivo " + ano + " iniciado."); }
    public void msgCancelamentoAvancoAno(int ano) { System.out.println(">> Transição cancelada. Mantemos o ano " + ano + "."); }
    public void mostrarCursoCanceladoFaltaUCs(String sigla) { System.out.println("   Erro: " + sigla + ": Sem estrutura curricular mínima."); }
    public void mostrarCancelamento() { System.out.println("\n>> Operação cancelada pelo utilizador."); }
    public void msgAnoIniciadoInscricoesFechadas() { System.out.println(">> O ano letivo já foi iniciado. As inscrições estão fechadas."); }
    public void msgSucessoRecuperacao() { System.out.println(">> Sucesso: Verifique a nova senha no seu email pessoal."); }
    public void msgErroDadosIncorretosOuFalhaEmail() { System.out.println(">> Erro: Utilizador e NIF não coorrespondem."); }
    public void msgErroArquivoNaoEncontrado(String c) { System.err.println(">> Erro: Ficheiro " + c + " não localizado."); }
    public void msgGestorNaoPodeRecuperar() { System.out.println(">> Utilizador Gestor não pode recuperar palavra-passe por este meio."); }
    public void msgErroDataFormato() { System.out.println(">> Erro: Formato inválido. Use DD-MM-AAAA."); }
    public void msgErroDataInexistente() { System.out.println(">> Erro: A data introduzida não existe no calendário."); }
    public void msgAnoJaIniciado() { System.out.println(">> O ano letivo já foi iniciado."); }
    public void msgExistemUcsSemAvaliacoes() { System.out.println(">> Existem UCs sem número de avaliações definido. Consulte o relatório."); }
    public void msgCursosDesativados() { System.out.println(">> Cursos foram desativados (consulte relatório)."); }
    public void msgInicioAnoSucesso(int ano) { System.out.println(">> Ano letivo " + ano + " iniciado com sucesso!"); }
    public void msgRelatorioGerado( String nomeFicheiro) { System.out.println(">> Relatório gerado: " + nomeFicheiro); }
    public void msgErroRelatorio() { System.out.println(">> Erro ao salvar o relatório."); }
    public void msgTransicaoBloqueada() { System.out.println(">> O ano letivo atual ainda não foi iniciado."); }
}