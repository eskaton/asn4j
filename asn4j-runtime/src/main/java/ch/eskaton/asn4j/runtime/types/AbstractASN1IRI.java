package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractASN1IRI implements ASN1Type {

    protected List<String> components;

    public void setValue(String... components) {
        setValue(Arrays.asList(components));
    }

    public void setValue(List<String> components) {
        this.components = new ArrayList<>(components);
    }

    public List<String> getValue() {
        return components;
    }

    @Override
    public String toString() {
        return ToString.builder(this).addAll()
                .map("components", c -> StringUtils.join(((List<Integer>) c), "/"))
                .build();
    }

}
