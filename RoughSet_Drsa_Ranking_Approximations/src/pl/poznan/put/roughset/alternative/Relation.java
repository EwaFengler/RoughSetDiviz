package pl.poznan.put.roughset.alternative;

public enum Relation {
    SAME(0), BETTER(1), WORSE(2), INCOMPARABLE(3);

    private static Relation[] values = {SAME, BETTER, WORSE, INCOMPARABLE};
    private final int value;

    Relation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Relation joinFlags(Relation other) {
        return values[this.value | other.value];
    }
}