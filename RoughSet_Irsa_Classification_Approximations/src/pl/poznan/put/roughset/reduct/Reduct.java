package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.attribute.Attribute;

import java.util.List;
import java.util.stream.Collectors;

public class Reduct {

    AttributeIndicesMask attributeIndicesMask;
    List<Attribute> attributes;

    public Reduct(AttributeIndicesMask attributeIndicesMask, List<Attribute> allAttributes) {
        this.attributeIndicesMask = attributeIndicesMask;
        this.attributes = collectAttributes(attributeIndicesMask, allAttributes);
    }

    private List<Attribute> collectAttributes(AttributeIndicesMask attrIndMask, List<Attribute> allAttributes) {
        return attrIndMask.getIndicesStream()
                .mapToObj(allAttributes::get)
                .collect(Collectors.toList());
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        List<String> ids = this.attributes.stream().map(Attribute::getId).collect(Collectors.toList());
        return String.join("+", ids);
    }
}
