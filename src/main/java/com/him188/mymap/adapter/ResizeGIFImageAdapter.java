package com.him188.mymap.adapter;

import cn.nukkit.math.Vector2;
import com.him188.mymap.utils.GifDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Him188 @ MyMap Project
 */
public class ResizeGIFImageAdapter extends ImageAdapter {
    private int count;
    private int n;
    private GifDecoder decoder;
    private SingleImageAdapter[] images;

    public ResizeGIFImageAdapter(File file) throws IOException {
        setImage(file);
    }

    public void setImage(File imageFile) throws IOException {
        this.n = 0;
        decoder = new GifDecoder();
        decoder.read(new FileInputStream(imageFile));
        loadImages();
    }

    private void loadImages() throws IOException {
        count = decoder.getFrameCount();
        this.images = new SingleImageAdapter[count];
        for (int i = 0; i < count; i++) {
            this.images[i] = new ResizeImageAdapter(decoder.getFrame(i));
        }
    }

    @Override
    public void doAdaptation(int width, int height) throws IOException {
        for (int i = 0; i < count; i++) {
            SingleImageAdapter subAdapter = new ResizeImageAdapter(decoder.getFrame(i));
            subAdapter.doAdaptation(width, height);
            this.images[i] = subAdapter;
        }
    }

    /**
     * Gets the first frame of gif image
     *
     * @return the first frame of gif image
     */
    public BufferedImage getImage() {
        return decoder.getFrame(0);
    }

    public int getDelay() {
        return decoder.getDelay(this.n);
    }

    public int getFrameCount() {
        return count;
    }

    public SingleImageAdapter getFrame() {
        return this.images[this.n];
    }

    public SingleImageAdapter getFrame(int n) {
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
}
