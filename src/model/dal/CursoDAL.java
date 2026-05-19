package model.dal;

import model.bll.Curso;
import model.bll.Departamento;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Cursos.
 * Ficheiro: bd/cursos.csv
 */
public class CursoDAL {

    private static final String FILE_PATH = "bd/cursos.csv";
    private DepartamentoDAL departamentoDAL;

    public CursoDAL() {
        departamentoDAL = new DepartamentoDAL();
    }

    public Curso buscar(String sigla) {
        for (Curso c : buscarTodos()) {
            if (c.getSigla().equalsIgnoreCase(sigla)) return c;
        }
        return null;
    }

    public List<Curso> buscarTodos() {
        List<Curso> cursos = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return cursos;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 5) continue;
                Departamento dep = departamentoDAL.buscar(dados[3]);
                if (dep == null) continue;
                Curso c = new Curso(dados[1], dados[2], dep);
                c.setAtivo(Boolean.parseBoolean(dados[4]));
                cursos.add(c);
            }
        } catch (IOException e) { }
        return cursos;
    }

    public void inserir(Curso c) {
        List<Curso> lista = buscarTodos();
        lista.add(c);
        salvarTodos(lista);
    }

    public void atualizar(Curso c) {
        List<Curso> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getSigla().equalsIgnoreCase(c.getSigla())) {
                lista.set(i, c);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void remover(String sigla) {
        List<Curso> lista = buscarTodos();
        lista.removeIf(c -> c.getSigla().equalsIgnoreCase(sigla));
        salvarTodos(lista);
    }

    private void salvarTodos(List<Curso> cursos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("TIPO;SIGLA;NOME;DEPARTAMENTO;ATIVO");
            for (Curso c : cursos) {
                pw.printf("CURSO;%s;%s;%s;%b\n",
                        c.getSigla(), c.getNome(), c.getDepartamento().getSigla(), c.isAtivo());
            }
        } catch (IOException e) { }
    }
}