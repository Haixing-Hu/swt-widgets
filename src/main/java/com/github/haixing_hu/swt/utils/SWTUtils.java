/******************************************************************************
 *
 * Copyright (c) 2014  Haixing Hu
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Haixing Hu (starfish.hu at gmail dot com)  - Initial implementation and API
 *
 ******************************************************************************/

package com.github.haixing_hu.swt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide utility functions for SWT.
 *
 * @author Haixing Hu
 */
public class SWTUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SWTUtils.class);

  private static Map<String, Image> IMAGE_CACHE = new HashMap<String, Image>();

  /**
   * Loads an image from a path.
   *
   * @param path
   *          the path of the image.
   * @return the image loaded, or null if failed.
   */
  public static Image getImage(String path) {
    LOGGER.debug("Loading image: {}", path);
    try {
      Image image = IMAGE_CACHE.get(path);
      if (image == null) {
        final InputStream is = SWTUtils.class.getResourceAsStream(path);
        image = getImage(is);
        if (image == null) {
          LOGGER.error("Failed to load the image: {}", path);
          return null;
        } else {
          IMAGE_CACHE.put(path, image);
        }
      }
      return image;
    } catch (final Exception e) {
      LOGGER.error("Failed to load the image: {}", path, e);
      return null;
    }
  }

  private static Image getImage(InputStream stream) throws IOException {
    if (stream == null) {
      return null;
    }
    try {
      final Display display = Display.getCurrent();
      final ImageData data = new ImageData(stream);
      if (data.transparentPixel > 0) {
        return new Image(display, data, data.getTransparencyMask());
      }
      return new Image(display, data);
    } finally {
      stream.close();
    }
  }

  /**
   * Sets the height of an existing SWT font.
   *
   * @param font
   *          an existing SWT font.
   * @param height
   *          the new height to be set.
   * @return a new SWT font, cloned from the existing font, but with the new
   *         height.
   */
  public static Font setFontHeight(Font font, int height) {
    final FontData[] fontData = font.getFontData();
    for (final FontData element : fontData) {
        element.setHeight(height);
    }
    return new Font(font.getDevice(), fontData);
  }

  /**
   * Sets the style of an existing SWT font.
   *
   * @param font
   *          an existing SWT font.
   * @param style
   *          the new style to be set.
   * @return a new SWT font, cloned from the existing font, but with the new
   *         style.
   */
  public static Font setFontStyle(Font font, int style) {
    final FontData[] fontData = font.getFontData();
    for (final FontData element : fontData) {
        element.setStyle(style);
    }
    return new Font(font.getDevice(), fontData);
  }

  /**
   * Sets the style and height of an existing SWT font.
   *
   * @param font
   *          an existing SWT font.
   * @param style
   *          the new style to be set.
   * @param height
   *          the new height to be set.
   * @return a new SWT font, cloned from the existing font, but with the new
   *         style and the new height.
   */
  public static Font setFontStyleAndHeight(Font font, int style, int height) {
    final FontData[] fontData = font.getFontData();
    for (final FontData element : fontData) {
        element.setStyle(style);
        element.setHeight(height);
    }
    return new Font(font.getDevice(), fontData);
  }

  private static final Pattern RGB_PATTERN =
      Pattern.compile("#([0-9a-fA-F][0-9a-fA-F])([0-9a-fA-F][0-9a-fA-F])([0-9a-fA-F][0-9a-fA-F])");

  /**
   * Parses a color from a string representation of an RGB color.
   * <p>
   * The string representation of an RGB color has the form: "#ebebeb",
   * where the RGB values are represented in HEX numbers.
   *
   * @param rgb
   *    The string representation of an RGB color.
   * @return
   *    The {@link Color} object corresponding to the RGB color.
   */
  public static Color parseRGB(String rgb) {
    final Matcher matcher = RGB_PATTERN.matcher(rgb);
    if (matcher.matches()) {
      final int r = Integer.valueOf(matcher.group(1), 16);
      final int g = Integer.valueOf(matcher.group(2), 16);
      final int b = Integer.valueOf(matcher.group(3), 16);
      return SWTResourceManager.getColor(r, g, b);
    } else {
      LOGGER.error("Failed to parse the RGB color: {}", rgb);
      return null;
    }
  }
}
