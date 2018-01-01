package com.him188.mymap;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;

/**
 * 用于保护画框不脱落, 展示框方块不被修改的事件监听器
 *
 * @author Him188 @ MyMap Project
 */
public final class FrameProtectionListener implements Listener {
    private final MyMap plugin;

    FrameProtectionListener(MyMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        for (MyMapFrame frame : plugin.getList()) {
            if (frame.inRange(event.getBlock())) {
                event.setCancelled();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(ItemFrameDropItemEvent event) {
        for (MyMapFrame frame : plugin.getList()) {
            if (frame.inRange(event.getBlock())) {
                if (event.getPlayer().isOp()) {
                    event.getPlayer().sendTip("ID: " + frame.getId());
                }
                event.setCancelled();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        for (MyMapFrame frame : plugin.getList()) {
            if (frame.inRange(event.getBlock())) {
                event.setCancelled();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUpdate(BlockUpdateEvent event) {
        for (MyMapFrame frame : plugin.getList()) {
            if (frame.inRange(event.getBlock())) {
                event.setCancelled();
            }
        }
    }
}
