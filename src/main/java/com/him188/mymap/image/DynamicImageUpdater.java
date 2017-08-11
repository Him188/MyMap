package com.him188.mymap.image;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockItemFrame;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.ItemMap;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import com.him188.mymap.MyMap;
import com.him188.mymap.adapter.ImageAdapter;
import com.him188.mymap.adapter.ResizeDynamicImageAdapter;
import com.him188.mymap.task.GIFPlayTask;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Him188 @ MyMap Project
 */
public class DynamicImageUpdater extends ImageUpdater {
    private final double VIEW_DISTANCE = 20;

    private GIFPlayTask task;
    private Map<Long, ItemMap> maps;

    public DynamicImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File imageFile) throws IOException {
        super(start, end, level, face, imageFile);
    }

    @Override
    public ResizeDynamicImageAdapter getImageAdapter() {
        return (ResizeDynamicImageAdapter) super.getImageAdapter();
    }

    @Override
    public ResizeDynamicImageAdapter initImageAdapter(File file) throws IOException {
        ResizeDynamicImageAdapter adapter = new ResizeDynamicImageAdapter(file); // TODO: 2017/8/10 选择适应方式

        adapter.doAdaptation(this.getXBlockCount() * SUB_IMAGE_WIDTH, this.getYBlockCount() * SUB_IMAGE_WIDTH);
        return adapter;
    }

    //private int x;
    public void update() {
        if (this.task != null) {
            task.setCancelled();
        }

        updateBlocks();

        task = new GIFPlayTask() {
            @Override
            public int nextDelay() {
                return getImageAdapter().getDelay();
            }

            @Override
            public void callback() {
                ImageAdapter adapter = getImageAdapter().getFrame();
                updateMaps(adapter);
                getImageAdapter().next();
            }
        };

        Server.getInstance().getScheduler().scheduleAsyncTask(MyMap.getInstance(), task);
    }

    private void updateBlocks() {
        maps = new HashMap<>();

        List<Vector2> result = this.getImageAdapter().crop(SUB_IMAGE_WIDTH);
        for (Vector2 vector2 : result) {
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
            maps.put(hash(vector2), map);
        }
    }

    private void updateMaps(ImageAdapter adapter) {
        adapter.cropAsSubImages(SUB_IMAGE_WIDTH).forEach((vector2, image) -> {
            if (!maps.containsKey(hash(vector2))) {
                MyMap.getInstance().getLogger().error("播放 GIF 时出现错误: 帧找不到有效地图");
                return;
            }
            ItemMap map = maps.get(hash(vector2));
            for (Player player : getLevel().getPlayers().values()) {
                if (player.loggedIn && new Vector2(player.getX(), player.getZ()).distance(vector2) >= VIEW_DISTANCE) {
                    sendImage(image, map.getMapId(), player);
                }
            }
        });
    }

    private static void sendImage(BufferedImage image, long mapId, Player player) {
        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = mapId;
        pk.update = 2;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;
        player.dataPacket(pk);
    }

    private static long hash(Vector2 vector2) {
        return (vector2.getFloorX() << 16) ^ vector2.getFloorY();
    }
}