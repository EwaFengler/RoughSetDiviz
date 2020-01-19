package pl.poznan.put.roughset.reduct;

import java.util.stream.IntStream;

public class AttributeIndicesMask {

    int mask;
    int noOfAttr;

    public AttributeIndicesMask(int mask, int noOfAttr) {
        this.mask = mask;
        this.noOfAttr = noOfAttr;
    }

    public IntStream getIndicesStream() {
        return IntStream.range(0, noOfAttr).filter(this::isAttributePresent);
    }

    private boolean isAttributePresent(int i) {
        int attributeBit = 1 << i;
        int presenceInMask = attributeBit & mask;
        return presenceInMask != 0;
    }
}
