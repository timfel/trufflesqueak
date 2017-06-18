package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.util.List;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.dsl.Specialization;

import de.hpi.swa.trufflesqueak.exceptions.PrimitiveFailed;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.model.NativeObject;

public final class FilePrimitives extends PrimitiveSet {
    @Override
    List<NodeFactory<? extends BuiltinPrimitive>> getNodeFactories() {
        return FilePrimitivesFactory.getFactories();
    }

    @Primitive(names = {"primitiveFileWrite"}, module = "FilePlugin", numberOfArguments = 5)
    @GenerateNodeFactory
    public static class FileWrite extends BuiltinPrimitive {
        public FileWrite(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        @TruffleBoundary
        int write(@SuppressWarnings("unused") Object receiver, int fd, NativeObject content, int start, int count) {
            // TODO: use registry of files
            String chars = content.toString();
            int elementSize = content.getElementSize();
            int byteStart = (start - 1) * elementSize;
            int byteEnd = Math.min(start - 1 + count, chars.length()) * elementSize;
            switch (fd) {
                case 1:
                    method.image.getOutput().append(chars, byteStart, byteEnd);
                    method.image.getOutput().flush();
                    break;
                case 2:
                    method.image.getError().append(chars, byteStart, byteEnd);
                    method.image.getError().flush();
                    break;
                default:
                    throw new PrimitiveFailed();
            }
            return (byteEnd - byteStart) / elementSize;
        }
    }

    @Primitive(names = {"primitiveFileSize"}, module = "FilePlugin", numberOfArguments = 1)
    @GenerateNodeFactory
    public static class FileSize extends BuiltinPrimitive {
        public FileSize(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        int size(@SuppressWarnings("unused") Object receiver, int fd) {
            // TODO: use registry of files
            if (fd <= 2) {
                return 0;
            }
            throw new PrimitiveFailed();
        }

        // TODO: double, long, BigInteger
    }

    @Primitive(names = {"primitiveFileStdioHandles"}, module = "FilePlugin")
    @GenerateNodeFactory
    public static class StdioHandles extends BuiltinPrimitive {
        public StdioHandles(CompiledCodeObject method2) {
            super(method2);
        }

        @Specialization
        public Object stdioHandles() {
            return method.image.wrap(0, 1, 2);
        }
    }
}
