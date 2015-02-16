package com.jabyftw.gameclient.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Rafael on 05/02/2015.
 */
public interface Listener {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface PacketListener {

        DeliverType deliverType() default DeliverType.RECEIVED;

        public enum DeliverType {
            RECEIVED,
            SENT
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface EventListener {

        boolean ignoreCancelled() default true;

    }
}
