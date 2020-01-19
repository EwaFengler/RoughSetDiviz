package pl.poznan.put.roughset.alternative;

import pl.poznan.put.roughset.attribute.Attribute;

import java.util.Objects;

public class ValueOnAttribute {

    private Attribute attribute;
    private String value;

    public ValueOnAttribute(Attribute attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueOnAttribute that = (ValueOnAttribute) o;
        return Objects.equals(attribute, that.attribute) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, value);
    }
}
