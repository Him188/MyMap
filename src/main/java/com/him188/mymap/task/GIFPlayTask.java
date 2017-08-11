package com.him188.mymap.task;

import cn.nukkit.scheduler.AsyncTask;

/**
 * @author Him188 @ MyMap Project
 */
public abstract class GIFPlayTask extends AsyncTask {
    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled() {
        this.cancelled = true;
    }

    public abstract int nextDelay();

    public abstract void callback();

    @Override
    public final void onRun() {
        while (!this.cancelled) {
            this.callback();
            try {
                Thread.sleep(this.nextDelay());
            } catch (InterruptedException ignored) {
                return;
            }
        }
    }
}
