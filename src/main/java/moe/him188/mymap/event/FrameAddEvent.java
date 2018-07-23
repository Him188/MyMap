package moe.him188.mymap.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import moe.him188.mymap.MyMapFrame;

/**
 * @author Him188 @ MyMap Project
 */
public class FrameAddEvent extends FrameEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    public FrameAddEvent(MyMapFrame frame) {
        super(frame);
    }
}
