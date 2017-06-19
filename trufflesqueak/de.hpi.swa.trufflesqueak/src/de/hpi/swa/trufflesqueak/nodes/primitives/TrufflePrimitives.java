package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.util.List;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Instrumentable;

import de.hpi.swa.trufflesqueak.exceptions.PrimitiveFailed;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.model.NativeObject;

public final class TrufflePrimitives extends PrimitiveSet {
    @Override
    List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return TrufflePrimitivesFactory.getFactories();
    }

    @Primitive(names = {"debugPrint"}, module = "TruffleSqueak")
    @GenerateNodeFactory
    public static class PrintArguments extends BuiltinPrimitive {
        public PrintArguments(CompiledCodeObject method2) {
            super(method2);
        }

        @TruffleBoundary
        private static void debugPrint(Object o) {
            if (o instanceof NativeObject) {
                System.out.println(((NativeObject) o).toString());
            } else {
                System.out.println(o.toString());
            }
        }

        @Specialization
        public Object printArguments(VirtualFrame frame) {
            Object[] arguments = frame.getArguments();
            for (int i = 1; i < arguments.length; i++) {
                debugPrint(arguments[i]);
            }
            return null;
        }
    }

    @Primitive(names = {"debugger"}, module = "TruffleSqueak")
    @GenerateNodeFactory
    @Instrumentable(factory = DebuggerWrapper.class)
    public static class Debugger extends BuiltinPrimitive {
        protected Debugger(Debugger pm) {
            super(pm.method);
        }

        public Debugger(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        public Object debugger() {
            throw new PrimitiveFailed();
        }

        @Override
        protected boolean isTaggedWith(Class<?> tag) {
            return tag == DebuggerTags.AlwaysHalt.class;
        }
    }
}
