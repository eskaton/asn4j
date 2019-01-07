package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;

import java.util.List;

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

}
