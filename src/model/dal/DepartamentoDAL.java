package model.dal;

import model.bll.Departamento;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Departamentos.
 * Ficheiro: bd/departamentos.csv
 */
public class DepartamentoDAL {

    private static final String FILE_PATH = "bd/departamentos.csv";

    public Departamento buscar(String sigla) {
        for (Departamento d : buscarTodos()) {
            if (d.getSigla().equalsIgnoreCase(sigla)) return d;
        }
        return null;
    }

    public List<Departamento> buscarTodos() {
        List<Departamento> lista = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho (agora tem 4 colunas)
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length < 3) continue;

                Departamento dep = new Departamento(p[1], p[2]);

                // Ler o estado ativo se existir (para compatibilidade com ficheiros antigos)
                if (p.length >= 4) {
                    dep.setAtivo(Boolean.parseBoolean(p[3]));
                } else {
                    dep.setAtivo(true); // valor default para backwards compatibility
                }

                lista.add(dep);
            }
        } catch (IOException e) { }
        return lista;
    }

    public void inserir(Departamento d) {
        List<Departamento> lista = buscarTodos();
        lista.add(d);
        salvarTodos(lista);
    }

    public void atualizar(Departamento d) {
        List<Departamento> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getSigla().equalsIgnoreCase(d.getSigla())) {
                lista.set(i, d);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void remover(String sigla) {
        List<Departamento> lista = buscarTodos();
        lista.removeIf(d -> d.getSigla().equalsIgnoreCase(sigla));
        salvarTodos(lista);
    }

    private void salvarTodos(List<Departamento> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("TIPO;SIGLA;NOME;ATIVO");  // Adicionar coluna ATIVO
            for (Departamento d : lista) {
                pw.printf("DEPARTAMENTO;%s;%s;%b\n", d.getSigla(), d.getNome(), d.isAtivo());
            }
        } catch (IOException e) { }
    }
}