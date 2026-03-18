package world.resources;

public enum AnimalType implements ICargo{
    UNKNOWN("???", -1),
    HORSE("Ló", 10),
    FISH("Hal", 5),
    PIG("Malac", 8),
    CAT("Cica", 4),
    BEAR("Medve", 20),
    SEAHORSE("Csikóhal", 25),
    GUINEAPIG("Tengerimalac", 18),
    RACOON("Mosómedve", 30),
    CAPYBARA("Capybara", 35);

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
