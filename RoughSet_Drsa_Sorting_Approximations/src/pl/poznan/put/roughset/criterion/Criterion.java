package pl.poznan.put.roughset.criterion;

public abstract class Criterion {
    protected String id;
    protected String name;
    protected CriterionType type;

    public Criterion(String id, String name, CriterionType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CriterionType getType() {
        return type;
    }
}

