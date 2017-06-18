package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;

import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.nodes.SqueakNode;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentNode;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentProfileNode;

public abstract class PrimitiveSet {
    abstract List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories();

    public BuiltinPrimitive forName(CompiledCodeObject cc, String module, String functionName) {
        List<NodeFactory<? extends BuiltinPrimitive>> factories = getNodeFactories();
        assert factories != null : "No factories found. Override getFactories() to resolve this.";
        for (NodeFactory<? extends BuiltinPrimitive> factory : factories) {
            Primitive annotation = factory.getNodeClass().getAnnotation(Primitive.class);
            if (annotation.module().equals(module)) {
                for (String name : annotation.names()) {
                    if (name.equals(functionName)) {
                        return getBuiltinPrimitive(cc, factory, annotation);
                    }
                }
            }
        }
        return null;
    }

    public BuiltinPrimitive forIdx(CompiledCodeObject cc, int index) {
        List<NodeFactory<? extends BuiltinPrimitive>> factories = getNodeFactories();
        assert factories != null : "No factories found. Override getFactories() to resolve this.";
        for (NodeFactory<? extends BuiltinPrimitive> factory : factories) {
            Primitive annotation = factory.getNodeClass().getAnnotation(Primitive.class);
            for (int idx : annotation.indices()) {
                if (idx == index) {
                    return getBuiltinPrimitive(cc, factory, annotation);
                }
            }
        }
        return null;
    }

    private static BuiltinPrimitive getBuiltinPrimitive(CompiledCodeObject cc, NodeFactory<? extends BuiltinPrimitive> factory, Primitive annotation) {
        int argumentNumber = Math.max(annotation.numberOfArguments(), annotation.maxNumberOfArguments());
        Object[] arguments = new Object[2];
        arguments[0] = cc;
        SqueakNode[] argumentNodes = new SqueakNode[argumentNumber];
        arguments[1] = argumentNodes;
        for (int i = 0; i < argumentNumber; i++) {
            argumentNodes[i] = new ArgumentProfileNode(ArgumentNode.create(i));
        }
        return factory.createNode(arguments);
    }
}