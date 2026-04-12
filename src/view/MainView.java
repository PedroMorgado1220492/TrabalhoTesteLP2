package view;

import model.bll.Curso;
import java.util.Scanner;

public class MainView {

    private Scanner scanner;

    public MainView() {
        this.scanner = new Scanner(System.in);
    }

    // ---------- MENUS E ARRANQUE ----------

    public void mostrarBemVindo() { System.out.println(">> Bem-vindo ao Sistema do ISSMF!"); }

    public void mostrarAnoLetivo(int ano) { System.out.println(">> --- Ano Letivo Atual: " + ano + " ---"); }

    public int mostrarMenu() {
        System.out.println("\n===== SISTEMA ISSMF =====");
        System.out.println("1 - Login");
        System.out.println("2 - Criar Estudante");
        System.out.println("3 - Avançar Ano");
        System.out.println("4 - Guardar");
        System.out.println("0 - Sair Sem Guardar");
        System.out.print("Opção: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------- LOGIN ----------

    public void mostrarCabecalhoLogin() { System.out.println("\n--- LOGIN DO SISTEMA ---"); }

    public String pedirEmail() {
        System.out.print("Email: ");
        return scanner.nextLine().trim();
    }

    public String pedirPassword() {
        java.io.Console console = System.console();
        if (console != null) {
            char[] passwordArray = console.readPassword("Password: ");
            return new String(passwordArray).trim();
        } else {
            System.out.println("[Aviso: A password será visível no IDE para a tornar invisivel usar a Consola]");
            System.out.print("Password: ");
            return scanner.nextLine().trim();
        }
    }

    // ---------- REGISTO DE ESTUDANTE ----------

    public void mostrarCabecalhoRegisto() { System.out.println("\n--- NOVO REGISTO DE ESTUDANTE ---"); }

    public String pedirNome() {
        System.out.print("Nome (Nome e Sobrenome): ");
        return scanner.nextLine().trim();
    }

    public String pedirNif() {
        System.out.print("NIF (9 dígitos): ");
        return scanner.nextLine().trim();
    }

    public String pedirMorada() {
        System.out.print("Morada: ");
        return scanner.nextLine().trim();
    }

    public String pedirDataNascimento() {
        System.out.print("Data de Nascimento (DD-MM-AAAA): ");
        return scanner.nextLine().trim();
    }

    public int pedirEscolhaCurso(Curso[] cursos, int total) {
        System.out.println("\n--- Escolha o Curso ---");
        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
        }
        System.out.print("Número do Curso: ");
        try {
            return Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void mostrarCredenciaisGeradas(int ano, int numMec, String email, String pass) {
        System.out.println("\n>> Estudante registado com sucesso no ano " + ano + "!");
        System.out.println(">> Nº Mec: " + numMec + " | Email: " + email + " | Pass: " + pass);
    }

    // ---------- TRANSIÇÃO DE ANO E VALIDAÇÃO DE CURSOS ----------

    public void mostrarCabecalhoTransicao() { System.out.println("\n--- TRANSIÇÃO DE ANO LETIVO ---"); }

    public boolean pedirConfirmacaoAvanco(int proximoAno) {
        System.out.print("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N): ");
        return scanner.nextLine().trim().equalsIgnoreCase("S");
    }

    public void mostrarAvisoValidacaoCursos() { System.out.println("\n>> A verificar o número mínimo de alunos (5) para as turmas de 1º ano..."); }
    public void mostrarFimValidacao() { System.out.println(">> Validação concluída!\n"); }

    public void mostrarCursoAprovado(String sigla, int inscritos) {
        System.out.println("   [OK] " + sigla + " aprovado para o 1º ano (" + inscritos + " inscritos).");
    }

    public void mostrarCursoCancelado(String sigla, int inscritos) {
        System.out.println("   [AVISO] " + sigla + " cancelado no 1º ano! Apenas " + inscritos + " inscritos.");
    }

    // ---------- MENSAGENS E FEEDBACK ----------

    public void msgPrepararRegisto() { System.out.println(">> A preparar o sistema de registo..."); }
    public void msgErroLogin() { System.out.println(">> Erro: Email ou Password incorretos."); }
    public void msgValidacaoSucesso(String tipo) { System.out.println(">> Login validado (" + tipo + "). A abrir ficheiros necessários..."); }
    public void msgBemVindoRole(String role) { System.out.println(">> Bem-vindo " + role + "!"); }
    public void msgSessaoEncerrada() { System.out.println(">> Sessão encerrada. Dados guardados e memória libertada com sucesso."); }
    public void msgSemCursosParaRegisto() { System.out.println(">> Atenção: De momento não existem cursos disponíveis no sistema para inscrição. Tente mais tarde."); }
    public void msgErroNome() { System.out.println(">> Erro: O nome deve conter pelo menos nome e sobrenome, utilizando apenas letras."); }
    public void msgErroNif() { System.out.println(">> Erro: O NIF deve conter exatamente 9 dígitos numéricos."); }
    public void msgErroData() { System.out.println(">> Erro: A data deve respeitar estritamente o formato DD-MM-AAAA."); }
    public void msgErroNumeroInvalido() { System.out.println(">> Erro: Escolha um número válido."); }
    public void msgErroLimiteEstudantes() { System.out.println(">> Erro: Limite máximo de estudantes atingido."); }
    public void msgSucessoAvancoAno(int ano) { System.out.println(">> Sucesso! O sistema avançou para o ano letivo de " + ano + "."); }
    public void msgCancelamentoAvancoAno(int ano) { System.out.println(">> Operação cancelada. Mantemo-nos em " + ano + "."); }
    public void msgAvisoAutoSave() { System.out.println(">> Não é necessário guardar manualmente. O sistema tem Auto-Save inteligente ao sair dos menus!"); }
    public void msgEncerramento() { System.out.println(">> A encerrar o sistema..."); }
    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção inválida."); }
    public void msgErroEmailDominio() { System.out.println(">> Erro: O email deve pertencer ao domínio '@issmf.ipp.pt'."); }
    public void msgErroArquivoNaoEncontrado(String caminho) { System.err.println("[DEBUG/ALERTA] O Java não conseguiu encontrar o ficheiro: " + caminho); }
}