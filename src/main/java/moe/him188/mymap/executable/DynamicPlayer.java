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
            long delay = this.nextDelay();

            long targetTime = System.currentTimeMillis() + delay;
            this.callback();
            if (targetTime > System.currentTimeMillis()) {
                try {
                    Thread.sleep(targetTime - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    setCancelled();
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
