package world.resources;

public enum PersonType implements ICargo {
    PERSON("Emberke", 15);

    private String displayName;
    private int value;

    PersonType(String name, int value) {
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
