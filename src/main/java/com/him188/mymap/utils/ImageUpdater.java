package com.him188.mymap.utils;

import cn.nukkit.block.BlockItemFrame;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.ItemMap;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import com.him188.mymap.adapter.ImageAdapter;
import com.him188.mymap.adapter.ResizeImageAdapter;

import java.io.File;
import java.io.IOException;

/**
 * @author Him188 @ MyMap Project
 */
public class ImageUpdater {
    private static final int SUB_IMAGE_WIDTH = 128; //map width

    private final int xBlockCount;
    private final int yBlockCount;

    private final Vector3 start;
    private final Vector3 end;
    private final Level level;
    private final BlockFace face;
    private final ImageAdapter imageAdapter;

    public ImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File imageFile) throws IOException {
        this.start = start;
        this.end = end;
        this.level = level;
        this.face = face;

        if (this.start.x == this.end.x) {
            this.xBlockCount = (int) (Math.abs(this.start.z - this.end.z) + 1);
        } else {
            this.xBlockCount = (int) (Math.abs(this.start.x - this.end.x) + 1);
        }
        this.yBlockCount = (int) Math.abs(this.start.y - this.end.y);

        this.imageAdapter = new ResizeImageAdapter(imageFile); // TODO: 2017/8/10 选择适应方式
        this.imageAdapter.doAdaptation(this.xBlockCount * 128, this.yBlockCount * 128);
    }

    public int getXBlockCount() {
        return xBlockCount;
    }

    public int getYBlockCount() {
        return yBlockCount;
    }

    public void update() {
        this.imageAdapter.cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
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

            BlockItemFrame frame = new BlockItemFrame();
            level.setBlock(pos, frame, true, false);

            FullChunk chunk = level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, true);
            if (chunk == null) {
                return;
            }
            CompoundTag nbt = new CompoundTag()
                    .putString("id", BlockEntityItemFrame.ITEM_FRAME)
                    .putInt("x", (int) pos.x)
                    .putInt("y", (int) pos.y)
                    .putInt("z", (int) pos.z);
            BlockEntityItemFrame frameEntity = new BlockEntityItemFrame(chunk, nbt);
            ItemMap map = new ItemMap();
            frameEntity.setItem(map);
            map.setImage(image);
        });
    }
}