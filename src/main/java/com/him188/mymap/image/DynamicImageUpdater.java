package com.him188.mymap.image;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import com.him188.mymap.MyMap;
import com.him188.mymap.adapter.ResizeDynamicImageAdapter;
import com.him188.mymap.adapter.SingleImageAdapter;
import com.him188.mymap.executable.DynamicPlayer;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * @author Him188 @ MyMap Project
 */
public class DynamicImageUpdater extends ImageUpdater {

    private DynamicPlayer task;

    public DynamicImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File imageDirectory) throws IOException {
        super(start, end, level, face, imageDirectory);
    }

    @Override
    public ResizeDynamicImageAdapter getImageAdapter() {
        return (ResizeDynamicImageAdapter) super.getImageAdapter();
    }

    @Override
    public ResizeDynamicImageAdapter initImageAdapter(File file) throws IOException {
        ResizeDynamicImageAdapter adapter = new ResizeDynamicImageAdapter(file);

        adapter.doAdaptation(this.getXBlockCount() * SUB_IMAGE_WIDTH, this.getYBlockCount() * SUB_IMAGE_WIDTH);
        return adapter;
    }

    //private int x;
    public void update() {
        if (this.task != null) {
            task.setCancelled();
        }

        updateBlocks();

        task = new DynamicPlayer() {
            @Override
            public int nextDelay() {
                return getImageAdapter().getNextDelay();
            }

            @Override
            public void callback() {
                SingleImageAdapter adapter = getImageAdapter().getNextFrame();
                updateMaps(adapter);
                getImageAdapter().next();
            }
        };

        task.start();
    }

    /**
     * 将展示框放置到地图, 并更新第一帧到缓存
     */
    private void updateBlocks() {
        initMapCache();
        final Player[] players = getLevel().getPlayers().values().toArray(new Player[0]);
        this.getImageAdapter().getCachedFrame(0).cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            long hash = this.updateMapCacheBlock(vector2);
            this.updatePacketCache(this.getBlockEntityId(hash), Utils.getClientboundMapItemDataPacket(image, this.getMapId(hash)));
            this.requestMapUpdate(players, false);
        });
    }
    // TODO: 2017/8/14 更改:  ImageUpdater中 (abstract?) updateBlocks, (abstract?) updateMaps. 或者写 MultiImageUpdater 让 dynamic 和 gif继承 ?

    /**
     * 将展示框放置到地图, 并更新第一帧到缓存
     */
    private void updateMaps(SingleImageAdapter adapter) {
        final Player[] players = getLevel().getPlayers().values().toArray(new Player[0]);
        adapter.cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            long hash = Utils.hash(vector2);
            if (!this.isValid(hash)) {
                MyMap.getInstance().getLogger().error("更换图片时出现错误: 帧找不到有效地图");
                return;
            }
            long mapId = this.getMapId(hash);
            long blockEntityId = this.getBlockEntityId(hash);
            this.updatePacketCache(blockEntityId, Utils.getClientboundMapItemDataPacket(image, mapId));
            this.requestMapUpdate(players, false);
        });
    }

    @Override
    public void close() {
        if (this.task != null) {
            this.task.setCancelled();
            this.task.interrupt();
        }
        super.close();
    }
}