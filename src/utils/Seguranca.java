package utils;

import model.bll.Estudante;
import model.bll.Docente;
import model.bll.Gestor;
import model.bll.Utilizador;
import model.dal.RepositorioDados;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe utilitária responsável pelas operações de segurança e criptografia do sistema.
 * Implementa a cifra de palavras-passe através de algoritmos de hash unidirecional (SHA-256)
 * e gere de forma segura os fluxos de recuperação de credenciais.
 *
 * @author ISSMF
 * @version 1.0
 */
public class Seguranca {

    /**
     * Construtor privado para impedir a instanciação da classe.
     * Segue o padrão Utility Class, já que todos os métodos expostos são estáticos.
     */
    private Seguranca() {}

    /**
     * Processa uma palavra-passe em texto limpo e converte-a numa Hash SHA-256 irreversível.
     * Este mecanismo garante que as credenciais nunca são armazenadas em formato legível,
     * protegendo os acessos mesmo em caso de extração indevida dos ficheiros da base de dados.
     *
     * @param password A palavra-passe original submetida pelo utilizador.
     * @return A representação hexadecimal alfanumérica da hash gerada.
     * @throws RuntimeException Caso o ambiente de execução não suporte o algoritmo SHA-256.
     */
    public static String encriptar(String password) {
        try {
            // Instanciação do algoritmo padrão de criptografia SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Conversão da string para um array de bytes e processamento numérico da hash
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            // Conversão dos bytes individuais para o formato hexadecimal (representação padrão 0-9, a-f)
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Interrompe o processo criticamente se a máquina virtual Java não suportar segurança básica
            throw new RuntimeException("Erro: Algoritmo de segurança não encontrado.");
        }
    }

    /**
     * Coordena o processo institucional de recuperação e redefinição de palavra-passe.
     * Exige a validação cruzada de dois pontos de dados (Email Institucional e NIF)
     * para mitigar tentativas de acesso ilegítimo ou sequestro de conta.
     *
     * O fluxo de recuperação executa os seguintes passos:
     * <ul>
     *   <li>Valida o email e NIF em conjunto através do repositório</li>
     *   <li>Gera uma nova password aleatória</li>
     *   <li>Encripta a nova password e atualiza o perfil do utilizador</li>
     *   <li>Persiste as alterações nos respetivos ficheiros CSV</li>
     *   <li>Atualiza o ficheiro de logins com a nova password</li>
     *   <li>Envia um email de notificação com as novas credenciais</li>
     * </ul>
     *
     * @param emailLogin O endereço de email institucional submetido no pedido de recuperação.
     * @param nif        O Número de Identificação Fiscal (NIF) submetido como prova de titularidade.
     * @param repo       O repositório de dados onde os registos serão validados e atualizados.
     * @return true se a validação for bem-sucedida e a conta atualizada; false caso os dados não correspondam.
     */
    public static boolean recuperarPassword(String emailLogin, String nif, RepositorioDados repo) {
        // Busca direta por email e NIF combinados (apenas Estudantes e Docentes)
        Utilizador user = encontrarUtilizadorPorEmailENif(emailLogin, nif, repo);

        if (user != null) {
            // Geração em texto limpo de uma nova palavra-passe aleatória
            String novaPassRaw = GeradorPassword.generatePassword();

            // Encriptação e injeção imediata da nova password no perfil do utilizador
            String novaPassEnc = encriptar(novaPassRaw);
            user.setPassword(novaPassEnc);

            // Persistir a alteração no respetivo ficheiro CSV
            if (user instanceof Estudante) {
                repo.atualizarEstudante((Estudante) user);
            } else if (user instanceof Docente) {
                repo.atualizarDocente((Docente) user);
            }

            // Atualizar também no ficheiro de logins centralizado
            model.dal.LoginDAL.atualizarPassword(emailLogin, novaPassEnc);

            // Aciona o serviço utilitário para expedir o email de notificação com a nova credencial
            return ServicoEmail.enviarEmailRecuperacao(user, novaPassRaw);
        }

        // Retorna falso intencionalmente de forma genérica
        // (não revela se o email existe ou se foi o NIF a falhar, por razões de segurança)
        return false;
    }

    /**
     * Procura um utilizador pelo email e NIF percorrendo todas as listas do repositório.
     * Apenas procura em Estudantes e Docentes, pois Gestores têm NIF="N/A" e não podem recuperar password.
     *
     * @param email Email do utilizador a procurar
     * @param nif NIF do utilizador a procurar
     * @param repo Repositório de dados
     * @return Utilizador encontrado (Estudante ou Docente) ou null
     */
    private static Utilizador encontrarUtilizadorPorEmailENif(String email, String nif, RepositorioDados repo) {
        String nifInput = nif.trim();

        // Procurar em Docentes (usando o método getDocentes que agora está correto)
        for (Docente d : repo.getDocentes()) {
            if (d != null && d.getEmail().equalsIgnoreCase(email) && d.getNif().equals(nifInput)) {
                return d;
            }
        }

        // Procurar em Estudantes
        for (Estudante e : repo.getEstudantes()) {
            if (e != null && e.getEmail().equalsIgnoreCase(email) && e.getNif().equals(nifInput)) {
                return e;
            }
        }

        return null;
    }
}