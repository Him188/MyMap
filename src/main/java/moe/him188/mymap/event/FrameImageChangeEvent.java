package moe.him188.mymap.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import moe.him188.mymap.MyMapFrame;

import java.io.File;

/**
 * @author Him188 @ MyMap Project
 */
public class FrameImageChangeEvent extends FrameEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    private final File imageFile;

    public FrameImageChangeEvent(MyMapFrame frame, File imageFile) {
        super(frame);
        this.imageFile = imageFile;
    }

    public File getImageFile() {
        return imageFile;
    }
}
