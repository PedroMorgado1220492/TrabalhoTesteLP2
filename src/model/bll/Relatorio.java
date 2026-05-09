package model.bll;

import model.dal.RepositorioDados;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe utilitária responsável pela geração de relatórios de verificação
 * para início do ano letivo.
 */
public class Relatorio {

    /**
     * Classe que agrupa os resultados da validação para início do ano letivo.
     */
    public static class ResultadoValidacao {
        private boolean todasUcsDefinidas;
        private boolean algumCursoDesativado;
        private boolean podeIniciar;
        private String relatorioConteudo;

        public ResultadoValidacao(boolean todasUcsDefinidas, boolean algumCursoDesativado, String relatorioConteudo) {
            this.todasUcsDefinidas = todasUcsDefinidas;
            this.algumCursoDesativado = algumCursoDesativado;
            this.podeIniciar = todasUcsDefinidas;
            this.relatorioConteudo = relatorioConteudo;
        }

        public boolean isTodasUcsDefinidas() { return todasUcsDefinidas; }
        public boolean isAlgumCursoDesativado() { return algumCursoDesativado; }
        public String getRelatorioConteudo() { return relatorioConteudo; }
    }

    /**
     * Gera o relatório de verificação e retorna um objeto com os resultados estruturados.
     *
     * @param repositorio O repositório de dados.
     * @return Objeto ResultadoValidacao contendo o conteúdo do relatório e os resultados das verificações.
     */
    public static ResultadoValidacao gerarRelatorioInicioAno(RepositorioDados repositorio) {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATÓRIO DE VERIFICAÇÃO PARA INÍCIO DO ANO LETIVO\n");
        relatorio.append("================================================\n\n");
        relatorio.append("Ano letivo: ").append(repositorio.getAnoAtual()).append("\n\n");

        boolean algumCursoDesativado = false;
        boolean todasUcsDefinidas = true;

        // 1. Verificar cursos
        for (int i = 0; i < repositorio.getTotalCursos(); i++) {
            Curso curso = repositorio.getCursos()[i];
            if (curso == null) continue;

            // Contar alunos por ano
            int alunosAno1 = 0, alunosAno2 = 0, alunosAno3 = 0;
            for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
                Estudante e = repositorio.getEstudantes()[j];
                if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(curso.getSigla())) {
                    if (e.getAnoFrequencia() == 1) alunosAno1++;
                    else if (e.getAnoFrequencia() == 2) alunosAno2++;
                    else if (e.getAnoFrequencia() == 3) alunosAno3++;
                }
            }

            // Verificar estrutura
            boolean estruturaValida = curso.temEstruturaValida();

            if (!estruturaValida) {
                // Desativar curso e remover todos os alunos
                curso.setAtivo(false);
                algumCursoDesativado = true;
                relatorio.append("\n--- Curso ").append(curso.getSigla()).append(" - ").append(curso.getNome()).append(" ---\n");
                relatorio.append("Situação: ESTRUTURA INVÁLIDA - CURSO DESATIVADO\n");
                relatorio.append("Alunos removidos: 1º ano: ").append(alunosAno1)
                        .append(" | 2º ano: ").append(alunosAno2)
                        .append(" | 3º ano: ").append(alunosAno3).append("\n");
                // Remover alunos do curso
                for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
                    Estudante e = repositorio.getEstudantes()[j];
                    if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(curso.getSigla())) {
                        e.setAtivo(false);
                    }
                }
            } else if (alunosAno1 > 0 && alunosAno1 < 5) {
                // Curso não abre para 1º ano - remover alunos do 1º ano
                relatorio.append("\n--- Curso ").append(curso.getSigla()).append(" - ").append(curso.getNome()).append(" ---\n");
                relatorio.append("Situação: 1º ano com ").append(alunosAno1).append(" alunos (mínimo 5) - 1º ANO NÃO ABRE\n");
                if (alunosAno2 > 0 || alunosAno3 > 0) {
                    relatorio.append("Nota: Alunos do 2º e 3º ano mantêm-se inscritos.\n");
                }
                relatorio.append("Alunos: 1º ano: 0 (removidos) | 2º ano: ").append(alunosAno2)
                        .append(" | 3º ano: ").append(alunosAno3).append("\n");
                // Remover alunos do 1º ano
                for (int j = 0; j < repositorio.getTotalEstudantes(); j++) {
                    Estudante e = repositorio.getEstudantes()[j];
                    if (e != null && e.getCurso() != null && e.getCurso().getSigla().equals(curso.getSigla()) && e.getAnoFrequencia() == 1) {
                        e.setAtivo(false);
                    }
                }
            } else if (alunosAno1 == 0 && (alunosAno2 > 0 || alunosAno3 > 0)) {
                relatorio.append("\n--- Curso ").append(curso.getSigla()).append(" - ").append(curso.getNome()).append(" ---\n");
                relatorio.append("Situação: NÃO ACEITA NOVOS ALUNOS NO 1º ANO\n");
                relatorio.append("Alunos: 1º ano: 0 | 2º ano: ").append(alunosAno2)
                        .append(" | 3º ano: ").append(alunosAno3).append("\n");
            } else if (alunosAno1 >= 5) {
                relatorio.append("\n--- Curso ").append(curso.getSigla()).append(" - ").append(curso.getNome()).append(" ---\n");
                relatorio.append("Situação: ATIVO - ABRE PARA 1º ANO\n");
                relatorio.append("Alunos: 1º ano: ").append(alunosAno1)
                        .append(" | 2º ano: ").append(alunosAno2)
                        .append(" | 3º ano: ").append(alunosAno3).append("\n");
            } else {
                relatorio.append("\n--- Curso ").append(curso.getSigla()).append(" - ").append(curso.getNome()).append(" ---\n");
                relatorio.append("Situação: SEM ALUNOS - NÃO ABRE\n");
                relatorio.append("Alunos: 1º ano: 0 | 2º ano: 0 | 3º ano: 0\n");
            }
        }

        // 2. Verificar UCs sem número de avaliações definido (apenas para cursos com estrutura válida)
        relatorio.append("\n--- UCs sem número de avaliações definido ---\n");
        boolean encontrouUcSemAv = false;
        for (int i = 0; i < repositorio.getTotalUcs(); i++) {
            UnidadeCurricular uc = repositorio.getUcs()[i];
            if (uc == null) continue;

            boolean cursoComEstruturaValida = false;
            for (Curso c : uc.getCursos()) {
                if (c != null && c.temEstruturaValida() && c.isAtivo()) {
                    cursoComEstruturaValida = true;
                    break;
                }
            }
            if (cursoComEstruturaValida && uc.getNumAvaliacoes() == null) {
                encontrouUcSemAv = true;
                todasUcsDefinidas = false;
                String docente = (uc.getDocenteResponsavel() != null) ? uc.getDocenteResponsavel().getNome() : "Sem docente";
                relatorio.append("UC ").append(uc.getSigla()).append(" - ").append(uc.getNome())
                        .append(" | Docente responsável: ").append(docente).append("\n");
            }
        }

        if (!encontrouUcSemAv) {
            relatorio.append("(todas as UCs têm número de avaliações definido)\n");
        }

        if (!todasUcsDefinidas) {
            relatorio.append("\n--> O ano letivo NÃO pode ser iniciado. Faltam definir o número de avaliações nas UCs acima.\n");
        } else {
            relatorio.append("\n--> Todas as UCs têm número de avaliações definido. Ano letivo INICIADO com sucesso!\n");
        }

        return new ResultadoValidacao(todasUcsDefinidas, algumCursoDesativado, relatorio.toString());
    }

    /**
     * Salva o conteúdo do relatório num ficheiro de texto na pasta "relatorios".
     *
     * @param conteudo      Conteúdo do relatório.
     * @param nomeFicheiro  Nome do ficheiro (ex: "relatorio_inicio_ano_2026.txt").
     * @return true se o ficheiro foi gerado com sucesso, false caso contrário.
     */
    public static boolean salvarRelatorio(String conteudo, String nomeFicheiro) {
        // Criar a pasta "relatorios" se não existir
        File diretorio = new File("relatorios");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(diretorio + "/" + nomeFicheiro))) {
            pw.print(conteudo);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
            return false;
        }
    }

    /**
     * Imprime o relatório na consola.
     *
     * @param conteudo Conteúdo do relatório.
     */
    public static void imprimirRelatorio(String conteudo) {
        System.out.println();
        System.out.println(conteudo);
    }
}