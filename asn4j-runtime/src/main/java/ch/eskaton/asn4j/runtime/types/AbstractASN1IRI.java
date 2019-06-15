package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractASN1IRI implements ASN1Type {

    protected List<String> components;

    public void setValue(String... value) {
        setValue(asList(value));
    }

    public void setValue(List<String> value) {
        if (!checkConstraint(value.toArray(new String[] {}))) {
            throw new ConstraintViolatedException(String.format("%s doesn't satisfy a constraint",
                    StringUtils.join(value, "/")));
        }

        this.components = new ArrayList<>(value);
    }

    public List<String> getValue() {
        return components;
    }

    @SuppressWarnings("squid:S1172")
    protected boolean checkConstraint(String... value) {
        return true;
    }

    @Override
    public String toString() {
        return ToString.builder(this).addAll()
                .map("components", c -> StringUtils.join(((List<Integer>) c), "/"))
                .build();
    }

}
