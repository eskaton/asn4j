package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.parser.ast.types.IRI;

public class IRICompiler implements NamedCompiler<IRI> {

    @Override
    public void compile(CompilerContext ctx, String name, IRI node) throws CompilerException {
        ctx.createClass(name, node, false);
        ctx.finishClass();
    }

}
