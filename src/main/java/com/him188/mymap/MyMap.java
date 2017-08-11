package com.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import com.him188.mymap.event.FrameAddEvent;
import com.him188.mymap.event.FrameRemoveEvent;
import com.him188.mymap.utils.FrameList;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Him188 @ MyMap Project
 */
public final class MyMap extends PluginBase {
    private static MyMap instance;

    public MyMap() {
        instance = this;
    }

    public static MyMap getInstance() {
        return instance;
    }

    private CommonListener commonListener;
    private SettingListener settingListener;
    private FrameProtectionListener protectionListener;
    private FrameList list;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        super.onEnable();

        MyMapFrame.IMAGE_DATA_FOLDER = new File(getDataFolder(), "images");
        MyMapFrame.DEFAULT_IMAGE_FILE = Utils.defineFile(new File(MyMapFrame.IMAGE_DATA_FOLDER, "default").getPath());
        MyMapFrame.IMAGE_DATA_FOLDER.mkdir();
        MyMapFrame.FRAME_DATA_FOLDER = new File(getDataFolder(), "frames");
        MyMapFrame.FRAME_DATA_FOLDER.mkdir();
        saveResource("default.jpg", "images/default.jpg", false);

        list = new FrameList();

        File[] files = MyMapFrame.FRAME_DATA_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    list.add(MyMapFrame.fromConfigSection(new Config(file, Config.YAML).getRootSection()));
                } catch (IOException e) {
                    getLogger().error("加载画框 " + file.getName() + " 时遇到错误", e);
                }
            }
        }

        if (settingListener == null) {
            settingListener = new SettingListener(this);
            getServer().getPluginManager().registerEvents(settingListener, this);
        }

        if (commonListener == null) {
            commonListener = new CommonListener(this);
            getServer().getPluginManager().registerEvents(commonListener, this);
        }

        if (protectionListener == null) {
            protectionListener = new FrameProtectionListener(this);
            getServer().getPluginManager().registerEvents(protectionListener, this);
        }

        getServer().getPluginManager().addPermission(new Permission("mymap.main", "MyMap main command", "op"));
        getServer().getCommandMap().register("mymap", new MainCommand("mymap", this));

        getServer().getScheduler().scheduleRepeatingTask(new PluginTask<MyMap>(this) {
            @Override
            public void onRun(int currentTick) {
                for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                    player.sendPopup("TPS: " + Server.getInstance().getTicksPerSecond());
                }
            }
        }, 10);
    }

    @Override
    public void onDisable() {
        SettingListener.clearSettingDataMap();
    }

    public FrameList getList() {
        return list;
    }

    public boolean removeFrame(MyMapFrame frame) {
        FrameRemoveEvent event = new FrameRemoveEvent(frame);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        frame.clear();
        list.remove(frame);
        return true;
    }

    public boolean addFrame(MyMapFrame frame) {
        Objects.requireNonNull(frame, "frame");
        FrameAddEvent event = new FrameAddEvent(frame);
        Server.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (!list.add(frame)) {
            return false;
        }

        frame.save();
        return true;
    }
}
