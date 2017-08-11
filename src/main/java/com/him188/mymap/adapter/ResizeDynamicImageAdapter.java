package com.him188.mymap.adapter;

import cn.nukkit.math.Vector2;
import com.him188.mymap.utils.GifDecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Him188 @ MyMap Project
 */
public class ResizeDynamicImageAdapter extends ImageAdapter {
    private int count;
    private int n;
    private ImageAdapter[] images;
    private GifDecoder decoder;

    public ResizeDynamicImageAdapter(BufferedImage file) throws IOException {
        super(file);
    }

    public ResizeDynamicImageAdapter(File file) throws IOException {
        super(file);
    }

    @Override
    public void setImage(BufferedImage image) throws IOException {
        this.n = 0;
        decoder = new GifDecoder();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", out);
        decoder.read(new ByteArrayInputStream(out.toByteArray()));
        getImages();
    }

    @Override
    public void setImage(File imageFile) throws IOException {
        this.n = 0;
        decoder = new GifDecoder();
        decoder.read(new FileInputStream(imageFile));
        getImages();
    }

    private void getImages() throws IOException {
        count = decoder.getFrameCount();
        this.images = new ImageAdapter[count];
        for (int i = 0; i < count; i++) {
            this.images[i] = new ResizeImageAdapter(decoder.getFrame(i));
        }
    }

    @Override
    public void doAdaptation(int width, int height) throws IOException {
        for (int i = 0; i < count; i++) {
            ImageAdapter subAdapter = new ResizeImageAdapter(decoder.getFrame(i));
            subAdapter.doAdaptation(width, height);
            this.images[i] = subAdapter;
        }
    }

    /**
     * Gets the first frame of gif image
     *
     * @return the first frame of gif image
     */
    @Override
    public BufferedImage getImage() {
        return decoder.getFrame(0);
    }

    public int getDelay() {
        return decoder.getDelay(this.n);
    }

    public int getFrameCount() {
        return count;
    }

    public ImageAdapter getFrame() {
        return this.images[this.n];
    }

    public ImageAdapter getFrame(int n) {
        return this.images[n];
    }

    public Dimension getFrameSize() {
        return this.decoder.getFrameSize();
    }

    public void next() {
        this.n++;
        if (this.n == this.count) {
            this.n = 0;
        }
    }

    public final List<Vector2> crop(int width) {
        int xCount = this.images[0].getImage().getWidth() / width;
        int yCount = this.images[0].getImage().getHeight() / width;
        List<Vector2> list = new ArrayList<>(xCount * yCount);
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                list.add(new Vector2(x, y));
            }
        }
        return list;
    }

    @Override
    public Map<Vector2, BufferedImage> cropAsSubImages(int width) {
        throw new UnsupportedOperationException();
    }
}
