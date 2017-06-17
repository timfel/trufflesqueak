package de.hpi.swa.trufflesqueak.nodes.primitives.impl;

import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;

import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentNode;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentProfileNode;
import de.hpi.swa.trufflesqueak.nodes.primitives.BuiltinPrimitive;
import de.hpi.swa.trufflesqueak.nodes.primitives.Primitive;

public abstract class Primitives {
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
        Object[] arguments = new Object[annotation.numberOfArguments() + 1];
        arguments[0] = cc;
        for (int i = 0; i < annotation.numberOfArguments(); i++) {
            arguments[i + 1] = new ArgumentProfileNode(new ArgumentNode(i));
        }
        return factory.createNode(arguments);
    }
}