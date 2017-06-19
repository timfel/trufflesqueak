package de.hpi.swa.trufflesqueak.model;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public final class FrameMarker implements TruffleObject {
    public ForeignAccess getForeignAccess() {
        return null;
    }
}
