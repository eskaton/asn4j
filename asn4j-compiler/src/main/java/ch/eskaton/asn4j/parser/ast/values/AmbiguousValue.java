package ch.eskaton.asn4j.parser.ast.values;

import java.util.Set;

public class AmbiguousValue implements Value {

    private Set<Value> values;

    public AmbiguousValue(Set<Value> values) {
        this.values = values;
    }

    public <T> T getValue(Class<T> clazz) {
        for (Value value : values) {
            if (value.getClass().equals(clazz)) {
                return (T) value;
            }
        }

        for (Value value : values) {
            if (clazz.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
        }

        return null;
    }

}
