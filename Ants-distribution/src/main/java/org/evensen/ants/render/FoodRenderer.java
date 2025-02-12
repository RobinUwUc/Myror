package org.evensen.ants.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.evensen.ants.AntWorld;
import org.evensen.ants.Position;

public class FoodRenderer {
    private static final int COLOR_PRIMARY = 0xFFA4DE02;
    private static final int COLOR_HIGHLIGHT = 0xFFE4E100;
    private static final int HIGHLIGHT_WIDTH = 1;
    private static final int HIGHLIGHT_HEIGHT = 2;
    private static final int COLOR_WEIGHT = 5;
    private static final int BLUR_WEIGHT = 3;

    private boolean dirtyFlag;
    private WritableImage cachedImage;

    public FoodRenderer() {
        this.dirtyFlag = true;
        this.cachedImage = null;
    }

    public void dirty() {
        this.dirtyFlag = true;
    }

    public void render(final GraphicsContext g, final AntWorld world) {
        if (this.dirtyFlag) {
            final int discreteScaleX = (int) g.getCanvas().getWidth() / world.getWidth() + 1;
            final int discreteScaleY = (int) g.getCanvas().getHeight() / world.getHeight() + 1;

            final int width = world.getWidth() * discreteScaleX;
            final int height = world.getHeight() * discreteScaleY;

            this.cachedImage = new WritableImage(width, height);

            final int[] buffer = new int[width * height];
            int i = 0;
            for (int y = 0; y < world.getHeight(); y++) {
                for (int k = 0; k < discreteScaleY; k++) {
                    renderScanlineIntoCachedImage(world, buffer, i, discreteScaleX, y);
                    i += width;
                }
            }

            int[] des = buffer;
            int[] src = new int[buffer.length];
            for (int w = 0; w < 1; w++) {
                int[] tmp = des;
                des = src;
                src = tmp;
                int avgR = 0;
                int avgG = 0;
                int avgB = 0;
                for(int j = 0; j < width * height; j++) {
                    if (src[j] != 0X00000000) {
                        int acc1 = 0;
                        int acc2 = 0;
                        int acc3 = 0;
                        for (int offsY = -4; offsY < 5; offsY++) {
                            final int k = j + width * offsY;
                            if (j > -1 && j < buffer.length) {
                                for (int offsX = -4; offsX < 5; offsX++) {
                                    final int l = k + offsX;
                                    if (l > -1 && l < buffer.length) {
                                        final int c = src[l];
                                        switch (c) {
                                                                          /*- Needs Java 14+ to work:
                                            case 0x00000000 -> acc1 += 1;
                                            case COLOR_PRIMARY -> acc2 += 1;
                                            case COLOR_HIGHLIGHT -> acc3 += 1;
                                            */
                                            case 0x00000000:
                                                acc1 += 1;
                                                break;
                                            case COLOR_PRIMARY:
                                                acc2 += 1;
                                                break;
                                            case COLOR_HIGHLIGHT:
                                                acc3 += 1;
                                                break;
                                        }
                                        avgR += c >> 16 & 0xFF;
                                        avgG += c >> 8 & 0xFF;
                                        avgB += c & 0xFF;
                                    }
                                }
                            }
                        }
                        avgR /= 64;
                        avgG /= 64;
                        avgB /= 64;

                        int c = 0x00000000;
                        int k = 32;
                        k -= acc1;
                        if (k < 1) {
                            c = 0x00000000;
                        } else {
                            k -= acc2;
                            if (k < 1) {
                                c = COLOR_PRIMARY;
                            } else {
                                k -= acc3;
                                if (k < 1) {
                                    c = COLOR_HIGHLIGHT;
                                }
                            }
                        }

                        if (c != 0x00000000) {
                            final int r1 = Math.min(255, (((c >> 16) & 0xFF) * COLOR_WEIGHT + avgR * BLUR_WEIGHT)
                                    / (COLOR_WEIGHT + BLUR_WEIGHT));
                            final int g1 = Math.min(255, (((c >> 8) & 0xFF) * COLOR_WEIGHT + avgG * BLUR_WEIGHT)
                                    / (COLOR_WEIGHT + BLUR_WEIGHT));
                            final int b1 = Math.min(255, (((c & 0xFF) * COLOR_WEIGHT + avgB * BLUR_WEIGHT)
                                    / (COLOR_WEIGHT + BLUR_WEIGHT)));
                            des[j] = (c & 0xFF000000) | (r1 << 16) | (g1 << 8) | b1;
                        }
                    }
                }
            }

            this.cachedImage.getPixelWriter().setPixels(
                    0, 0, width, height, PixelFormat.getIntArgbInstance(), des, 0, width);

            this.dirtyFlag = false;
        }

        g.drawImage(this.cachedImage, 0.0, 0.0, world.getWidth(), world.getHeight());
    }

    private void renderScanlineIntoCachedImage(final AntWorld world,
                                               final int[] buffer,
                                               final int i,
                                               final int scale,
                                               final int y) {
        int j = i;
        for (int x = 0; x < world.getWidth(); x++) {
            for (int k = 0; k < scale; k++) {
                final Position p = new Position(x, y);
                if (world.containsFood(p)) {
                    for (int offsetX = 1; offsetX <= HIGHLIGHT_WIDTH; offsetX++) {
                        for (int offsetY = 1; offsetY <= HIGHLIGHT_HEIGHT; offsetY++) {
                            final Position offsetP = p.offset(offsetX, offsetY);
                            if (offsetP.isInBounds(world.getWidth(), world.getHeight()) && !world.containsFood(offsetP)) {
                                buffer[j] = COLOR_HIGHLIGHT;
                                break;
                            } else {
                                buffer[j] = COLOR_PRIMARY;
                            }
                        }
                    }
                }
                j++;
            }
        }
    }
}
