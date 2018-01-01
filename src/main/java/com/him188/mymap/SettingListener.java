package com.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.him188.mymap.utils.LanguageBase.ID.*;
import static com.him188.mymap.utils.LanguageBase.getMessage;

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
                    event.getPlayer().sendMessage(getMessage(SET_END_POS));
                    return;
                }

                case SET_END_POS: {
                    if (data.level.getId() != event.getBlock().getLevel().getId()) {
                        event.getPlayer().sendMessage(getMessage(TOUCH_THE_SAME_WORLD));
                        removeSettingPlayer(event.getPlayer());
                        return;
                    }
                    data.endPos = event.getBlock();
                    if (!data.checkPos()) {
                        event.getPlayer().sendMessage(getMessage(WRONG_WIDTH));
                        removeSettingPlayer(event.getPlayer());
                        return;
                    }
                    data.step = Step.SET_FACE;
                    event.getPlayer().sendMessage(getMessage(TOUCH_FACE));
                    return;
                }

                case SET_FACE: {
                    data.face = event.getFace();
                    removeSettingPlayer(event.getPlayer());
                    try {
                        if (!plugin.addFrame(data.toFrame())) {
                            event.getPlayer().sendMessage(getMessage(OPTION_CANCELLED));
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        event.getPlayer().sendMessage(getMessage(CAN_NOT_READ_DEFAULT));
                        return;
                    }
                    event.getPlayer().sendMessage(getMessage(ADD_DONE));
                    return;
                }

                default: {
                    removeSettingPlayer(event.getPlayer());
                    event.getPlayer().sendMessage(getMessage(OPTION_INVALID));
                }
            }
        }
    }
}
