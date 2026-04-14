package model.bll;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária responsável pela formatação e geração de pautas de avaliação.
 */
public class Pauta {

    /**
     * Gera um ficheiro de texto (.txt) com a pauta de notas de uma Unidade Curricular.
     *
     * @param uc     A Unidade Curricular em avaliação.
     * @param alunos A lista de estudantes inscritos na UC.
     * @return O caminho do ficheiro gerado ou null em caso de erro.
     */
    public static String gerarFicheiroPauta(UnidadeCurricular uc, Estudante[] alunos) {
        File diretorio = new File("pautas");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        String nomeUcFormatado = uc.getNome().replace(" ", "_");
        String caminhoTxt = "pautas/pauta_" + nomeUcFormatado + "_" + dataAtual + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(caminhoTxt))) {
            pw.println("PAUTA DE AVALIAÇÃO - " + uc.getNome());
            pw.println("=================================================");

            boolean temAlunosAvaliados = false;

            for (int i = 0; i < alunos.length; i++) {
                Estudante e = alunos[i];

                if (e != null && e.isAtivo()) {
                    Avaliacao av = e.getAvaliacaoAtual(uc.getSigla());

                    // Só imprime na pauta oficial os alunos que já têm pelo menos uma nota lançada
                    if (av != null && av.getTotalAvaliacoesLancadas() > 0) {
                        pw.println(e.getNumeroMecanografico() + " - " + e.getNome() + " | Notas: " + formatarNotasAluno(av));
                        temAlunosAvaliados = true;
                    }
                }
            }

            if (!temAlunosAvaliados) {
                pw.println("Não existem avaliações submetidas para esta Unidade Curricular.");
            }

            return caminhoTxt;

        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Formata o array de avaliações de um aluno numa string padronizada.
     *
     * @param av O objeto de Avaliação do aluno.
     * @return String formatada com as notas.
     */
    public static String formatarNotasAluno(Avaliacao av) {
        if (av == null || av.getTotalAvaliacoesLancadas() == 0) {
            return "Ainda sem notas.";
        }

        StringBuilder sb = new StringBuilder("[ ");
        for (int n = 0; n < av.getTotalAvaliacoesLancadas(); n++) {
            sb.append(av.getResultadosAvaliacoes()[n]).append(" ");
        }
        sb.append("] -> Média Atual: ").append(Math.round(av.calcularMedia() * 100.0) / 100.0);

        return sb.toString();
    }
}