package model.bll;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe utilitária responsável pela geração de certificados de conclusão de curso.
 * Exporta o percurso académico e a classificação final do estudante para um documento de texto estruturado.
 */
public class Certificado {

    /**
     * Gera um ficheiro de texto (.txt) com o certificado oficial de conclusão do estudante.
     * O documento consolida os dados demográficos, o curso, a média final e a listagem de classificações.
     *
     * @param e            A instância do estudante que concluiu o curso.
     * @param anoConclusao O ano letivo de transição em que o curso foi finalizado.
     */
    public static String gerarCertificado(Estudante e, int anoConclusao) {
// 1. Criar a pasta dedicada para certificados se ela não existir
        File diretorio = new File("certificados");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        // 2. Obter o próximo número sequencial e registar no CSV
        String csvCaminho = "bd/certificados.csv";

        registarNoCSV(csvCaminho, e.getNumeroMecanografico(), null, "NUM_MECANOGRAFICO");

        // 3. Define o caminho do documento de texto
        String caminhoTxt = "certificados/certificado_" + e.getNumeroMecanografico() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminhoTxt))) {

            // Impressão do cabeçalho institucional do documento
            pw.println("=====================================================");
            pw.println("             CERTIFICADO DE CONCLUSÃO                ");
            pw.println("=====================================================");

            // Impressão dos dados de identificação pessoal
            pw.println("O aluno " + e.getNome() + ", com a morada " + e.getMorada() + ",");
            pw.println("número de contribuinte " + e.getNif() + " terminou o curso");

            // Validação e impressão do nome do curso
            String nomeCurso = (e.getCurso() != null) ? e.getCurso().getNome() : "Desconhecido";
            pw.println("no ano " + anoConclusao + ", curso de " + nomeCurso + ",");

            // Processamento do cálculo e arredondamento da média final (2 casas decimais)
            double mediaDecimal = e.calcularMediaFinal();
            long mediaArredondada = Math.round(mediaDecimal);

            pw.println("com média final de " + mediaArredondada + " valores.");
            pw.println("=====================================================");
            pw.println("UNIDADES CURRICULARES CONCLUÍDAS:");

            // Estruturação visual das classificações agrupadas por ano curricular (1º, 2º e 3º ano)
            for (int ano = 1; ano <= 3; ano++) {
                pw.println("\n--- " + ano + "º ANO ---");
                for (int i = 0; i < e.getTotalHistorico(); i++) {
                    Avaliacao av = e.getHistoricoAvaliacoes()[i];

                    // Identifica no histórico as avaliações que correspondem ao ano em iteração
                    if (av != null && av.getUc().getAnoCurricular() == ano) {
                        pw.println("  [" + av.getUc().getSigla() + "] " + av.getUc().getNome() +
                                " | Nota Final: " + Math.round(av.calcularMedia() * 100.0) / 100.0);
                    }
                }
            }
            pw.println("=====================================================");
            pw.println("Certificado Identificador: " + e.getNumeroMecanografico());

            return caminhoTxt;

        } catch (IOException ex) {
            return null;
        }
    }

    private static void registarNoCSV(String caminho, int numMec, String extra, String cabecalho) {
        java.io.File ficheiro = new java.io.File(caminho);
        boolean ficheiroJaExiste = ficheiro.exists(); // Verifica se é a primeira vez que criamos o CSV

        try (PrintWriter pw = new PrintWriter(new FileWriter(ficheiro, true))) {
            // Se o ficheiro é novo, escrevemos o cabeçalho primeiro!
            if (!ficheiroJaExiste) {
                pw.println(cabecalho);
            }
            // Depois gravamos a linha de dados normal
            pw.println(numMec + (extra != null ? ";" + extra : ""));
        } catch(IOException e) {}
    }
}