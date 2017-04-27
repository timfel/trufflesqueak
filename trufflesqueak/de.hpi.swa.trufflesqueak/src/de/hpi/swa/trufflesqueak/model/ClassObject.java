package de.hpi.swa.trufflesqueak.model;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.utilities.CyclicAssumption;

import de.hpi.swa.trufflesqueak.SqueakImageContext;
import de.hpi.swa.trufflesqueak.util.Chunk;

public class ClassObject extends PointersObject {
    private static final int METHODDICT_NAMES_INDEX = 2;
    private static final int METHODDICT_VALUES_INDEX = 1;
    private static final int NAME_INDEX = 6;
    private static final int FORMAT_INDEX = 2;
    private static final int METHODDICT_INDEX = 1;
    private static final int SUPERCLASS_INDEX = 0;
    private final CyclicAssumption methodLookupStable = new CyclicAssumption("unnamed");
    private final Set<ClassObject> subclasses = new HashSet<>();

    @Override
    public boolean isClass() {
        assert getImage().metaclass == getSqClass() || getImage().metaclass == getSqClass().getSqClass();
        return true;
    }

    @Override
    public String nameAsClass() {
        assert isClass();
        if (getSqClass() == getImage().metaclass) {
            // metaclasses store their singleton instance in the last field
            BaseSqueakObject classInstance = at0(getPointers().length - 1);
            if (classInstance instanceof ClassObject) {
                return "Metaclass (" + ((ClassObject) classInstance).getName() + ")";
            }
        } else {
            BaseSqueakObject nameObj = getName();
            if (nameObj instanceof NativeObject) {
                return nameObj.toString();
            }
        }
        return "UnknownClass";
    }

    @Override
    public void fillin(Chunk chunk, SqueakImageContext ctxt) {
        super.fillin(chunk, ctxt);
        // initialize the subclasses set
        setSuperclass(getSuperclass());
    }

    public void setSuperclass(BaseSqueakObject superclass) {
        BaseSqueakObject oldSuperclass = getSuperclass();
        if (oldSuperclass instanceof ClassObject) {
            ((ClassObject) oldSuperclass).detachSubclass(this);
        }
        atput0(SUPERCLASS_INDEX, superclass);
        if (superclass instanceof ClassObject) {
            ((ClassObject) superclass).attachSubclass(this);
        }
        for (ClassObject subclass : subclasses) {
            subclass.invalidateMethodLookup();
        }
    }

    private void invalidateMethodLookup() {
        methodLookupStable.invalidate();
    }

    private void attachSubclass(ClassObject classObject) {
        subclasses.add(classObject);
    }

    private void detachSubclass(ClassObject classObject) {
        subclasses.remove(classObject);
    }

    public BaseSqueakObject getSuperclass() {
        return at0(SUPERCLASS_INDEX);
    }

    public BaseSqueakObject getMethodDict() {
        return at0(METHODDICT_INDEX);
    }

    public BaseSqueakObject getFormat() {
        return at0(FORMAT_INDEX);
    }

    public BaseSqueakObject getName() {
        return at0(NAME_INDEX);
    }

    public Assumption getMethodLookupStable() {
        return methodLookupStable.getAssumption();
    }

    // TODO: cache the methoddict in a better structure than what Squeak provides
    // ... or use the Squeak hash to decide where to put stuff
    public BaseSqueakObject lookup(Predicate<BaseSqueakObject> test) {
        BaseSqueakObject lookupClass = this;
        while (lookupClass instanceof ClassObject) {
            BaseSqueakObject methodDict = ((ClassObject) lookupClass).getMethodDict();
            if (methodDict instanceof ListObject) {
                BaseSqueakObject values = methodDict.at0(METHODDICT_VALUES_INDEX);
                for (int i = METHODDICT_NAMES_INDEX; i < methodDict.size(); i++) {
                    BaseSqueakObject methodSelector = methodDict.at0(i);
                    if (test.test(methodSelector)) {
                        return values.at0(i - METHODDICT_NAMES_INDEX);
                    }
                }
            }
            lookupClass = ((ClassObject) lookupClass).getSuperclass();
        }
        return null;
    }

    public BaseSqueakObject lookup(BaseSqueakObject selector) {
        BaseSqueakObject result = lookup(methodSelector -> methodSelector == selector);
        if (result == null) {
            return doesNotUnderstand();
        }
        return result;
    }

    public BaseSqueakObject lookup(String selector) {
        return lookup(methodSelector -> methodSelector.toString().equals(selector));
    }

    public BaseSqueakObject doesNotUnderstand() {
        BaseSqueakObject result = lookup(getImage().doesNotUnderstand);
        if (result == null) {
            throw new RuntimeException("doesNotUnderstand missing!");
        }
        return result;
    }
}