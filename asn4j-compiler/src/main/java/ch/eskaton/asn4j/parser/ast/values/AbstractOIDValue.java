package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.ast.OIDComponentNode;

import java.util.List;

public class AbstractOIDValue implements Value {

    protected List<OIDComponentNode> components;

    public AbstractOIDValue() {
    }

    public AbstractOIDValue(List<OIDComponentNode> components) {
        this.components = components;
    }

    public List<OIDComponentNode> getComponents() {
        return components;
    }

}
