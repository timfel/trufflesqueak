package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.model.CompiledMethodObject;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.ArithmeticPrimitives;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimClosureValueFactory;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimDebugger;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimFileSizeNodeGen;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimFileStdioHandles;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimFileWriteNodeGen;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimNextHandlerContextNodeGen;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimPerform;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimPrintArgs;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimQuit;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimSystemAttributeNodeGen;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.PrimUtcClockNodeGen;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.Primitives;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.QuickPrimitives;
import de.hpi.swa.trufflesqueak.nodes.primitives.impl.StoragePrimitives;

public abstract class PrimitiveNodeFactory {
    private static Primitives[] primitiveSets = new Primitives[]{
                    new ArithmeticPrimitives(), new StoragePrimitives(), new QuickPrimitives()
    };

    public static enum IndexedPrimitives {
        QUIT(PrimQuit.class, 113),
        // EXTERNAL_CALL(PrimCall.class, 117),
        SYSTEM_ATTR(PrimSystemAttributeNodeGen.class, 149),
        UTC_MICROSECOND_CLOCK(PrimUtcClockNodeGen.class, 240),
        NEXT_HANDLER_CONTEXT(PrimNextHandlerContextNodeGen.class, 197),
        //
        PERFORM(PrimPerform.class, 83),
        CLOSURE_VALUE(PrimClosureValueFactory.PrimClosureValue0NodeGen.class, 201),
        CLOSURE_VALUE_(PrimClosureValueFactory.PrimClosureValue1NodeGen.class, 202),
        CLOSURE_VALUE__(PrimClosureValueFactory.PrimClosureValue2NodeGen.class, 203),
        CLOSURE_VALUE___(PrimClosureValueFactory.PrimClosureValue3NodeGen.class, 204),
        CLOSURE_VALUE____(PrimClosureValueFactory.PrimClosureValue4NodeGen.class, 205),
        CLOSURE_VALUE_ARGS(PrimClosureValueFactory.PrimClosureValueAryNodeGen.class, 206);
        //

        IndexedPrimitives(Class<? extends PrimitiveNode> cls, int idx) {
        }
    }

    public static enum NamedPrimitives {
        FILE_WRITE(PrimFileWriteNodeGen.class, "FilePlugin", "primitiveFileWrite"),
        FILE_SIZE(PrimFileSizeNodeGen.class, "FilePlugin", "primitiveFileSize"),
        FILE_STDIO_HANDLES(PrimFileStdioHandles.class, "FilePlugin", "primitiveFileStdioHandles"),
        //
        TRUFFLE_PRINT(PrimPrintArgs.class, "TruffleSqueak", "debugPrint"),
        TRUFFLE_DEBUG(PrimDebugger.class, "TruffleSqueak", "debugger"),
        //
        LAST(PrimitiveNode.class, "nil", "nil");

        NamedPrimitives(Class<? extends PrimitiveNode> cls, String modulename, String functionname) {
        }
    }

    @TruffleBoundary
    public static BuiltinPrimitive forIdx(CompiledCodeObject method, int primitiveIdx) {
        for (Primitives primSet : primitiveSets) {
            BuiltinPrimitive primitive = primSet.forIdx(method, primitiveIdx);
            if (primitive != null) {
                return primitive;
            }
        }
        return null;
    }

    @TruffleBoundary
    public static BuiltinPrimitive forName(CompiledMethodObject method, String modulename, String functionname) {
        for (Primitives primSet : primitiveSets) {
            BuiltinPrimitive primitive = primSet.forName(method, modulename, functionname);
            if (primitive != null) {
                return primitive;
            }
        }
        return null;
    }
}
