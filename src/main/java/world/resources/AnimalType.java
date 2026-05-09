package world.resources;

public enum AnimalType implements ICargo {
    UNKNOWN("???", -1),
    HORSE("Ló", 1000),
    FISH("Hal", 500),
    PIG("Malac", 800),
    CAT("Cica", 600),
    BEAR("Medve", 1000),
    SEAHORSE("Csikóhal", 5000),
    GUINEAPIG("Tengerimalac", 2200),
    RACOON("Mosómedve", 3500),
    CAPYBARA("Capybara", 4000);

    private String displayName;
    private int value;

    AnimalType(String name, int value) {
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
