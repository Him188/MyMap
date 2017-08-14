package com.him188.mymap.executable;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Collection;

/**
 * GIF 帧更新 / 自动切换图更新
 *
 * @author Him188 @ MyMap Project
 */
public class AsyncFrameUpdateTask extends AsyncTask {
    private final ClientboundMapItemDataPacket packet;
    private final Collection<Player> players;

    public AsyncFrameUpdateTask(Collection<Player> players, ClientboundMapItemDataPacket packet) {
        this.players = players;
        this.packet = packet;
    }

    @Override
    public void onRun() {
        Server.broadcastPacket(players, packet);
    }
}
