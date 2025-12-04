package com.mygame.client;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Small helper to extract a sub-image from a sheet.
 */
public class WritableImageView {
    private final Image src;
    private final int sx, sy, sw, sh;

    public WritableImageView(Image src, int sx, int sy, int sw, int sh) {
        this.src = src;
        this.sx = sx; this.sy = sy; this.sw = sw; this.sh = sh;
    }

    public WritableImage toImage() {
        ImageView iv = new ImageView(src);
        iv.setViewport(new javafx.geometry.Rectangle2D(sx, sy, sw, sh));
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage w = new WritableImage(sw, sh);
        iv.snapshot(params, w);
        return w;
    }
}
