package utils;

import model.bll.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Classe utilitária responsável pela comunicação externa via protocolo SMTP.
 * Gere a configuração do servidor de correio e a construção de mensagens.
 * As mensagens de feedback para o utilizador são delegadas aos controladores
 * através de retornos booleanos.
 */
public class ServicoEmail {

    // ---------- CONFIGURAÇÕES DO REMETENTE ----------
    private static final String REMETENTE = "backofficeissmf@gmail.com";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORTA = "587";
    private static final String PASSWORD_EMAIL = "zshi neph uhas efll";

    /**
     * Construtor privado para garantir o padrão de classe utilitária.
     */
    private ServicoEmail() {}

    /**
     * Configura as propriedades do protocolo SMTP.
     * @return Sessão autenticada.
     */
    private static Session configurarSessao() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORTA);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE, PASSWORD_EMAIL);
            }
        });
    }

    // ---------- MÉTODOS DE ENVIO (AGORA COM RETORNO LÓGICO) ----------

    /**
     * Tenta enviar um e-mail de boas-vindas.
     * * @param user    O utilizador registado.
     * @param passRaw A password em texto limpo.
     * @return true se o e-mail foi enviado com sucesso; false caso ocorra uma falha técnica.
     */
    public static boolean enviarEmailBoasVindas(Utilizador user, String passRaw) {
        String destino = user.getEmailPessoal();

        if (destino == null || destino.isEmpty()) {
            return false;
        }

        try {
            Message message = new MimeMessage(configurarSessao());
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject("Bem-vindo ao Sistema ISSMF - Credenciais de Acesso");

            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá ").append(user.getNome()).append(",\n\n");
            corpo.append("A sua conta foi criada com sucesso no Sistema ISSMF.\n\n");
            corpo.append("- Login: ").append(user.getEmail()).append("\n");
            corpo.append("- Password: ").append(passRaw).append("\n\n");

            if (user instanceof Estudante) {
                Estudante e = (Estudante) user;
                corpo.append("- Curso: ").append(e.getCurso() != null ? e.getCurso().getNome() : "N/A").append("\n");
            }

            corpo.append("\nAltere a sua password no primeiro acesso.");
            message.setText(corpo.toString());

            Transport.send(message);
            return true; // Sucesso

        } catch (MessagingException e) {
            return false; // Falha
        }
    }

    /**
     * Tenta enviar um e-mail de recuperação de password.
     * * @param user     O utilizador alvo.
     * @param novaPass A nova password gerada.
     * @return true se enviado; false se falhar.
     */
    public static boolean enviarEmailRecuperacao(Utilizador user, String novaPass) {
        String destino = user.getEmailPessoal();

        if (destino == null || destino.isEmpty()) {
            return false;
        }

        try {
            Message message = new MimeMessage(configurarSessao());
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject("Recuperação de Password - Sistema ISSMF");

            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá ").append(user.getNome()).append(",\n\n");
            corpo.append("A sua password foi redefinida. Utilize os dados abaixo para aceder:\n\n");
            corpo.append("- Login: ").append(user.getEmail()).append("\n");
            corpo.append("- Nova Password: ").append(novaPass).append("\n\n");
            corpo.append("Equipa ISSMF");

            message.setText(corpo.toString());

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            return false;
        }
    }

    /**
     * Prepara e envia o e-mail com o Certificado de Conclusão de Curso em anexo.
     * * @param emailDestino O e-mail do estudante.
     * @param nomeAluno O nome do estudante para personalizar a mensagem.
     * @param caminhoAnexo O caminho do ficheiro .txt do certificado.
     * @return true se foi enviado com sucesso, false caso contrário.
     */
    public static boolean enviarEmailCertificado(String emailDestino, String nomeAluno, String caminhoAnexo) {
        String assunto = "ISSMF - Certificado de Conclusão de Curso";
        String corpo = "Muitos parabéns " + nomeAluno + "!\n\n"
                + "É com enorme orgulho que lhe enviamos em anexo o seu Certificado de Conclusão de Curso.\n\n"
                + "Votos de muito sucesso profissional e pessoal!\n\n"
                + "A Direção do ISSMF.";

        return enviarEmailComAnexo(emailDestino, assunto, corpo, caminhoAnexo);
    }

    /**
     * Prepara e envia o e-mail com o Recibo de Pagamento de Propinas em anexo.
     *
     * @param emailDestino  O e-mail pessoal do estudante.
     * @param nomeAluno     O nome do aluno para personalizar (opcional, mas recomendado).
     * @param caminhoRecibo O caminho do ficheiro .txt do recibo.
     * @return true se enviado com sucesso.
     */
    public static boolean enviarEmailRecibo(String emailDestino, String nomeAluno, String caminhoRecibo) {
        String assunto = "ISSMF - Recibo de Pagamento";
        String corpo = "Caro(a) Estudante " + nomeAluno + ",\n\n"
                + "Segue em anexo o seu recibo de pagamento das propinas.\n\n"
                + "Com os melhores cumprimentos,\n"
                + "Serviços Financeiros - ISSMF.";

        return enviarEmailComAnexo(emailDestino, assunto, corpo, caminhoRecibo);
    }

    /**
     * Envia um e-mail com um ficheiro em anexo.
     * * @param destino O e-mail do destinatário.
     * @param assunto O assunto do e-mail.
     * @param corpoTexto A mensagem de texto no corpo do e-mail.
     * @param caminhoAnexo O caminho (path) para o ficheiro a ser anexado.
     * @return true se enviado com sucesso, false caso contrário.
     */
    /**
     * Envia um e-mail com um ficheiro em anexo (se existir).
     * @param destino O e-mail do destinatário.
     * @param assunto O assunto do e-mail.
     * @param corpoTexto A mensagem de texto no corpo do e-mail.
     * @param caminhoAnexo O caminho (path) para o ficheiro a ser anexado (pode ser null).
     * @return true se enviado com sucesso, false caso contrário.
     */
    public static boolean enviarEmailComAnexo(String destino, String assunto, String corpoTexto, String caminhoAnexo) {
        if (destino == null || destino.isEmpty()) {
            return false;
        }

        try {
            Message message = new MimeMessage(configurarSessao());
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject(assunto);

            // Criação de uma mensagem multi-partes (Texto + Anexo)
            Multipart multipart = new MimeMultipart();

            // Parte 1: Corpo de Texto
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(corpoTexto);
            multipart.addBodyPart(textPart);

            // Parte 2: O Ficheiro Anexo (Só envia se o caminho não for nulo)
            if (caminhoAnexo != null && !caminhoAnexo.isEmpty()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                try {
                    attachmentPart.attachFile(caminhoAnexo);
                } catch (Exception ex) {
                    return false; // Falha a ler o ficheiro
                }
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);
            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            return false;
        }
    }

    /**
     * Envia um e-mail ao estudante sempre que o docente lança uma avaliação individual,
     * listando todas as notas obtidas nessa UC no ano letivo atual.
     */
    public static boolean enviarEmailAvaliacao(Estudante estudante, String nomeUc, Avaliacao avaliacao) {
        String assunto = "ISSMF - Nova Avaliação Lançada: " + nomeUc;

        StringBuilder corpo = new StringBuilder();
        corpo.append("Caro(a) Estudante ").append(estudante.getNome()).append(",\n\n");
        corpo.append("Foi registada uma nova nota à unidade curricular de ").append(nomeUc).append(".\n\n");
        corpo.append("O seu registo atual de avaliações nesta UC é o seguinte:\n");

        for (int i = 0; i < avaliacao.getTotalAvaliacoesLancadas(); i++) {
            corpo.append("- Avaliação ").append(i + 1).append(": ")
                    .append(avaliacao.getResultadosAvaliacoes()[i]).append(" valores\n");
        }

        corpo.append("\nMédia atual: ").append(Math.round(avaliacao.calcularMedia() * 100.0) / 100.0).append(" valores\n\n");
        corpo.append("Votos de bom estudo,\nOs Serviços Académicos - ISSMF.");

        boolean enviadoPessoal = false;
        boolean enviadoInstitucional = false;

        // Envia para o email pessoal
        String emailPessoal = estudante.getEmailPessoal();
        if (emailPessoal != null && !emailPessoal.isEmpty()) {
            enviadoPessoal = enviarEmailComAnexo(emailPessoal, assunto, corpo.toString(), null);
        }

        // Envia para o email institucional
        String emailInstitucional = estudante.getEmail();
        if (emailInstitucional != null && !emailInstitucional.isEmpty()) {
            enviadoInstitucional = enviarEmailComAnexo(emailInstitucional, assunto, corpo.toString(), null);
        }

        return enviadoPessoal || enviadoInstitucional;
    }

    /**
     * Envia as credenciais do novo Gestor criado para um e-mail fixo de administração.
     */
    public static boolean enviarEmailNovoGestor(String emailGestor, String passRaw, String destino) {
        String assunto = "ISSMF - Novas Credenciais de Gestor (Backoffice)";
        String corpo = "Olá,\n\n"
                + "Foi gerada uma nova conta de Gestor no sistema ISSMF com os seguintes dados:\n\n"
                + "- Login: " + emailGestor + "\n"
                + "- Password: " + passRaw + "\n\n"
                + "A Equipa de Administração ISSMF.";

        return enviarEmailComAnexo(destino, assunto, corpo, null);
    }
}