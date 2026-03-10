package controller;

import model.*;

public class AuthController {

    private SistemaAcademico sistema;

    public AuthController(SistemaAcademico sistema) {
        this.sistema = sistema;
    }

    public Utilizador login(String email, String password) {

        // verificar estudantes
        Estudante[] estudantes = sistema.getEstudantes();
        int totalEstudantes = sistema.getTotalEstudantes();

        for (int i = 0; i < totalEstudantes; i++) {

            if (estudantes[i].getEmail().equals(email) &&
                    estudantes[i].verificarPassword(password)) {

                return estudantes[i];
            }
        }

        // verificar docentes
        Docente[] docentes = sistema.getDocentes();
        int totalDocentes = sistema.getTotalDocentes();

        for (int i = 0; i < totalDocentes; i++) {

            if (docentes[i].getEmail().equals(email) &&
                    docentes[i].verificarPassword(password)) {

                return docentes[i];
            }
        }

        // verificar gestor
        if (email.equals("backoffice@issmf.ipp.pt") && password.equals("admin")) {

            return new Gestor(email, password);
        }

        return null;
    }
}