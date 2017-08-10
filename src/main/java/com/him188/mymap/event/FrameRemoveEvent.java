package com.him188.mymap.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import com.him188.mymap.MyMapFrame;

/**
 * @author Him188 @ MyMap Project
 */
public class FrameRemoveEvent extends FrameEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    public FrameRemoveEvent(MyMapFrame frame) {
        super(frame);
    }
}
