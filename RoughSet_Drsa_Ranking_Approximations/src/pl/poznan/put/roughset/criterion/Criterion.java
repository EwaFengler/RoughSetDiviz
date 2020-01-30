package pl.poznan.put.roughset.criterion;

public abstract class Criterion {

    protected String id;
    protected String name;
    protected CriterionType criterionType;

    public Criterion(String id, String name, CriterionType criterionType) {
        this.id = id;
        this.name = name;
        this.criterionType = criterionType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CriterionType getCriterionType() {
        return criterionType;
    }
}

