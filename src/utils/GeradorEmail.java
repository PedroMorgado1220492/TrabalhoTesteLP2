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
}