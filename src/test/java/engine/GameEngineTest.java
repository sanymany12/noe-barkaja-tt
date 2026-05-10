package engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.World;

import java.awt.event.ActionEvent;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    private GameEngine gameEngine;
    private TestGameListener testListener;

    // Saját hallgató az MVC architektúra teszteléséhez
    // Ezzel le tudjuk mérni, hányszor hívta meg a motor a GUI frissítését
    private class TestGameListener implements GameListener {
        public int tickCount = 0;
        public int newDayCount = 0;
        public int lastReportedDay = 0;

        @Override
        public void onTick() {
            tickCount++;
        }

        @Override
        public void onNewDay(int day) {
            newDayCount++;
            lastReportedDay = day;
        }

        @Override
        public void afterSpending(int amount){}
    }

    @BeforeEach
    public void setUp() {
        gameEngine = new GameEngine();
        testListener = new TestGameListener();

        // A start() felépíti a világot, a kamerát, a menedzsereket
        gameEngine.start(testListener);
    }

    @AfterEach
    public void tearDown() {
        // Takarítás: Ha a save/load teszt létrehozott egy fájlt, töröljük
        File saveFile = new File("test_engine_savegame.json");
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }

    // --- INICIALIZÁLÁS TESZTEK ---

    @Test
    public void testEngineStart_InitializesAllComponents() {
        assertNotNull(gameEngine.getWorld(), "A World nem jött létre!");
        assertNotNull(gameEngine.getCamera(), "A Kamera nem jött létre!");
        assertNotNull(gameEngine.getRenderer(), "A Renderer nem jött létre!");
        assertNotNull(gameEngine.getMinimap(), "A Minimap nem jött létre!");
        assertNotNull(gameEngine.getBuildManager(), "A BuildManager nem jött létre!");

        // Alapértelmezetten szüneteltetve kell lennie a játéknak
        assertEquals(TimeSpeed.PAUSED, gameEngine.getTimeSpeed(), "Indításkor PAUSED állapotban kell lennie!");
    }

    // --- IDŐMENEDZSMENT TESZT ---

    @Test
    public void testSetTimeMultiplier_UpdatesSpeed() {

        TimeSpeed newSpeed = TimeSpeed.values()[1]; // Veszünk egy nem-PAUSED sebességet

        gameEngine.setTimeMultiplier(newSpeed);

        assertEquals(newSpeed, gameEngine.getTimeSpeed(), "A sebességnek frissülnie kellett volna!");
    }

    // ---  A BELSŐ TIMER TESZTELÉSE ---

    @Test
    public void testTimerListener_TicksAndTriggersNewDay() {

        GameEngine.TimerListener timerListener = gameEngine.new TimerListener();

        int ticksPerDay = gameEngine.getWorld().getTicksPerDay(); // Alapból 100

        // Szimuláljuk, hogy a Timer "ketyeg" pontosan egy napnyit
        for (int i = 0; i < ticksPerDay; i++) {
            timerListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }

        assertEquals(ticksPerDay, testListener.tickCount, "A listener.onTick() nem hívódott meg elégszer!");
        assertEquals(1, testListener.newDayCount, "Pontosan 1 új napnak kellett volna eltelnie!");
        assertEquals(2, testListener.lastReportedDay, "A GUI felé jelentett nap számának 1-nek kell lennie!");

        // Ellenőrizzük, hogy a világ belső ideje is nőtt-e
        assertEquals(1, gameEngine.getWorld().getElapsedTime(), "A World belső ideje nem nőtt meg!");
    }

    // ---  MENTÉS ÉS BETÖLTÉS TESZT ---

    @Test
    public void testSaveAndLoadGame() {
        String testFileName = "test_engine_savegame.json";

        // 1. Módosítjuk a világot (adunk hozzá pénzt), hogy legyen mit ellenőrizni
        World originalWorld = gameEngine.getWorld();
        int startingMoney = originalWorld.getMoney();
        originalWorld.receiveMoney(5000);
        assertEquals(startingMoney + 5000, originalWorld.getMoney());

        // 2. Kimentjük a játékot
        gameEngine.saveGame(testFileName);

        // 3. Mesterségesen "elrontjuk" a memóriában lévő világot,
        // hogy biztosak legyünk benne, tényleg a fájlból tölt vissza
        originalWorld.spendMoney(10000);

        // 4. Visszatöltjük a játékot
        gameEngine.loadGame(testFileName);

        World loadedWorld = gameEngine.getWorld();

        // Ellenőrizzük, hogy a referenciák (Renderer, BuildManager) is megkapták-e az új világot
        assertNotEquals(originalWorld, loadedWorld, "A betöltött világ nem lehet ugyanaz a memóriapéldány, mint a régi!");
        assertEquals(loadedWorld, gameEngine.getRenderer().getWorld(), "A Renderer nem kapta meg a betöltött világot!");

        // Ellenőrizzük az adatot: a pénznek pontosan annyinak kell lennie, mint a mentés pillanatában
        assertEquals(startingMoney + 5000, loadedWorld.getMoney(), "A mentett pénzösszeg nem töltődött vissza sikeresen!");

        // Ellenőrizzük, hogy az MVC listener kapott-e értesítést a betöltésről
        assertTrue(testListener.newDayCount > 0, "A betöltés után nem hívódott meg az onNewDay frissítés!");
    }
}