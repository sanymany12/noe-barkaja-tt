import UI.ingameGUI;
import UI.menu;
import controller.GameController;
import engine.GameEngine;
import engine.AssetManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args)
    {
        AssetManager.getInstance();

        SwingUtilities.invokeLater(() -> {
            menu mainMenu = new menu();

            mainMenu.setStartGameListener((String loadFilePath) -> {

                GameEngine model = new GameEngine();
                ingameGUI view = new ingameGUI();
                GameController controller = new GameController(model, view);

                if (loadFilePath != null) {
                    model.loadGame(loadFilePath);
                    view.setBalance(model.getWorld().getMoney());
                    view.setDay(model.getWorld().getElapsedTime());
                    view.mapRefresh();
                }

                view.show();
            });

            mainMenu.showMenu();
        });
    }
}
