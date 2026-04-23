package utils;

public class GeradorEmail {

    // ---------- CONSTRUTOR ----------
    /**
     * Construtor privado para evitar instanciação.
     * Esta é uma classe utilitária estática.
     */
    private GeradorEmail() {}

    // ---------- MÉTODOS DE LÓGICA E AÇÃO ----------

    /**
     * Gera o endereço de email institucional para um Estudante.
     * Regra: numeromecanografico@issmf.ipp.pt
     * * @param numeroMecanografico Número de identificação do estudante.
     * @return O email gerado em formato String.
     */
    public static String gerarEmailEstudante(int numeroMecanografico) {
        return numeroMecanografico + "@issmf.ipp.pt";
    }

    /**
     * Gera o endereço de email institucional para um Docente.
     * Regra: sigla@issmf.ipp.pt (em minúsculas).
     * * @param sigla Sigla de 3 letras do docente.
     * @return O email gerado em formato String.
     */
    public static String gerarEmailDocente(String sigla) {
        return sigla.toLowerCase() + "@issmf.ipp.pt";
    }

    /**
     * Gera automaticamente um endereço de email institucional para um Gestor (Backoffice).
     * O formato gerado segue o padrão: "backoffice_" + nome (em minúsculas e sem espaços nas extremidades) + "@issmf.ipp.pt".
     * * @param nome O nome do Gestor a ser utilizado na construção do email.
     * @return Uma {@code String} contendo o endereço de email gerado (ex: backoffice_joao@issmf.ipp.pt).
     */
    public static String gerarEmailGestor(String nome) {
        return "backoffice_" + nome.trim().toLowerCase() + "@issmf.ipp.pt";
    }

}