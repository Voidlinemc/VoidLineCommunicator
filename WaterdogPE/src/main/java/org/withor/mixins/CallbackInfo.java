package org.withor.mixins;

import lombok.Getter;

public class CallbackInfo<R> {
    @Getter private boolean cancelled = false;
    @Getter private R returnValue = null;

    public void cancel(R value) {
        cancel();
        this.returnValue = value;
    }

    public void cancel() {
        this.cancelled = true;
    }
}