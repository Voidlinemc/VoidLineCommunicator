package org.withor.mixins;

import java.lang.reflect.Method;

public class InjectionData {
    public final Class<?> mixinClass;
    public final Method method;
    public final String targetMethod;
    public final String at;
    public final boolean cancellable;

    InjectionData(Class<?> mixinClass, Method method, Inject inj) {
        this.mixinClass = mixinClass;
        this.method = method;
        this.targetMethod = inj.method();
        this.at = inj.at().value();
        this.cancellable = inj.cancellable();
    }
}
