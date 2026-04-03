import controller.MainController;
import view.MainView;
import model.dal.RepositorioDados;

public class Main {
    public static void main(String[] args) {

        System.out.println(">>> O Java está a procurar ficheiros na pasta: " + System.getProperty("user.dir"));

        // 1. Criar o Ecrã (View)
        MainView view = new MainView();

        // 2. Criar a Memória (Model/Repositorio)
        RepositorioDados repositorio = new RepositorioDados();

        // 3. Criar o Controlador e entregar-lhe as peças
        MainController mc = new MainController(view, repositorio);

        // 4. Arrancar o sistema
        mc.iniciarSistema();
    }
}