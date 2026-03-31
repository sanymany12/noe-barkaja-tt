import UI.ingameGUI;
import controller.GameController;
import engine.GameEngine;

public class Main {
    public static void main(String[] args)
    {
        engine.AssetManager.getInstance();

        GameEngine model = new GameEngine();

        ingameGUI view = new ingameGUI();

        GameController controller = new GameController(model, view);

        view.show();
    }
}
