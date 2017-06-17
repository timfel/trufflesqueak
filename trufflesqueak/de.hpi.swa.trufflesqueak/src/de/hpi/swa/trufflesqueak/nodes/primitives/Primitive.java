/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hpi.swa.trufflesqueak.nodes.primitives;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author tim
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Primitive {
    String module() default "";

    String[] names() default {};

    int[] indices() default {0};

    int numberOfArguments() default 0;

    boolean needsFrame() default false;
}
