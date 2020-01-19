package pl.poznan.put.roughset.alternative;

import java.util.ArrayList;
import java.util.List;

public class Alternative {

    private String id;
    private List<ValueOnAttribute> valuesOnAttributes = new ArrayList<>();

    public Alternative(String id) {
        this.id = id;
    }

    public void addToValuesOnAttributes(ValueOnAttribute valueOnAttribute) {
        valuesOnAttributes.add(valueOnAttribute);
    }

    public String getId() {
        return id;
    }

    public List<ValueOnAttribute> getValuesOnAttributes() {
        return valuesOnAttributes;
    }
}
