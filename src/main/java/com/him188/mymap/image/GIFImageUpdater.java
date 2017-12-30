package com.him188.mymap.image;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import com.him188.mymap.MyMap;
import com.him188.mymap.adapter.ResizeGIFImageAdapter;
import com.him188.mymap.adapter.SingleImageAdapter;
import com.him188.mymap.executable.DynamicPlayer;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Him188 @ MyMap Project
 */
public class GIFImageUpdater extends ImageUpdater {

    private DynamicPlayer task;

    public GIFImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File imageFile) throws IOException {
        super(start, end, level, face, imageFile);
    }

    @Override
    public ResizeGIFImageAdapter getImageAdapter() {
        return (ResizeGIFImageAdapter) super.getImageAdapter();
    }

    @Override
    public ResizeGIFImageAdapter initImageAdapter(File file) throws IOException {
        ResizeGIFImageAdapter adapter = new ResizeGIFImageAdapter(file);

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
                return getImageAdapter().getDelay();
            }

            @Override
            public void callback() {
                SingleImageAdapter adapter = getImageAdapter().getFrame();
                updateMaps(adapter);
                getImageAdapter().next();
            }
        };

        task.start();
    }

    private void updateBlocks() {
        initMapCache();
        List<Vector2> result = this.getImageAdapter().crop(SUB_IMAGE_WIDTH);
        for (Vector2 vector2 : result) {
            this.updateMapCacheBlock(vector2);
        }
    }

    private void updateMaps(SingleImageAdapter adapter) {
        final Player[] players = getLevel().getPlayers().values().toArray(new Player[0]);
        adapter.cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            long hash = Utils.hash(vector2);
            if (!this.isValid(hash)) {
                MyMap.getInstance().getLogger().error("播放 GIF 时出现错误: 帧找不到有效地图");
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