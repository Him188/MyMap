package com.him188.mymap.adapter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Him188 @ MyMap Project
 */
public class ResizeImageAdapter extends SingleImageAdapter {
    public ResizeImageAdapter(File image) throws IOException {
        super(image);
    }

    public ResizeImageAdapter(BufferedImage image) {
        super(image);
    }

    @Override
    public void doAdaptation(int width, int height) {
        BufferedImage result = new BufferedImage(width, height, this.getImage().getType());
        Graphics2D g = result.createGraphics();
        g.drawImage(this.getImage(), 0, 0, width, height, null);
        this.image = result;
    }
}
