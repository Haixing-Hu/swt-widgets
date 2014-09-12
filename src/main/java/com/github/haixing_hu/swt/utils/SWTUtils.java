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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Haixing Hu
 */
public final class SWTUtils {

  private static Logger LOGGER = LoggerFactory.getLogger(SWTUtils.class);

  private SWTUtils() {
    // empty
  }

  /**
   * Opens an URI with the operating system's default application.
   * <p>
   * If the URI is a URL, this function will launch the default browser to
   * display the webpage at the specified URL; if the URI has a "mailto:"
   * schema, this function will launch the mail composing window of the user
   * default mail client, filling the message fields specified by the
   * "mailto:" URI.
   *
   * @param uri
   *    A specified URI represented as a string.
   */
  public static void openUri(String uri) {
    try {
      openUri(new URI(uri));
    } catch (final URISyntaxException e) {
      LOGGER.error("Cannot open the invalid URI: {}", uri);
    }
  }

  /**
   * Opens an URI with the operating system's default application.
   * <p>
   * If the URI is a URL, this function will launch the default browser to
   * display the webpage at the specified URL; if the URI has a "mailto:"
   * schema, this function will launch the mail composing window of the user
   * default mail client, filling the message fields specified by the
   * "mailto:" URI.
   *
   * @param uri
   *    A specified URI.
   */
  public static void openUri(URI uri) {
    if (Desktop.isDesktopSupported()) {
      LOGGER.error("The java.awt.Desktop is not supported. Ignore the action.");
      return;
    }
    final Desktop desktop = Desktop.getDesktop();
    try {
      if ("mailto".equals(uri.getScheme())) {
        desktop.mail(uri);
      } else {
        desktop.browse(uri);
      }
    } catch (final IOException e) {
      LOGGER.error("An error occurred while opening the URI: {}", uri, e);
    }
  }

  /**
   * Apply a very basic pseudo-HTML formating to a text stored in a StyledText
   * widget. Supported tags are <b>, <i>, <u> , <COLOR>, <backgroundcolor>,
   * <size> and <BbrR/>
   *
   * @param styledText
   *          styled text that contains an HTML text
   */
  public static void applyHTMLFormating(final StyledText styledText) {
    try {
      new HTMLStyledTextParser(styledText).parse();
    } catch (final IOException e) {
      LOGGER.error("Failed to apply the HTML formating: {}", styledText, e);
    }
  }

  /**
   * Center a shell on the primary monitor
   *
   * @param shell
   *          shell to center
   */
  public static void centerShell(final Shell shell) {
    final Monitor primary = shell.getDisplay().getPrimaryMonitor();
    final Rectangle bounds = primary.getBounds();
    final Rectangle rect = shell.getBounds();
    final int x = bounds.x + ((bounds.width - rect.width) / 2);
    final int y = bounds.y + ((bounds.height - rect.height) / 2);
    shell.setLocation(x, y);
  }

  /**
   * Gets the bounds of the monitor on which the specified shell is running.
   *
   * @param shell
   *          a specified shell.
   * @return the bounds of the monitor on which the shell is running.
   */
  public static Rectangle getMonitorBounds(final Shell shell) {
    final Display display = shell.getDisplay();
    for (final Monitor monitor : display.getMonitors()) {
      final Rectangle monitorBounds = monitor.getBounds();
      final Rectangle shellBounds = shell.getBounds();
      if (monitorBounds.contains(shellBounds.x, shellBounds.y)) {
        return monitorBounds;
      }
    }
    final Monitor primary = display.getPrimaryMonitor();
    return primary.getBounds();
  }

  /**
   * Average blurs a given image data.
   *
   * @param oldImageData
   *          The ImageData to be average blurred. Transparency information will
   *          be ignored.
   * @param radius
   *          the number of radius pixels to consider when blurring image.
   * @return A blurred copy of the image data, or null if an error occurred.
   * @author Nicholas Rajendram
   * @see http://www.eclipse.org/articles/article.php?file=Article-
   *      SimpleImageEffectsForSWT/index.html
   */
  public static ImageData blur(final ImageData oldImageData, int radius) {
    if (radius < 1) {
      return oldImageData;
    }

    // prepare new image data with 24-bit direct palette to hold blurred copy of
    // image
    final ImageData newImageData = new ImageData(oldImageData.width,
        oldImageData.height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000));
    if ((radius >= newImageData.height) || (radius >= newImageData.width)) {
      radius = Math.min(newImageData.height, newImageData.width) - 1;
    }
    // initialize cache
    final ArrayList<RGB[]> rowCache = new ArrayList<RGB[]>();
    final int cacheSize = ((radius * 2) + 1) > newImageData.height ? newImageData.height
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
   */
  private static RGB[] blurRow(final ImageData originalImageData,
      final int row, final int radius) {
    // resulting rgb averages
    final RGB[] rowRGBAverages = new RGB[originalImageData.width];
    final int[] lineData = new int[originalImageData.width];
    originalImageData.getPixels(0, row, originalImageData.width, lineData, 0);
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
          rgb = originalImageData.palette.getRGB(lineData[rightSumBoundary]);
          r += rgb.red;
          g += rgb.green;
          b += rgb.blue;
          numCols++;
          rightSumBoundary++;
        } while (rightSumBoundary <= radius); // to initialize summing scope at start
      }
      // subtract sum of left pixel as summing scope moves right
      if ((targetColumn - leftSumBoundary) > radius) {
        rgb = originalImageData.palette.getRGB(lineData[leftSumBoundary]);
        r -= rgb.red;
        g -= rgb.green;
        b -= rgb.blue;
        numCols--;
        leftSumBoundary++;
      }
      // calculate RGB averages
      rowRGBAverages[targetColumn] = new RGB(r / numCols, g / numCols, b
          / numCols);
      targetColumn++;
    }
    return rowRGBAverages;
  }
}
