package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractASN1IRI implements ASN1Type{

    protected List<String> components;

    public void setValue(String... components) throws ValidationException {
        setValue(Arrays.asList(components));
    }

    public void setValue(List<String> components) throws ValidationException {
        this.components = new ArrayList<>(components);
    }

    public List<String> getValue() {
        return components;
    }

}
