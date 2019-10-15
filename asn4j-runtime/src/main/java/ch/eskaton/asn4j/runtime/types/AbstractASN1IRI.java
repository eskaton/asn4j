package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractASN1IRI implements ASN1Type, HasConstraint {

    protected List<String> components;

    public AbstractASN1IRI() {
        this.components = new ArrayList<>();
    }

    public AbstractASN1IRI(String... components) {
        this.components = asList(components);
    }

    public AbstractASN1IRI(List<String> components) {
        this.components = components;
    }

    public void setValue(String... components) {
        setValue(asList(components));
    }

    public void setValue(List<String> components) {
        this.components = new ArrayList<>(components);
    }

    public List<String> getValue() {
        return components;
    }

    @Override
    public void checkConstraint() {
        if (!doCheckConstraint()) {
            throw new ConstraintViolatedException(String.format("%s doesn't satisfy a constraint",
                    StringUtils.join(getValue(), "/")));
        }
    }

    @Override
    public String toString() {
        return ToString.builder(this).addAll()
                .map("components", c -> StringUtils.join(((List<Integer>) c), "/"))
                .build();
    }

}
