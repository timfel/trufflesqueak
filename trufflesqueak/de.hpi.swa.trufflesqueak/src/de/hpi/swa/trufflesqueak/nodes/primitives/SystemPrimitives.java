package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.time.Instant;
import java.util.List;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;

import de.hpi.swa.trufflesqueak.exceptions.SqueakExit;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;

public final class SystemPrimitives extends PrimitiveSet {
    @Override
    List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return SystemPrimitivesFactory.getFactories();
    }

    @Primitive(indices = {113})
    @GenerateNodeFactory
    public static class Quit extends BuiltinPrimitive {
        public Quit(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        public Object quit() {
            throw new SqueakExit(0);
        }
    }

    @Primitive(indices = {149}, numberOfArguments = 2)
    @GenerateNodeFactory
    public static class SystemAttribute extends BuiltinPrimitive {
        public SystemAttribute(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        @TruffleBoundary
        public Object getSystemAttribute(@SuppressWarnings("unused") Object image, int idx) {
            if (idx >= 2 && idx <= 1000) {
                String[] restArgs = method.image.config.getRestArgs();
                if (restArgs.length > idx - 2) {
                    return method.image.wrap(restArgs[idx - 2]);
                } else {
                    return null;
                }
            }
            switch (idx) {
                case 1001:
                    return method.image.wrap("java");
                case 1002:
                    return method.image.wrap(System.getProperty("java.version"));
            }
            return null;
        }
    }

    @Primitive(indices = {240})
    @GenerateNodeFactory
    public static class UTCMicrosecondClock extends BuiltinPrimitive {
        // The Delta between Squeak Epoch (Jan 1st 1901) and POSIX Epoch (Jan 1st 1970)
        private final long SQUEAK_EPOCH_DELTA_MICROSECONDS = 2177452800000000L;
        private final long SEC2USEC = 1000 * 1000;
        private final long USEC2NANO = 1000;

        public UTCMicrosecondClock(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        protected long time() {
            Instant now = Instant.now();
            long epochSecond = now.getEpochSecond();
            int nano = now.getNano();
            return epochSecond * SEC2USEC + nano / USEC2NANO + SQUEAK_EPOCH_DELTA_MICROSECONDS;
        }
    }
}
