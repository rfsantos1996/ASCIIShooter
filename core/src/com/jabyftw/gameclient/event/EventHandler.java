package com.jabyftw.gameclient.event;

import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.event.util.AbstractListenerHandler;
import com.jabyftw.gameclient.event.util.CancellableEvent;
import com.jabyftw.gameclient.event.util.Event;
import com.jabyftw.gameclient.event.util.ListenerFilter;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Stack;

/**
 * Created by Rafael on 06/02/2015.
 */
public class EventHandler extends AbstractListenerHandler implements Tickable {

    private final Stack<Event> eventQueue = new Stack<Event>();

    @Override
    public void update(float deltaTime) {
        while(!eventQueue.empty()) {
            Event event = eventQueue.remove(0);
            handleEvents(event);
        }
    }

    public void callEvent(Event event) {
        if(event == null)
            throw new NullPointerException("Event can't be null.");

        if(Constants.isDebugging)
            System.out.println("Event called: " + event.getEventType().name() + " id: " + event.getId());

        if(event.getEventType().runOnMainThread() && Thread.currentThread() != Main.getMainLoopThread()) {
            eventQueue.push(event);
        } else {
            handleEvents(event);
        }
    }

    private void handleEvents(final Event event) {
        Util.handleAnnotationEventSystem(new ListenerFilter() {
            @Override
            public Class<? extends Annotation> getAnnotationClass() {
                return Listener.EventListener.class;
            }

            @Override
            public Object[] getMethodArguments() {
                return new Object[]{event};
            }

            @Override
            public boolean filterAnnotation(Method method, Annotation annotation) {
                Listener.EventListener eventListener = (Listener.EventListener) annotation;

                // ignore cancelled OR event isn't cancellable OR event is not cancelled
                if(!eventListener.ignoreCancelled() || !(event instanceof CancellableEvent) || !((CancellableEvent) event).isCancelled())
                    for(Parameter parameter : method.getParameters()) {
                        if(parameter.getType().isAssignableFrom(event.getClass()))
                            return true;
                    }
                return false;
            }
        }, listenerArray);
    }
}
