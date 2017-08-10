package com.him188.mymap;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;

/**
 * @author Him188 @ MyMap Project
 */
public class FrameList extends ArrayList<MyMapFrame> {
    public MyMapFrame getById(String id) {
        for (MyMapFrame frame : this) {
            if (frame.getId().equalsIgnoreCase(id)) {
                return frame;
            }
        }
        return null;
    }

    public MyMapFrame getByPosition(Vector3 vector3, Level level) {
        for (MyMapFrame frame : this) {
            if (frame.getLevel().getId() == level.getId() && frame.inRange(vector3)) {
                return frame;
            }
        }
        return null;
    }

    public MyMapFrame getByPosition(Position position) {
        return getByPosition(position, position.getLevel());
    }
}
