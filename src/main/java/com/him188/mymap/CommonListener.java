package com.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;

/**
 * 用于画框更新等的普通事件监听器
 *
 * @author Him188 @ MyMap Project
 */
public class CommonListener implements Listener {
    private final MyMap plugin;

    CommonListener(MyMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLevelChange(EntityLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {

        }
    }
}
