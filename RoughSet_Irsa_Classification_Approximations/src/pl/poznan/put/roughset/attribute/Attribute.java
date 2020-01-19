package pl.poznan.put.roughset.attribute;

import java.util.Objects;

public class Attribute {

    private String id;
    private String name;

    public Attribute(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return Objects.equals(id, attribute.id) &&
                Objects.equals(name, attribute.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
