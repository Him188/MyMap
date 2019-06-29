package moe.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.MapInfoRequestPacket;
import cn.nukkit.scheduler.AsyncTask;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于画框更新等的普通事件监听器
 *
 * @author Him188 @ MyMap Project
 */
public final class CommonListener implements Listener {
    private final MyMap plugin;

    CommonListener(MyMap plugin) { // TODO: 2017/8/11 remove this
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLevelChange(EntityLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Server.getInstance().getScheduler().scheduleAsyncTask(this.plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    for (MyMapFrame frame : MyMap.getInstance().getList()) {
                        frame.getImageUpdater().requestBlockUpdate((Player) event.getEntity(), event.getTarget());
                        frame.getImageUpdater().requestMapUpdate((Player) event.getEntity(), event.getTarget(), true);
                    }
                }
            });
        }
    }

    private List<Long> playersNeedUpdate = new LinkedList<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (playersNeedUpdate.remove(event.getPlayer().getId())) {
            Server.getInstance().getScheduler().scheduleAsyncTask(this.plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    for (MyMapFrame frame : MyMap.getInstance().getList()) {
                        frame.getImageUpdater().requestBlockUpdate(event.getPlayer(), event.getPlayer().getLevel());
                        frame.getImageUpdater().requestMapUpdate(event.getPlayer(), true);
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        playersNeedUpdate.add(event.getPlayer().getId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPacketReceive(DataPacketReceiveEvent event) {
        if (event.getPacket() instanceof MapInfoRequestPacket) {
            for (MyMapFrame frame : MyMap.getInstance().getList()) {
                if (frame.getImageUpdater().containsMapIdCache(((MapInfoRequestPacket) event.getPacket()).mapId)) {
                    event.setCancelled();
                    frame.getImageUpdater().requestMapUpdate(event.getPlayer(), ((MapInfoRequestPacket) event.getPacket()).mapId);
                    return;
                }
            }
        }
    }
}
