package gg.moonflower.pollen.impl;

import gg.moonflower.molangcompiler.api.MolangCompiler;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangSyntaxException;
import gg.moonflower.molangcompiler.core.ast.Node;
import gg.moonflower.molangcompiler.core.compiler.BytecodeCompiler;
import gg.moonflower.molangcompiler.core.compiler.MolangLexer;
import gg.moonflower.molangcompiler.core.compiler.MolangTokenizer;
import org.jetbrains.annotations.ApiStatus;

/**
 * This class exists to make sure the bytecode compiler is a child of the mod class loader.
 */
@ApiStatus.Internal
public class PollenMolangCompiler implements MolangCompiler {

    private final BytecodeCompiler compiler;

    public PollenMolangCompiler(int flags, ClassLoader classLoader) {
        this.compiler = new BytecodeCompiler(flags, classLoader);
    }

    @Override
    public MolangExpression compile(String input) throws MolangSyntaxException {
        MolangTokenizer.Token[] tokens = MolangTokenizer.createTokens(input);
        Node node = MolangLexer.parseTokens(tokens);
        return this.compiler.build(node);
    }
}
