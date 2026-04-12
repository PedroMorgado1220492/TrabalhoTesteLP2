package model.bll;

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
    public static void gerarCertificado(Estudante e, int anoConclusao) {
        // Define o caminho e o nome do ficheiro, garantindo a sua unicidade através do número mecanográfico
        String caminho = "bd/certificado_" + e.getNumeroMecanografico() + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
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
            double media = calcularMediaFinal(e);
            pw.println("com média final de " + Math.round(media * 100.0) / 100.0 + " valores.");
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
        } catch (IOException ex) {
            // Falha silenciosa: a geração não bloqueia a transição global de ano letivo em caso de erro no I/O
        }
    }

    /**
     * Calcula a classificação final do curso baseando-se no histórico de avaliações do estudante.
     *
     * @param e A instância do estudante.
     * @return O valor da média aritmética ponderada, ou 0.0 caso não constem avaliações.
     */
    private static double calcularMediaFinal(Estudante e) {
        double soma = 0;
        int count = 0;

        // Itera pelo histórico consolidado de avaliações do aluno
        for(int i = 0; i < e.getTotalHistorico(); i++) {
            Avaliacao av = e.getHistoricoAvaliacoes()[i];

            // Filtra e contabiliza exclusivamente as unidades curriculares realizadas com aproveitamento positivo
            if (av != null && av.calcularMedia() >= 9.5) {
                soma += av.calcularMedia();
                count++;
            }
        }

        // Previne erros de divisão por zero na devolução do resultado final
        return count > 0 ? soma / count : 0.0;
    }
}