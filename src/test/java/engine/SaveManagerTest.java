package engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import world.World;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SaveManagerTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSaveAndLoadGame_SuccessfulRoundtrip() {

        Path saveFilePath = tempDir.resolve("test_savegame.json");


        World originalWorld = new World(20, 20);
        originalWorld.initWorld();

        SaveManager.saveGame(originalWorld, saveFilePath.toString());

        assertTrue(Files.exists(saveFilePath), "A mentési fájl nem jött létre a lemezen!");

        World loadedWorld = SaveManager.loadGame(saveFilePath.toString());

        assertNotNull(loadedWorld, "A betöltött világ null lett!");

        assertEquals(originalWorld.getRows(), loadedWorld.getRows(), "A sorok száma nem egyezik betöltés után!");
        assertEquals(originalWorld.getCols(), loadedWorld.getCols(), "Az oszlopok száma nem egyezik betöltés után!");

        assertNotNull(loadedWorld.get(0, 0), "A csempék (Tile) nem töltődtek be megfelelően!");
    }

    @Test
    public void testLoadGame_FileNotFound_ReturnsNull() {

        Path nonExistentPath = tempDir.resolve("missing_file.json");


        World loadedWorld = SaveManager.loadGame(nonExistentPath.toString());

        assertNull(loadedWorld, "Nem létező fájl esetén null-t kellene visszakapnunk!");
    }
}