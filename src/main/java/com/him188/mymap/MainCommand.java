package com.him188.mymap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParameter;
import com.him188.mymap.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.him188.mymap.utils.LanguageBase.ID.*;
import static com.him188.mymap.utils.LanguageBase.getMessage;

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
                        sender.sendMessage(getMessage(SET_HAS_BEEN_CANCELED));
                        return true;
                    }

                    if (getPlugin().getList().getById(args[1]) != null) {
                        sender.sendMessage(getMessage(FRAME_IS_EXISTS));
                        return true;
                    }

                    SettingListener.addSettingPlayer((Player) sender, args[1]);
                    sender.sendMessage(getMessage(SET_START));
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
                    sender.sendMessage(getMessage(FRAME_DOES_NOT_EXISTS, args[1]));
                    return true;
                }

                if (!getPlugin().removeFrame(frame)) {
                    sender.sendMessage(getMessage(OPTION_CANCELLED));
                    return true;
                }

                sender.sendMessage(getMessage(SUCCESSFULLY_DELETED));
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
                    sender.sendMessage(getMessage(FRAME_DOES_NOT_EXISTS, args[1]));
                    return true;
                }

                File file = Utils.detectImageFile(new File(MyMapFrame.IMAGE_DATA_FOLDER, args[2]).getPath());
                if (file == null) {
                    sender.sendMessage(getMessage(FILE_DOES_NOT_EXISTS, args[2]));
                    return true;
                }

                try {
                    if (!frame.setImageFile(file)) {
                        sender.sendMessage(getMessage(OPTION_CANCELLED));
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    sender.sendMessage(getMessage(FILE_CAN_NOT_READ));
                    return true;
                }
                frame.save();

                sender.sendMessage(getMessage(SUCCESSFULLY_SET));
                return true;
            }
        }

        return false;
    }
}
