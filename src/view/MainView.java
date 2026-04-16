package view;

/**
 * Interface de utilizador principal (View) do sistema ISSMF.
 * Responsável por toda a interação de entrada e saída (I/O) no terminal,
 * garantindo a separação entre a lógica de apresentação e a lógica de negócio (MVC).
 */
public class MainView {

    public MainView() {

    }

    // ---------- MENUS PRINCIPAIS E CABEÇALHOS ----------

    public void mostrarBemVindo() { System.out.println(">> Bem-vindo ao Sistema do ISSMF!"); }

    public void mostrarAnoLetivo(int ano) { System.out.println(">> --- Ano Letivo Atual: " + ano + " ---"); }

    public int mostrarMenu() {
        System.out.println("\n===== SISTEMA ISSMF =====");
        System.out.println("1 - Login");
        System.out.println("2 - Criar Estudante");
        System.out.println("3 - Avançar Ano");
        System.out.println("4 - Recuperar Password");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");

        return utils.Consola.lerOpcaoMenu();
    }

    public void mostrarCabecalhoLogin() { System.out.println("\n--- LOGIN DO SISTEMA ---"); }

    public void mostrarCabecalhoRegisto() { System.out.println("\n--- NOVO REGISTO DE ESTUDANTE ---"); }

    public void mostrarCabecalhoTransicao() { System.out.println("\n--- TRANSIÇÃO DE ANO LETIVO ---"); }

    // ---------- INPUTS DE DADOS ----------

    public String pedirEmail() {
        java.io.Console console = System.console();

        // Se estiver a correr num terminal real (tudo em modo texto)
        if (console != null) {
            System.out.print("Email: ");
            return new java.util.Scanner(System.in).nextLine().trim();
        }
        // A correr no IDE (Abre a janela gráfica para o Email)
        else {
            System.out.println(">> Por favor, introduza o Email na janela que acabou de abrir...");

            javax.swing.JFrame frameFantasma = new javax.swing.JFrame();
            frameFantasma.setAlwaysOnTop(true);
            frameFantasma.setLocationRelativeTo(null);

            String email = javax.swing.JOptionPane.showInputDialog(
                    frameFantasma,
                    "Introduza o Email Institucional:",
                    "Autenticação ISSMF",
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

            frameFantasma.dispose();

            return email != null ? email.trim() : "";
        }
    }

    /**
     * Lê a password do utilizador.
     * Tenta utilizar a consola nativa para ocultar os caracteres. Se não estiver
     * disponível (ex: execução dentro do IDE), abre um pop-up seguro nativo do Java.
     */
    public String pedirPassword() {
        java.io.Console console = System.console();

        // Se estiver a correr num terminal real (Windows cmd, Linux shell)
        if (console != null) {
            char[] passwordArray = console.readPassword("Password: ");
            return new String(passwordArray).trim();
        }
        // A correr no IDE (IntelliJ, Eclipse)
        else {
            System.out.println(">> Por favor, introduza a password na janela de segurança que acabou de abrir...");

            javax.swing.JFrame frameFantasma = new javax.swing.JFrame();
            frameFantasma.setAlwaysOnTop(true);
            frameFantasma.setLocationRelativeTo(null);


            javax.swing.JPasswordField pf = new javax.swing.JPasswordField();

            int opcao = javax.swing.JOptionPane.showConfirmDialog(
                    frameFantasma,
                    pf,
                    "Autenticação ISSMF",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION,
                    javax.swing.JOptionPane.PLAIN_MESSAGE
            );

            frameFantasma.dispose();

            if (opcao == javax.swing.JOptionPane.OK_OPTION) {
                return new String(pf.getPassword()).trim();
            }

            return "";
        }
    }

    public String pedirNome() { return utils.Consola.lerString("Nome (Nome e Sobrenome): "); }

    public String pedirNif() { return utils.Consola.lerString("NIF (9 dígitos): "); }

    public String pedirMorada() { return utils.Consola.lerString("Morada: "); }

    public String pedirDataNascimento() { return utils.Consola.lerString("Data de Nascimento (DD-MM-AAAA): "); }

    public String pedirEmailPessoal() { return utils.Consola.lerString("Email Pessoal: "); }

    public int pedirEscolhaCurso(model.bll.Curso[] cursos, int total) {
        System.out.println("\n--- Escolha o Curso ---");
        for (int i = 0; i < total; i++) {
            if (cursos[i] != null && cursos[i].isAtivo()) {
                System.out.println((i + 1) + " - " + cursos[i].getNome() + " (" + cursos[i].getSigla() + ")");
            } else if (cursos[i] != null) {
                System.out.println((i + 1) + " - [INDISPONÍVEL] " + cursos[i].getNome());
            }
        }
        System.out.print("Número do Curso: ");
        return utils.Consola.lerOpcaoMenu() - 1;
    }

    public boolean pedirConfirmacaoAvanco(int proximoAno) {
        String input = utils.Consola.lerString("Deseja mesmo avançar para o ano letivo " + proximoAno + "? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    /**
     * Solicita a confirmação final antes de persistir os dados no sistema.
     * @return true se o utilizador confirmar com 'S'.
     */
    public boolean confirmarDados() {
        String input = utils.Consola.lerString("\nOs dados estão corretos? (S/N): ");
        return input.equalsIgnoreCase("S");
    }

    // ---------- REVISÃO E EXIBIÇÃO DE DADOS ----------

    public void mostrarRevisaoEstudante(String nome, String nif, String morada, String dataNasc, String emailPessoal, String nomeCurso) {
        System.out.println("\n--- REVISÃO DE DADOS ---");
        System.out.println("Nome: " + nome + " | NIF: " + nif);
        System.out.println("Morada: " + morada + " | Nasc: " + dataNasc);
        System.out.println("Email Pessoal: " + emailPessoal);
        System.out.println("Curso: " + nomeCurso);
    }

    public void mostrarCredenciaisGeradas(int ano, int numMec, String email, String pass) {
        System.out.println("\n>> Estudante registado com sucesso no ano " + ano + "!");
        System.out.println(">> Nº Mec: " + numMec + " | Email: " + email);
    }

    // ---------- FEEDBACK AO UTILIZADOR (MENSAGENS E AVISOS) ----------

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
    public void msgOpcaoInvalida() { System.out.println(">> Erro: Opção inválida."); }
    public void msgErroEmailDominio() { System.out.println(">> Erro: O email deve pertencer ao domínio '@issmf.ipp.pt'."); }
    public void msgErroArquivoNaoEncontrado(String caminho) { System.err.println("[O Java não conseguiu encontrar o ficheiro: " + caminho); }
    public void msgErroInativo() { System.out.println(">> Erro: Esta conta encontra-se inativa. Contacte os serviços académicos."); }
    public void msgErroCursoInativo() { System.out.println(">> Erro: O curso selecionado encontra-se inativo e não aceita matrículas."); }
    public void msgRegistoCancelado() { System.out.println(">> Registo cancelado. Nenhuma alteração foi guardada."); }

    public void msgSucessoAvancoAno(int ano) { System.out.println(">> O sistema avançou para o ano letivo de " + ano + "."); }
    public void msgCancelamentoAvancoAno(int ano) { System.out.println(">> Operação cancelada. Mantemo-nos em " + ano + "."); }
    public void msgEncerramento() { System.out.println(">> A encerrar o sistema..."); }

    public void mostrarAvisoValidacaoCursos() { System.out.println("\n>> A verificar o número mínimo de alunos (5) para as turmas de 1º ano..."); }
    public void mostrarFimValidacao() { System.out.println(">> Validação concluída!\n"); }
    public void mostrarCursoAprovado(String sigla, int inscritos) { System.out.println("   [OK] " + sigla + " aprovado para o 1º ano (" + inscritos + " inscritos)."); }
    public void mostrarCursoCancelado(String sigla, int inscritos) { System.out.println("   [AVISO] " + sigla + " cancelado no 1º ano! Apenas " + inscritos + " inscritos."); }
    public void mostrarCursoCanceladoFaltaUCs(String sigla) { System.out.println("   [ERRO] " + sigla + " bloqueado! O curso não possui pelo menos 1 UC em cada ano (1º, 2º e 3º)."); }

    // --- Feedback de Recuperação e Emails ---

    public void msgSucessoRecuperacao() { System.out.println(">> Uma nova password foi gerada e enviada para o seu Email Pessoal."); }

    /**
     * Mensagem que cobre os dois cenários de falha na recuperação (erro humano nos dados ou erro técnico de SMTP).
     */
    public void msgErroDadosIncorretosOuFalhaEmail() {
        System.out.println(">> Erro: O Email/NIF estão incorretos, ou ocorreu uma falha técnica de rede no envio da notificação.");
    }

    public void msgSucessoEnvioEmail(String email) { System.out.println(">> Sucesso: Credenciais enviadas para o endereço: " + email); }
    public void msgErroEnvioEmail() { System.out.println(">> Aviso: Não foi possível enviar o email automático. Verifique a ligação à internet ou as credenciais SMTP do sistema."); }

}