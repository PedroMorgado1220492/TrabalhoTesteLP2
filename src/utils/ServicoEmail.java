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
    private static final String PASSWORD_EMAIL = "pgwk vsuc hfhl cksq";

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
            // O erro é registado apenas para log interno do servidor, não para o utilizador final aqui
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
     * Envia um e-mail com um ficheiro em anexo.
     * * @param destino O e-mail do destinatário.
     * @param assunto O assunto do e-mail.
     * @param corpoTexto A mensagem de texto no corpo do e-mail.
     * @param caminhoAnexo O caminho (path) para o ficheiro a ser anexado.
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

            // Parte 2: O Ficheiro Anexo
            MimeBodyPart attachmentPart = new MimeBodyPart();
            try {
                attachmentPart.attachFile(caminhoAnexo);
            } catch (Exception ex) {
                return false; // Falha a ler o ficheiro
            }
            multipart.addBodyPart(attachmentPart);

            // Junta tudo e envia
            message.setContent(multipart);
            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            return false;
        }
    }
}