package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.util.Arrays;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameInstance;
import com.oracle.truffle.api.frame.FrameInstanceVisitor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ValueProfile;

import de.hpi.swa.trufflesqueak.exceptions.SqueakExit;
import de.hpi.swa.trufflesqueak.model.BlockClosure;
import de.hpi.swa.trufflesqueak.model.ClassObject;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.model.ContextObject;
import de.hpi.swa.trufflesqueak.model.ListObject;
import de.hpi.swa.trufflesqueak.nodes.BlockActivationNode;
import de.hpi.swa.trufflesqueak.nodes.BlockActivationNodeGen;
import de.hpi.swa.trufflesqueak.nodes.DispatchNode;
import de.hpi.swa.trufflesqueak.nodes.DispatchNodeGen;
import de.hpi.swa.trufflesqueak.nodes.LookupNode;
import de.hpi.swa.trufflesqueak.nodes.LookupNodeGen;
import de.hpi.swa.trufflesqueak.nodes.SqueakNode;
import de.hpi.swa.trufflesqueak.nodes.SqueakTypesGen;
import de.hpi.swa.trufflesqueak.nodes.context.ArgumentNode;
import de.hpi.swa.trufflesqueak.nodes.context.SqueakLookupClassNode;
import de.hpi.swa.trufflesqueak.nodes.context.SqueakLookupClassNodeGen;

public final class EvaluationPrimitives extends PrimitiveSet {
    @Override
    List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return EvaluationPrimitivesFactory.getFactories();
    }

    @Primitive(indices = {83})
    @GenerateNodeFactory
    public static class Perform extends BuiltinPrimitive {
        private final ValueProfile classProfile = ValueProfile.createClassProfile();
        @Child public SqueakNode receiverNode;
        @Child public SqueakNode selectorNode;
        @Child protected SqueakLookupClassNode lookupClassNode;
        @Child private LookupNode lookupNode;
        @Child private DispatchNode dispatchNode;

        public Perform(CompiledCodeObject cm) {
            super(cm);
            lookupClassNode = SqueakLookupClassNodeGen.create(method);
            dispatchNode = DispatchNodeGen.create();
            lookupNode = LookupNodeGen.create();
        }

        @Specialization
        @ExplodeLoop
        public Object perform(VirtualFrame frame) {
            Object[] args = frame.getArguments();
            Object receiver = classProfile.profile(args[0]);
            ClassObject rcvrClass;
            try {
                rcvrClass = SqueakTypesGen.expectClassObject(lookupClassNode.executeLookup(receiver));
            } catch (UnexpectedResultException e) {
                throw new RuntimeException("receiver has no class");
            }
            Object selector = args[1];
            Object[] newArguments = Arrays.copyOfRange(args, 1, args.length);
            newArguments[0] = receiver; // second argument was selector, replace with receiver
            Object lookupResult = lookupNode.executeLookup(rcvrClass, selector);
            return dispatchNode.executeDispatch(lookupResult, newArguments);
        }
    }

    @Primitive(indices = {197}, numberOfArguments = 0)
    @GenerateNodeFactory
    public static class NextHandlerContext extends BuiltinPrimitive {
        private static final int EXCEPTION_HANDLER_MARKER = 199;

        public NextHandlerContext(CompiledCodeObject cm) {
            super(cm);
        }

        @Specialization
        Object findNext(ContextObject receiver) {
            Object handlerContext = Truffle.getRuntime().iterateFrames(new FrameInstanceVisitor<Object>() {
                final Object marker = receiver.getFrameMarker();
                boolean foundSelf = false;

                @Override
                public Object visitFrame(FrameInstance frameInstance) {
                    Frame current = frameInstance.getFrame(FrameInstance.FrameAccess.READ_ONLY);
                    FrameDescriptor frameDescriptor = current.getFrameDescriptor();
                    FrameSlot methodSlot = frameDescriptor.findFrameSlot(CompiledCodeObject.METHOD);
                    FrameSlot markerSlot = frameDescriptor.findFrameSlot(CompiledCodeObject.MARKER);
                    if (methodSlot != null && markerSlot != null) {
                        Object frameMethod = FrameUtil.getObjectSafe(current, methodSlot);
                        Object frameMarker = FrameUtil.getObjectSafe(current, markerSlot);
                        if (frameMarker == marker) {
                            foundSelf = true;
                        }
                        if (foundSelf) {
                            if (frameMethod instanceof CompiledCodeObject) {
                                if (((CompiledCodeObject) frameMethod).primitiveIndex() == EXCEPTION_HANDLER_MARKER) {
                                    return frameMethod;
                                }
                            }
                        }
                    }
                    return null;
                }
            });
            if (handlerContext == null) {
                printException();
            }
            return handlerContext;
        }

        @TruffleBoundary
        private void printException() {
            method.image.getOutput().println("=== Unhandled Error ===");
            Truffle.getRuntime().iterateFrames(new FrameInstanceVisitor<Object>() {
                @Override
                public Object visitFrame(FrameInstance frameInstance) {
                    Frame current = frameInstance.getFrame(FrameInstance.FrameAccess.READ_ONLY);
                    FrameDescriptor frameDescriptor = current.getFrameDescriptor();
                    FrameSlot methodSlot = frameDescriptor.findFrameSlot(CompiledCodeObject.METHOD);
                    if (methodSlot != null) {
                        method.image.getOutput().println(FrameUtil.getObjectSafe(current, methodSlot));
                        for (Object arg : current.getArguments()) {
                            method.image.getOutput().append("   ");
                            method.image.getOutput().println(arg);
                        }
                    }
                    return null;
                }
            });
            throw new SqueakExit(1);
        }
    }

    @Primitive(indices = {201, 202, 203, 204, 205}, maxNumberOfArguments = 5)
    @GenerateNodeFactory
    public static class ClosureValue extends BuiltinPrimitive {
        @Child protected BlockActivationNode dispatch;

        public ClosureValue(CompiledCodeObject method2) {
            super(method2);
            dispatch = BlockActivationNodeGen.create();
        }

        @Specialization
        protected Object value(BlockClosure block, Object a1, Object a2, Object a3, Object a4) {
            Object[] frameArguments;
            if (a1 == ArgumentNode.NO_ARGUMENT) {
                frameArguments = block.getFrameArguments();
            } else if (a2 == ArgumentNode.NO_ARGUMENT) {
                frameArguments = block.getFrameArguments(a1);
            } else if (a3 == ArgumentNode.NO_ARGUMENT) {
                frameArguments = block.getFrameArguments(a1, a2);
            } else if (a4 == ArgumentNode.NO_ARGUMENT) {
                frameArguments = block.getFrameArguments(a1, a2, a3);
            } else {
                frameArguments = block.getFrameArguments(a1, a2, a3, a4);
            }
            return dispatch.executeBlock(block, frameArguments);
        }
    }

    @Primitive(indices = {206}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class ClosureValueVarargs extends BuiltinPrimitive {
        @Child protected BlockActivationNode dispatch;

        public ClosureValueVarargs(CompiledCodeObject method2) {
            super(method2);
            dispatch = BlockActivationNodeGen.create();
        }

        @Specialization
        protected Object value(BlockClosure block, ListObject argArray) {
            return dispatch.executeBlock(block, block.getFrameArguments(argArray.getPointers()));
        }
    }
}
