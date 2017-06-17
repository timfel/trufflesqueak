package de.hpi.swa.trufflesqueak.nodes.primitives.impl;

import java.util.ArrayList;
import java.util.List;

import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.hpi.swa.trufflesqueak.exceptions.LocalReturn;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.nodes.SqueakNode;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ReceiverNode;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ReceiverVariableNode;
import de.hpi.swa.trufflesqueak.nodes.primitives.BuiltinPrimitive;

public class QuickPrimitives extends Primitives {
    @Override
    List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return new ArrayList<>();
    }

    @Override
    public BuiltinPrimitive forIdx(CompiledCodeObject cc, int index) {
        if (index >= 264 && index <= 520) {
            return new QuickReturn(cc, new ReceiverVariableNode(cc, -1, index - 264));
        } else {
            switch (index) {
                case 256:
                    return new QuickReturn(cc, new ReceiverNode(cc, -1));
                case 257:
                    return new QuickReturnConstant(cc, true);
                case 258:
                    return new QuickReturnConstant(cc, false);
                case 259:
                    return new QuickReturnConstant(cc, null);
                case 260:
                    return new QuickReturnConstant(cc, -1);
                case 261:
                    return new QuickReturnConstant(cc, 0);
                case 262:
                    return new QuickReturnConstant(cc, 1);
                case 263:
                    return new QuickReturnConstant(cc, -1);
                default:
                    return null;
            }
        }
    }

    public static interface QuickReturnPrimitive {
    }

    public static class QuickReturn extends BuiltinPrimitive implements QuickReturnPrimitive {
        @Child SqueakNode actual;

        public QuickReturn(CompiledCodeObject cm, SqueakNode actual2) {
            super(cm);
            actual = actual2;
        }

        @Override
        public Object executeGeneric(VirtualFrame frame) {
            throw new LocalReturn(actual.executeGeneric(frame));
        }
    }

    public static class QuickReturnConstant extends BuiltinPrimitive implements QuickReturnPrimitive {
        private final Object constant;

        public QuickReturnConstant(CompiledCodeObject method2, Object constant2) {
            super(method2);
            constant = constant2;
        }

        @Override
        public Object executeGeneric(VirtualFrame frame) {
            throw new LocalReturn(constant);
        }
    }
}
