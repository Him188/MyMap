package com.him188.mymap.adapter;

import java.io.IOException;

/**
 * 图像适应器. 用于将图像的宽高变更为画框所需要的宽高
 *
 * @author Him188 @ MyMap Project
 */
public abstract class ImageAdapter {
    /**
     * 适应图片为目标宽高
     *
     * @param width  目标图片宽
     * @param height 目标图片高
     */
    public abstract void doAdaptation(int width, int height) throws IOException;
}
