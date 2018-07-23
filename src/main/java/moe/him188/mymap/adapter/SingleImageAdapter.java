package moe.him188.mymap.adapter;

import cn.nukkit.math.Vector2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Him188 @ MyMap Project
 */
public abstract class SingleImageAdapter extends ImageAdapter {
    protected BufferedImage image;

    protected SingleImageAdapter(File image) throws IOException {
        setImage(image);
    }

    protected SingleImageAdapter(BufferedImage image) {
        setImage(image);
    }

    public void setImage(File imageFile) throws IOException {
        this.image = ImageIO.read(imageFile);
        if (this.image == null) {
            throw new IllegalArgumentException("argument image is invalid");
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = Objects.requireNonNull(image, "image");
    }

    /**
     * 将图像裁剪为多个小块
     *
     * @param width 每块宽度 (128)
     *
     * @return 小图像集合
     */
    public final Map<Vector2, BufferedImage> cropAsSubImages(final int width) {
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