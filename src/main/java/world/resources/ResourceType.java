package world.resources;

public enum ResourceType implements ICargo{
    GRAIN("Gabona", 0),
    FOOD("Ételkonzerv", 1);

    private String displayName;
    private int value;

    ResourceType(String name, int value) {
        this.displayName = name;
        this.value = value;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
