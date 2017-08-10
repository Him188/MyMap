package com.him188.mymap.adapter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Him188 @ MyMap Project
 */
public class CropImageAdapter extends ImageAdapter {
    public CropImageAdapter(BufferedImage image) {
        super(image);
    }

    public CropImageAdapter(File image) throws IOException {
        super(image);
    }

    @Override
    public void doAdaptation(int width, int height) {
        this.setImage(this.getImage().getSubimage(0, 0, width, height));
    }
}
