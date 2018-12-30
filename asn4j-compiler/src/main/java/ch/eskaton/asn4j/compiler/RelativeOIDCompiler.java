package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.parser.ast.types.RelativeOID;

public class RelativeOIDCompiler implements NamedCompiler<RelativeOID> {

    @Override
    public void compile(CompilerContext ctx, String name, RelativeOID node) throws CompilerException {
        ctx.createClass(name, node, false);
        ctx.finishClass();
    }

}
