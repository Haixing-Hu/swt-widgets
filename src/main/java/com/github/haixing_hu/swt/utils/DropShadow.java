/******************************************************************************
 *
 * Copyright (c) 2014  Haixing Hu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Haixing Hu (https://github.com/Haixing-Hu/) - Initial implementation and API.
 *
 ******************************************************************************/

package com.github.haixing_hu.swt.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import static com.github.haixing_hu.lang.Argument.requireNonNull;

/**
 * A class used to draw the drop shadow of a shape or a image.
 * <p>
 * This class is inspired by the algorithms implemented by Romain Guy. I port it
 * from AWT to SWT and add the support for polygon.
 *
 * @author Haixing Hu
 * @see http://www.curious-creature.org/2005/07/07/fast-or-good-drop-shadows/
 */
public class DropShadow {

  public static final float DEFAULT_ANGLE = 30.0f;

  public static final int DEFAULT_DISTANCE = 5;

  public static final int DEFAULT_SHADOW_SIZE = 5;

  public static final float DEFAULT_SHADOW_OPACITY = 0.5f;

  public static final RGB DEFAULT_SHADOW_COLOR = new RGB(0, 0, 0);

  public static final boolean DEFAULT_HIGH_QUALITY = false;

  private float angle;
  private int distance;
  private int shadowSize;
  private float shadowOpacity;
  private Color shadowColor;
  private boolean highQuality;
  // cached values for fast painting
  private final int distance_x;
  private final int distance_y;

  /**
   * Constructs a {@link DropShadow} object.
   */
  public DropShadow() {
    angle = DEFAULT_ANGLE;
    distance = DEFAULT_DISTANCE;
    shadowSize = DEFAULT_SHADOW_SIZE;
    shadowOpacity = DEFAULT_SHADOW_OPACITY;
    shadowColor = SWTResourceManager.getColor(DEFAULT_SHADOW_COLOR);
    highQuality = DEFAULT_HIGH_QUALITY;
    // cached values for fast painting
    distance_x = 0;
    distance_y = 0;
  }

  /**
   * Gets the angle.
   *
   * @return the angle.
   */
  public float getAngle() {
    return angle;
  }

  /**
   * Sets the angle.
   *
   * @param angle
   *          the new angle to set.
   */
  public void setAngle(float angle) {
    this.angle = angle;
  }

  /**
   * Gets the distance.
   *
   * @return the distance.
   */
  public int getDistance() {
    return distance;
  }

  /**
   * Sets the distance.
   *
   * @param distance
   *          the new distance to set.
   */
  public void setDistance(int distance) {
    this.distance = distance;
  }

  /**
   * Gets the shadowSize.
   *
   * @return the shadowSize.
   */
  public int getShadowSize() {
    return shadowSize;
  }

  /**
   * Sets the shadowSize.
   *
   * @param shadowSize
   *          the new shadowSize to set.
   */
  public void setShadowSize(int shadowSize) {
    this.shadowSize = shadowSize;
  }

  /**
   * Gets the shadowOpacity.
   *
   * @return the shadowOpacity.
   */
  public float getShadowOpacity() {
    return shadowOpacity;
  }

  /**
   * Sets the shadowOpacity.
   *
   * @param shadowOpacity
   *          the new shadowOpacity to set.
   */
  public void setShadowOpacity(float shadowOpacity) {
    this.shadowOpacity = shadowOpacity;
  }

  /**
   * Gets the shadowColor.
   *
   * @return the shadowColor.
   */
  public Color getShadowColor() {
    return shadowColor;
  }

  /**
   * Sets the shadowColor.
   *
   * @param shadowColor
   *          the new shadowColor to set.
   */
  public void setShadowColor(Color shadowColor) {
    this.shadowColor = requireNonNull("shadowColor", shadowColor);
  }

  /**
   * Tests whether to use the high quality algorithm to draw the drop shadow.
   * <p>
   * The high quality algorithm is slower than the low quality algorithm.
   *
   * @return Indicates whether to use the high quality algorithm to draw the
   *         drop shadow.
   */
  public boolean isHighQuality() {
    return highQuality;
  }

  /**
   * Sets whether to use the high quality algorithm to draw the drop shadow.
   * <p>
   * The high quality algorithm is slower than the low quality algorithm.
   *
   * @param highQuality
   *          Indicates whether to use the high quality algorithm to draw the
   *          drop shadow.
   */
  public void setHighQuality(boolean highQuality) {
    this.highQuality = highQuality;
  }
/*
  public ImageData createDropShadow(ImageData image) {
    final ImageData subject = prepareImage(image);
    if (highQuality) {
      final ImageData shadow = new ImageData(subject.getWidth(),
          subject.getHeight(), BufferedImage.TYPE_INT_ARGB);
      final BufferedImage shadowMask = createShadowMask(subject);
      getLinearBlurOp(shadowSize).filter(shadowMask, shadow);
      return shadow;
    }
    applyShadow(subject);
    return subject;
  }

  private ImageData prepareImage(ImageData image) {
    final ImageData subject = new ImageData(image.width + (shadowSize * 2),
        image.height + (shadowSize * 2), 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));



    final Graphics2D g2 = subject.createGraphics();
    g2.drawImage(image, null, shadowSize, shadowSize);
    g2.dispose();
    return subject;
  }

  private ImageData createShadowMask(ImageData image) {
    final BufferedImage mask = new BufferedImage(image.getWidth(),
        image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    final Graphics2D g2d = mask.createGraphics();
    g2d.drawImage(image, 0, 0, null);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN,
        shadowOpacity));
    g2d.setColor(shadowColor);
    g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    g2d.dispose();
    return mask;
  }

  private ConvolveOp getLinearBlurOp(int size) {
    final float[] data = new float[size * size];
    final float value = 1.0f / (size * size);
    for (int i = 0; i < data.length; i++) {
      data[i] = value;
    }
    return new ConvolveOp(new Kernel(size, size, data));
  }

  private void applyShadow(ImageData image) {
    final int dstWidth = image.getWidth();
    final int dstHeight = image.getHeight();

    final int left = (shadowSize - 1) >> 1;
    final int right = shadowSize - left;
    final int xStart = left;
    final int xStop = dstWidth - right;
    final int yStart = left;
    final int yStop = dstHeight - right;

    final int shadowRgb = shadowColor.getRGB() & 0x00FFFFFF;

    final int[] aHistory = new int[shadowSize];
    int historyIdx = 0;

    int aSum;

    final int[] dataBuffer = ((DataBufferInt) image.getRaster().getDataBuffer())
        .getData();
    final int lastPixelOffset = right * dstWidth;
    final float sumDivider = shadowOpacity / shadowSize;

    // horizontal pass

    for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y
        * dstWidth) {
      aSum = 0;
      historyIdx = 0;
      for (int x = 0; x < shadowSize; x++, bufferOffset++) {
        final int a = dataBuffer[bufferOffset] >>> 24;
        aHistory[x] = a;
        aSum += a;
      }

      bufferOffset -= right;

      for (int x = xStart; x < xStop; x++, bufferOffset++) {
        int a = (int) (aSum * sumDivider);
        dataBuffer[bufferOffset] = (a << 24) | shadowRgb;

        // substract the oldest pixel from the sum
        aSum -= aHistory[historyIdx];

        // get the lastest pixel
        a = dataBuffer[bufferOffset + right] >>> 24;
        aHistory[historyIdx] = a;
        aSum += a;

        if (++historyIdx >= shadowSize) {
          historyIdx -= shadowSize;
        }
      }
    }

    // vertical pass
    for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
      aSum = 0;
      historyIdx = 0;
      for (int y = 0; y < shadowSize; y++, bufferOffset += dstWidth) {
        final int a = dataBuffer[bufferOffset] >>> 24;
        aHistory[y] = a;
        aSum += a;
      }

      bufferOffset -= lastPixelOffset;

      for (int y = yStart; y < yStop; y++, bufferOffset += dstWidth) {
        int a = (int) (aSum * sumDivider);
        dataBuffer[bufferOffset] = (a << 24) | shadowRgb;

        // substract the oldest pixel from the sum
        aSum -= aHistory[historyIdx];

        // get the lastest pixel
        a = dataBuffer[bufferOffset + lastPixelOffset] >>> 24;
        aHistory[historyIdx] = a;
        aSum += a;

        if (++historyIdx >= shadowSize) {
          historyIdx -= shadowSize;
        }
      }
    }
  }
*/
}
