package de.hpi.swa.trufflesqueak.nodes.context;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.profiles.ValueProfile;

import de.hpi.swa.trufflesqueak.instrumentation.PrettyPrintVisitor;
import de.hpi.swa.trufflesqueak.model.BaseSqueakObject;
import de.hpi.swa.trufflesqueak.nodes.SqueakNode;

@NodeChildren({@NodeChild(value = "objectNode", type = SqueakNode.class)})
public abstract class ObjectAtNode extends SqueakNode {
    public final int index;
    private final ValueProfile classProfile = ValueProfile.createClassProfile();

    protected ObjectAtNode(int variableIndex) {
        index = variableIndex;
    }

    @Specialization
    protected Object readObject(BaseSqueakObject object) {
        return classProfile.profile(object).at0(index);
    }

    @Override
    public void accept(PrettyPrintVisitor str) {
        str.visit(this);
    }
}
