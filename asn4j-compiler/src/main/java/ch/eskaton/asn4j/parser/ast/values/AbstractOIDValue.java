package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;

import java.util.List;
import java.util.Objects;

public class AbstractOIDValue extends AbstractValue {

    protected List<OIDComponentNode> components;

    public AbstractOIDValue(Position position) {
        super(position);
    }

    public AbstractOIDValue(Position position, List<OIDComponentNode> components) {
        super(position);

        this.components = components;
    }

    public List<OIDComponentNode> getComponents() {
        return components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractOIDValue that = (AbstractOIDValue) o;

        return Objects.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components);
    }

}
