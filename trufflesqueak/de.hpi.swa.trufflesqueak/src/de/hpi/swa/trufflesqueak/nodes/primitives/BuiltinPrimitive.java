/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hpi.swa.trufflesqueak.nodes.primitives;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.frame.VirtualFrame;
import de.hpi.swa.trufflesqueak.exceptions.PrimitiveFailed;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.nodes.SqueakNode;
import de.hpi.swa.trufflesqueak.nodes.SqueakNodeWithMethod;

/**
 *
 * @author tim
 */
@NodeChildren({@NodeChild(value = "arguments", type = SqueakNode[].class)})
public class BuiltinPrimitive extends SqueakNodeWithMethod {
    public BuiltinPrimitive(CompiledCodeObject method2) {
        super(method2);
    }
    
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        if (method.image.config.isVerbose()) {
            System.out.println("Primitive not yet written: " + method.toString());
        }
        throw new PrimitiveFailed();
    }
}
