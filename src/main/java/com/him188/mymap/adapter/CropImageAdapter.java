package com.him188.mymap.adapter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Him188 @ MyMap Project
 */
public class CropImageAdapter extends SingleImageAdapter {
    public CropImageAdapter(File image) throws IOException {
        super(image);
    }

    public CropImageAdapter(BufferedImage image) throws IOException {
        super(image);
    }
    @Override
    public void doAdaptation(int width, int height) throws IOException {
        BufferedImage result = new BufferedImage(width, height, this.getImage().getType());
        Graphics2D g = result.createGraphics();
        g.drawImage(this.getImage(), 0, 0, this.getImage().getWidth(), this.getImage().getHeight(), null);
        g.dispose();
        this.image = result;
    }
}
