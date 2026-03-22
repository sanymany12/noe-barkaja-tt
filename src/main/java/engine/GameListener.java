package engine;

public interface GameListener {
    void onTick();
    void onNewDay(int currentDay);
}
