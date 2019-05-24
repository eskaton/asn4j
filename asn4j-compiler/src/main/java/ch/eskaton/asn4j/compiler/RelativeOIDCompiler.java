package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;

public class RelativeOIDCompiler implements NamedCompiler<RelativeOID, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, RelativeOID node) {
        ctx.createClass(name, node, false);
        ctx.finishClass();

        return new CompiledType(node);
    }

}
