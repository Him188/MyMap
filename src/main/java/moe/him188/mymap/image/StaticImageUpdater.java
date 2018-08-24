package moe.him188.mymap.image;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import moe.him188.mymap.adapter.ResizeImageAdapter;
import moe.him188.mymap.adapter.SingleImageAdapter;
import moe.him188.mymap.utils.Utils;

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
    public SingleImageAdapter initImageAdapter(File file) throws IOException {
        ResizeImageAdapter adapter = new ResizeImageAdapter(file);

        adapter.doAdaptation(this.getXBlockCount() * SUB_IMAGE_WIDTH, this.getYBlockCount() * SUB_IMAGE_WIDTH);
        return adapter;
    }

    @Override
    public SingleImageAdapter getImageAdapter() {
        return (SingleImageAdapter) super.getImageAdapter();
    }

    public void update() {
        initMapCache();
        final Player[] players = getLevel().getPlayers().values().toArray(new Player[0]);
        this.getImageAdapter().cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            long hash = this.updateMapCacheBlock(vector2);
            long mapId = this.getMapId(hash);
            long blockEntityId = this.getBlockEntityId(hash);
            this.updatePacketCache(blockEntityId, Utils.getClientboundMapItemDataPacket(image, SUB_IMAGE_WIDTH, SUB_IMAGE_WIDTH, mapId));
            this.requestMapUpdate(players, true);
        });
    }
}