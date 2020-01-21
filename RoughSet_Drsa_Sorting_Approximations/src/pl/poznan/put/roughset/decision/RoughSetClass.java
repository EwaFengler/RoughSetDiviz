package pl.poznan.put.roughset.decision;

import pl.poznan.put.roughset.alternative.Alternative;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RoughSetClass implements Comparable<RoughSetClass> {

    private String id;
    private String name;
    private Map<String, Integer> classHierarchy;

    private Set<Alternative> alternatives = new HashSet<>();
    private ClassUnion upwardUnion;
    private ClassUnion downwardUnion;

    public RoughSetClass(String id, String name, Map<String, Integer> classHierarchy) {
        this.id = id;
        this.name = name;
        this.classHierarchy = classHierarchy;
    }

    public void addAlternative(Alternative alternative) {
        alternatives.add(alternative);
    }

    public String getId() {
        return id;
    }

    public Set<Alternative> getAlternatives() {
        return alternatives;
    }

    public ClassUnion getUpwardUnion() {
        return upwardUnion;
    }

    public void setUpwardUnion(ClassUnion upwardUnion) {
        this.upwardUnion = upwardUnion;
    }

    public ClassUnion getDownwardUnion() {
        return downwardUnion;
    }

    public void setDownwardUnion(ClassUnion downwardUnion) {
        this.downwardUnion = downwardUnion;
    }

    @Override
    public int compareTo(RoughSetClass roughSetClass) {
        return this.classHierarchy.get(this.id) - this.classHierarchy.get(roughSetClass.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoughSetClass that = (RoughSetClass) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
