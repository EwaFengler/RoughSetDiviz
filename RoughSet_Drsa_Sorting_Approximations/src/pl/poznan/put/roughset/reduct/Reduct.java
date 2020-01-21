package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.criterion.Criterion;

import java.util.List;
import java.util.stream.Collectors;

public class Reduct {

    CriteriaIndicesMask criteriaIndicesMask;
    List<Criterion> criteria;

    public Reduct(CriteriaIndicesMask criteriaIndicesMask, List<Criterion> criteria) {
        this.criteriaIndicesMask = criteriaIndicesMask;
        this.criteria = collectCriteria(criteriaIndicesMask, criteria);
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
