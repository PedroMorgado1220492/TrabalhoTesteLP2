package model.bll;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Certificado {

    public static void gerarCertificado(Estudante e, int anoConclusao) {
        String caminho = "bd/certificado_" + e.getNumeroMecanografico() + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminho))) {
            pw.println("=====================================================");
            pw.println("             CERTIFICADO DE CONCLUSÃO                ");
            pw.println("=====================================================");
            pw.println("O aluno " + e.getNome() + ", com a morada " + e.getMorada() + ",");
            pw.println("número de contribuinte " + e.getNif() + " terminou o curso");

            String nomeCurso = (e.getCurso() != null) ? e.getCurso().getNome() : "Desconhecido";
            pw.println("no ano " + anoConclusao + ", curso de " + nomeCurso + ",");

            double media = calcularMediaFinal(e);
            pw.println("com média final de " + Math.round(media * 100.0) / 100.0 + " valores.");
            pw.println("=====================================================");
            pw.println("UNIDADES CURRICULARES CONCLUÍDAS:");

            for (int ano = 1; ano <= 3; ano++) {
                pw.println("\n--- " + ano + "º ANO ---");
                for (int i = 0; i < e.getTotalHistorico(); i++) {
                    Avaliacao av = e.getHistoricoAvaliacoes()[i];
                    if (av != null && av.getUc().getAnoCurricular() == ano) {
                        pw.println("  [" + av.getUc().getSigla() + "] " + av.getUc().getNome() +
                                " | Nota Final: " + Math.round(av.calcularMedia() * 100.0) / 100.0);
                    }
                }
            }
            pw.println("=====================================================");
        } catch (IOException ex) {
            // Falha silenciosa
        }
    }

    private static double calcularMediaFinal(Estudante e) {
        double soma = 0;
        int count = 0;
        for(int i = 0; i < e.getTotalHistorico(); i++) {
            Avaliacao av = e.getHistoricoAvaliacoes()[i];
            if (av != null && av.calcularMedia() >= 9.5) {
                soma += av.calcularMedia();
                count++;
            }
        }
        return count > 0 ? soma / count : 0.0;
    }
}