package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractASN1OID implements ASN1Type {

    protected List<Integer> components;

    public void setValue(int... value) {
        setValue(IntStream.of(value).boxed().collect(Collectors.toList()));
    }

    public void setValue(List<Integer> value) {
        if (!checkConstraint(value.stream().mapToInt(Integer::valueOf).toArray())) {
            throw new ConstraintViolatedException(String.format("%s doesn't satisfy a constraint", value));
        }

        this.components = verifiedComponents(new ArrayList<>(value));
    }

    public List<Integer> getValue() {
        return components;
    }

    protected abstract List<Integer> verifiedComponents(List<Integer> components);

    @SuppressWarnings("squid:S1172")
    protected boolean checkConstraint(int... value) {
        return true;
    }

    @Override
    public String toString() {
        return ToString.builder(this).addAll()
                .map("value", c -> StringUtils.join(((List<Integer>) c), "."))
                .build();
    }

}
