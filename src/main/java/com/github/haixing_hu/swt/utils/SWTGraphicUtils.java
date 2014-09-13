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

import java.util.ArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * Provides useful graphic algorithms .
 *
 * @author Haixing Hu
 */
public final class SWTGraphicUtils {

  private SWTGraphicUtils() {}


  /**
   * Average blurs a given image data.
   * <p>
   * This method will vertically blur all the pixels in a row at once. This
   * blurring is performed incrementally to each row.
   * <p>
   * In order to vertically blur any given pixel, maximally (radius * 2 + 1)
   * pixels must be examined. Since each of these pixels exists in the same
   * column, they span across a series of consecutive rows. These rows are
   * horizontally blurred before being cached and used as input for the vertical
   * blur. Blurring a pixel horizontally and then vertically is equivalent to
   * blurring the pixel with both its horizontal and vertical neighbors at
   * once.
   * <p>
   * Pixels are blurred under the notion of a 'summing scope'. A certain scope
   * of pixels in a column are summed then averaged to determine a target
   * pixel's resulting RGB value. When the next lower target pixel is being
   * calculated, the topmost pixel is removed from the summing scope (by
   * subtracting its RGB) and a new pixel is added to the bottom of the scope
   * (by adding its RGB). In this sense, the summing scope is moving downward.
   *
   * @param oldImageData
   *          The ImageData to be average blurred. Transparency information will
   *          be ignored.
   * @param radius
   *          the number of radius pixels to consider when blurring image.
   * @return A blurred copy of the image data, or null if an error occurred.
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-SimpleImageEffectsForSWT/index.html
   * @see http://www.jasonwaltman.com/thesis/filter-blur.html
   * @see http://www.blackpawn.com/texts/blur/default.html
   */
  public static ImageData blur(final ImageData oldImageData, int radius) {
    if (radius < 1) {
      return oldImageData;
    }
    // prepare new image data with 24-bit direct palette to hold blurred copy
    // of image
    final ImageData newImageData = new ImageData(oldImageData.width,
        oldImageData.height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
    if ((radius >= newImageData.height) || (radius >= newImageData.width)) {
      radius = Math.min(newImageData.height, newImageData.width) - 1;
    }
    // initialize cache
    final ArrayList<RGB[]> rowCache = new ArrayList<RGB[]>();
    final int cacheSize = ((radius * 2) + 1) > newImageData.height
        ? newImageData.height
        : (radius * 2) + 1; // number of rows of imageData we cache
    int cacheStartIndex = 0; // which row of imageData the cache begins with
    for (int row = 0; row < cacheSize; row++) {
      // row data is horizontally blurred before caching
      rowCache.add(rowCache.size(), blurRow(oldImageData, row, radius));
    }

    // sum red, green, and blue values separately for averaging
    final RGB[] rowRGBSums = new RGB[newImageData.width];
    final int[] rowRGBAverages = new int[newImageData.width];
    int topSumBoundary = 0; // current top row of summed values scope
    int targetRow = 0; // row with RGB averages to be determined
    int bottomSumBoundary = 0; // current bottom row of summed values scope
    int numRows = 0; // number of rows included in current summing scope
    for (int i = 0; i < newImageData.width; i++) {
      rowRGBSums[i] = new RGB(0, 0, 0);
    }

    while (targetRow < newImageData.height) {
      if (bottomSumBoundary < newImageData.height) {
        do {
          // sum pixel RGB values for each column in our radius scope
          for (int col = 0; col < newImageData.width; col++) {
            final RGB[] row = rowCache.get(bottomSumBoundary - cacheStartIndex);
            rowRGBSums[col].red += row[col].red;
            rowRGBSums[col].green += row[col].green;
            rowRGBSums[col].blue += row[col].blue;
          }
          numRows++;
          bottomSumBoundary++; // move bottom scope boundary lower
          if ((bottomSumBoundary < newImageData.height)
              && ((bottomSumBoundary - cacheStartIndex) > (radius * 2))) {
            // grow cache
            rowCache.add(rowCache.size(),
                blurRow(oldImageData, bottomSumBoundary, radius));
          }
        } while (bottomSumBoundary <= radius); // to initialize rowRGBSums at
                                               // start
      }

      if ((targetRow - topSumBoundary) > radius) {
        // subtract values of top row from sums as scope of summed
        // values moves down
        for (int col = 0; col < newImageData.width; col++) {
          final RGB[] row = rowCache.get(topSumBoundary - cacheStartIndex);
          rowRGBSums[col].red -= row[col].red;
          rowRGBSums[col].green -= row[col].green;
          rowRGBSums[col].blue -= row[col].blue;
        }
        numRows--;
        topSumBoundary++; // move top scope boundary lower
        rowCache.remove(0); // remove top row which is out of summing
        // scope
        cacheStartIndex++;
      }

      // calculate each column's RGB-averaged pixel
      for (int col = 0; col < newImageData.width; col++) {
        rowRGBAverages[col] = newImageData.palette.getPixel(
            new RGB(rowRGBSums[col].red / numRows,
                rowRGBSums[col].green / numRows,
                rowRGBSums[col].blue / numRows));
      }
      // replace original pixels
      newImageData.setPixels(0, targetRow, newImageData.width,
          rowRGBAverages, 0);
      targetRow++;
    }
    return newImageData;
  }

  /**
   * Average blurs a given row of image data. Returns the blurred row as a
   * matrix of separated RGB values.
   *
   * @param oldImageData
   *    the original image data.
   * @param row
   *    the current row.
   * @param radius
   *    the number of radius pixels to consider when blurring image.
   * @return
   *    the array of RGB color of pixels of the current row of the resulting
   *    image.
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-SimpleImageEffectsForSWT/index.html
   */
  private static RGB[] blurRow(final ImageData oldImageData,
      final int row, final int radius) {
    // resulting rgb averages
    final RGB[] rowRGBAverages = new RGB[oldImageData.width];
    final int[] lineData = new int[oldImageData.width];
    oldImageData.getPixels(0, row, oldImageData.width, lineData, 0);
    int r = 0, g = 0, b = 0; // sum red, green, and blue values separately for averaging
    int leftSumBoundary = 0; // beginning index of summed values scope
    int targetColumn = 0; // column of RGB average to be determined
    int rightSumBoundary = 0; // ending index of summed values scope
    int numCols = 0; // number of columns included in current summing scope
    RGB rgb;
    while (targetColumn < lineData.length) {
      if (rightSumBoundary < lineData.length) {
        // sum RGB values for each pixel in our radius scope
        do {
          rgb = oldImageData.palette.getRGB(lineData[rightSumBoundary]);
          r += rgb.red;
          g += rgb.green;
          b += rgb.blue;
          numCols++;
          rightSumBoundary++;
        } while (rightSumBoundary <= radius); // to initialize summing scope at start
      }
      // subtract sum of left pixel as summing scope moves right
      if ((targetColumn - leftSumBoundary) > radius) {
        rgb = oldImageData.palette.getRGB(lineData[leftSumBoundary]);
        r -= rgb.red;
        g -= rgb.green;
        b -= rgb.blue;
        numCols--;
        leftSumBoundary++;
      }
      // calculate RGB averages
      rowRGBAverages[targetColumn] = new RGB(r / numCols, g / numCols,
          b / numCols);
      targetColumn++;
    }
    return rowRGBAverages;
  }

  /**
   * Creates a glowing visual effect for an image.
   * <p>
   * This method will surround an existing image with a glowing border. This
   * glow is created by adding a solid colored border around an image. Alpha
   * values are then manipulated in order to blend the border with its
   * background. This gives a glowing appearance.
   * <p>
   * To obtain the alpha value of a glow pixel, its position in the border
   * radius as a percent of the radius' total width is first calculated. This
   * percentage is multiplied by the maximum opacity level, giving pixels an
   * outward linear blend from the image from opaque to transparent.
   * <p>
   * A highlight radius increases the intensity of a given radius of pixels
   * surrounding the image to better highlight it. When there is a highlight
   * radius, the entire glow's overall alpha blending is non-linear.
   *
   * @param oldImageData
   *          The original image. Transparency information will be ignored.
   * @param color
   *          The color of the glow.
   * @param radius
   *          The radius of the glow in pixels.
   * @param highlightRadius
   *          The radius of the highlight area.
   * @param opacity
   *          The opacity of the glow.
   * @return The glowing image. This image data will be larger than the
   *         original. The same image data will be returned if the glow radius
   *         is 0, or null if an error occurred.
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-SimpleImageEffectsForSWT/index.html
   */
  public static ImageData glow(ImageData oldImageData, Color color,
      int radius, int highlightRadius, int opacity) {
    if (oldImageData == null) {
      return null;
    }
    if (color == null) {
      return null;
    }
    if (radius == 0) {
      return oldImageData;
    }
    // the percent increase in color intensity in the highlight radius
    final double highlightRadiusIncrease;
    if (radius < (highlightRadius * 2)) {
      highlightRadiusIncrease = 0.15;
    } else if (radius < (highlightRadius * 3)) {
      highlightRadiusIncrease = 0.09;
    } else {
      highlightRadiusIncrease = 0.02;
    }
    if (opacity > 255) {
      opacity = 255;
    } else if (opacity < 0) {
      opacity = 0;
    }
    // prepare new image data with 24-bit direct palette to hold glowing copy of
    // image
    final ImageData newImageData = new ImageData(
        oldImageData.width + (radius * 2),
        oldImageData.height + (radius * 2),
        24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
    int[] pixels = new int[oldImageData.width];
    // copy image data
    for (int row = radius; row < (radius + oldImageData.height); row++) {
      oldImageData.getPixels(0, row - radius, oldImageData.width, pixels, 0);
      for (int col = 0; col < pixels.length; col++) {
        final RGB rgb = oldImageData.palette.getRGB(pixels[col]);
        pixels[col] = newImageData.palette.getPixel(rgb);
      }
      newImageData.setPixels(radius, row, oldImageData.width, pixels, 0);
    }
    // initialize glow pixel data
    final int colorInt = newImageData.palette.getPixel(color.getRGB());
    pixels = new int[newImageData.width];
    for (int i = 0; i < newImageData.width; i++) {
      pixels[i] = colorInt;
    }
    // initialize alpha values
    final byte[] alphas = new byte[newImageData.width];
    // deal with alpha values on rows above and below the photo
    for (int row = 0; row < newImageData.height; row++) {
      if (row < radius) {
        // only calculate alpha values for top border. they will reflect to the
        // bottom border
        final byte intensity = (byte) (opacity * ((((row + 1)) / (double) (radius))));
        for (int col = 0; col < ((alphas.length / 2) + (alphas.length % 2)); col++) {
          if (col < radius) {
            // deal with corners:
            // calculate pixel's distance from image corner
            final double hypotenuse = Math.sqrt(Math.pow(radius - col - 1, 2.0)
                + Math.pow(radius - 1 - row, 2.0));
            // calculate alpha based on percent distance from image
            alphas[col] = (byte)(opacity * Math.max(((radius - hypotenuse) / radius), 0));
            alphas[alphas.length - col - 1] = alphas[col];
            // add highlight radius
            if (hypotenuse < Math.min(highlightRadius, radius * .5)) {
              alphas[col]  = (byte) Math.min(255,
                  (alphas[col] & 0x0FF)
                  * (1 + (highlightRadiusIncrease
                            * Math.max(((radius - hypotenuse) / radius), 0)))
              );
              alphas[alphas.length - col - 1] = alphas[col];
            }
          } else {
            if (row > Math.max(radius - highlightRadius - 1, radius * .5)) {
              alphas[col] = (byte) Math.min(255,
                  (intensity & 0x0FF) * (1 + ((highlightRadiusIncrease * row) / radius)));
            } else {
              alphas[col] = intensity;
            }
            alphas[alphas.length - 1 - col] = alphas[col];
          }
        }
        newImageData.setAlphas(0, row, newImageData.width, alphas, 0);
        newImageData.setAlphas(0, newImageData.height - 1 - row,
            newImageData.width, alphas, 0);
        newImageData.setPixels(0, row, newImageData.width, pixels, 0);
        newImageData.setPixels(0, newImageData.height - 1 - row,
            newImageData.width, pixels, 0);
      }
      // deal with rows the image resides on
      else if (row <= (newImageData.height / 2)) {
        // calculate alpha values
        double intensity = 0;
        for (int col = 0; col < alphas.length; col++) {
          if (col < radius) {
            intensity = (opacity * ((col + 1) / (double) radius));
            if (col > Math.max(radius - highlightRadius - 1, radius * .5)) {
              intensity = Math.min(255,
                  (intensity) * (1 + ((highlightRadiusIncrease * col) / radius)));
            }
            alphas[col] = (byte) (intensity);
            alphas[newImageData.width - col - 1] = alphas[col];
          } else if (col <= ((newImageData.width / 2) + (newImageData.width % 2))) {
            // original image pixels are full opacity
            alphas[col] = alphas[newImageData.width - col - 1] = (byte) (255);
          }
        }
        newImageData.setPixels(0, row, radius, pixels, 0);
        newImageData.setPixels(oldImageData.width + radius, row, radius,
            pixels, 0);
        newImageData.setAlphas(0, row, newImageData.width, alphas, 0);
        newImageData.setPixels(0, newImageData.height - 1 - row, radius,
            pixels, 0);
        newImageData.setPixels(oldImageData.width + radius,
            newImageData.height - 1 - row, radius, pixels, 0);
        newImageData.setAlphas(0, newImageData.height - 1 - row,
            newImageData.width, alphas, 0);
      }
    }
    return newImageData;
  }

  /**
   * Creates a embossing visual effect for an image.
   *
   * @param oldImageData
   *          The image data to be embossed. Transparency information will be
   *          ignored.
   * @param grayLevel
   *          Minimum base level gray to use in embossed outcome.
   * @return An embossed copy of the image data.
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-SimpleImageEffectsForSWT/index.html
   */
  public static ImageData emboss(ImageData oldImageData, int grayLevel) {
    // prepare new image data with 24-bit direct palette to hold embossed copy
    // of image
    final ImageData newImageData = new ImageData(oldImageData.width,
        oldImageData.height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
    if (grayLevel < 0) {
      grayLevel = 0;
    } else if (grayLevel > 255) {
      grayLevel = 255;
    }
    int rDiff = 0, gDiff = 0, bDiff = 0, gray = 0; // store intensity differences
    final int[] rowResult = new int[newImageData.width];
    RGB[] rowRGBData1 = new RGB[newImageData.width];
    RGB[] rowRGBData2 = new RGB[newImageData.width];
    getRGBRowData(oldImageData, rowRGBData2, 0); // get first line of pixel data

    for (int row = 0; row < newImageData.height; row++) {
      final RGB[] tempRow = rowRGBData1;
      // swap references. the second row of pixel data now becomes the first
      if (row < (newImageData.height - 1)) {
        rowRGBData1 = rowRGBData2;
        rowRGBData2 = tempRow;
        getRGBRowData(oldImageData, rowRGBData2, row + 1);
      }
      for (int col = 0; col < newImageData.width; col++) {
        // for first two columns, compare the target pixel to the pixel above
        if (col < 2) {
          rDiff = Math.abs(rowRGBData2[col].red - rowRGBData1[col].red);
          gDiff = Math.abs(rowRGBData2[col].green - rowRGBData1[col].green);
          bDiff = Math.abs(rowRGBData2[col].blue - rowRGBData1[col].blue);
        } else {
          // for all columns after the second column, compare the target pixel
          // to the pixel two pixels to the left on the row above
          rDiff = Math.abs(rowRGBData2[col].red - rowRGBData1[col - 2].red);
          gDiff = Math.abs(rowRGBData2[col].green - rowRGBData1[col - 2].green);
          bDiff = Math.abs(rowRGBData2[col].blue - rowRGBData1[col - 2].blue);
        }
        // calculate gray level
        gray = Math.min(grayLevel + (Math.max(rDiff, Math.max(gDiff, bDiff))), 255);
        rowResult[col] = newImageData.palette.getPixel(new RGB(gray, gray, gray));
      }
      newImageData.setPixels(0, row, newImageData.width, rowResult, 0);
    }
    return newImageData;
  }

  /**
   * Gets a row of pixel data as RGB values.
   *
   * @param imageData
   *          The ImageData to retrieve pixel data from
   * @param resultData
   *          RGB array to store retrieved pixel data in
   * @param row
   *          Row of imageData required
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-SimpleImageEffectsForSWT/index.html
   */
  private static void getRGBRowData(ImageData originalImageData,
      RGB[] resultData, int row) {
    // assuming resultData.length == originalImageData.width
    final int[] pixels = new int[originalImageData.width];
    originalImageData.getPixels(0, row, originalImageData.width, pixels, 0);
    for (int col = 0; col < originalImageData.width; col++) {
      resultData[col] = originalImageData.palette.getRGB(pixels[col]);
    }
  }

  /**
   * Creates a drop shadow visual effect for an image.
   * <p>
   * This method will create a drop shadow to the bottom-right of an existing
   * image. This drop shadow is created by creating an altered one-sided glow,
   * and shifting its position around the image. See the Glow class for more
   * details of how the glow is calculated.
   *
   * @param oldImageData
   *          The original image. Transparency information will be ignored.
   * @param color
   *          The color of the drop shadow.
   * @param radius
   *          The radius of the drop shadow in pixels.
   * @param highlightRadius
   *          The radius of the highlight area.
   * @param opacity
   *          The opacity of the drop shadow.
   * @return The drop shadowed image. This image data will be larger than the
   *         original. The same image data will be returned if the shadow radius
   *         is 0, or null if an error occurred.
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-
   *      SimpleImageEffectsForSWT/index.html
   */
  public static ImageData dropShadow(ImageData oldImageData, Color color,
      int radius, int highlightRadius, int opacity) {
    if (oldImageData == null) {
      return null;
    }
    if (color == null) {
      return null;
    }
    if (radius == 0) {
      return oldImageData;
    }
    final int shift = (int) (radius * 1.5); // distance to shift "glow" from image
    // the percent increase in color intensity in the highlight radius
    final double highlightRadiusIncrease;
    if (radius < (highlightRadius * 2)) {
      highlightRadiusIncrease = 0.15;
    } else if (radius < (highlightRadius * 3)) {
      highlightRadiusIncrease = 0.09;
    } else {
      highlightRadiusIncrease = 0.02;
    }
    if (opacity > 255) {
      opacity = 255;
    } else if (opacity < 0) {
      opacity = 0;
    }
    // prepare new image data with 24-bit direct palette to hold shadowed copy
    // of image
    final ImageData newImageData = new ImageData(
        oldImageData.width + (radius * 2),
        oldImageData.height + (radius * 2),
        24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
    int[] pixels = new int[oldImageData.width];
    // copy image data
    for (int row = radius; row < (radius + oldImageData.height); row++) {
      oldImageData.getPixels(0, row - radius, oldImageData.width, pixels, 0);
      for (int col = 0; col < pixels.length; col++) {
        final RGB rgb = oldImageData.palette.getRGB(pixels[col]);
        pixels[col] = newImageData.palette.getPixel(rgb);
      }
      newImageData.setPixels(radius, row, oldImageData.width, pixels, 0);
    }
    // initialize glow pixel data
    final int colorInt = newImageData.palette.getPixel(color.getRGB());
    pixels = new int[newImageData.width];
    for (int i = 0; i < newImageData.width; i++) {
      pixels[i] = colorInt;
    }
    // initialize alpha values
    final byte[] alphas = new byte[newImageData.width];
    // deal with alpha values on rows above and below the photo
    for (int row = 0; row < newImageData.height; row++) {
      if (row < radius) {
        // only calculate alpha values for top border. they will reflect to the
        // bottom border
        final byte intensity = (byte) (opacity * ((((row + 1)) / (double) (radius))));
        for (int col = 0; col < ((alphas.length / 2) + (alphas.length % 2)); col++) {
          if (col < radius) {
            // deal with corners:
            // calculate pixel's distance from image corner
            final double hypotenuse = Math.sqrt(Math.pow(radius - col - 1, 2.0)
                + Math.pow(radius - 1 - row, 2.0));
            // calculate alpha based on percent distance from image
            byte v = (byte) (opacity * Math.max(((radius - hypotenuse) / radius), 0));
            alphas[col + shift] = alphas[alphas.length - col - 1] =  v;
            // add highlight radius
            if (hypotenuse < Math.min(highlightRadius, radius * .5)) {
              v = (byte) Math.min(255,
                      (alphas[col + shift] & 0x0FF)
                          * (1 + (highlightRadiusIncrease
                              * Math.max(((radius - hypotenuse) / radius), 0))));
              alphas[col + shift] = alphas[alphas.length - col - 1] = v;
            }
          } else {
            byte v = intensity;
            if (row > Math.max(radius - highlightRadius - 1, radius * .5)) {
              v = (byte) Math.min(255,
                  (intensity & 0x0FF) * (1 + ((highlightRadiusIncrease * row) / radius)));
            }
            alphas[col + shift] = alphas[alphas.length - col - 1] = v;
          }
        }
        if ((row + shift) < newImageData.height) {
          newImageData.setAlphas(newImageData.width - radius, row + shift,
              radius, alphas, alphas.length - radius);
          newImageData.setPixels(newImageData.width - radius, row + shift,
              radius, pixels, alphas.length - radius);
        }
        newImageData.setAlphas(0, newImageData.height - 1 - row,
            newImageData.width, alphas, 0);
        newImageData.setPixels(0, newImageData.height - 1 - row,
            newImageData.width, pixels, 0);
      } else if (row <= (newImageData.height / 2)) { // deal with rows the image resides on
        // calculate alpha values
        double intensity = 0;
        for (int col = 0; col < alphas.length; col++) {
          if (col < radius) {
            intensity = (opacity * ((col + 1) / (double) radius));
            if (col > Math.max(radius - highlightRadius - 1, radius * .5)) {
              intensity = Math.min(255, (intensity)
                  * (1 + ((highlightRadiusIncrease * col) / radius)));
            }
            alphas[newImageData.width - col - 1] = (byte) (int) (intensity);
            alphas[col] = 0;
          } else if (col <= ((newImageData.width / 2) + (newImageData.width % 2))) {
            // original image pixels are full opacity
            alphas[col] = alphas[newImageData.width - col - 1] = (byte) (255);
          }
        }
        newImageData.setPixels(0, newImageData.height - 1 - row, radius,
            pixels, 0);
        newImageData.setPixels(oldImageData.width + radius,
            newImageData.height - 1 - row, radius, pixels, 0);
        newImageData.setAlphas(0, newImageData.height - 1 - row,
            newImageData.width, alphas, 0);
        if (row >= (shift + radius)) {
          newImageData.setPixels(0, row, radius, pixels, 0);
          newImageData.setPixels(oldImageData.width + radius, row, radius,
              pixels, 0);
          newImageData.setAlphas(0, row, newImageData.width, alphas, 0);
        } else {
          newImageData.setPixels(0, row, radius, pixels, 0);
          newImageData
              .setAlphas(0, row, newImageData.width - radius, alphas, 0);
        }
      }
    }
    return newImageData;
  }




//  private static final int ALPHA_CONSTANT = 0x8F;


//  /**
//   * Draws a drop shadow to a rounded rectangle.
//   *
//   * @param gc
//   *    a graphic context.
//   * @param bounds
//   *    the bounds of a rounded rectangle.
//   * @param radius
//   *    the width and height of the arc of the round corner of the round
//   *    rectangle.
//   * @param xOffset
//   *    the X-offset of the shadow.
//   * @param yOffset
//   *    the Y-offset of the shadow.
//   * @author Andy Hawkes
//   * @see http://www.brickyardtech.com/blog/2011/04/swt-drop-shadow-effect/
//   */
//  public static void drawDropShadowToRoundRectangle(GC gc, Rectangle bounds,
//      int radius, int xOffset, int yOffset) {
//    gc.setAdvanced(true);
//    gc.setAntialias(SWT.ON);
//    gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
//    gc.setAlpha(ALPHA_CONSTANT / radius);
//    for (int i = 0; i < radius; i++) {
//      final Rectangle shadowBounds = new Rectangle(bounds.x + xOffset,
//          bounds.y + yOffset, bounds.width - i, bounds.height - i);
//      gc.fillRoundRectangle(shadowBounds.x, shadowBounds.y, shadowBounds.width,
//          shadowBounds.height, radius, radius);
//    }
//    gc.setAlpha(0xFF);
//  }

//
//  /**
//   * Draws a drop shadow to a rounded rectangle.
//   *
//   * @param gc
//   *    a graphic context.
//   * @param bounds
//   *    the bounds of a rounded rectangle.
//   * @param radius
//   *    the width and height of the arc of the round corner of the round
//   *    rectangle.
//   * @param xOffset
//   *    the X-offset of the shadow.
//   * @param yOffset
//   *    the Y-offset of the shadow.
//   * @param radius
//   *    the radius of the shadow.
//   * @author Haixing Hu
//   * @see http://www.brickyardtech.com/blog/2011/04/swt-drop-shadow-effect/
//   */
//  public static void drawDropShadowToPolygon(GC gc, int[] polygon, int xOffset,
//      int yOffset, int radius) {
//    gc.setAdvanced(true);
//    gc.setAntialias(SWT.ON);
//    gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
//    gc.setAlpha(ALPHA_CONSTANT / radius);
//    for (int i = 0; i < radius; i++) {
//      final Rectangle shadowBounds = new Rectangle(bounds.x + xOffset,
//          bounds.y + yOffset, bounds.width - i, bounds.height - i);
//      gc.fillRoundRectangle(shadowBounds.x, shadowBounds.y, shadowBounds.width,
//          shadowBounds.height, radius, radius);
//    }
//    gc.setAlpha(0xFF);
//  }


}
