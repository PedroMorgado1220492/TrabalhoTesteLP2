package utils;

public class Validador {

    // ---------- CONSTRUTOR ----------
    /**
     * Construtor privado para evitar instanciação.
     * Esta é uma classe utilitária estática.
     */
    private Validador() {}

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Valida se um nome contém pelo menos o primeiro e último nome (separados por espaço)
     * e se é constituído unicamente por letras (incluindo acentuadas).
     * * @param nome Nome a validar.
     * @return true se o nome for válido, false caso contrário.
     */
    public static boolean isNomeValido(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        String[] partes = nome.trim().split(" ");
        if (partes.length < 2) {
            return false;
        }

        return nome.matches("^[a-zA-ZÀ-ÿ\\s]+$");
    }

    /**
     * Valida se o Número de Identificação Fiscal (NIF) contém exatamente 9 dígitos numéricos.
     * * @param nif NIF a validar.
     * @return true se o NIF for válido, false caso contrário.
     */
    public static boolean isNifValido(String nif) {
        if (nif == null) {
            return false;
        }
        return nif.matches("^[0-9]{9}$");
    }

    /**
     * Valida se a data de nascimento obedece rigorosamente ao padrão DD-MM-AAAA
     * e se os dias/meses inseridos se encontram em limites lógicos padrão.
     * * @param data Data a validar.
     * @return true se a data possuir um formato correto, false caso contrário.
     */
    public static boolean isDataNascimentoValida(String data) {
        if (data == null) {
            return false;
        }
        return data.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-[0-9]{4}$");
    }
}