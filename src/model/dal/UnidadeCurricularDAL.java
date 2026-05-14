package model.dal;

import model.bll.UnidadeCurricular;
import model.bll.Docente;
import model.bll.Curso;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Unidades Curriculares.
 * Ficheiro: bd/ucs.csv
 */
public class UnidadeCurricularDAL {

    private static final String FILE_PATH = "bd/ucs.csv";
    private DocenteDAL docenteDAL;
    private CursoDAL cursoDAL;

    public UnidadeCurricularDAL() {
        docenteDAL = new DocenteDAL();
        cursoDAL = new CursoDAL();

        File dir = new File("bd");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public UnidadeCurricular buscar(String sigla) {
        for (UnidadeCurricular uc : buscarTodos()) {
            if (uc.getSigla().equalsIgnoreCase(sigla)) return uc;
        }
        return null;
    }

    // Método original sem parâmetros (para compatibilidade)
    public List<UnidadeCurricular> buscarTodos() {
        return buscarTodos(new ArrayList<>());
    }

    public List<UnidadeCurricular> buscarTodos(List<Curso> cursosExistentes) {
        List<UnidadeCurricular> lista = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] p = linha.split(";");
                if (p.length < 7) continue;

                Docente docente = docenteDAL.buscar(p[4]);
                UnidadeCurricular uc = new UnidadeCurricular(p[1], p[2], Integer.parseInt(p[3]), docente);
                uc.setAtivo(Boolean.parseBoolean(p[6]));

                if (p.length > 7 && !p[7].isEmpty()) {
                    uc.setNumAvaliacoes(Integer.parseInt(p[7]));
                }

                // Associar o docente à UC (importante!)
                if (docente != null) {
                    docente.adicionarUcLecionada(uc);
                    if (uc.getDocenteResponsavel() != null &&
                            uc.getDocenteResponsavel().getSigla().equals(docente.getSigla())) {
                        docente.adicionarUcResponsavel(uc);
                    }
                }

                // Associar cursos
                if (p.length > 5 && !p[5].isEmpty()) {
                    String[] siglasCursos = p[5].split(",");
                    for (String siglaCurso : siglasCursos) {
                        Curso curso = null;
                        for (Curso c : cursosExistentes) {
                            if (c.getSigla().equalsIgnoreCase(siglaCurso.trim())) {
                                curso = c;
                                break;
                            }
                        }
                        if (curso == null) {
                            curso = cursoDAL.buscar(siglaCurso.trim());
                        }
                        if (curso != null) {
                            uc.adicionarCurso(curso);
                            curso.adicionarUnidadeCurricular(uc);
                        }
                    }
                }

                lista.add(uc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void inserir(UnidadeCurricular uc) {
        List<UnidadeCurricular> lista = buscarTodos();
        lista.add(uc);
        salvarTodos(lista);
    }

    public void atualizar(UnidadeCurricular uc) {
        List<UnidadeCurricular> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getSigla().equalsIgnoreCase(uc.getSigla())) {
                lista.set(i, uc);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void remover(String sigla) {
        List<UnidadeCurricular> lista = buscarTodos();
        lista.removeIf(uc -> uc.getSigla().equalsIgnoreCase(sigla));
        salvarTodos(lista);
    }

    private void salvarTodos(List<UnidadeCurricular> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("TIPO;SIGLA;NOME;ANO;DOCENTE_RESPONSAVEL;CURSOS;ATIVO;NUM_AVALIACOES");
            for (UnidadeCurricular uc : lista) {
                String docenteSigla = (uc.getDocenteResponsavel() != null) ? uc.getDocenteResponsavel().getSigla() : "";

                StringBuilder cursosSiglas = new StringBuilder();
                for (Curso c : uc.getCursos()) {
                    if (c != null) {
                        if (cursosSiglas.length() > 0) cursosSiglas.append(",");
                        cursosSiglas.append(c.getSigla());
                    }
                }

                String numAvaliacoes = (uc.getNumAvaliacoes() == null) ? "" : String.valueOf(uc.getNumAvaliacoes());

                pw.printf("UC;%s;%s;%d;%s;%s;%b;%s\n",
                        uc.getSigla(), uc.getNome(), uc.getAnoCurricular(),
                        docenteSigla, cursosSiglas.toString(), uc.isAtivo(), numAvaliacoes);
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar UCs: " + e.getMessage());
        }
    }
}