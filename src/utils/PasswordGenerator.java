package utils;

public class PasswordGenerator {

    // ---------- CONSTRUTOR ----------
    /**
     * Construtor privado para evitar instanciação.
     * Esta é uma classe utilitária estática.
     */
    private PasswordGenerator() {}

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Gera uma palavra-passe aleatória de 8 caracteres alfanuméricos.
     * Utilizada no primeiro registo de estudantes e docentes.
     * * @return A palavra-passe gerada.
     */
    public static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        for(int i = 0; i < 8; i++) {
            int index = (int)(Math.random() * chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}