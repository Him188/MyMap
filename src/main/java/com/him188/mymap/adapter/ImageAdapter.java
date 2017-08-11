package com.him188.mymap.adapter;

import cn.nukkit.math.Vector2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 图像适应器. 用于将图像的宽高变更为画框所需要的宽高
 *
 * @author Him188 @ MyMap Project
 */
public abstract class ImageAdapter {
    private BufferedImage image;

    public ImageAdapter(BufferedImage image) throws IOException {
        setImage(image);
    }

    public ImageAdapter(File image) throws IOException {
        setImage(image);
    }

    public void setImage(BufferedImage image) throws IOException {
        this.image = Objects.requireNonNull(image, "image");
    }

    public void setImage(File imageFile) throws IOException {
        this.image = ImageIO.read(Objects.requireNonNull(imageFile, "imageFile"));
        if (this.image == null) {
            throw new IOException("argument image is invalid");
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * 适应图片为目标宽高
     *
     * @param width  目标图片宽
     * @param height 目标图片高
     */
    public abstract void doAdaptation(int width, int height) throws IOException;

    /**
     * 将图像裁剪为多个小块
     *
     * @param width 每块宽度 (128)
     *
     * @return 小图像集合
     */
    public Map<Vector2, BufferedImage> cropAsSubImages(final int width) {
        int xCount = this.getImage().getWidth() / width;
        int yCount = this.getImage().getHeight() / width;
        Map<Vector2, BufferedImage> map = new HashMap<>();
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                map.put(new Vector2(x, y), this.getImage().getSubimage(x * width, y * width, width, width));
            }
        }
        return map;
    }
}
