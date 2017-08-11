package com.him188.mymap.image;

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
public class StaticImageUpdater extends ImageUpdater {
    private static final int SUB_IMAGE_WIDTH = 128; //map width

    public StaticImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File imageFile) throws IOException {
        super(start, end, level, face, imageFile);
    }

    @Override
    public ImageAdapter initImageAdapter(File file) throws IOException {
        ResizeImageAdapter adapter = new ResizeImageAdapter(file); // TODO: 2017/8/10 选择适应方式

        adapter.doAdaptation(this.getXBlockCount() * SUB_IMAGE_WIDTH, this.getYBlockCount() * SUB_IMAGE_WIDTH);
        return adapter;
    }

    //private int x;

    public void update() {
        this.imageAdapter.cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            Vector3 pos;
            pos = calculatePos(vector2);

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