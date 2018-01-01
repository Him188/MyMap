package com.him188.mymap;

import cn.nukkit.Server;
import cn.nukkit.block.BlockAir;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.him188.mymap.event.FrameImageChangeEvent;
import com.him188.mymap.executable.AsyncImageUpdateTask;
import com.him188.mymap.image.ImageUpdater;
import com.him188.mymap.utils.LanguageBase;
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

    private ImageUpdater imageUpdater;
    private final Config config;
    private final File configFile;

    private final Vector3[] blocks;

    public MyMapFrame(String id, Vector3 startPos, Vector3 endPos, Level level, BlockFace face, File imageFile) throws IOException {
        this.id = id;
        this.startPos = startPos;
        this.endPos = endPos;
        this.face = face;
        Utils.analyzeVector3(this.startPos, this.endPos, this.getFace());
        this.imageFile = imageFile == null ? DEFAULT_IMAGE_FILE : Utils.detectImageFile(imageFile.getPath());

        this.level = level;
        initImageUpdater();
        this.configFile = new File(MyMapFrame.FRAME_DATA_FOLDER, this.getId() + ".yml");
        this.config = new Config(configFile, Config.YAML);
        this.updateImage(false);

        this.blocks = new Vector3[this.getImageUpdater().getXBlockCount() * this.getImageUpdater().getYBlockCount()];

        Vector3 v = this.startPos.clone();

        Vector3 min = new Vector3(Math.min(this.startPos.x, this.endPos.x), Math.min(this.startPos.y, this.endPos.y), Math.min(this.startPos.z, this.endPos.z));
        Vector3 max = new Vector3(Math.max(v.x, this.endPos.x), Math.max(v.y, this.endPos.y), Math.max(v.z, this.endPos.z));
        Vector3 subtract = max.subtract(min).floor();
        int i = 0;
        for (int x = 0; x <= subtract.getX(); x++) {
            for (int y = 0; y <= subtract.getY(); y++) {
                for (int z = 0; z <= subtract.getZ(); z++) {
                    Vector3 pos = min.add(x, y, z);
                    this.blocks[i++] = pos;
                }
            }
        }
    }

    public Vector3[] getBlocks() {
        return blocks;
    }

    private void initImageUpdater() throws IOException {
        this.imageFile = this.imageFile == null ? DEFAULT_IMAGE_FILE : this.imageFile;
        if (this.imageUpdater != null) {
            this.imageUpdater.close();
        }
        this.imageUpdater = ImageUpdater.getImageUpdater(this.startPos, this.endPos, this.level, this.face, this.imageFile);
    }

    public void save() {
        this.config.set("id", id);
        this.config.set("startPos", Utils.parseConfigSection(startPos));
        this.config.set("endPos", Utils.parseConfigSection(endPos));
        this.config.set("level", level.getFolderName());
        this.config.set("face", face.getIndex());
        this.config.set("imageFile", imageFile.getPath());
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

    public boolean setImageFile(File imageFile) throws IOException {
        FrameImageChangeEvent event = new FrameImageChangeEvent(this, imageFile);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.imageFile = event.getImageFile();
        this.initImageUpdater();
        this.updateImage(false);
        return true;
    }

    public boolean inRange(Vector3 vector3) {
        return ((endPos.x >= vector3.x && vector3.x >= startPos.x) || (startPos.x >= vector3.x && vector3.x >= endPos.x))
               && ((endPos.y >= vector3.y && vector3.y >= startPos.y) || (startPos.y >= vector3.y && vector3.y >= endPos.y))
               && ((endPos.z >= vector3.z && vector3.z >= startPos.z) || (startPos.z >= vector3.z && vector3.z >= endPos.z));
    }


    private boolean updating;

    @SuppressWarnings("UnusedReturnValue")
    public boolean updateImage(boolean async) {
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
        if (this.configFile.exists() && this.configFile.isFile()) {
            if (!this.configFile.delete()) {
                this.config.setAll(new ConfigSection());
                this.config.save();
                MyMap.getInstance().getLogger().error(LanguageBase.getMessage(LanguageBase.ID.CAN_NOT_DELETE_CONFIG, this.getId()));
            }
        }

        Vector3 v = this.startPos.clone();

        Vector3 min = new Vector3(Math.min(this.startPos.x, this.endPos.x), Math.min(this.startPos.y, this.endPos.y), Math.min(this.startPos.z, this.endPos.z));
        Vector3 max = new Vector3(Math.max(v.x, this.endPos.x), Math.max(v.y, this.endPos.y), Math.max(v.z, this.endPos.z));
        Vector3 subtract = max.subtract(min).floor();
        for (int x = 0; x <= subtract.getX(); x++) {
            for (int y = 0; y <= subtract.getY(); y++) {
                for (int z = 0; z <= subtract.getZ(); z++) {
                    Vector3 pos = min.add(x, y, z);
                    BlockEntity blockEntity = this.level.getBlockEntity(pos);
                    if (blockEntity != null && blockEntity instanceof BlockEntityItemFrame) {
                        ((BlockEntityItemFrame) blockEntity).setItem(new ItemBlock(new BlockAir()));
                    }
                    this.level.setBlock(pos, new BlockAir(), true, false);
                }
            }
        }
    }
}
