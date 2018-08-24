package moe.him188.mymap.image;

import cn.nukkit.Player;
import cn.nukkit.item.ItemMap;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import moe.him188.mymap.MyMap;
import moe.him188.mymap.adapter.ResizeDynamicImageAdapter;
import moe.him188.mymap.adapter.SingleImageAdapter;
import moe.him188.mymap.executable.DynamicPlayer;
import moe.him188.mymap.utils.Utils;

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
            this.updatePacketCache(this.getBlockEntityId(hash), Utils.getClientboundMapItemDataPacket(image, SUB_IMAGE_WIDTH, SUB_IMAGE_WIDTH, this.getMapId(hash)));
            this.requestMapUpdate(players, false);
        });
    }
    // TODO: 2017/8/14 更改:  ImageUpdater中 (abstract?) updateBlocks, (abstract?) updateMaps. 或者写 MultiImageUpdater 让 dynamic 和 gif继承 ?

    /**
     * 更新 {@link ItemMap}, 并更新一帧到缓存
     */
    private void updateMaps(SingleImageAdapter adapter) {
        final Player[] players = getLevel().getPlayers().values().toArray(new Player[0]);
        adapter.cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            long hash = Utils.hash(vector2);
            if (this.isInvalid(hash)) {
                MyMap.getInstance().getLogger().error("更换图片时出现错误: 帧找不到有效地图");
                return;
            }
            long mapId = this.getMapId(hash);
            long blockEntityId = this.getBlockEntityId(hash);
            this.updatePacketCache(blockEntityId, Utils.getClientboundMapItemDataPacket(image, SUB_IMAGE_WIDTH, SUB_IMAGE_WIDTH, mapId));
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