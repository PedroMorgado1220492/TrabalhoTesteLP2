package utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe utilitária responsável pela validação de dados de entrada (Input Validation).
 * Implementa verificações rigorosas através de expressões regulares para garantir que
 * os dados demográficos (Nome, NIF, Data) cumprem os requisitos de integridade do sistema.
 */
public class Validador {

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor privado para impedir a instanciação da classe.
     * Sendo uma classe de utilidade estática, não deve ser instanciada.
     */
    private Validador() {}

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Valida se um nome cumpre os requisitos mínimos de legibilidade e formato.
     * Critérios: conter pelo menos primeiro e último nome (separados por espaço)
     * e ser constituído exclusivamente por letras (incluindo caracteres acentuados).
     * * @param nome O nome a ser validado.
     * @return true se o nome possuir pelo menos duas palavras e apenas caracteres alfabéticos; false caso contrário.
     */
    public static boolean isNomeValido(String nome) {
        // Verifica se a string é nula ou composta apenas por espaços brancos
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        // Divide a string por espaços para validar a existência de apelido (pelo menos 2 partes)
        String[] partes = nome.trim().split(" ");
        if (partes.length < 2) {
            return false;
        }

        /* * Expressão Regular:
         * ^[a-zA-ZÀ-ÿ\s]+$
         * Garante que a string contém apenas letras (maiúsculas, minúsculas e acentuadas) e espaços.
         */
        return nome.matches("^[a-zA-ZÀ-ÿ\\s]+$");
    }

    /**
     * Valida se o Número de Identificação Fiscal (NIF) obedece ao padrão nacional.
     * Critério: Conter exatamente 9 dígitos numéricos.
     * * @param nif O NIF a ser validado.
     * @return true se a string for composta por precisamente 9 algarismos; false caso contrário.
     */
    public static boolean isNifValido(String nif) {
        if (nif == null) {
            return false;
        }

        // Expressão Regular que valida estritamente 9 dígitos numéricos
        return nif.matches("^[0-9]{9}$");
    }

    /**
     * Valida se uma data de nascimento respeita o formato estrutural e lógico do sistema.
     * Critério: Seguir rigorosamente o padrão DD-MM-AAAA, ser uma data real do calendário e não estar no futuro.
     * * @param data A data de nascimento em formato String.
     * @return true se a data for válida, real e no passado; false caso contrário.
     */
    public static boolean isDataNascimentoValida(String data) {
        if (data == null) {
            return false;
        }

        /* * 1. Validação de Formato Base (Regex)
         * Garante que tem a estrutura DD-MM-AAAA
         */
        if (!data.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-[0-9]{4}$")) {
            return false;
        }

        /*
         * 2. Validação Lógica de Calendário e Futuro
         */
        try {
            // Define o formato esperado
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            // Tenta converter a String para um objeto de Data (falha se for ex: 31 de Fevereiro)
            LocalDate dataNascimento = LocalDate.parse(data, formatter);

            // Verifica se a data introduzida é depois da data de hoje (futuro)
            if (dataNascimento.isAfter(LocalDate.now())) {
                return false;
            }

            return true;

        } catch (DateTimeParseException e) {

            return false;
        }
    }

    /**
     * Valida se um e-mail pertence ao domínio institucional.
     * @param email O e-mail a validar.
     * @return true se pertencer ao domínio @issmf.ipp.pt, false caso contrário.
     */
    public static boolean isEmailInstitucionalValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.toLowerCase().endsWith("@issmf.ipp.pt");
    }

    /**
     * Valida se o nome do Gestor é composto por apenas uma única palavra.
     * Aceita apenas letras (incluindo acentuação portuguesa), sem espaços.
     *
     * @param nome A string a validar.
     * @return true se for uma única palavra válida, false caso contrário.
     */
    public static boolean isNomeGestorValido(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        // A expressão regular ^[a-zA-ZÀ-ÿ]+$ garante que tem apenas letras e nenhum espaço
        return nome.trim().matches("^[a-zA-ZÀ-ÿ]+$");
    }
}