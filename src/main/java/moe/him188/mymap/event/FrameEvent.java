package moe.him188.mymap.event;

import cn.nukkit.event.Event;
import moe.him188.mymap.MyMapFrame;

/**
 * @author Him188 @ MyMap Project
 */
public abstract class FrameEvent extends Event {
    private final MyMapFrame frame;

    public FrameEvent(MyMapFrame frame) {
        this.frame = frame;
    }

    public MyMapFrame getFrame() {
        return frame;
    }
}
