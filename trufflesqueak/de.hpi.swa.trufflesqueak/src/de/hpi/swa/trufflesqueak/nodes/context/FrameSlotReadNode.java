package de.hpi.swa.trufflesqueak.nodes.context;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameUtil;
import com.oracle.truffle.api.frame.VirtualFrame;

import de.hpi.swa.trufflesqueak.instrumentation.PrettyPrintVisitor;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;

public abstract class FrameSlotReadNode extends FrameSlotNode {
    protected FrameSlotReadNode(CompiledCodeObject cm, FrameSlot frameSlot) {
        super(cm, frameSlot);
    }

    public static FrameSlotReadNode create(CompiledCodeObject cm, FrameSlot frameSlot) {
        return FrameSlotReadNodeGen.create(cm, frameSlot);
    }

    public static FrameSlotReadNode temp(CompiledCodeObject cm, int index) {
        if (cm.getNumStackSlots() >= index) {
            return create(cm, cm.getStackSlot(index));
        }
        return null;
    }

    public static FrameSlotReadNode receiver(CompiledCodeObject method) {
        return create(method, method.receiverSlot);
    }

    @Specialization(guards = "isInt(frame)")
    public int readInt(VirtualFrame frame) {
        return FrameUtil.getIntSafe(frame, slot);
    }

    @Specialization(guards = "isLong(frame)", rewriteOn = ArithmeticException.class)
    public int readLongAsInt(VirtualFrame frame) {
        long longSafe = FrameUtil.getLongSafe(frame, slot);
        return Math.toIntExact(longSafe);
    }

    @Specialization(guards = "isLong(frame)")
    public long readLong(VirtualFrame frame) {
        return FrameUtil.getLongSafe(frame, slot);
    }

    @Specialization(guards = "isDouble(frame)")
    public double readDouble(VirtualFrame frame) {
        return FrameUtil.getDoubleSafe(frame, slot);
    }

    @Specialization(guards = "isBoolean(frame)")
    public boolean readBool(VirtualFrame frame) {
        return FrameUtil.getBooleanSafe(frame, slot);
    }

    @Specialization(guards = "isObject(frame)")
    public Object readObject(VirtualFrame frame) {
        return FrameUtil.getObjectSafe(frame, slot);
    }

    @Specialization(guards = "isIllegal(frame)")
    public Object readIllegal(@SuppressWarnings("unused") VirtualFrame frame) {
        return null;
    }

    @Override
    public void accept(PrettyPrintVisitor b) {
        b.visit(this);
    }
}
