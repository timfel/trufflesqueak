package de.hpi.swa.trufflesqueak.instrumentation;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import de.hpi.swa.trufflesqueak.SqueakLanguage;
import de.hpi.swa.trufflesqueak.model.CompiledCodeObject;
import de.hpi.swa.trufflesqueak.nodes.SqueakNodeWithMethod;

public class SqueakSource {
    private static final Map<String, Source> sourceMap = new HashMap<>();
    private static final Source noSource = Source.newBuilder("").mimeType(SqueakLanguage.MIME_TYPE).name("").build();

    private static Source getSource(String name, String src) {
        Source source = sourceMap.get(name);
        if (source == null) {
            source = Source.newBuilder(src).mimeType(SqueakLanguage.MIME_TYPE).name(name).build();
            sourceMap.put(name, source);
        }
        return source;
    }

    public static SourceSection build(CompiledCodeObject method, SqueakNodeWithMethod squeakNodeWithMethod) {
        try {
            String sourceStr = method.prettyPrint();
            Source source = getSource(method.toString(), sourceStr);
            String nodeStr = squeakNodeWithMethod.prettyPrint();
            int indexOf = Math.max(0, sourceStr.indexOf(nodeStr));
            return source.createSection(indexOf, nodeStr.length());
        } catch (NullPointerException e) {
            return noSource.createUnavailableSection();
        }
    }
}