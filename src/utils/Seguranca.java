package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import model.bll.Utilizador;
import model.dal.RepositorioDados;

/**
 * Classe utilitária responsável pelas operações de segurança e criptografia do sistema.
 * Implementa a cifra de palavras-passe através de algoritmos de hash unidirecional (SHA-256)
 * e gere de forma segura os fluxos de recuperação de credenciais.
 */
public class Seguranca {

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
     * @param emailLogin O endereço de email institucional submetido no pedido de recuperação.
     * @param nif        O Número de Identificação Fiscal (NIF) submetido como prova de titularidade.
     * @param repo       O repositório de dados onde os registos serão validados.
     * @return true se a validação for bem-sucedida e a conta atualizada; false caso os dados não correspondam.
     */
    public static boolean recuperarPassword(String emailLogin, String nif, RepositorioDados repo) {
        // Efetua uma busca transversal no repositório à procura da conta associada ao email
        Utilizador user = repo.procurarUtilizadorPorEmail(emailLogin);

        // O processo de redefinição só avança se a conta existir e o NIF registado coincidir exatamente
        if (user != null && user.getNif().equals(nif)) {

            // Geração em texto limpo de uma nova palavra-passe aleatória
            String novaPassRaw = GeradorPassword.generatePassword();

            // Encriptação e injeção imediata da nova password no perfil do utilizador
            user.setPassword(encriptar(novaPassRaw));

            // Aciona o serviço utilitário para expedir o email de notificação com a credencial temporária
            return ServicoEmail.enviarEmailRecuperacao(user, novaPassRaw);
        }

        // Retorna falso intencionalmente de forma genérica (não revela se o email existe ou se foi o NIF a falhar)
        return false;
    }
}