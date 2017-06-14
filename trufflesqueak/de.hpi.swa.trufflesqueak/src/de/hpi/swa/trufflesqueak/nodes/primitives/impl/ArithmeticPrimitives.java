/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hpi.swa.trufflesqueak.nodes.primitives.impl;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import de.hpi.swa.trufflesqueak.exceptions.PrimitiveFailed;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentNode;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentProfileNode;
import de.hpi.swa.trufflesqueak.nodes.primitives.BuiltinPrimitive;
import de.hpi.swa.trufflesqueak.nodes.primitives.Primitive;
import java.math.BigInteger;
import java.util.List;

public final class ArithmeticPrimitives {

    protected List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return ArithmeticPrimitivesFactory.getFactories();
    }

    public BuiltinPrimitive forIdx(CompiledCodeObject cc, int index) {
        List<NodeFactory<? extends BuiltinPrimitive>> factories = getNodeFactories();
        assert factories != null : "No factories found. Override getFactories() to resolve this.";
        for (NodeFactory<? extends BuiltinPrimitive> factory : factories) {
            Primitive annotation = factory.getNodeClass().getAnnotation(Primitive.class);
            for (int idx : annotation.indices()) {
                if (idx == index) {
                    Object[] arguments = new Object[index + 1];
                    arguments[0] = cc;
                    for (int i = 0; i < annotation.numberOfArguments(); i++) {
                        arguments[i + 1] = new ArgumentProfileNode(new ArgumentNode(i));
                    }
                    return factory.createNode(arguments);
                }
            }
        }
        return null;
    }

    @Primitive(indices = {1, 21, 41}, numberOfArguments = 2)
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

    @Primitive(indices = {2, 22, 42}, numberOfArguments = 2)
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

    @Primitive(indices = {9, 29, 49}, numberOfArguments = 2)
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

    @Primitive(indices = {11, 31, 51}, numberOfArguments = 2)
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
}
