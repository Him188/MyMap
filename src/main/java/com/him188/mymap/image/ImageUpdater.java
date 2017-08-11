package com.him188.mymap.image;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import com.him188.mymap.adapter.ImageAdapter;
import com.sun.imageio.plugins.gif.GIFImageReader;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author Him188 @ MyMap Project
 */
public abstract class ImageUpdater {
    public static final int SUB_IMAGE_WIDTH = 128; //map width

    public static ImageUpdater getImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File file) throws IOException {
        if (isGIF(new FileInputStream(file))) {
            DynamicImageUpdater updater = new DynamicImageUpdater(start, end, level, face, file);
            if (updater.getImageAdapter().getFrameCount() != 1) {
                return updater;
            }
        }

        return new StaticImageUpdater(start, end, level, face, file);
    }

    private static boolean isGIF(InputStream stream) {
        Iterator itr = ImageIO.getImageReaders(new MemoryCacheImageInputStream(stream));
        return itr.hasNext() && itr.next() instanceof GIFImageReader;
    }

    protected final int xBlockCount;
    protected final int yBlockCount;

    protected final Vector3 start;
    protected final Vector3 end;
    protected final Level level;
    protected final BlockFace face;
    protected final ImageAdapter imageAdapter;

    public ImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File file) throws IOException {
        this.start = start;
        this.end = end;
        this.level = level;
        this.face = face;

        if (this.start.x == this.end.x) {
            this.xBlockCount = (int) (Math.abs(this.start.z - this.end.z) + 1);
        } else {
            this.xBlockCount = (int) (Math.abs(this.start.x - this.end.x) + 1);
        }
        this.yBlockCount = (int) Math.abs(this.start.y - this.end.y) + 1;

        this.imageAdapter = initImageAdapter(file); // TODO: 2017/8/10 选择适应方式
    }

    public abstract ImageAdapter initImageAdapter(File file) throws IOException;

    public abstract void update();


    public int getXBlockCount() {
        return xBlockCount;
    }

    public int getYBlockCount() {
        return yBlockCount;
    }

    public BlockFace getFace() {
        return face;
    }

    public Level getLevel() {
        return level;
    }

    public ImageAdapter getImageAdapter() {
        return imageAdapter;
    }

    public Vector3 getStart() {
        return start;
    }

    public Vector3 getEnd() {
        return end;
    }

    protected Vector3 calculatePos(Vector2 vector2) {
        Vector3 pos;
        if (this.face == BlockFace.SOUTH || this.face == BlockFace.WEST) {
            if (this.start.x == this.end.x) {
                pos = this.end.add(0, -vector2.y, vector2.x);
            } else {
                pos = this.end.add(vector2.x, -vector2.y, 0);
            }
        } else {
            if (this.start.x == this.end.x) {
                pos = this.end.subtract(0, vector2.y, vector2.x);
            } else {
                pos = this.end.subtract(vector2.x, vector2.y, 0);
            }
        }
        return pos;
        //return pos.subtract(0, 1, 0);
    }
}
