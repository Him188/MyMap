package com.him188.mymap;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.him188.mymap.event.FrameImageChangeEvent;
import com.him188.mymap.task.AsyncImageUpdateTask;
import com.him188.mymap.utils.ImageUpdater;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * @author Him188 @ MyMap Project
 */
@SuppressWarnings("WeakerAccess")
public class MyMapFrame {
    public static File DEFAULT_IMAGE_FILE;
    public static File IMAGE_DATA_FOLDER;
    public static File FRAME_DATA_FOLDER;

    public static MyMapFrame fromConfigSection(ConfigSection section) throws IOException {
        return new MyMapFrame(
                section.getString("id"),
                Utils.parseVector3(section.getSection("startPos")),
                Utils.parseVector3(section.getSection("endPos")),
                Server.getInstance().getLevelByName(section.getString("level")),
                BlockFace.fromIndex(section.getInt("face")),
                new File(section.getString("imageFile"))
        );
    }

    private final String id;
    private final Vector3 startPos;
    private final Vector3 endPos;
    private File imageFile;
    private final Level level;
    private final BlockFace face;

    private final ImageUpdater imageUpdater;
    private final Config config;

    public MyMapFrame(String id, Vector3 startPos, Vector3 endPos, Level level, BlockFace face, File imageFile) throws IOException {
        this.id = id;
        this.startPos = startPos;
        this.endPos = endPos;
        this.face = face;
        Utils.analyzeVector3(this.startPos, this.endPos, this.getFace());
        this.imageFile = imageFile == null ? DEFAULT_IMAGE_FILE : imageFile;
        this.level = level;
        this.imageUpdater = new ImageUpdater(this.startPos, this.endPos, this.level, this.face, this.imageFile);
        this.config = new Config(new File(MyMapFrame.FRAME_DATA_FOLDER, this.getId() + ".yml"), Config.YAML);
        this.updateImage(true);
    }

    public void save() {
        this.config.set("startPos", Utils.parseConfigSection(startPos));
        this.config.set("endPos", Utils.parseConfigSection(endPos));
        this.config.set("level", level.getFolderName());
        this.config.set("face", face.getIndex());
        this.config.set("imageFile", imageFile.getAbsolutePath());
        this.config.save();
    }

    public String getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public BlockFace getFace() {
        return face;
    }

    public Vector3 getEndPos() {
        return endPos;
    }

    public Vector3 getStartPos() {
        return startPos;
    }

    public File getImageFile() {
        return imageFile;
    }

    public ImageUpdater getImageUpdater() {
        return imageUpdater;
    }

    public boolean setImageFile(File imageFile) {
        FrameImageChangeEvent event = new FrameImageChangeEvent(this, imageFile);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.imageFile = event.getImageFile();
        this.updateImage(true);
        return true;
    }

    public boolean inRange(Vector3 vector3) {
        return endPos.x >= vector3.x && vector3.x >= startPos.x
               && endPos.y >= vector3.y && vector3.y >= startPos.y
               && endPos.z >= vector3.z && vector3.z >= startPos.z;
    }


    private boolean updating;

    @SuppressWarnings("UnusedReturnValue")
    public final boolean updateImage(boolean async) {
        if (this.updating) {
            return false;
        }
        this.updating = true;
        if (async) {
            Server.getInstance().getScheduler().scheduleAsyncTask(MyMap.getInstance(), new AsyncImageUpdateTask() {
                @Override
                public void onRun() {
                    updateImage0();
                }
            });
        } else {
            updateImage0();
        }
        return true;
    }

    private void updateImage0() {
        getImageUpdater().update();
        this.updating = false;
    }

    /**
     * 用空气替换区域
     */
    public void clear() {
        // TODO: 2017/8/10
    }
}
