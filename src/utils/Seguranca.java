package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}