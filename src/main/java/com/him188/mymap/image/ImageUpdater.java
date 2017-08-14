package com.him188.mymap.image;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.item.ItemMap;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import com.him188.mymap.MyMap;
import com.him188.mymap.MyMapFrame;
import com.him188.mymap.adapter.ImageAdapter;
import com.him188.mymap.utils.Utils;
import com.sun.imageio.plugins.gif.GIFImageReader;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Him188 @ MyMap Project
 */
public abstract class ImageUpdater {
    protected static final int SUB_IMAGE_WIDTH = 128; //map width
    protected static final double VIEW_DISTANCE = 20;

    public static ImageUpdater getImageUpdater(Vector3 start, Vector3 end, Level level, BlockFace face, File file) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.isDirectory()) {
            if (isDirectoryEmpty(file)) {
                throw new IOException("directory is empty");
            }

            return new DynamicImageUpdater(start, end, level, face, file);
        }

        if (isGIF(new FileInputStream(file))) {
            GIFImageUpdater updater = new GIFImageUpdater(start, end, level, face, file);
            if (updater.getImageAdapter().getFrameCount() != 1) {
                return updater;
            }
        }

        return new StaticImageUpdater(start, end, level, face, file);
    }

    private static boolean isDirectoryEmpty(File file) {
        File[] files = file.listFiles();
        return files == null || files.length == 0;
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

        this.imageAdapter = initImageAdapter(file);
    }

    public abstract ImageAdapter initImageAdapter(File file) throws IOException;

    public abstract void update();

    /**
     * @see BlockEntityItemFrame#getId()
     * @see ClientboundMapItemDataPacket
     */
    private final Map<Long, ClientboundMapItemDataPacket> cachedMaps = new ConcurrentHashMap<>();
    /**
     * @see Utils#hash(Vector2)
     * @see BlockEntityItemFrame#getId()
     */
    private Map<Long, Long> blockEntities = new ConcurrentHashMap<>();
    /**
     * @see Utils#hash(Vector2)
     * @see ItemMap#getMapId()
     */
    private Map<Long, Long> maps = new ConcurrentHashMap<>();

    protected final void updatePacketCache(long blockEntityId, ClientboundMapItemDataPacket packet) {
        this.cachedMaps.put(blockEntityId, packet);
    }

    protected final void initMapCache() {
        this.maps.clear();
        this.blockEntities.clear();
    }

    protected final long updateMapCacheBlock(Vector2 vector2) {
        ItemMap map = Utils.newMap();
        long blockEntityId = Utils.setItemMapToLevel(this.level, this.face, map, calculatePos(vector2));
        long positionHash = Utils.hash(vector2);
        this.maps.put(positionHash, map.getMapId());
        this.blockEntities.put(positionHash, blockEntityId);
        return positionHash;
    }

    protected final long getBlockEntityId(long hash) {
        return this.blockEntities.get(hash);
    }

    protected final long getMapId(long hash) {
        return this.maps.get(hash);
    }

    protected final boolean isValid(long hash) {
        return this.maps.containsKey(hash) && this.blockEntities.containsKey(hash);
    }

    public final boolean containsMapIdCache(long id) {
        return this.maps.containsValue(id);
    }

    public final boolean containsBlockEntityIdCache(long id) {
        return this.blockEntities.containsValue(id);
    }

    public final void requestBlockUpdate(Player player, Level level) {
        for (MyMapFrame frame : MyMap.getInstance().getList().toArray(new MyMapFrame[0])) {
            level.sendBlocks(new Player[]{player}, frame.getBlocks());
        }
    }

    public final void requestMapUpdate(Player player, long map_uuid) {
        for (ClientboundMapItemDataPacket packet : this.cachedMaps.values()) {
            if (packet.mapId == map_uuid) {
                player.dataPacket(packet);
            }
        }
    }

    public final void requestMapUpdate(Player player, Level level, boolean ignoreViewingDistance) {
        final BlockEntity[] list = level.getBlockEntities().values().toArray(new BlockEntity[0]);
        if (ignoreViewingDistance) {
            for (BlockEntity blockEntity : list) {
                if (this.cachedMaps.containsKey(blockEntity.getId())) {
                    player.dataPacket(this.cachedMaps.get(blockEntity.getId()));
                }
            }
        } else {
            for (BlockEntity blockEntity : list) {
                if (this.cachedMaps.containsKey(blockEntity.getId()) && blockEntity.distance(player) < VIEW_DISTANCE) {
                    player.dataPacket(this.cachedMaps.get(blockEntity.getId()));
                }
            }
        }
    }

    public final void requestMapUpdate(Player[] players, Level level, boolean ignoreViewingDistance) {
        for (Player player : players) {
            this.requestMapUpdate(player, level, ignoreViewingDistance);
        }
    }

    public final void requestMapUpdate(Player[] players, boolean ignoreViewingDistance) {
        for (Player player : players) {
            this.requestMapUpdate(player, player.getLevel(), ignoreViewingDistance);
        }
    }

    public final void requestMapUpdate(Player player, boolean ignoreViewingDistance) {
        this.requestMapUpdate(player, player.getLevel(), ignoreViewingDistance);
    }

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

    public void close() {
        this.blockEntities.clear();
        this.maps.clear();
        this.cachedMaps.clear();
    }
}
