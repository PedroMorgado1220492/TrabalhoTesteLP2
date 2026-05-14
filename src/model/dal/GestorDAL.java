package model.dal;

import model.bll.Gestor;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Gestores.
 * Ficheiro: bd/gestores.csv
 */
public class GestorDAL {

    private static final String FILE_PATH = "bd/gestores.csv";

    private String obterPassword(String email) {
        return LoginDAL.obterPassword(email);
    }

    public Gestor buscar(String email) {
        for (Gestor g : buscarTodos()) {
            if (g.getEmail().equalsIgnoreCase(email)) return g;
        }
        return null;
    }

    public List<Gestor> buscarTodos() {
        List<Gestor> lista = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(";");
                if (p.length < 5) continue;
                String pass = obterPassword(p[1]);
                Gestor g = new Gestor(p[1], pass, p[2], p[3]);
                g.setAtivo(Boolean.parseBoolean(p[4])); // LER O ESTADO
                lista.add(g);
            }
        } catch (IOException e) { }
        return lista;
    }

    public void inserir(Gestor g) {
        List<Gestor> lista = buscarTodos();
        lista.add(g);
        salvarTodos(lista);

        // REGISTAR LOGIN
        String passEncriptada = g.getPassword(); // Já vem encriptada
        LoginDAL.registarLogin("GESTOR", g.getEmail(), passEncriptada);
    }

    public void atualizar(Gestor g) {
        List<Gestor> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getEmail().equalsIgnoreCase(g.getEmail())) {
                lista.set(i, g);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void remover(String email) {
        List<Gestor> lista = buscarTodos();
        lista.removeIf(g -> g.getEmail().equalsIgnoreCase(email));
        salvarTodos(lista);
    }

    private void salvarTodos(List<Gestor> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("TIPO;EMAIL;NOME;MORADA;ATIVO");
            for (Gestor g : lista) {
                pw.printf("GESTOR;%s;%s;%s;%b\n",
                        g.getEmail(), g.getNome(), g.getMorada(), g.isAtivo());
            }
        } catch (IOException e) { }
    }
}