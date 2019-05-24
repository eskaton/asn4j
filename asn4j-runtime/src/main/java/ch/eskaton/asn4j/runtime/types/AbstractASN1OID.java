package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractASN1OID implements ASN1Type {

    protected List<Integer> components;

    public void setValue(int... components) throws ValidationException {
        setValue(IntStream.of(components).boxed().collect(Collectors.toList()));
    }

    public void setValue(List<Integer> components) throws ValidationException {
        this.components = verifiedComponents(new ArrayList<>(components));
    }

    public List<Integer> getValue() {
        return components;
    }

    protected abstract ArrayList<Integer> verifiedComponents(ArrayList<Integer> components) throws ValidationException;

}
