package com.him188.mymap.utils;

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
import cn.nukkit.utils.ConfigSection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * @author Him188 @ MyMap Project
 */
public final class Utils {
    private Utils() {

    }

    public static Vector3 parseVector3(ConfigSection section) {
        return new Vector3(section.getDouble("x"), section.getDouble("y"), section.getDouble("z"));
    }

    public static ConfigSection parseConfigSection(Vector3 vector3) {
        return new ConfigSection() {
            {
                put("x", vector3.getX());
                put("y", vector3.getY());
                put("z", vector3.getZ());
            }
        };
    }

    public static void analyzeVector3(Vector3 start, Vector3 end, BlockFace face) {
        Vector3 p = start.clone();
        if (face == BlockFace.SOUTH || face == BlockFace.WEST) {
            start.setComponents(
                    Math.max(start.x, end.x),
                    Math.min(start.y, end.y),
                    Math.max(start.z, end.z)
            );
            end.setComponents(
                    Math.min(p.x, end.x),
                    Math.max(p.y, end.y),
                    Math.min(p.z, end.z)
            );
        } else {
            start.setComponents(
                    Math.min(start.x, end.x),
                    Math.min(start.y, end.y),
                    Math.min(start.z, end.z)
            );
            end.setComponents(
                    Math.max(p.x, end.x),
                    Math.max(p.y, end.y),
                    Math.max(p.z, end.z)
            );
        }
    }

    private static final String[] IMAGE_EXTENSIONS = new String[]{
            "",
            ".gif",
            ".png",
            ".webp",
            ".jpg",
            ".jpeg",
            ".bmp",
            ".wbmp",
    };

    public static File detectImageFile(String path) {
        for (String extension : IMAGE_EXTENSIONS) {
            File file = new File(path + extension);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    public static int getMetaByFace(BlockFace face) {
        switch (face) {
            case NORTH:
                return 3;
            case SOUTH:
                return 2;
            case WEST:
                return 1;
            case EAST:
                return 0;
            default:
                return -1;
        }
    }

    public static ItemMap newMap() {
        CompoundTag tag = new CompoundTag();
        tag.putString("map_uuid", "" + random());
        return (ItemMap) new ItemMap().setNamedTag(tag);
    }

    public static long random() {
        return Math.abs((long) new Random().nextInt() << 32 ^ (System.currentTimeMillis() << 32));
    }

    public static long setItemMapToLevel(Level level, BlockFace face, ItemMap map, Vector3 pos) {
        BlockItemFrame frame = new BlockItemFrame(Utils.getMetaByFace(face));
        level.setBlock(pos, frame, true, false);

        FullChunk chunk = level.getChunk(pos.getFloorX() >> 4, pos.getFloorZ() >> 4, false);
        if (chunk == null) {
            return -1;
        }
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntityItemFrame.ITEM_FRAME)
                .putInt("x", (int) pos.x)
                .putInt("y", (int) pos.y)
                .putInt("z", (int) pos.z);
        BlockEntityItemFrame frameEntity = new BlockEntityItemFrame(chunk, nbt);
        frameEntity.setItem(map);
        return frameEntity.getId();
    }

    public static ClientboundMapItemDataPacket getClientboundMapItemDataPacket(BufferedImage image, long mapId) {
        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = mapId;
        pk.update = 2;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;
        //long time = System.currentTimeMillis();
        pk.encode();
        //System.out.println(System.currentTimeMillis() - time);
        pk.isEncoded = true;
        return pk;
    }

    public static long hash(Vector2 vector2) {
        return (vector2.getFloorX() << 16) ^ vector2.getFloorY();
    }
}
