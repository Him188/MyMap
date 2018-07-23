package moe.him188.mymap.executable;

import cn.nukkit.InterruptibleThread;

/**
 * @author Him188 @ MyMap Project
 */
public abstract class DynamicPlayer extends Thread implements InterruptibleThread {
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
    public final void run() {
        while (!this.cancelled) {
            final long startingTime = System.currentTimeMillis();
            long delay = this.nextDelay();
            this.callback();
            final long targetTime = startingTime + delay;
            while (System.currentTimeMillis() < targetTime) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ignored) {
                    return;
                }
            }

            /*
            this.callback();
            try {
                Thread.sleep(this.nextDelay());
            } catch (InterruptedException ignored) {
                return;
            }
            */
        }
    }
}
