package de.hpi.swa.trufflesqueak.nodes.context;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.hpi.swa.trufflesqueak.nodes.SqueakNode;

public abstract class ArgumentNode extends SqueakNode {
    protected final int idx;

    protected ArgumentNode(int index) {
        idx = index;
    }

    public static ArgumentNode create(int idx) {
        return new UninitializedArgumentNode(idx);
    }

    public static ArgumentNode createUnsafe(int idx) {
        return new GivenArgumentNode(idx);
    }

    private static final class GivenArgumentNode extends ArgumentNode {
        protected GivenArgumentNode(int index) {
            super(index);
        }

        @Override
        public Object executeGeneric(VirtualFrame frame) {
            Object[] args = frame.getArguments();
            return args[idx];
        }
    }

    private static final class OutOfBoundsArgumentNode extends ArgumentNode {
        protected OutOfBoundsArgumentNode(int index) {
            super(index);
        }

        @Override
        public Object executeGeneric(VirtualFrame frame) {
            return null;
        }
    }

    private static final class UninitializedArgumentNode extends ArgumentNode {
        protected UninitializedArgumentNode(int index) {
            super(index);
        }

        @Override
        public Object executeGeneric(VirtualFrame frame) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            Object[] arguments = frame.getArguments();
            if (idx >= arguments.length) {
                replace(new OutOfBoundsArgumentNode(idx));
                return null;
            } else {
                replace(new GivenArgumentNode(idx));
                return arguments[idx];
            }
        }
    }
}
