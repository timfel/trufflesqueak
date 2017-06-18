package de.hpi.swa.trufflesqueak.nodes.primitives;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import de.hpi.swa.trufflesqueak.model.BaseSqueakObject;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;

public abstract class PrimitiveNodeFactory {
    private static PrimitiveSet[] primitiveSets = new PrimitiveSet[]{
                    new ArithmeticPrimitives(),
                    new EvaluationPrimitives(),
                    new FilePrimitives(),
                    new QuickPrimitives(),
                    new StoragePrimitives(),
                    new SystemPrimitives(),
                    new TrufflePrimitives()
    };

    @TruffleBoundary
    public static BuiltinPrimitive forIdx(CompiledCodeObject method, int primitiveIdx) {
        if (primitiveIdx == 117) {
            Object descriptor = method.getLiteral(0);
            if (descriptor instanceof BaseSqueakObject && ((BaseSqueakObject) descriptor).size() >= 2) {
                String modulename = ((BaseSqueakObject) descriptor).at0(0).toString();
                String functionname = ((BaseSqueakObject) descriptor).at0(1).toString();
                for (PrimitiveSet primSet : primitiveSets) {
                    BuiltinPrimitive primitive = primSet.forName(method, modulename, functionname);
                    if (primitive != null) {
                        return primitive;
                    }
                }
            }
        } else {
            for (PrimitiveSet primSet : primitiveSets) {
                BuiltinPrimitive primitive = primSet.forIdx(method, primitiveIdx);
                if (primitive != null) {
                    return primitive;
                }
            }
        }
        return new BuiltinPrimitive(method);
    }
}
