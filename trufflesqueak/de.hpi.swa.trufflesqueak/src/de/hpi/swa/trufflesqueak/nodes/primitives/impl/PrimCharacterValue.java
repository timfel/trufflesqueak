package de.hpi.swa.trufflesqueak.nodes.primitives.impl;

import com.oracle.truffle.api.dsl.Specialization;

import de.hpi.swa.trufflesqueak.model.BaseSqueakObject;
import de.hpi.swa.trufflesqueak.model.CompiledMethodObject;
import de.hpi.swa.trufflesqueak.nodes.primitives.PrimitiveBinaryOperation;

public class PrimCharacterValue extends PrimitiveBinaryOperation {

    public PrimCharacterValue(CompiledMethodObject cm) {
        super(cm);
    }

    @Specialization
    protected char value(@SuppressWarnings("unused") BaseSqueakObject ignored, char value) {
        return value;
    }

    @Specialization
    protected char value(@SuppressWarnings("unused") BaseSqueakObject ignored, int value) {
        return (char) value;
    }
}
