public class EmailGenerator {

    public static String generateEmail(String nome) {

        String email = nome.toLowerCase().replace(" ", ".");
        return email + "@isep.ipp.pt";
    }
}