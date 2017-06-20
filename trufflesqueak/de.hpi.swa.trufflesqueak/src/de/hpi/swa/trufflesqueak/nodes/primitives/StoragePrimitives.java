package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.math.BigInteger;
import java.util.List;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;

import de.hpi.swa.trufflesqueak.exceptions.PrimitiveFailed;
import de.hpi.swa.trufflesqueak.model.AbstractPointersObject;
import de.hpi.swa.trufflesqueak.model.BaseSqueakObject;
import de.hpi.swa.trufflesqueak.model.BlockClosure;
import de.hpi.swa.trufflesqueak.model.ClassObject;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.model.EmptyObject;
import de.hpi.swa.trufflesqueak.model.LargeInteger;
import de.hpi.swa.trufflesqueak.model.ListObject;
import de.hpi.swa.trufflesqueak.model.NativeObject;
import de.hpi.swa.trufflesqueak.nodes.context.SqueakLookupClassNode;
import de.hpi.swa.trufflesqueak.nodes.context.SqueakLookupClassNodeGen;

public final class StoragePrimitives extends PrimitiveSet {
    @Override
    List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return StoragePrimitivesFactory.getFactories();
    }

    @Primitive(indices = {60}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class IndexAt extends At {

        public IndexAt(CompiledCodeObject method2) {
            super(method2);
        }

        @Override
        @Specialization
        protected Object at(AbstractPointersObject receiver, int idx) {
            return receiver.at0(idx - 1 + receiver.instsize());
        }

        @Override
        @Specialization
        protected Object at(BaseSqueakObject receiver, int idx) {
            return super.at(receiver, idx);
        }
    }

    @Primitive(indices = {61}, numberOfArguments = 3)
    @GenerateNodeFactory
    public static class IndexAtPut extends AtPut {
        public IndexAtPut(CompiledCodeObject method2) {
            super(method2);
        }

        @Override
        @Specialization
        protected Object atput(AbstractPointersObject receiver, int idx, Object value) {
            receiver.atput0(idx - 1 + receiver.instsize(), value);
            return value;
        }

        @Override
        @Specialization
        protected Object atput(BaseSqueakObject receiver, int idx, Object value) {
            return super.atput(receiver, idx, value);
        }
    }

    @Primitive(indices = {62}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class Size extends BuiltinPrimitive {
        public Size(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        public int size(@SuppressWarnings("unused") char obj) {
            return 0;
        }

        @Specialization
        public int size(@SuppressWarnings("unused") boolean o) {
            return 0;
        }

        @Specialization
        public int size(@SuppressWarnings("unused") int o) {
            return 0;
        }

        @Specialization
        public int size(@SuppressWarnings("unused") long o) {
            return 0;
        }

        @Specialization
        public int size(String s) {
            return s.getBytes().length;
        }

        @Specialization
        public int size(BigInteger i) {
            return LargeInteger.byteSize(i);
        }

        @Specialization
        public int size(@SuppressWarnings("unused") double o) {
            return 2; // Float in words
        }

        @Specialization(guards = "!isNull(obj)")
        public int size(BaseSqueakObject obj) {
            return obj.size();
        }

        @Specialization
        public int size(@SuppressWarnings("unused") Object obj) {
            return 0;
        }
    }

    @Primitive(indices = {63}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class StringAt extends BuiltinPrimitive {
        public StringAt(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        char stringAt(NativeObject obj, int idx) {
            byte nativeAt0 = ((Long) obj.getNativeAt0(idx - 1)).byteValue();
            return (char) nativeAt0;
        }

    }

    @Primitive(indices = {64}, numberOfArguments = 3)
    @GenerateNodeFactory
    public static class StringAtPut extends BuiltinPrimitive {
        public StringAtPut(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        char atput(NativeObject obj, int idx, char value) {
            obj.setNativeAt0(idx - 1, value);
            return value;
        }

        @Specialization
        char atput(NativeObject obj, int idx, int value) {
            char charValue = (char) ((Integer) value).byteValue();
            obj.setNativeAt0(idx - 1, charValue);
            return charValue;
        }

    }

    @Primitive(indices = {68}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class CompiledCodeAt extends BuiltinPrimitive {
        public CompiledCodeAt(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        Object literalAt(CompiledCodeObject receiver, int idx) {
            return receiver.getLiteral(idx - 1);
        }
    }

    @Primitive(indices = {69}, numberOfArguments = 3)
    @GenerateNodeFactory
    public static class CompiledCodeAtPut extends BuiltinPrimitive {
        public CompiledCodeAtPut(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        Object setLiteral(CompiledCodeObject cc, int idx, Object value) {
            cc.setLiteral(idx, value);
            return value;
        }
    }

    @Primitive(indices = {70}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class New extends BuiltinPrimitive {
        final static int NEW_CACHE_SIZE = 3;

        public New(CompiledCodeObject method2) {
            super(method2);
        }

        @SuppressWarnings("unused")
        @Specialization(limit = "NEW_CACHE_SIZE", guards = {"receiver == cachedReceiver"}, assumptions = {"classFormatStable"})
        BaseSqueakObject newDirect(ClassObject receiver,
                        @Cached("receiver") ClassObject cachedReceiver,
                        @Cached("cachedReceiver.getClassFormatStable()") Assumption classFormatStable) {
            return cachedReceiver.newInstance();
        }

        @Specialization(replaces = "newDirect")
        BaseSqueakObject newIndirect(ClassObject receiver) {
            return receiver.newInstance();
        }

    }

    @Primitive(indices = {71}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class NewArg extends BuiltinPrimitive {
        final static int NEW_CACHE_SIZE = 3;

        public NewArg(CompiledCodeObject cm) {
            super(cm);
        }

        @SuppressWarnings("unused")
        @Specialization(limit = "NEW_CACHE_SIZE", guards = {"receiver == cachedReceiver", "size > 0", "cachedIsVariable"}, assumptions = {"classFormatStable"})
        BaseSqueakObject newWithArgDirect(ClassObject receiver, int size,
                        @Cached("receiver") ClassObject cachedReceiver,
                        @Cached("cachedReceiver.isVariable()") boolean cachedIsVariable,
                        @Cached("cachedReceiver.getClassFormatStable()") Assumption classFormatStable) {
            return cachedReceiver.newInstance(size);
        }

        @Specialization(replaces = "newWithArgDirect")
        BaseSqueakObject newWithArg(ClassObject receiver, int size) {
            if (size <= 0)
                throw new PrimitiveFailed();
            if (!receiver.isVariable())
                throw new PrimitiveFailed();
            return receiver.newInstance(size);
        }
    }

    @Primitive(indices = {73, 143, 165, 173, 210}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class At extends BuiltinPrimitive {
        public At(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        protected int at(char receiver, int idx) {
            if (idx == 1) {
                return receiver;
            } else {
                throw new PrimitiveFailed();
            }
        }

        @Specialization
        protected Object at(LargeInteger receiver, int idx) {
            return receiver.at0(idx - 1);
        }

        @Specialization
        protected long intAt(BigInteger receiver, int idx) {
            return LargeInteger.byteAt0(receiver, idx - 1);
        }

        @Specialization
        protected long at(double receiver, int idx) {
            long doubleBits = Double.doubleToLongBits(receiver);
            if (idx == 1) {
                return 0xFFFFFFFF & (doubleBits >> 32);
            } else if (idx == 2) {
                return 0xFFFFFFFF & doubleBits;
            } else {
                throw new PrimitiveFailed();
            }
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        protected int intAt(NativeObject receiver, int idx) throws ArithmeticException {
            return Math.toIntExact(receiver.getNativeAt0(idx - 1));
        }

        @Specialization
        protected long longAt(NativeObject receiver, int idx) {
            return receiver.getNativeAt0(idx - 1);
        }

        @Specialization
        protected Object at(BlockClosure receiver, int idx) {
            return receiver.at0(idx - 1);
        }

        @Specialization
        protected Object at(CompiledCodeObject receiver, int idx) {
            return receiver.at0(idx - 1);
        }

        @Specialization
        protected Object at(EmptyObject receiver, int idx) {
            return receiver.at0(idx - 1);
        }

        @Specialization
        protected Object at(AbstractPointersObject receiver, int idx) {
            return receiver.at0(idx - 1);
        }

        @Specialization
        protected Object at(BaseSqueakObject receiver, int idx) {
            return receiver.at0(idx - 1);
        }
    }

    @Primitive(indices = {74, 144, 166, 174, 211}, numberOfArguments = 3)
    @GenerateNodeFactory
    public static class AtPut extends BuiltinPrimitive {
        public AtPut(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        protected char atput(LargeInteger receiver, int idx, char value) {
            receiver.atput0(idx - 1, value);
            return value;
        }

        @Specialization
        protected int atput(LargeInteger receiver, int idx, int value) {
            receiver.atput0(idx - 1, value);
            return value;
        }

        @Specialization
        protected char atput(NativeObject receiver, int idx, char value) {
            receiver.setNativeAt0(idx - 1, value);
            return value;
        }

        @Specialization
        protected int atput(NativeObject receiver, int idx, int value) {
            receiver.setNativeAt0(idx - 1, value);
            return value;
        }

        @Specialization
        protected long atput(NativeObject receiver, int idx, long value) {
            receiver.setNativeAt0(idx - 1, value);
            return value;
        }

        @Specialization
        protected Object atput(BlockClosure receiver, int idx, Object value) {
            receiver.atput0(idx - 1, value);
            return value;
        }

        @Specialization
        protected Object atput(ClassObject receiver, int idx, Object value) {
            receiver.atput0(idx - 1, value);
            return value;
        }

        @Specialization
        protected Object atput(CompiledCodeObject receiver, int idx, Object value) {
            receiver.atput0(idx - 1, value);
            return value;
        }

        @SuppressWarnings("unused")
        @Specialization
        protected Object atput(EmptyObject receiver, int idx, Object value) {
            throw new PrimitiveFailed();
        }

        @Specialization
        protected Object atput(AbstractPointersObject receiver, int idx, Object value) {
            receiver.atput0(idx - 1, value);
            return value;
        }

        @Specialization
        protected Object atput(BaseSqueakObject receiver, int idx, Object value) {
            receiver.atput0(idx - 1, value);
            return value;
        }
    }

    @Primitive(indices = {75, 171, 175}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class IdentityHash extends BuiltinPrimitive {
        public IdentityHash(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        int hash(char obj) {
            return obj;
        }

        @Specialization
        int hash(int obj) {
            return obj;
        }

        @Specialization
        int hash(long obj) {
            return (int) obj;
        }

        @Specialization
        int hash(BigInteger obj) {
            return obj.hashCode();
        }

        @Specialization
        int hash(BaseSqueakObject obj) {
            return obj.squeakHash();
        }
    }

    @Primitive(indices = {105}, numberOfArguments = 5)
    @GenerateNodeFactory
    public static class ReplaceFromTo extends BuiltinPrimitive {
        public ReplaceFromTo(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        Object replace(LargeInteger rcvr, int start, int stop, LargeInteger repl, int replStart) {
            return replaceInLarge(rcvr, start, stop, repl.getBytes(), replStart);
        }

        @Specialization
        Object replace(LargeInteger rcvr, int start, int stop, NativeObject repl, int replStart) {
            return replaceInLarge(rcvr, start, stop, repl.getBytes(), replStart);
        }

        @Specialization
        Object replace(LargeInteger rcvr, int start, int stop, BigInteger repl, int replStart) {
            return replaceInLarge(rcvr, start, stop, LargeInteger.getSqueakBytes(repl), replStart);
        }

        private static Object replaceInLarge(LargeInteger rcvr, int start, int stop, byte[] replBytes, int replStart) {
            byte[] rcvrBytes = rcvr.getBytes();
            int repOff = replStart - start;
            for (int i = start - 1; i < stop; i++) {
                rcvrBytes[i] = replBytes[repOff + i];
            }
            rcvr.setBytes(rcvrBytes);
            return rcvr;
        }

        @Specialization
        Object replace(NativeObject rcvr, int start, int stop, LargeInteger repl, int replStart) {
            int repOff = replStart - start;
            byte[] replBytes = repl.getBytes();
            for (int i = start - 1; i < stop; i++) {
                rcvr.setNativeAt0(i, replBytes[repOff + i]);
            }
            return rcvr;
        }

        @Specialization
        Object replace(NativeObject rcvr, int start, int stop, NativeObject repl, int replStart) {
            int repOff = replStart - start;
            for (int i = start - 1; i < stop; i++) {
                rcvr.setNativeAt0(i, repl.getNativeAt0(repOff + i));
            }
            return rcvr;
        }

        @Specialization
        Object replace(NativeObject rcvr, int start, int stop, BigInteger repl, int replStart) {
            int repOff = replStart - start;
            byte[] bytes = LargeInteger.getSqueakBytes(repl);
            for (int i = start - 1; i < stop; i++) {
                rcvr.setNativeAt0(i, bytes[repOff + i]);
            }
            return rcvr;
        }

        @Specialization
        Object replace(ListObject rcvr, int start, int stop, ListObject repl, int replStart) {
            int repOff = replStart - start;
            for (int i = start - 1; i < stop; i++) {
                rcvr.atput0(i, repl.at0(repOff + i));
            }
            return rcvr;
        }
    }

    @Primitive(indices = {110}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class Equivalent extends BuiltinPrimitive {
        public Equivalent(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean equivalent(char a, char b) {
            return a == b;
        }

        @Specialization
        boolean equivalent(int a, int b) {
            return a == b;
        }

        @Specialization
        boolean equivalent(long a, long b) {
            return a == b;
        }

        @Specialization
        boolean equivalent(boolean a, boolean b) {
            return a == b;
        }

        @Specialization
        boolean equivalent(BigInteger a, BigInteger b) {
            return a.equals(b);
        }

        @Specialization
        boolean equivalent(Object a, Object b) {
            return a == b;
        }
    }

    @Primitive(indices = {111}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class SqueakClass extends BuiltinPrimitive {
        @Child SqueakLookupClassNode node;

        public SqueakClass(CompiledCodeObject method2) {
            super(method2);
            node = SqueakLookupClassNodeGen.create(method2);
        }

        @Specialization
        public Object lookup(Object arg) {
            return node.executeLookup(arg);
        }

    }

    @Primitive(indices = {148}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class ShallowCopy extends BuiltinPrimitive {
        public ShallowCopy(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        Object copy(BaseSqueakObject self) {
            return self.shallowCopy();
        }
    }

    @Primitive(indices = {170}, numberOfArguments = 1, ignoresReceiver = true)
    @GenerateNodeFactory
    public static class CharacterValue extends BuiltinPrimitive {

        public CharacterValue(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        protected char value(char value) {
            return value;
        }

        @Specialization
        protected char value(int value) {
            return (char) value;
        }
    }
}
