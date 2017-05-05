package de.hpi.swa.trufflesqueak.nodes.primitives.impl;

import com.oracle.truffle.api.dsl.Specialization;

import de.hpi.swa.trufflesqueak.model.CompiledMethodObject;
import de.hpi.swa.trufflesqueak.nodes.primitives.PrimitiveBinaryOperation;

public class PrimBitAnd extends PrimitiveBinaryOperation {
    public PrimBitAnd(CompiledMethodObject cm) {
        super(cm);
    }

    @Specialization
    protected int bitAnd(int receiver, int arg) {
        return receiver & arg;
    }
}