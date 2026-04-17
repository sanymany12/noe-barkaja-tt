package engine;

import world.World;
import world.tile.TerrainType;
import world.tile.Tile;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ForestManager {
    private int chanceToSpread; //esély százalék
    private int chanceToGrow; //esély százalék
    private World world;

    public void setChanceToSpread(int chance) {
        if(chance <= 0 || chance > 100){
            throw new IllegalArgumentException("A chance értékének [1,100] közötti intervallumban kell lennie");
        }

        chanceToSpread = chance;
    }

    public void setChanceToGrow(int chance) {
        if(chance <= 0 || chance > 100){
            throw new IllegalArgumentException("A chance értékének [1,100] közötti intervallumban kell lennie");
        }

        this.chanceToGrow = chance;
    }

    public int getChanceToGrow() {
        return chanceToGrow;
    }

    public int getChanceToSpread() {
        return chanceToSpread;
    }

    public ForestManager(World world) {
        this.world = world;
        chanceToGrow = 15;
        chanceToSpread = 10;
    }

    public void updateForests(){
        /*
        HashSet ArrayList helyett mert: nem kell rendezettnek lennie + nem engedi a duplikált értékeket
        a rengeteg contains() nagyon lelassítaná a programot.
        tiszta lappal indulunk, hogy törölgetni se kelljen a már teljesen megnőtt 4fás erdőket
         */
        Set<Tile> spreadTo = new HashSet<>();

        for (int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {
                Tile tile = world.get(i,j);
                int treeCount = tile.getTreeCount();
                if(treeCount == 4) {
                    addGrowSpots(tile, spreadTo);
                }else if(treeCount > 0 && treeCount < 4){ //ha 0 és 4 közötti fát tartalmaz akkor esélye van növekedni még egy fának
                    if(ThreadLocalRandom.current().nextInt(100) < chanceToGrow){ // esély kiszámítása: 0-99 közötti szám 100as chance-nél 100% hogy kisebb lesz
                       tile.setTreeCount(treeCount+1);
                    }
                }

            }
        }

        spreadForests(spreadTo);
    }
    // erdők terjedése
    private void spreadForests(Set<Tile> spreadTo){
        for (Tile t : spreadTo){
            if(ThreadLocalRandom.current().nextInt(100) < chanceToSpread){ // esély kiszámítása: 0-99 közötti szám 100as chance-nél 100% hogy kisebb lesz
                t.setTreeCount(1);
            }
        }
    }
    // hozzáadja a spreadTo listához azokat a mezőket amikre terjedhet tovább az erdő
    private void addGrowSpots(Tile tile, Set<Tile> spreadTo) {
        for (int i = -1; i <= 1; i++) { // megnézzük a környező mezőket
            for (int j = -1; j <= 1; j++) {
                if (Math.abs(i) == Math.abs(j)){
                    continue; // ha 1,-1 -1,1, 1,1 -1,-1 tehát átlós azt kihagyjuk valamint önmagunkat is 0==0
                }
                Tile surtile = world.get(tile.getCoordinate().x + i, tile.getCoordinate().y + j);
                if(surtile != null && surtile.getTreeCount() == 0
                        && surtile.getTerrainType() == TerrainType.LAND && surtile.getBuilding() == null){ // csak üres land típusú tile lehet
                    spreadTo.add(surtile); // ha valid tile, nincs rajta fa és még nem tároljuk akkor hozzáadjuk a listához
                }
            }
        }
    }
    public void setWorld(World world) { this.world = world;}
}
