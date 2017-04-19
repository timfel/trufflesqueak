package de.hpi.swa.trufflesqueak.exceptions;

import de.hpi.swa.trufflesqueak.model.BaseSqueakObject;

public class Return extends Exception {
    private static final long serialVersionUID = 1L;
    public final BaseSqueakObject returnValue;

    public Return(BaseSqueakObject object) {
        returnValue = object;
    }
}
