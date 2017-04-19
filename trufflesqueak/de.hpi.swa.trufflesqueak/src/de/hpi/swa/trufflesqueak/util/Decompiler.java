package de.hpi.swa.trufflesqueak.util;

import java.util.Vector;

import de.hpi.swa.trufflesqueak.SqueakImageContext;
import de.hpi.swa.trufflesqueak.model.CompiledMethodObject;
import de.hpi.swa.trufflesqueak.model.SmallInteger;
import de.hpi.swa.trufflesqueak.nodes.BytecodeSequence;
import de.hpi.swa.trufflesqueak.nodes.PrimitiveNode;
import de.hpi.swa.trufflesqueak.nodes.SqueakBytecodeNode;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.CallPrimitive;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.DoubleExtendedDoAnything;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.Dup;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ExtendedPush;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ExtendedStore;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ExtendedStoreAndPop;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.Pop;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushActiveContext;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushClosure;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushConst;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushLiteralConst;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushNewArray;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushReceiver;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushReceiverVariable;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushRemoteTemp;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushTemp;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.PushVariable;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ReturnConst;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ReturnReceiver;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ReturnTopFromBlock;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.ReturnTopFromMethod;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.SecondExtendedSend;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.Send;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.SendSelector;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.SingleExtendedSend;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.SingleExtendedSuper;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.StoreAndPopRcvr;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.StoreAndPopRemoteTemp;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.StoreAndPopTemp;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.StoreRemoteTemp;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.UnknownBytecode;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.jump.LongJump;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.jump.LongJumpIfFalse;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.jump.LongJumpIfTrue;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.jump.ShortCondJump;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.jump.ShortJump;

public class Decompiler {
    private byte[] bytes;
    private CompiledMethodObject method;
    private SqueakImageContext image;

    public Decompiler(SqueakImageContext img, CompiledMethodObject cm, byte[] bc) {
        image = img;
        method = cm;
        bytes = bc;
    }

    public BytecodeSequence getAST() {
        int index[] = {0};
        Vector<SqueakBytecodeNode> sequence = new Vector<>();
        while (index[0] < bytes.length) {
            int idx = index[0];
            sequence.add(idx, decodeByteAt(index));
        }
        return new BytecodeSequence(sequence.toArray(new SqueakBytecodeNode[sequence.size()]));
    }

    private SqueakBytecodeNode decodeByteAt(int[] indexRef) {
        int index = indexRef[0];
        int b = bytes[index];
        if (b < 0) {
            b = Math.abs(b) + 127;
        }
        indexRef[0] = index + 1;
        switch (b) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return new PushReceiverVariable(method, index, b & 15);
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                return new PushTemp(method, index, b & 15);
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return new PushLiteralConst(method, index, b & 31);
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
                return new PushVariable(method, index, b & 31);
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
                return new StoreAndPopRcvr(method, index, b & 7);
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
                return new StoreAndPopTemp(method, index, b & 7);
            case 112:
                return new PushReceiver(method, index);
            case 113:
                return new PushConst(method, index, image.sqTrue);
            case 114:
                return new PushConst(method, index, image.sqFalse);
            case 115:
                return new PushConst(method, index, image.nil);
            case 116:
                return new PushConst(method, index, new SmallInteger(-1));
            case 117:
                return new PushConst(method, index, new SmallInteger(0));
            case 118:
                return new PushConst(method, index, new SmallInteger(1));
            case 119:
                return new PushConst(method, index, new SmallInteger(2));
            case 120:
                return new ReturnReceiver(method, index);
            case 121:
                return new ReturnConst(method, index, image.sqTrue);
            case 122:
                return new ReturnConst(method, index, image.sqFalse);
            case 123:
                return new ReturnConst(method, index, image.nil);
            case 124:
                return new ReturnTopFromMethod(method, index);
            case 125:
                return new ReturnTopFromBlock(method, index);
            case 126:
                return new UnknownBytecode(method, index);
            case 127:
                return new UnknownBytecode(method, index);
            case 128:
                return new ExtendedPush(method, index, bytes[indexRef[0] = ++index]);
            case 129:
                return new ExtendedStore(method, index, bytes[indexRef[0] = ++index]);
            case 130:
                return new ExtendedStoreAndPop(method, index, bytes[indexRef[0] = ++index]);
            case 131:
                return new SingleExtendedSend(method, index, bytes[indexRef[0] = ++index]);
            case 132:
                return new DoubleExtendedDoAnything(method, index, bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index]);
            case 133:
                return new SingleExtendedSuper(method, index, bytes[indexRef[0] = ++index]);
            case 134:
                return new SecondExtendedSend(method, index, bytes[indexRef[0] = ++index]);
            case 135:
                return new Pop(method, index);
            case 136:
                return new Dup(method, index);
            case 137:
                return new PushActiveContext(method, index);
            case 138:
                return new PushNewArray(method, index, bytes[indexRef[0] = ++index]);
            case 139:
                return new CallPrimitive(method, index, bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index]);
            case 140:
                return new PushRemoteTemp(method, index, bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index]);
            case 141:
                return new StoreRemoteTemp(method, index, bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index]);
            case 142:
                return new StoreAndPopRemoteTemp(method, index, bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index]);
            case 143:
                return new PushClosure(method, index, bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index], bytes[indexRef[0] = ++index]);
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
                return new ShortJump(method, index, b);
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
                return new ShortCondJump(method, index, b);
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
                return new LongJump(method, index, b, bytes[indexRef[0] = ++index]);
            case 168:
            case 169:
            case 170:
            case 171:
                return new LongJumpIfTrue(method, index, b, bytes[indexRef[0] = ++index]);
            case 172:
            case 173:
            case 174:
            case 175:
                return new LongJumpIfFalse(method, index, b, bytes[indexRef[0] = ++index]);
            case 176:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.ADD.index);
            case 177:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.SUB.index);
            case 178:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.LESSTHAN.index);
            case 179:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.GREATERTHAN.index);
            case 180:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.LESSOREQUAL.index);
            case 181:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.GREATEROREQUAL.index);
            case 182:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.EQUAL.index);
            case 183:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.NOTEQUAL.index);
            case 184:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.MULTIPLY.index);
            case 185:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.DIVIDE.index);
            case 186:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.MOD.index);
            case 187:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.MAKE_POINT.index);
            case 188:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.BIT_SHIFT.index);
            case 189:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.DIV.index);
            case 190:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.BIT_AND.index);
            case 191:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.BIT_OR.index);
            case 192:
                return new SendSelector(method, index, "at:");
            case 193:
                return new SendSelector(method, index, "at:put:");
            case 194:
                return new SendSelector(method, index, "size");
            case 195:
                return new SendSelector(method, index, "next");
            case 196:
                return new SendSelector(method, index, "nextPut:");
            case 197:
                return new SendSelector(method, index, "atEnd");
            case 198:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.EQUIVALENT.index);
            case 199:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.CLASS.index);
            case 200:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.BLOCK_COPY.index);
            case 201:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.CLOSURE_VALUE.index);
            case 202:
                return PrimitiveNode.forIdx(method, PrimitiveNode.Primitives.CLOSURE_VALUE_WITH_ARG.index);
            case 203:
                return new SendSelector(method, index, "do:");
            case 204:
                return new SendSelector(method, index, "new");
            case 205:
                return new SendSelector(method, index, "new:");
            case 206:
                return new SendSelector(method, index, "x");
            case 207:
                return new SendSelector(method, index, "y");
            default:
                return new Send(method, index, b);
        }
    }
}
