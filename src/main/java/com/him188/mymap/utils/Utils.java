package com.him188.mymap.utils;

import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ConfigSection;

import java.io.File;

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
    };

    public static File defineFile(String path) {
        for (String extension : IMAGE_EXTENSIONS) {
            File file = new File(path + extension);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}
