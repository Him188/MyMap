package com.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用于设置画框的事件监听器
 *
 * @author Him188 @ MyMap Project
 */
public final class SettingListener implements Listener {
    private final MyMap plugin;

    SettingListener(MyMap plugin) {
        this.plugin = plugin;
    }

    private static final class SettingData {
        private SettingData(String id) {
            this.id = id;
        }

        private String id;
        private Step step = Step.SET_START_POS;
        private Vector3 startPos;
        private Vector3 endPos;
        private Level level;
        private BlockFace face;

        private boolean checkPos() {
            if (startPos.x == endPos.x) {
                // if (Math.abs(startPos.z - endPos.z) == 1) {
                return true;
                //}
            } else if (startPos.z == endPos.z) {
                // if (Math.abs(startPos.x - endPos.x) == 1) {
                return true;
                // }
            }
            return false;
        }

        private MyMapFrame toFrame() throws IOException {
            return new MyMapFrame(id, startPos, endPos, level, face, null);
        }
    }

    private enum Step {
        SET_START_POS,
        SET_END_POS,
        SET_FACE
    }

    private static Map<UUID, SettingData> settingDataMap = new HashMap<>();

    static void clearSettingDataMap() {
        settingDataMap.clear();
    }

    static boolean isSettingPlayer(Player player) {
        return settingDataMap.containsKey(player.getUniqueId());
    }

    static void addSettingPlayer(Player player, String id) {
        settingDataMap.put(player.getUniqueId(), new SettingData(id));
    }

    static void removeSettingPlayer(Player player) {
        settingDataMap.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (settingDataMap.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled();
            SettingData data = settingDataMap.get(event.getPlayer().getUniqueId());
            switch (data.step) {
                case SET_START_POS: {
                    data.startPos = event.getBlock();
                    data.level = event.getBlock().getLevel();
                    data.step = Step.SET_END_POS;
                    event.getPlayer().sendMessage(TextFormat.AQUA + "请点击另一个点以确定一个矩形框. 请保证矩形框只有一格宽度");
                    return;
                }

                case SET_END_POS: {
                    if (data.level.getId() != event.getBlock().getLevel().getId()) {
                        event.getPlayer().sendMessage(TextFormat.RED + "请点击同一个世界内的方块. 本次操作已取消");
                        removeSettingPlayer(event.getPlayer());
                        return;
                    }
                    data.endPos = event.getBlock();
                    if (!data.checkPos()) {
                        event.getPlayer().sendMessage(TextFormat.RED + "请设置宽度为1的矩形框. 本次操作已取消");
                        removeSettingPlayer(event.getPlayer());
                        return;
                    }
                    data.step = Step.SET_FACE;
                    event.getPlayer().sendMessage(TextFormat.AQUA + "请点击需要显示面");
                    return;
                }

                case SET_FACE: {
                    data.face = event.getFace();
                    removeSettingPlayer(event.getPlayer());
                    try {
                        if (!plugin.addFrame(data.toFrame())) {
                            event.getPlayer().sendMessage(TextFormat.RED + "操作被意外终止");
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        event.getPlayer().sendMessage(TextFormat.RED + "无法读取默认图片, 添加失败. 请放置 default.* 至 " + MyMapFrame.IMAGE_DATA_FOLDER);
                        return;
                    }
                    event.getPlayer().sendMessage(TextFormat.AQUA + "添加成功");
                    return;
                }

                default: {
                    removeSettingPlayer(event.getPlayer());
                    event.getPlayer().sendMessage(TextFormat.RED + "无效操作");
                }
            }
        }
    }
}
