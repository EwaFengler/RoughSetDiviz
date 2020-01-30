package pl.poznan.put.roughset.reduct;

import java.util.stream.IntStream;

public class CriteriaIndicesMask {

    int mask;
    int noOfCriteria;

    public CriteriaIndicesMask(int mask, int noOfCriteria) {
        this.mask = mask;
        this.noOfCriteria = noOfCriteria;
    }

    public IntStream getIndicesStream() {
        return IntStream.range(0, noOfCriteria).filter(this::isCriterionPresent);
    }

    private boolean isCriterionPresent(int i) {
        int criterionBit = 1 << i;
        int presenceInMask = criterionBit & mask;
        return presenceInMask != 0;
    }
}
