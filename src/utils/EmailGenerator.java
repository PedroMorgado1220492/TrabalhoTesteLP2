// Ficheiro: utils/EmailGenerator.java
package utils;

public class EmailGenerator {

    // Regra: numero mecanografico @ issmf.ipp.pt
    public static String gerarEmailEstudante(int numeroMecanografico) {
        return numeroMecanografico + "@issmf.ipp.pt";
    }

    // Regra: sigla de 3 letras @ issmf.ipp.pt
    public static String gerarEmailDocente(String sigla) {
        return sigla.toLowerCase() + "@issmf.ipp.pt";
    }
}