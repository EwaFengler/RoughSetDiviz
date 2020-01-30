package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.criterion.Criterion;

import java.util.List;
import java.util.stream.Collectors;

public class Reduct {

    CriteriaIndicesMask critIndMask;
    List<Criterion> criteria;

    public Reduct(CriteriaIndicesMask critIndMask, List<Criterion> allCriteria) {
        this.critIndMask = critIndMask;
        this.criteria = collectCriteria(critIndMask, criteria);
    }

    private List<Criterion> collectCriteria(CriteriaIndicesMask criteriaIndicesMask, List<Criterion> criteria) {
        return criteriaIndicesMask.getIndicesStream()
                .mapToObj(criteria::get)
                .collect(Collectors.toList());
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        List<String> names = this.criteria.stream().map(Criterion::getName).collect(Collectors.toList());
        return String.join("+", names);
    }
}
