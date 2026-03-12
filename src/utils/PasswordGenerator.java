package utils;

public class PasswordGenerator {

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