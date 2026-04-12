package utils;

import model.bll.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class ServicoEmail {

    // Configurações do Remetente
    private static final String REMETENTE = "pedromorgado41@gmail.com";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORTA = "587";
    private static final String PASSWORD_EMAIL = "nyma moxb hnsa lzsa";

    private static Session configurarSessao() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORTA);

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE, PASSWORD_EMAIL);
            }
        });
    }

    /**
     * Envia e-mail de boas-vindas após criação de conta.
     */
    public static void enviarEmailBoasVindas(Utilizador user, String passRaw) {
        String destino = user.getEmailPessoal();
        if (destino == null || destino.isEmpty()) return;

        try {
            Message message = new MimeMessage(configurarSessao());
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject("Bem-vindo ao Sistema ISSMF - Credenciais de Acesso");

            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá ").append(user.getNome()).append(",\n\n");
            corpo.append("A sua conta foi criada com sucesso. Aqui estão os seus dados:\n");
            corpo.append("- Email de Login: ").append(user.getEmail()).append("\n");
            corpo.append("- Password: ").append(passRaw).append("\n\n");

            if (user instanceof Estudante) {
                Estudante e = (Estudante) user;
                corpo.append("- Curso: ").append(e.getCurso() != null ? e.getCurso().getNome() : "N/A").append("\n");
            } else if (user instanceof Docente) {
                Docente d = (Docente) user;
                corpo.append("- Unidades Curriculares:\n");
                for (int i = 0; i < d.getTotalUcsLecionadas(); i++) {
                    corpo.append("  * ").append(d.getUcsLecionadas()[i].getSigla()).append(" - ")
                            .append(d.getUcsLecionadas()[i].getNome()).append("\n");
                }
            }

            corpo.append("\nPor motivos de segurança, altere a sua password no primeiro acesso.");
            message.setText(corpo.toString());

            Transport.send(message);
            System.out.println(">> Email enviado para: " + destino);

        } catch (MessagingException e) {
            System.err.println(">> Erro ao enviar email: " + e.getMessage());
        }
    }

    /**
     * Envia e-mail com a nova password recuperada.
     */
    public static void enviarEmailRecuperacao(Utilizador user, String novaPass) {
        String destino = user.getEmailPessoal();

        // Verificação de segurança: garantir que o utilizador tem email pessoal
        if (destino == null || destino.isEmpty()) {
            System.err.println(">> Erro: Não foi possível enviar o email de recuperação. O utilizador não possui um Email Pessoal registado.");
            return;
        }

        try {
            Message message = new MimeMessage(configurarSessao());
            message.setFrom(new InternetAddress(REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            message.setSubject("Recuperação de Password - Sistema ISSMF"); // Assunto diferente

            // Construção do corpo do email
            StringBuilder corpo = new StringBuilder();
            corpo.append("Olá ").append(user.getNome()).append(",\n\n");
            corpo.append("Recebemos um pedido para recuperar a sua password no Sistema do ISSMF.\n");
            corpo.append("A sua password foi redefinida com sucesso. Aqui estão os seus novos dados de acesso:\n\n");

            corpo.append("- Email de Login: ").append(user.getEmail()).append("\n");
            corpo.append("- Nova Password: ").append(novaPass).append("\n\n");

            corpo.append("Por motivos de segurança, recomendamos vivamente que altere esta password no menu 'Atualizar Dados Pessoais' logo após iniciar sessão.\n\n");
            corpo.append("Com os melhores cumprimentos,\n");
            corpo.append("Equipa do Sistema ISSMF");

            message.setText(corpo.toString());

            Transport.send(message);
            System.out.println(">> Email de recuperação enviado com sucesso para o endereço: " + destino);

        } catch (MessagingException e) {
            System.err.println(">> Erro crítico ao tentar enviar email de recuperação: " + e.getMessage());
        }
    }
}