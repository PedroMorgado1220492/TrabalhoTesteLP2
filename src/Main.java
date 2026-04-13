import controller.MainController;
import view.MainView;
import model.dal.RepositorioDados;

/**
 * Classe principal (Entry Point) do Sistema de Gestão Académica ISSMF.
 * Esta classe é responsável por orquestrar o arranque da aplicação,
 * instanciando os componentes core do padrão MVC e delegando o controlo
 * ao MainController.
 */
public class Main {

    /**
     * Método principal que serve de gatilho para a execução do programa.
     * * @param args Argumentos da linha de comandos (não utilizados).
     */
    public static void main(String[] args) {

        // Log de diagnóstico para ajudar na localização dos ficheiros CSV na base de dados
        System.out.println(">>> Diretoria de trabalho: " + System.getProperty("user.dir"));

        // 1. Instanciação da VIEW (Camada de Apresentação)
        MainView view = new MainView();

        // 2. Instanciação do MODEL (Camada de Dados - Repositório em Memória)
        RepositorioDados repositorio = new RepositorioDados();

        // 3. Instanciação do CONTROLLER (Liga a View ao Model)
        // Passamos as referências da View e do Repositório para que o Controller os possa gerir.
        MainController mc = new MainController(view, repositorio);

        // 4. Início do ciclo de vida do sistema
        mc.iniciarSistema();
    }
}