package model.bll;

import utils.GeradorEmail;
import utils.GeradorPassword;

/**
 * Representa um Gestor (administrador) do sistema.
 * Esta classe atua como uma fábrica (Factory) para a instanciação das principais entidades
 * do domínio, garantindo a geração automática de credenciais e a aplicação de regras de negócio iniciais.
 */
public class Gestor extends Utilizador {

    // ---------- CONSTRUTOR ----------

    /**
     * Construtor da classe Gestor.
     * Inicializa o perfil com os dados essenciais de autenticação e contacto.
     * Nota: Os campos correspondentes ao NIF, data de nascimento e email pessoal
     * são omitidos (passados como vazios ou nulos) para este perfil estritamente administrativo.
     *
     * @param email    O email institucional (login) do gestor.
     * @param password A password encriptada para acesso ao sistema.
     * @param nome     O nome de registo do gestor.
     * @param morada   A morada ou localização física (ex: Campus).
     */
    public Gestor(String email, String password, String nome, String morada) {
        super(email, password, nome, "", morada, "", null);
    }

    // ---------- MÉTODOS DE LÓGICA E AÇÃO (FACTORY METHODS) ----------

    /**
     * Instancia um novo Estudante, gerando automaticamente as suas credenciais de acesso
     * e executando a matrícula automática nas Unidades Curriculares do primeiro ano.
     *
     * @param numeroMecanografico  O número mecanográfico único atribuído ao aluno.
     * @param nome                 O nome completo do estudante.
     * @param nif                  O Número de Identificação Fiscal.
     * @param morada               A morada de residência.
     * @param dataNascimento       A data de nascimento.
     * @param curso                O curso em que o aluno ingressou.
     * @param anoPrimeiraInscricao O ano letivo correspondente à matrícula inicial.
     * @param emailPessoal         O email pessoal do estudante para recuperação de dados.
     * @return O objeto Estudante recém-criado, configurado e matriculado.
     */
    public Estudante criarEstudante(int numeroMecanografico, String nome, String nif, String morada,
                                    String dataNascimento, Curso curso, int anoPrimeiraInscricao, String emailPessoal) {

        // Processamento automático de credenciais institucionais
        String emailGerado = GeradorEmail.gerarEmailEstudante(numeroMecanografico);
        String passwordGerada = GeradorPassword.generatePassword();

        Estudante novoEstudante = new Estudante(numeroMecanografico, emailGerado, passwordGerada, nome, nif, morada, dataNascimento, curso, anoPrimeiraInscricao, emailPessoal);

        // --- AUTO-ALOCAÇÃO (INSCRIÇÃO NAS UCs DE 1º ANO) ---
        // Regra de Negócio: O aluno é imediatamente inscrito nas disciplinas do ano base da sua matriz curricular
        if (curso != null && novoEstudante.getPercursoAcademico() != null) {
            for (int i = 0; i < curso.getTotalUCs(); i++) {
                UnidadeCurricular uc = curso.getUnidadesCurriculares()[i];

                if (uc.getAnoCurricular() == 1) {
                    novoEstudante.getPercursoAcademico().inscreverEmUc(uc);
                }
            }
        }

        return novoEstudante;
    }

    /**
     * Instancia um novo Docente, gerando automaticamente as suas credenciais institucionais.
     *
     * @param sigla          A sigla identificativa do docente (ex: ABC).
     * @param nome           O nome completo do docente.
     * @param nif            O Número de Identificação Fiscal.
     * @param morada         A morada de residência.
     * @param dataNascimento A data de nascimento.
     * @param emailPessoal   O email pessoal do docente para fins de segurança e contacto.
     * @return O objeto Docente devidamente instanciado.
     */
    public Docente criarDocente(String sigla, String nome, String nif, String morada, String dataNascimento, String emailPessoal) {
        // A geração do email baseia-se na sigla fornecida
        String emailGerado = GeradorEmail.gerarEmailDocente(sigla);
        String passwordGerada = GeradorPassword.generatePassword();

        return new Docente(sigla, emailGerado, passwordGerada, nome, nif, morada, dataNascimento, emailPessoal);
    }

    /**
     * Instancia um novo Departamento para agregação de cursos.
     *
     * @param sigla A sigla do departamento (ex: DEP-INF).
     * @param nome  O nome completo do departamento.
     * @return O objeto Departamento instanciado.
     */
    public Departamento criarDepartamento(String sigla, String nome) {
        return new Departamento(sigla, nome);
    }

    /**
     * Instancia um novo Curso, vinculando-o de forma imediata à orgânica de um Departamento.
     *
     * @param sigla        A sigla identificadora do curso.
     * @param nome         O nome do curso.
     * @param departamento O departamento que assume a tutela do curso.
     * @return O objeto Curso instanciado.
     */
    public Curso criarCurso(String sigla, String nome, Departamento departamento) {
        return new Curso(sigla, nome, departamento);
    }

    /**
     * Instancia uma nova Unidade Curricular e define a respetiva regência.
     *
     * @param sigla              A sigla atribuída à UC.
     * @param nome               O nome extenso da UC.
     * @param anoCurricular      A alocação da UC no plano de estudos (ex: 1º Ano).
     * @param docenteResponsavel O docente encarregue da coordenação administrativa da UC.
     * @return O objeto Unidade Curricular instanciado.
     */
    public UnidadeCurricular criarUnidadeCurricular(String sigla, String nome, int anoCurricular, Docente docenteResponsavel) {
        return new UnidadeCurricular(sigla, nome, anoCurricular, docenteResponsavel);
    }
}