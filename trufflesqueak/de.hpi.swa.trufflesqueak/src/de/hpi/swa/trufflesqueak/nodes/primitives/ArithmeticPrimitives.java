package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.math.BigInteger;
import java.util.List;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;

import de.hpi.swa.trufflesqueak.exceptions.PrimitiveFailed;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.model.CompiledMethodObject;
import de.hpi.swa.trufflesqueak.model.LargeInteger;
import de.hpi.swa.trufflesqueak.model.ListObject;
import de.hpi.swa.trufflesqueak.model.NativeObject;

public final class ArithmeticPrimitives extends PrimitiveSet {
    @Override
    protected List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return ArithmeticPrimitivesFactory.getFactories();
    }

    @Primitive(indices = {1, 21, 41}, numberOfArguments = 2, module = "LargeIntegers", names = {"primDigitAdd"})
    @GenerateNodeFactory
    public abstract static class Add extends BuiltinPrimitive {

        public Add(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int add(int a, int b) {
            return Math.addExact(a, b);
        }

        @Specialization
        long addOverflow(int a, int b) {
            return (long) a + (long) b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long add(long a, long b) {
            return Math.addExact(a, b);
        }

        @Specialization
        BigInteger add(BigInteger a, BigInteger b) {
            return a.add(b);
        }

        @Specialization
        double add(double a, double b) {
            return a + b;
        }
    }

    @Primitive(indices = {2, 22, 42}, numberOfArguments = 2, module = "LargeIntegers", names = {"primDigitSubtract"})
    @GenerateNodeFactory
    public abstract static class Sub extends BuiltinPrimitive {

        public Sub(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int sub(int a, int b) {
            return Math.subtractExact(a, b);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int subInt(long a, long b) {
            return Math.toIntExact(Math.subtractExact(a, b));
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long sub(long a, long b) {
            return Math.subtractExact(a, b);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int subInt(BigInteger a, BigInteger b) {
            return a.subtract(b).intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long sub(BigInteger a, BigInteger b) {
            return a.subtract(b).longValueExact();
        }

        @Specialization
        BigInteger subBig(BigInteger a, BigInteger b) {
            return a.subtract(b);
        }

        @Specialization
        double sub(double a, double b) {
            return a - b;
        }
    }

    @Primitive(indices = {3, 23, 43}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class LessThan extends BuiltinPrimitive {

        public LessThan(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean lt(int a, int b) {
            return a < b;
        }

        @Specialization
        boolean lt(long a, long b) {
            return a < b;
        }

        @Specialization
        boolean lt(BigInteger a, BigInteger b) {
            return a.compareTo(b) < 0;
        }

        @Specialization
        boolean lt(double a, double b) {
            return a < b;
        }
    }

    @Primitive(indices = {4, 24, 44}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class GreaterThan extends BuiltinPrimitive {

        public GreaterThan(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean gt(int a, int b) {
            return a > b;
        }

        @Specialization
        boolean gt(long a, long b) {
            return a > b;
        }

        @Specialization
        boolean gt(BigInteger a, BigInteger b) {
            return a.compareTo(b) > 0;
        }

        @Specialization
        boolean gt(double a, double b) {
            return a > b;
        }
    }

    @Primitive(indices = {5, 25, 45}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class LessOrEqual extends BuiltinPrimitive {

        public LessOrEqual(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean le(int a, int b) {
            return a <= b;
        }

        @Specialization
        boolean le(long a, long b) {
            return a <= b;
        }

        @Specialization
        boolean le(BigInteger a, BigInteger b) {
            return a.compareTo(b) <= 0;
        }

        @Specialization
        boolean le(double a, double b) {
            return a <= b;
        }
    }

    @Primitive(indices = {6, 26, 46}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class GreaterOrEqual extends BuiltinPrimitive {

        public GreaterOrEqual(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean ge(int a, int b) {
            return a >= b;
        }

        @Specialization
        boolean ge(long a, long b) {
            return a >= b;
        }

        @Specialization
        boolean ge(BigInteger a, BigInteger b) {
            return a.compareTo(b) >= 0;
        }

        @Specialization
        boolean ge(double a, double b) {
            return a >= b;
        }
    }

    @Primitive(indices = {7, 27, 47}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class Equal extends BuiltinPrimitive {

        public Equal(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean equal(int a, int b) {
            return a == b;
        }

        @Specialization
        boolean equal(long a, long b) {
            return a == b;
        }

        @Specialization
        boolean equal(BigInteger a, BigInteger b) {
            return a.compareTo(b) == 0;
        }

        @Specialization
        boolean equal(double a, double b) {
            return a == b;
        }
    }

    @Primitive(indices = {8, 28, 48}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class NotEqual extends BuiltinPrimitive {

        public NotEqual(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        boolean ne(int a, int b) {
            return a == b;
        }

        @Specialization
        boolean ne(long a, long b) {
            return a != b;
        }

        @Specialization
        boolean ne(BigInteger a, BigInteger b) {
            return a.compareTo(b) != 0;
        }

        @Specialization
        boolean ne(double a, double b) {
            return a != b;
        }
    }

    @Primitive(indices = {9, 29, 49}, numberOfArguments = 2, module = "LargeIntegers", names = {"primDigitMultiplyNegative"})
    @GenerateNodeFactory
    public static class Mul extends BuiltinPrimitive {

        public Mul(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int mul(int a, int b) {
            return Math.multiplyExact(a, b);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long mul(long a, long b) {
            return Math.multiplyExact(a, b);
        }

        @Specialization
        BigInteger mul(BigInteger a, BigInteger b) {
            return a.multiply(b);
        }

        @Specialization
        double mul(double a, double b) {
            return a * b;
        }
    }

    @Primitive(indices = {10, 30, 50}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class Divide extends BuiltinPrimitive {

        public Divide(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int divide(int a, int b) {
            if (a % b != 0) {
                throw new PrimitiveFailed();
            }
            return a / b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long divideInt(long a, long b) {
            if (a % b != 0) {
                throw new PrimitiveFailed();
            }
            return Math.toIntExact(a / b);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long divide(long a, long b) {
            if (a % b != 0) {
                throw new PrimitiveFailed();
            }
            return a / b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int divdideInt(BigInteger a, BigInteger b) {
            if (a.mod(b.abs()).compareTo(BigInteger.ZERO) != 0) {
                throw new PrimitiveFailed();
            }
            return a.divide(b).intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long divide(BigInteger a, BigInteger b) {
            if (a.mod(b.abs()).compareTo(BigInteger.ZERO) != 0) {
                throw new PrimitiveFailed();
            }
            return a.divide(b).longValueExact();
        }

        @Specialization
        BigInteger divBig(BigInteger a, BigInteger b) {
            if (a.mod(b.abs()).compareTo(BigInteger.ZERO) != 0) {
                throw new PrimitiveFailed();
            }
            return a.divide(b);
        }

        @Specialization
        double div(double a, double b) {
            return a / b;
        }
    }

    @Primitive(indices = {11, 31}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class Mod extends BuiltinPrimitive {
        public Mod(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        int mod(int a, int b) {
            return a % b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int modInt(long a, long b) {
            return Math.toIntExact(a % b);
        }

        @Specialization
        long mod(long a, long b) {
            return a % b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int modInt(BigInteger a, BigInteger b) {
            return doBigModulo(a, b).intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long mod(BigInteger a, BigInteger b) {
            return doBigModulo(a, b).longValueExact();
        }

        @Specialization
        BigInteger modBig(BigInteger a, BigInteger b) {
            return doBigModulo(a, b);
        }

        private static BigInteger doBigModulo(BigInteger a, BigInteger b) {
            BigInteger mod = a.mod(b.abs());
            if (a.signum() + b.signum() <= 0) {
                return mod.negate();
            } else {
                return mod;
            }
        }
    }

    @Primitive(indices = {12, 32}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class Div extends BuiltinPrimitive {
        public Div(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int div(int a, int b) {
            if (a == Integer.MIN_VALUE && b == -1) {
                throw new ArithmeticException();
            }
            return Math.floorDiv(a, b);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int divInt(long a, long b) {
            if (a == Long.MIN_VALUE && b == -1) {
                throw new ArithmeticException();
            }
            return Math.toIntExact(Math.floorDiv(a, b));
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long div(long a, long b) {
            if (a == Long.MIN_VALUE && b == -1) {
                throw new ArithmeticException();
            }
            return Math.floorDiv(a, b);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int divInt(BigInteger a, BigInteger b) {
            return a.divide(b).intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long div(BigInteger a, BigInteger b) {
            return a.divide(b).longValueExact();
        }

        @Specialization
        BigInteger divBig(BigInteger a, BigInteger b) {
            return a.divide(b);
        }
    }

    @Primitive(indices = {13, 33}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class Quo extends BuiltinPrimitive {
        public Quo(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        int quo(int a, int b) {
            return a / b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int quoInt(long a, long b) {
            return Math.toIntExact(a / b);
        }

        @Specialization
        long quo(long a, long b) {
            return a / b;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int quoInt(BigInteger a, BigInteger b) {
            return a.divide(b).intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long quo(BigInteger a, BigInteger b) {
            return a.divide(b).longValueExact();
        }

        @Specialization
        BigInteger quoBig(BigInteger a, BigInteger b) {
            return a.divide(b);
        }
    }

    @Primitive(indices = {14, 44}, numberOfArguments = 2, module = "LargeIntegers", names = {"primDigitBitAnd"})
    @GenerateNodeFactory
    public static class BitAnd extends BuiltinPrimitive {
        public BitAnd(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        protected int bitAnd(int receiver, int arg) {
            return receiver & arg;
        }

        @Specialization
        protected long bitAnd(long receiver, long arg) {
            return receiver & arg;
        }

        @Specialization
        protected BigInteger bitAnd(BigInteger receiver, BigInteger arg) {
            return receiver.and(arg);
        }
    }

    @Primitive(indices = {15, 45}, numberOfArguments = 2, module = "LargeIntegers", names = {"primDigitBitOr"})
    @GenerateNodeFactory
    public static class BitOr extends BuiltinPrimitive {
        public BitOr(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        protected int bitOr(int receiver, int arg) {
            return receiver | arg;
        }

        @Specialization
        protected long bitOr(long receiver, long arg) {
            return receiver | arg;
        }

        @Specialization
        protected BigInteger bitOr(BigInteger receiver, BigInteger arg) {
            return receiver.or(arg);
        }
    }

    @Primitive(indices = {16, 46}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class BitXor extends BuiltinPrimitive {
        public BitXor(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        protected int bitXor(int receiver, int arg) {
            return receiver ^ arg;
        }

        @Specialization
        protected long bitXor(long receiver, long arg) {
            return receiver ^ arg;
        }

        @Specialization
        protected BigInteger bitXor(BigInteger receiver, BigInteger arg) {
            return receiver.xor(arg);
        }
    }

    @Primitive(indices = {17, 47}, numberOfArguments = 2, module = "LargeIntegers", names = {"primDigitBitShiftMagnitude"})
    @GenerateNodeFactory
    public static class BitShift extends BuiltinPrimitive {
        @Child Normalize normalizeNode;

        public BitShift(CompiledCodeObject cm) {
            super(cm);
            normalizeNode = new Normalize(cm);
        }

        @Specialization(guards = {"arg <= 0"})
        protected int bitShiftRightInt(int receiver, int arg) {
            return receiver >> -arg;
        }

        @Specialization(guards = {"arg <= 0"}, rewriteOn = ArithmeticException.class)
        protected int bitShiftRightInt(long receiver, int arg) {
            return Math.toIntExact(receiver >> -arg);
        }

        @Specialization(guards = {"arg <= 0"})
        protected long bitShiftRightLong(long receiver, int arg) {
            return receiver >> -arg;
        }

        @Specialization(guards = {"arg <= 0"}, rewriteOn = ArithmeticException.class)
        protected int bitShiftRightInt(BigInteger receiver, int arg) {
            return receiver.shiftRight(-arg).intValueExact();
        }

        @Specialization(guards = {"arg <= 0"}, rewriteOn = ArithmeticException.class)
        protected long bitShiftRightLong(BigInteger receiver, int arg) {
            return receiver.shiftRight(-arg).longValueExact();
        }

        @Specialization(guards = {"arg <= 0"})
        protected BigInteger bitShiftRightBig(BigInteger receiver, int arg) {
            return receiver.shiftRight(-arg);
        }

        @Specialization(guards = {"arg > 0"}, rewriteOn = ArithmeticException.class)
        protected int bitShiftLeftInt(BigInteger receiver, int arg) {
            return receiver.shiftLeft(arg).intValueExact();
        }

        @Specialization(guards = {"arg > 0"}, rewriteOn = ArithmeticException.class)
        protected long bitShiftLeftLong(BigInteger receiver, int arg) {
            return receiver.shiftLeft(arg).longValueExact();
        }

        @Specialization(guards = {"arg > 0"})
        protected BigInteger bitShiftLeft(BigInteger receiver, int arg) {
            return receiver.shiftLeft(arg);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        protected long bitShiftNativeLong(NativeObject receiver, int arg) {
            return shiftNative(receiver, arg).longValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        protected BigInteger bitShiftNativeBig(NativeObject receiver, int arg) {
            return shiftNative(receiver, arg);
        }

        private BigInteger shiftNative(NativeObject receiver, int arg) {
            BigInteger integer = normalizeNode.normalizeBig(receiver);
            if (arg < 0) {
                return integer.shiftRight(-arg);
            } else {
                return integer.shiftLeft(arg);
            }
        }
    }

    @Primitive(indices = {40}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class AsFloat extends BuiltinPrimitive {

        public AsFloat(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double asFloat(int v) {
            return v;
        }

        @Specialization
        double asFloat(long v) {
            return v;
        }

        @Specialization
        double asFloat(double v) {
            return v;
        }
    }

    @Primitive(indices = {51}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class FloatTruncated extends BuiltinPrimitive {
        public FloatTruncated(CompiledMethodObject cm) {
            super(cm);
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int truncateToInt(double receiver) {
            return Math.toIntExact((long) Math.floor(receiver));
        }

        @Specialization
        long truncate(double receiver) {
            return (long) Math.floor(receiver);
        }
    }

    @Primitive(indices = {53}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class FloatExponent extends BuiltinPrimitive {
        public FloatExponent(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        int exponentAsInt(double receiver) {
            return Math.getExponent(receiver);
        }
    }

    @Primitive(indices = {54}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class FloatTimesTwoPower extends BuiltinPrimitive {
        public FloatTimesTwoPower(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double calc(double receiver, long argument) {
            return receiver * Math.pow(2, argument);
        }
    }

    @Primitive(indices = {55}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class SquareRoot extends BuiltinPrimitive {
        public SquareRoot(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double squareRoot(double a) {
            return Math.sqrt(a);
        }
    }

    @Primitive(indices = {56}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class Sin extends BuiltinPrimitive {
        public Sin(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double sin(double a) {
            return Math.sin(a);
        }
    }

    @Primitive(indices = {57}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class ArcTan extends BuiltinPrimitive {
        public ArcTan(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double atan(double a) {
            return Math.atan(a);
        }
    }

    @Primitive(indices = {58}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class LogN extends BuiltinPrimitive {
        public LogN(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double log(double a) {
            return Math.log(a);
        }
    }

    @Primitive(indices = {59}, numberOfArguments = 1)
    @GenerateNodeFactory
    public static class Exp extends BuiltinPrimitive {
        public Exp(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        double exp(double a) {
            return Math.exp(a);
        }
    }

    @Primitive(names = {"primDigitDivNegative"}, module = "LargeIntegers")
    @GenerateNodeFactory
    public static class LargeDigitDiv extends BuiltinPrimitive {
        public LargeDigitDiv(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        ListObject div(BigInteger rcvr, BigInteger arg) {
            BigInteger[] divRem = rcvr.divideAndRemainder(arg);
            return method.image.wrap(new Object[]{
                            method.image.wrap(divRem[0]),
                            method.image.wrap(divRem[1])});
        }
    }

    @Primitive(names = {"primNormalizePositive", "primNormalizeNegative"}, module = "LargeIntegers")
    @GenerateNodeFactory
    public static class Normalize extends BuiltinPrimitive {
        public Normalize(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        int normalizeInt(int o) {
            return o;
        }

        @Specialization
        long normalizeLong(long o) {
            return o;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int normalizeInt(BigInteger o) {
            return o.intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long normalizeLong(BigInteger o) {
            return o.longValueExact();
        }

        @Specialization
        BigInteger normalizeBig(BigInteger o) {
            return o;
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        int normalizeInt(NativeObject o) {
            return bigIntFromNative(o).intValueExact();
        }

        @Specialization(rewriteOn = ArithmeticException.class)
        long normalizeLong(NativeObject o) {
            return bigIntFromNative(o).longValueExact();
        }

        @Specialization
        BigInteger normalizeBig(NativeObject o) {
            return bigIntFromNative(o);
        }

        private BigInteger bigIntFromNative(NativeObject o) {
            return new LargeInteger(method.image, o.getSqClass(), o.getBytes()).getValue();
        }
    }
}
