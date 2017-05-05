package de.hpi.swa.trufflesqueak.nodes.bytecodes.jump;

import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;

import de.hpi.swa.trufflesqueak.model.CompiledMethodObject;
import de.hpi.swa.trufflesqueak.nodes.SqueakNode;
import de.hpi.swa.trufflesqueak.nodes.bytecodes.SqueakBytecodeNode;

public class ConditionalJump extends AbstractJump {
    @Child private LoopNode loopNode;
    @Child private IfThenNode ifThenNode;
    protected final int offset;
    private final boolean isIfTrue;

    private ConditionalJump(CompiledMethodObject cm, int idx, int off, boolean condition) {
        super(cm, idx);
        offset = off;
        isIfTrue = condition;
        loopNode = null;
        ifThenNode = null;
    }

    public ConditionalJump(CompiledMethodObject cm, int idx, int bytecode) {
        this(cm, idx, shortJumpOffset(bytecode), false);
    }

    public ConditionalJump(CompiledMethodObject cm, int idx, int bytecode, int parameter, boolean condition) {
        this(cm, idx + 1, longJumpOffset(bytecode, parameter), condition);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (ifThenNode != null) {
            return ifThenNode.executeGeneric(frame);
        } else {
            assert loopNode != null;
            loopNode.executeLoop(frame);
            return null;
        }
    }

    @Override
    public void interpretOn(Stack<SqueakNode> stack, Stack<SqueakNode> statements, Vector<SqueakBytecodeNode> sequence) {
        assert offset > 0;
        SqueakNode branchCondition = makeBranchCondition(isIfTrue, stack);

        // the nodes making up our branch
        int firstBranchBC = index + 1;
        Vector<SqueakBytecodeNode> thenBranchNodes = new Vector<>(sequence.subList(firstBranchBC, firstBranchBC + offset));
        Vector<SqueakBytecodeNode> elseBranchNodes = null;

        SqueakBytecodeNode lastNode = thenBranchNodes.get(thenBranchNodes.size() - 1);
        if (lastNode instanceof UnconditionalJump) {
            thenBranchNodes.remove(lastNode);
            sequence.set(sequence.indexOf(lastNode), null);
            UnconditionalJump jumpOutNode = (UnconditionalJump) lastNode;
            if (jumpOutNode.offset < 0) {
                // we're the condition of a loop, the unconditional back jump will jump before us
                assert sequence.indexOf(jumpOutNode) + jumpOutNode.offset < index;
                loopNode = Truffle.getRuntime().createLoopNode(new LoopRepeatingNode(
                                method,
                                branchCondition,
                                blockFrom(thenBranchNodes, sequence, new Stack<>())));
                statements.push(this);
                return;
            } else {
                // else branch
                assert jumpOutNode.offset > 0;
                int firstElseBranchBC = firstBranchBC + offset;
                elseBranchNodes = new Vector<>(sequence.subList(firstElseBranchBC, firstElseBranchBC + jumpOutNode.offset));
            }
        }
        Stack<SqueakNode> subStack = new Stack<>();
        SqueakNode[] thenStatements = blockFrom(thenBranchNodes, sequence, subStack);
        SqueakNode thenResult = subStack.empty() ? null : subStack.pop();
        SqueakNode[] elseStatements = blockFrom(elseBranchNodes, sequence, subStack);
        SqueakNode elseResult = subStack.empty() ? null : subStack.pop();
        ifThenNode = new IfThenNode(
                        method,
                        branchCondition,
                        thenStatements,
                        thenResult,
                        elseStatements,
                        elseResult);
        assert subStack.empty();
        if (thenResult != null || elseResult != null) {
            stack.push(this);
        } else {
            statements.push(this);
        }
    }

    private static SqueakNode[] blockFrom(List<SqueakBytecodeNode> nodes, Vector<SqueakBytecodeNode> sequence, Stack<SqueakNode> subStack) {
        if (nodes == null)
            return null;
        Stack<SqueakNode> subStatements = new Stack<>();
        for (int i = 0; i < nodes.size(); i++) {
            SqueakBytecodeNode node = nodes.get(i);
            if (node != null) {
                if (sequence.contains(node)) {
                    int size = subStatements.size();
                    node.interpretOn(subStack, subStatements, sequence);
                    if (!subStack.empty() && subStatements.size() > size) {
                        // some statement was discovered, but something remains on the stack.
                        // this means the statement didn't consume everything from under itself,
                        // and we need to insert the stack item before the last statement.
                        // FIXME: can these asserts be wrong?
                        assert subStatements.size() == size + 1;
                        assert subStack.size() == 1;
                        subStatements.insertElementAt(subStack.pop(), size);
                    }
                    sequence.set(sequence.indexOf(node), null);
                }
            }
        }
        return subStatements.toArray(new SqueakNode[0]);
    }

    private static SqueakNode makeBranchCondition(boolean isIfTrue, Stack<SqueakNode> stack) {
        SqueakNode branchCondition;
        if (isIfTrue) {
            branchCondition = IfTrueNodeGen.create(stack.pop());
        } else {
            branchCondition = IfFalseNodeGen.create(stack.pop());
        }
        return branchCondition;
    }

    @Override
    public void interpretOn(Stack<SqueakNode> stack, Stack<SqueakNode> sequence) {
        // nothing
    }
}