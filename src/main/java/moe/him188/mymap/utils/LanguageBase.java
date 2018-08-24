package moe.him188.mymap.utils;

import moe.him188.mymap.MyMapFrame;

import java.util.EnumMap;
import java.util.Locale;

import static cn.nukkit.utils.TextFormat.AQUA;
import static cn.nukkit.utils.TextFormat.RED;
import static moe.him188.mymap.utils.LanguageBase.ID.*;

/**
 * @author Him188 @ MyMap Project
 */
public final class LanguageBase {
    public enum ID {
        LOAD_FRAME_ERROR,
        SET_HAS_BEEN_CANCELED,
        FRAME_IS_EXISTS,
        SET_START,
        FRAME_DOES_NOT_EXISTS,
        OPTION_CANCELLED,
        OPTION_INVALID,
        SUCCESSFULLY_DELETED,
        FILE_DOES_NOT_EXISTS,
        FILE_CAN_NOT_READ,
        SUCCESSFULLY_SET,
        CAN_NOT_DELETE_CONFIG,

        SET_END_POS,
        TOUCH_THE_SAME_WORLD,
        WRONG_WIDTH,
        TOUCH_FACE,
        ADD_DONE,
        CAN_NOT_READ_DEFAULT,
    }


    public static final LanguageBase CHINESE = new LanguageBase(new EnumMap<ID, String>(ID.class) {
        {
            put(LOAD_FRAME_ERROR, "加载画框 %0 时遇到错误");
            put(SET_HAS_BEEN_CANCELED, AQUA + "已取消设置");
            put(FRAME_IS_EXISTS, RED + "此 ID 的画框已经存在了");
            put(SET_START, AQUA + "开始设置, 请拆除一个点, 这个点将作为矩形画框起始点");
            put(FRAME_DOES_NOT_EXISTS, RED + "ID为 %0 的画框不存在");
            put(OPTION_CANCELLED, RED + "操作被意外终止");
            put(SUCCESSFULLY_DELETED, AQUA + "删除成功");
            put(FILE_DOES_NOT_EXISTS, RED + "文件 %0 不存在. 请设置位于 " + MyMapFrame.IMAGE_DATA_FOLDER + " 目录下的文件名. 后缀自动检测(支持jpg,gif(包括动态),bmp,webp,png)");
            put(FILE_CAN_NOT_READ, RED + "文件无法读取. 请更换图片");
            put(SUCCESSFULLY_SET, AQUA + "设置成功");
            put(CAN_NOT_DELETE_CONFIG, "无法删除 %0 的配置文件.");

            put(SET_END_POS, AQUA + "请拆除另一个点以确定一个矩形框. 请保证矩形框只有一格宽度");
            put(TOUCH_THE_SAME_WORLD, RED + "请拆除同一个世界内的方块. 本次操作已取消");
            put(WRONG_WIDTH, RED + "请设置宽度为1的矩形框. 本次操作已取消");
            put(TOUCH_FACE, AQUA + "请点击需要显示面");
            put(OPTION_INVALID, RED + "无效操作");
            put(ADD_DONE, AQUA + "添加成功");
            put(CAN_NOT_READ_DEFAULT, RED + "无法读取默认图片, 添加失败. 请放置 default.* 至 " + MyMapFrame.IMAGE_DATA_FOLDER);
        }
    });

    public static final LanguageBase ENGLISH = new LanguageBase(new EnumMap<ID, String>(ID.class) {
        {
            put(LOAD_FRAME_ERROR, "Error while loading frame %0");
            put(SET_HAS_BEEN_CANCELED, AQUA + "Operation has been cancelled");
            put(FRAME_IS_EXISTS, RED + "The frame with id %0 does already exists");
            put(SET_START, AQUA + "Setting starts. Please break a block, and this block will be the starting point for rectangular frame");
            put(FRAME_DOES_NOT_EXISTS, RED + "The frame with id %0 does not exists");
            put(OPTION_CANCELLED, RED + "Operation has been cancelled accidentally");
            put(SUCCESSFULLY_DELETED, AQUA + "Successfully deleted");
            put(FILE_DOES_NOT_EXISTS, RED + "File %0 does not exists. Please use files in folder " + MyMapFrame.IMAGE_DATA_FOLDER + ". Suffix will be automatically detected(Allows jpg,gif,bmp,webp,png)");
            put(FILE_CAN_NOT_READ, RED + "Files can not be read. Please change another picture");
            put(SUCCESSFULLY_SET, AQUA + "Successfully set");
            put(CAN_NOT_DELETE_CONFIG, "Can not delete config of %0");

            put(SET_END_POS, AQUA + "Please break on another point to determine a rectangular frame. Please ensure that only one width rectangle");
            put(TOUCH_THE_SAME_WORLD, RED + "Please break in the same world. This operation has been cancelled");
            put(WRONG_WIDTH, RED + "Please set the width of 1 rectangular frame. This operation has been cancelled");
            put(TOUCH_FACE, AQUA + "Please click on the need to display surface");
            put(OPTION_INVALID, RED + "Invalid operation");
            put(ADD_DONE, AQUA + "Successfully added");
            put(CAN_NOT_READ_DEFAULT, RED + "Con not read default image, this operation has been cancelled. Please put image default.* into " + MyMapFrame.IMAGE_DATA_FOLDER);
        }
    });


    public static LanguageBase CURRENT_LANGUAGE = getDefaultLanguage();

    private static LanguageBase type;

    public static LanguageBase getDefaultLanguage() {
        if (type != null) {
            return type;
        }

        Locale locale = Locale.getDefault();

        if (locale == Locale.SIMPLIFIED_CHINESE) {
            return type = CHINESE;
        } else if (locale == Locale.TRADITIONAL_CHINESE) {
            return type = CHINESE;
        } else {
            return type = ENGLISH;
        }
    }

    public static String getMessage(ID id, String... args) {
        if (id == null) {
            return null;
        }
        String string = CURRENT_LANGUAGE.getMap().get(id);
        if (string == null) {
            return null;
        }
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                string = string.replace("%" + i, args[i]);
            }
        }
        return string;
    }

    private final EnumMap<ID, String> map;

    private LanguageBase(final EnumMap<ID, String> map) {
        this.map = map;
    }

    public EnumMap<ID, String> getMap() {
        return map;
    }
}
