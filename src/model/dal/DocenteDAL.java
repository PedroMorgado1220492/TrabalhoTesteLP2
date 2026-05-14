package model.dal;

import model.bll.Docente;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Docentes.
 * Ficheiro: bd/docentes.csv
 */
public class DocenteDAL {

    private static final String FILE_PATH = "bd/docentes.csv";

    private String obterPassword(String email) {
        return LoginDAL.obterPassword(email);
    }

    public Docente buscar(String sigla) {
        for (Docente d : buscarTodos()) {
            if (d.getSigla().equalsIgnoreCase(sigla)) return d;
        }
        return null;
    }

    public List<Docente> buscarTodos() {
        List<Docente> lista = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length < 9) continue;
                String pass = obterPassword(p[2]);
                Docente d = new Docente(p[1], p[2], pass, p[3], p[4], p[5], p[6], p[7]);
                d.setAtivo(Boolean.parseBoolean(p[8]));
                lista.add(d);
            }
        } catch (IOException e) { }
        return lista;
    }

    public void inserir(Docente d) {
        List<Docente> lista = buscarTodos();
        lista.add(d);
        salvarTodos(lista);

        // REGISTAR LOGIN
        String passEncriptada = d.getPassword(); // Já vem encriptada
        LoginDAL.registarLogin("DOCENTE", d.getEmail(), passEncriptada);
    }

    public void atualizar(Docente d) {
        List<Docente> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getSigla().equalsIgnoreCase(d.getSigla())) {
                lista.set(i, d);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void remover(String sigla) {
        List<Docente> lista = buscarTodos();
        lista.removeIf(d -> d.getSigla().equalsIgnoreCase(sigla));
        salvarTodos(lista);
    }

    private void salvarTodos(List<Docente> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("TIPO;SIGLA;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO;EMAIL_PESSOAL;ATIVO");
            for (Docente d : lista) {
                pw.printf("DOCENTE;%s;%s;%s;%s;%s;%s;%s;%b\n",
                        d.getSigla(), d.getEmail(), d.getNome(), d.getNif(),
                        d.getMorada(), d.getDataNascimento(), d.getEmailPessoal(), d.isAtivo());
            }
        } catch (IOException e) { }
    }
}