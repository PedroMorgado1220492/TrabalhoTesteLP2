package controller;

import model.*;

public class EstudanteController {

    public void verDados(Estudante e) {
        System.out.println("Número: " + e.getNumeroMecanografico());
        System.out.println("Nome: " + e.getNome());
        System.out.println("Email: " + e.getEmail());
        System.out.println("Curso: " + (e.getCurso() != null ? e.getCurso().getNome() : "Sem curso"));
        System.out.println("Ano 1a inscrição: " + e.getAnoPrimeiraInscricao());
    }

    public void verPercurso(Estudante e) {
        PercursoAcademico pa = e.getPercursoAcademico();
        if (pa == null) {
            System.out.println("Sem percurso académico.");
            return;
        }
        System.out.println("Ano Curricular: " + pa.getAnoCurricular());
        System.out.println("UCs Inscritas:");
        for (int i = 0; i < pa.getTotalUCsInscritas(); i++) {
            UnidadeCurricular uc = pa.getUcsInscritas()[i];
            System.out.println("- " + uc.getNome() + " (Ano: " + uc.getAnoCurricular() + ")");
        }

        System.out.println("UCs Aprovadas:");
        for (int i = 0; i < pa.getTotalUCsAprovadas(); i++) {
            UnidadeCurricular uc = pa.getUcsAprovadas()[i];
            double nota = pa.getNotas()[i];
            System.out.println("- " + uc.getNome() + " | Nota: " + nota);
        }
    }
}