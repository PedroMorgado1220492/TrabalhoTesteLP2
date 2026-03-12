// Ficheiro: utils/Validador.java
package utils;

public class Validador {

    // Valida se tem pelo menos duas palavras e apenas letras (incluindo acentos) e espaços
    public static boolean isNomeValido(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        // Verifica se tem pelo menos um espaço no meio para separar o nome do sobrenome
        String[] partes = nome.trim().split(" ");
        if (partes.length < 2) {
            return false;
        }

        // Regex: Aceita letras minúsculas, maiúsculas, letras com acentos (português) e espaços
        return nome.matches("^[a-zA-ZÀ-ÿ\\s]+$");
    }

    // Valida se tem exatamente 9 dígitos numéricos
    public static boolean isNifValido(String nif) {
        if (nif == null) {
            return false;
        }
        // Regex: Exatamente 9 números de 0 a 9
        return nif.matches("^[0-9]{9}$");
    }

    // Valida o padrão DD-MM-AAAA
    public static boolean isDataNascimentoValida(String data) {
        if (data == null) {
            return false;
        }
        // Regex: 
        // Dia: 01-09, 10-29 ou 30-31
        // Mês: 01-09 ou 10-12
        // Ano: 4 dígitos numéricos
        return data.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-[0-9]{4}$");
    }
}