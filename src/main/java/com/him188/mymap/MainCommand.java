package com.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Him188 @ MyMap Project
 */
public final class MainCommand extends PluginCommand<MyMap> implements CommandExecutor {
    public MainCommand(String name, MyMap owner) {
        super(name, owner);
        setCommandParameters(new HashMap<String, CommandParameter[]>() {
            {
                put("set", new CommandParameter[]{
                        new CommandParameter("arg", new String[]{"set", "add"}),
                        new CommandParameter("id", CommandParameter.ARG_TYPE_STRING, false),
                });

                put("remove", new CommandParameter[]{
                        new CommandParameter("arg", new String[]{"remove"}),
                        new CommandParameter("id", CommandParameter.ARG_TYPE_STRING, false),
                });

                put("set_picture", new CommandParameter[]{
                        new CommandParameter("arg", new String[]{"setpicture", "setimage", "sp", "si"}),
                        new CommandParameter("id", CommandParameter.ARG_TYPE_STRING, false),
                        new CommandParameter("image file", CommandParameter.ARG_TYPE_RAW_TEXT, false),
                });
            }
        });
        setUsage("/mymap set|add|remove <id> | /mymap setpictrue|setimage|sp|si <id> <image file>");
        setDescription("MyMap");
        setExecutor(this);
        setPermission("mymap.main");
        setAliases(new String[]{"mapadmin"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String arg = args[0].toLowerCase();
        if (sender instanceof Player) {
            switch (arg) {
                case "set":
                case "add": {
                    if (args.length != 2) {
                        return false;
                    }

                    if (SettingListener.isSettingPlayer((Player) sender)) {
                        SettingListener.removeSettingPlayer((Player) sender);
                        sender.sendMessage(TextFormat.AQUA + "已取消设置");
                        return true;
                    }

                    if (getPlugin().getList().getById(args[1]) != null) {
                        sender.sendMessage(TextFormat.RED + "此 ID 的画框已经存在了");
                        return true;
                    }

                    SettingListener.addSettingPlayer((Player) sender, args[1]);
                    sender.sendMessage(TextFormat.AQUA + "开始设置, 请点击一个点, 这个点将作为矩形画框起始点");
                    return true;
                }
            }
        }

        switch (arg) {
            case "remove": {
                if (args.length != 2) {
                    return false;
                }

                MyMapFrame frame = getPlugin().getList().getById(args[1]);
                if (frame == null) {
                    sender.sendMessage(TextFormat.RED + "ID为 " + args[1] + " 的画框不存在");
                    return true;
                }

                if (!getPlugin().removeFrame(frame)) {
                    sender.sendMessage(TextFormat.RED + "操作被意外终止");
                    return true;
                }

                sender.sendMessage(TextFormat.AQUA + "删除成功");
                return true;
            }

            case "setpicture":
            case "sp":
            case "setimage":
            case "si": {
                if (args.length < 3) {
                    return false;
                }

                MyMapFrame frame = getPlugin().getList().getById(args[1]);
                if (frame == null) {
                    sender.sendMessage(TextFormat.RED + "ID为 " + args[1] + " 的画框不存在");
                    return true;
                }

                File file = Utils.defineFile(new File(MyMapFrame.IMAGE_DATA_FOLDER, args[2]).getPath());
                if (file == null) {
                    sender.sendMessage(TextFormat.RED + "文件 " + args[2] + " 不存在. 请设置位于 " + MyMapFrame.IMAGE_DATA_FOLDER + " 目录下的文件名. 后缀自动检测(支持jpg,gif(包括动态),bmp,webp,png)");
                    return true;
                }

                try {
                    if (!frame.setImageFile(file)) {
                        sender.sendMessage(TextFormat.RED + "操作被意外终止");
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    sender.sendMessage(TextFormat.RED + "文件无法读取. 请更换图片");
                    return true;
                }
                frame.save();

                sender.sendMessage(TextFormat.AQUA + "设置成功");
                return true;
            }
        }

        return false;
    }
}
