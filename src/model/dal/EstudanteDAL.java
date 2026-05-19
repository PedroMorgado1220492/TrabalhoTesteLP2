package model.dal;

import model.bll.Estudante;
import model.bll.Curso;
import java.io.*;
import java.util.*;

/**
 * Classe DAL para gestão de Estudantes.
 * Ficheiro: bd/estudantes.csv
 */
public class EstudanteDAL {

    private static final String FILE_PATH = "bd/estudantes.csv";
    private CursoDAL cursoDAL;

    public EstudanteDAL() {
        cursoDAL = new CursoDAL();
    }

    private String obterPassword(String email) {
        return LoginDAL.obterPassword(email);
    }

    public Estudante buscar(int numMec) {
        for (Estudante e : buscarTodos()) {
            if (e.getNumeroMecanografico() == numMec) return e;
        }
        return null;
    }

    public List<Estudante> buscarTodos() {
        List<Estudante> estudantes = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return estudantes;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length < 12) continue;
                String pass = obterPassword(dados[2]);
                Curso curso = cursoDAL.buscar(dados[8]);
                Estudante e = new Estudante(
                        Integer.parseInt(dados[1]), dados[2], pass, dados[3],
                        dados[4], dados[5], dados[6], curso, Integer.parseInt(dados[7]), dados[9]
                );
                e.setAtivo(Boolean.parseBoolean(dados[10]));
                e.setAnoFrequencia(Integer.parseInt(dados[11]));
                estudantes.add(e);
            }
        } catch (IOException e) { }
        return estudantes;
    }

    public void inserir(Estudante e) {
        List<Estudante> lista = buscarTodos();
        lista.add(e);
        salvarTodos(lista);

        // REGISTAR LOGIN
        String passEncriptada = e.getPassword(); // Já vem encriptada
        LoginDAL.registarLogin("ESTUDANTE", e.getEmail(), passEncriptada);
    }

    public void atualizar(Estudante e) {
        List<Estudante> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNumeroMecanografico() == e.getNumeroMecanografico()) {
                lista.set(i, e);
                break;
            }
        }
        salvarTodos(lista);
    }

    public void remover(int numMec) {
        List<Estudante> lista = buscarTodos();
        lista.removeIf(e -> e.getNumeroMecanografico() == numMec);
        salvarTodos(lista);
    }

    private void salvarTodos(List<Estudante> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("TIPO;NUM_MEC;EMAIL;NOME;NIF;MORADA;DATANASCIMENTO;ANO_MATRICULA;CURSO;EMAIL_PESSOAL;ATIVO;ANO_FREQUENCIA");
            for (Estudante e : lista) {
                String cursoSigla = (e.getCurso() != null) ? e.getCurso().getSigla() : "";
                pw.printf("ESTUDANTE;%d;%s;%s;%s;%s;%s;%d;%s;%s;%b;%d\n",
                        e.getNumeroMecanografico(), e.getEmail(), e.getNome(),
                        e.getNif(), e.getMorada(), e.getDataNascimento(),
                        e.getAnoPrimeiraInscricao(), cursoSigla, e.getEmailPessoal(),
                        e.isAtivo(), e.getAnoFrequencia());
            }
        } catch (IOException e) { }
    }
}