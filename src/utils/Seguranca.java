package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.bll.Utilizador;
import model.dal.RepositorioDados;

public class Seguranca {

    /**
     * Recebe uma password em texto limpo e devolve uma Hash SHA-256 irreversível.
     */
    public static String encriptar(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro: Algoritmo de segurança não encontrado.");
        }
    }

    public static boolean recuperarPassword(String emailLogin, String nif, RepositorioDados repo) {
        Utilizador user = repo.procurarUtilizadorPorEmail(emailLogin);

        // Verifica se encontrou o utilizador e se o NIF bate certo
        if (user != null && user.getNif().equals(nif)) {

            String novaPassRaw = PasswordGenerator.generatePassword();
            user.setPassword(encriptar(novaPassRaw));

            // Envia o email!
            ServicoEmail.enviarEmailRecuperacao(user, novaPassRaw);

            return true;
        }
        return false;
    }
}