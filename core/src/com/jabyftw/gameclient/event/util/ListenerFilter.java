package com.jabyftw.gameclient.event.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by Rafael on 06/02/2015.
 */
public interface ListenerFilter {

    public Class<? extends Annotation> getAnnotationClass();

    public Object[] getMethodArguments();

    public boolean filterAnnotation(Method method, Annotation annotation);

}
