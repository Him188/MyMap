package com.him188.mymap;

import cn.nukkit.Server;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.him188.mymap.event.FrameAddEvent;
import com.him188.mymap.event.FrameRemoveEvent;
import com.him188.mymap.utils.FrameList;
import com.him188.mymap.utils.LanguageBase;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 *                     _ooOoo_
 *                    o8888888o
 *                    88" . "88
 *                    (| -_- |)
 *                    O\  =  /O
 *                 ____/`---'\____
 *               .'  \\|     |//  `.
 *              /  \\|||  :  |||//  \
 *             /  _||||| -:- |||||-  \
 *             |   | \\\  -  /// |   |
 *             | \_|  ''\---/''  |   |
 *             \  .-\__  `-`  ___/-. /
 *           ___`. .'  /--.--\  `. . __
 *        ."" '<  `.___\_<|>_/___.'  >'"".
 *       | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *       \  \ `-.   \_ __\ /__ _/   .-` /  /
 *  ======`-.____`-.___\_____/___.-`____.-'======
 *                     `=---='
 *  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *             佛祖保佑        永无BUG
 *    佛曰:
 *           写字楼里写字间，写字间里程序员；
 *           程序人员写程序，又拿程序换酒钱。
 *           酒醒只在网上坐，酒醉还来网下眠；
 *           酒醉酒醒日复日，网上网下年复年。
 *           但愿老死电脑间，不愿鞠躬老板前；
 *           奔驰宝马贵者趣，公交自行程序员。
 *           别人笑我忒疯癫，我笑自己命太贱；
 *           不见满街漂亮妹，哪个归得程序员？
 *
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

        saveResource("default.jpg", "images/default.jpg", false);
        MyMapFrame.IMAGE_DATA_FOLDER = new File(getDataFolder(), "images");
        MyMapFrame.DEFAULT_IMAGE_FILE = Utils.detectImageFile(new File(MyMapFrame.IMAGE_DATA_FOLDER, "default").getPath());
        MyMapFrame.IMAGE_DATA_FOLDER.mkdir();
        MyMapFrame.FRAME_DATA_FOLDER = new File(getDataFolder(), "frames");
        MyMapFrame.FRAME_DATA_FOLDER.mkdir();

        saveDefaultConfig();
        reloadConfig();
        String language = getConfig().get("language", "english");
        if ("chinese".equalsIgnoreCase(language) || "chs".equalsIgnoreCase(language) || "中文".equals(language)) {
            LanguageBase.CURRENT_LANGUAGE = LanguageBase.CHINESE;
        } else {
            LanguageBase.CURRENT_LANGUAGE = LanguageBase.ENGLISH;
        }

        list = new FrameList();

        File[] files = MyMapFrame.FRAME_DATA_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    list.add(MyMapFrame.fromConfigSection(new Config(file, Config.YAML).getRootSection()));
                } catch (IOException e) {
                    getLogger().error(LanguageBase.getMessage(LanguageBase.ID.LOAD_FRAME_ERROR, file.getName()), e);
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
