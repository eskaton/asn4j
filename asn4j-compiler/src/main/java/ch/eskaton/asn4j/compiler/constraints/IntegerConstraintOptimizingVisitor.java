package ch.eskaton.asn4j.compiler.constraints;

import ch.eskaton.asn4j.compiler.constraints.ast.*;

import java.util.List;

import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.NEGATION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.VALUE;

public class IntegerConstraintOptimizingVisitor implements Visitor<Node, List<IntegerRange>> {

    @Override
    public Node visit(AllValuesNode node) {
        return node;
    }

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        List<IntegerRange> leftValue;
        List<IntegerRange> rightValue;
        Node temp;

        switch (left.getType().getId() << 16 | right.getType().getId()) {
            case VALUE << 16 | VALUE:
                leftValue = ((IntegerRangeValueNode) left).getValue();
                rightValue = ((IntegerRangeValueNode) right).getValue();

                switch (node.getType()) {
                    case UNION:
                        return new IntegerRangeValueNode(IntegerRange.union(leftValue, rightValue));
                    case INTERSECTION:
                        return new IntegerRangeValueNode(IntegerRange.intersection(leftValue, rightValue));
                    case COMPLEMENT:
                        return new IntegerRangeValueNode(IntegerRange.exclude(leftValue, rightValue));
                    default:
                        throw new IllegalStateException("Unimplemented node type: " + node.getType());
                }

            case NEGATION << 16 | VALUE:
                temp = right;
                right = left;
                left = temp;

            case VALUE << 16 | NEGATION:
                right = (((OpNode) right).getNode());

                if (right.getType() == NodeType.VALUE) {
                    leftValue = ((IntegerRangeValueNode) left).getValue();
                    rightValue = ((IntegerRangeValueNode) right).getValue();

                    if (node.getType() == INTERSECTION) {
                        return new IntegerRangeValueNode(IntegerRange.exclude(leftValue, rightValue));
                    } else {
                        throw new IllegalStateException("Unimplemented node type: " + node.getType());
                    }
                }

                return node;

            default:
                return node;
        }
    }

    @Override
    public Node visit(OpNode node) {
        return node;
    }

    @Override
    public Node visit(SizeNode node) {
        return node;
    }

    @Override
    public Node visit(ValueNode<List<IntegerRange>> node) {
        return node;
    }

}
