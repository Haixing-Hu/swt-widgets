/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *    Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing OS resources associated with SWT controls such as
 * colors, fonts, images, etc.
 * <p>
 * !!! IMPORTANT !!! Application code must explicitly invoke the
 * <code>dispose()</code> method to release the operating system resources
 * managed by cached objects when those objects and OS resources are no longer
 * needed (e.g. on application shutdown)
 * <p>
 * This class may be freely distributed as part of any application or plugin.
 * <p>
 *
 * @author scheglov_ke
 * @author Dan Rubel
 * @author Haixing Hu
 */
public class SWTResourceManager {

  private static Logger LOGGER = LoggerFactory
      .getLogger(SWTResourceManager.class);

  // //////////////////////////////////////////////////////////////////////////
  //
  // Color
  //
  // //////////////////////////////////////////////////////////////////////////
  private static Map<RGB, Color> colorCache = new HashMap<RGB, Color>();

  private static final int MISSING_IMAGE_SIZE = 10;

  /**
   * Gets the system {@link Color} matching the specific ID.
   *
   * @param systemColorID
   *          the ID value for the color
   * @return the system {@link Color} matching the specific ID
   */
  public static Color getColor(int systemColorID) {
    final Display display = Display.getCurrent();
    return display.getSystemColor(systemColorID);
  }

  /**
   * Gets the system {@link Color} matching the specific ID.
   *
   * @param display
   *          a specified display.
   * @param systemColorID
   *          the ID value for the color
   * @return the system {@link Color} matching the specific ID
   */
  public static Color getColor(Display display, int systemColorID) {
    return display.getSystemColor(systemColorID);
  }

  /**
   * Gets a {@link Color} given its red, green and blue component values.
   *
   * @param r
   *          the red component of the color
   * @param g
   *          the green component of the color
   * @param b
   *          the blue component of the color
   * @return the {@link Color} matching the given red, green and blue component
   *         values
   */
  public static Color getColor(int r, int g, int b) {
    return getColor(new RGB(r, g, b));
  }

  /**
   * Gets a {@link Color} given its red, green and blue component values.
   *
   * @param display
   *          the specified display.
   * @param r
   *          the red component of the color
   * @param g
   *          the green component of the color
   * @param b
   *          the blue component of the color
   * @return the {@link Color} matching the given red, green and blue component
   *         values
   */
  public static Color getColor(Display display, int r, int g, int b) {
    return getColor(display, new RGB(r, g, b));
  }

  /**
   * Gets a {@link Color} given its RGB value.
   *
   * @param rgb
   *          the {@link RGB} value of the color
   * @return the {@link Color} matching the RGB value
   */
  public static Color getColor(RGB rgb) {
    return getColor(Display.getCurrent(), rgb);
  }

  /**
   * Gets a {@link Color} given its RGB value.
   *
   * @param display
   *          the specified display.
   * @param rgb
   *          the {@link RGB} value of the color
   * @return the {@link Color} matching the RGB value
   */
  public static Color getColor(Display display, RGB rgb) {
    Color color = colorCache.get(rgb);
    if (color == null) {
      color = new Color(display, rgb);
      colorCache.put(rgb, color);
    }
    return color;
  }

  private static final Pattern RGB_PATTERN = Pattern
      .compile("#([0-9a-fA-F][0-9a-fA-F])([0-9a-fA-F][0-9a-fA-F])"
          + "([0-9a-fA-F][0-9a-fA-F])");

  /**
   * Parses a color from a string representation of an RGB color.
   * <p>
   * The string representation of an RGB color has the form: "#ebebeb", where
   * the RGB values are represented in HEX numbers.
   *
   * @param rgbstr
   *          The string representation of an RGB color.
   * @return The {@link Color} object corresponding to the RGB color, or null if
   *         the RGB string is not valid.
   */
  public static Color getColor(String rgbstr) {
    final RGB rgb = parseRGB(rgbstr);
    if (rgb != null) {
      return getColor(rgb);
    } else {
      return null;
    }
  }

  /**
   * Parses a color from a string representation of an RGB color.
   * <p>
   * The string representation of an RGB color has the form: "#ebebeb", where
   * the RGB values are represented in HEX numbers.
   *
   * @param rgbstr
   *          The string representation of an RGB color.
   * @return The {@link Color} object corresponding to the RGB color, or null if
   *         the RGB string is not valid.
   */
  public static Color getColor(Display display, String rgbstr) {
    final RGB rgb = parseRGB(rgbstr);
    if (rgb != null) {
      return getColor(display, rgb);
    } else {
      return null;
    }
  }

  private static RGB parseRGB(String rgbstr) {
    final Matcher matcher = RGB_PATTERN.matcher(rgbstr);
    if (matcher.matches()) {
      final int r = Integer.valueOf(matcher.group(1), 16);
      final int g = Integer.valueOf(matcher.group(2), 16);
      final int b = Integer.valueOf(matcher.group(3), 16);
      return new RGB(r, g, b);
    } else {
      LOGGER.error("Failed to parse the RGB color: {}", rgbstr);
      return null;
    }
  }

  /**
   * Dispose of all the cached {@link Color}'s.
   */
  public static void disposeColors() {
    for (final Color color : colorCache.values()) {
      color.dispose();
    }
    colorCache.clear();
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Image
  //
  // //////////////////////////////////////////////////////////////////////////
  /**
   * Maps image paths to images.
   */
  private static Map<String, Image> imageCache = new HashMap<String, Image>();

  /**
   * Gets an {@link Image} encoded by the specified {@link InputStream}.
   *
   * @param display
   *
   * @param stream
   *          the {@link InputStream} encoding the image data
   * @return the {@link Image} encoded by the specified input stream
   */
  protected static Image getImage(Display display, InputStream stream)
        throws IOException {
    try {
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
   * Gets an {@link Image} stored in the file at the specified path.
   *
   * @param path
   *          the path to the image file
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(String path) {
    return getImage(Display.getCurrent(), path);
  }

  /**
   * Gets an {@link Image} stored in the file at the specified path.
   *
   * @param display
   *          a specified display.
   * @param path
   *          the path to the image file
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(Display display, String path) {
    Image image = imageCache.get(path);
    if (image == null) {
      try {
        image = getImage(display, new FileInputStream(path));
        imageCache.put(path, image);
      } catch (final Exception e) {
        LOGGER.error("Failed to load the image from path: {}", path, e);
        image = getMissingImage();
        imageCache.put(path, image);
      }
    }
    return image;
  }


  /**
   * Gets an {@link Image} stored in the file at the specified path relative
   * to the specified class.
   *
   * @param clazz
   *          the {@link Class} relative to which to find the image
   * @param path
   *          the path to the image file, if starts with <code>'/'</code>
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(Class<?> clazz, String path) {
    return getImage(Display.getCurrent(), clazz, path);
  }

  /**
   * Gets an {@link Image} stored in the file at the specified path relative
   * to the specified class.
   *
   * @param display
   *          a specified display.
   * @param clazz
   *          the {@link Class} relative to which to find the image
   * @param path
   *          the path to the image file, if starts with <code>'/'</code>
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(Display display, Class<?> clazz, String path) {
    final String key = clazz.getName() + '|' + path;
    Image image = imageCache.get(key);
    if (image == null) {
      try {
        image = getImage(display, clazz.getResourceAsStream(path));
        imageCache.put(key, image);
      } catch (final Exception e) {
        LOGGER.error("Failed to load the image from resource: {}", path, e);
        image = getMissingImage();
        imageCache.put(key, image);
      }
    }
    return image;
  }

  /**
   * Gets the placeholder image for missing image with the default width and
   * height.
   *
   * @return the {@link Image} that can be used as placeholder for missing
   *         image. Note that the returned image is not cached by the resource
   *         manager, therefore it should be disposed by the function caller.
   */
  public static Image getMissingImage() {
    return getMissingImage(MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
  }

  /**
   * Gets the placeholder image for missing image.
   *
   * @param width
   *          the width of the missing image.
   * @param height
   *          the height of the missing image.
   * @return the {@link Image} that can be used as placeholder for missing
   *         image. Note that the returned image is not cached by the resource
   *         manager, therefore it should be disposed by the function caller.
   */
  public static Image getMissingImage(int width, int height) {
    final Image image = new Image(Display.getCurrent(), width, height);
    final GC gc = new GC(image);
    gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
    gc.dispose();
    return image;
  }

  /**
   * Style constant for placing decorator image in top left corner of base
   * image.
   */
  public static final int TOP_LEFT = 1;
  /**
   * Style constant for placing decorator image in top right corner of base
   * image.
   */
  public static final int TOP_RIGHT = 2;
  /**
   * Style constant for placing decorator image in bottom left corner of base
   * image.
   */
  public static final int BOTTOM_LEFT = 3;
  /**
   * Style constant for placing decorator image in bottom right corner of base
   * image.
   */
  public static final int BOTTOM_RIGHT = 4;
  /**
   * Internal value.
   */
  protected static final int LAST_CORNER_KEY = 5;
  /**
   * Maps images to decorated images.
   */
  @SuppressWarnings("unchecked")
  private static Map<Image, Map<Image, Image>>[] m_decoratedImageMap = new Map[LAST_CORNER_KEY];

  /**
   * Gets an {@link Image} composed of a base image decorated by another
   * image.
   *
   * @param baseImage
   *          the base {@link Image} that should be decorated
   * @param decorator
   *          the {@link Image} to decorate the base image
   * @return {@link Image} The resulting decorated image
   */
  public static Image decorateImage(Image baseImage, Image decorator) {
    return decorateImage(baseImage, decorator, BOTTOM_RIGHT);
  }

  /**
   * Gets an {@link Image} composed of a base image decorated by another
   * image.
   *
   * @param display
   *          a specified display.
   * @param baseImage
   *          the base {@link Image} that should be decorated
   * @param decorator
   *          the {@link Image} to decorate the base image
   * @return {@link Image} The resulting decorated image
   */
  public static Image decorateImage(Display display, Image baseImage, Image decorator) {
    return decorateImage(display, baseImage, decorator, BOTTOM_RIGHT);
  }

  /**
   * Gets an {@link Image} composed of a base image decorated by another
   * image.
   *
   * @param baseImage
   *          the base {@link Image} that should be decorated
   * @param decorator
   *          the {@link Image} to decorate the base image
   * @param corner
   *          the corner to place decorator image
   * @return the resulting decorated {@link Image}
   */
  public static Image decorateImage(final Image baseImage,
      final Image decorator, final int corner) {
    return decorateImage(Display.getCurrent(), baseImage, decorator, corner);
  }

  /**
   * Gets an {@link Image} composed of a base image decorated by another
   * image.
   *
   * @param display
   *          a specified display.
   * @param baseImage
   *          the base {@link Image} that should be decorated
   * @param decorator
   *          the {@link Image} to decorate the base image
   * @param corner
   *          the corner to place decorator image
   * @return the resulting decorated {@link Image}
   */
  public static Image decorateImage(Display display, final Image baseImage,
      final Image decorator, final int corner) {
    if ((corner <= 0) || (corner >= LAST_CORNER_KEY)) {
      throw new IllegalArgumentException("Wrong decorate corner");
    }
    Map<Image, Map<Image, Image>> cornerDecoratedImageMap = m_decoratedImageMap[corner];
    if (cornerDecoratedImageMap == null) {
      cornerDecoratedImageMap = new HashMap<Image, Map<Image, Image>>();
      m_decoratedImageMap[corner] = cornerDecoratedImageMap;
    }
    Map<Image, Image> decoratedMap = cornerDecoratedImageMap.get(baseImage);
    if (decoratedMap == null) {
      decoratedMap = new HashMap<Image, Image>();
      cornerDecoratedImageMap.put(baseImage, decoratedMap);
    }
    Image result = decoratedMap.get(decorator);
    if (result == null) {
      final Rectangle bib = baseImage.getBounds();
      final Rectangle dib = decorator.getBounds();
      result = new Image(display, bib.width, bib.height);
      final GC gc = new GC(result);
      gc.drawImage(baseImage, 0, 0);
      if (corner == TOP_LEFT) {
        gc.drawImage(decorator, 0, 0);
      } else if (corner == TOP_RIGHT) {
        gc.drawImage(decorator, bib.width - dib.width, 0);
      } else if (corner == BOTTOM_LEFT) {
        gc.drawImage(decorator, 0, bib.height - dib.height);
      } else if (corner == BOTTOM_RIGHT) {
        gc.drawImage(decorator, bib.width - dib.width, bib.height - dib.height);
      }
      gc.dispose();
      decoratedMap.put(decorator, result);
    }
    return result;
  }

  /**
   * Dispose all of the cached {@link Image}'s.
   */
  public static void disposeImages() {
    // dispose loaded images
    {
      for (final Image image : imageCache.values()) {
        image.dispose();
      }
      imageCache.clear();
    }
    // dispose decorated images
    for (int i = 0; i < m_decoratedImageMap.length; i++) {
      final Map<Image, Map<Image, Image>> cornerDecoratedImageMap = m_decoratedImageMap[i];
      if (cornerDecoratedImageMap != null) {
        for (final Map<Image, Image> decoratedMap : cornerDecoratedImageMap
            .values()) {
          for (final Image image : decoratedMap.values()) {
            image.dispose();
          }
          decoratedMap.clear();
        }
        cornerDecoratedImageMap.clear();
      }
    }
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Font
  //
  // //////////////////////////////////////////////////////////////////////////

  private static final Map<String, Font> fontCache = new HashMap<String, Font>();

  /**
   * The font style constant indicating an strikeout font (value is 1&lt;&lt;2).
   */
  public static final int STRIKEOUT = 1 << 2;

  /**
   * The font style constant indicating an underline font (value is 1&lt;&lt;3).
   */
  public static final int UNDERLINE = 1 << 3;

  private static String getFontName(String name, int size, int style) {
    final String fontName = name + '|' + size + '|' + style;
    return fontName;
  }

  /**
   * Gets a {@link Font} based on its name, height and style.
   *
   * @param name
   *          the name of the font
   * @param height
   *          the height of the font
   * @param style
   *          the style of the font
   * @return {@link Font} The font matching the name, height and style
   */
  public static Font getFont(String name, int height, int style) {
    return getFont(Display.getCurrent(), name, height, style);
  }

  /**
   * Gets a {@link Font} based on its name, height and style.
   *
   * @param display
   *          a specified display.
   * @param name
   *          the name of the font
   * @param height
   *          the height of the font
   * @param style
   *          the style of the font
   * @return {@link Font} The font matching the name, height and style
   */
  public static Font getFont(Display display, String name, int height, int style) {
    return getFont(display, name, height, style, false, false);
  }

  /**
   * Gets a {@link Font} based on the name of an existing font, height and
   * style.
   *
   * @param baseFont
   *          the base font of the new font.
   * @param size
   *          the size of the font
   * @param style
   *          the style of the font
   * @return {@link Font} The font matching the name, height, style, strikeout
   *         and underline
   */
  public static Font getFont(Font baseFont, int size, int style) {
    return getFont(Display.getCurrent(), baseFont, size, style);
  }

  /**
   * Gets a {@link Font} based on the name of an existing font, height and
   * style.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the base font of the new font.
   * @param size
   *          the size of the font
   * @param style
   *          the style of the font
   * @return {@link Font} The font matching the name, height, style, strikeout
   *         and underline
   */
  public static Font getFont(Display display, Font baseFont, int size, int style) {
    return getFont(display, baseFont, size, style, false, false);
  }

  /**
   * Gets a {@link Font} based on its name, height and style. Windows-specific
   * strikeout and underline flags are also supported.
   *
   * @param name
   *          the name of the font
   * @param size
   *          the size of the font
   * @param style
   *          the style of the font
   * @param strikeout
   *          the strikeout flag (warning: Windows only)
   * @param underline
   *          the underline flag (warning: Windows only)
   * @return {@link Font} The font matching the name, height, style, strikeout
   *         and underline
   */
  public static Font getFont(String name, int size, int style,
      boolean strikeout, boolean underline) {
    return getFont(Display.getCurrent(), name, size, style, strikeout,
        underline);
  }

  /**
   * Gets a {@link Font} based on its name, height and style. Windows-specific
   * strikeout and underline flags are also supported.
   *
   * @param display
   *          a specified display.
   * @param name
   *          the name of the font
   * @param size
   *          the size of the font
   * @param style
   *          the style of the font
   * @param strikeout
   *          the strikeout flag (warning: Windows only)
   * @param underline
   *          the underline flag (warning: Windows only)
   * @return {@link Font} The font matching the name, height, style, strikeout
   *         and underline
   */
  public static Font getFont(Display display, String name, int size, int style,
      boolean strikeout, boolean underline) {
    if (strikeout) {
      style |= STRIKEOUT;
    }
    if (underline) {
      style |= UNDERLINE;
    }
    final String fontName = getFontName(name, size, style);
    Font font = fontCache.get(fontName);
    if (font == null) {
      final FontData fontData = new FontData(name, size, style);
      if (strikeout || underline) {
        try {
          final Class<?> logFontClass = Class
              .forName("org.eclipse.swt.internal.win32.LOGFONT");
          final Object logFont = FontData.class.getField("data").get(fontData);
          if ((logFont != null) && (logFontClass != null)) {
            if (strikeout) {
              logFontClass.getField("lfStrikeOut").set(logFont,
                  Byte.valueOf((byte) 1));
            }
            if (underline) {
              logFontClass.getField("lfUnderline").set(logFont,
                  Byte.valueOf((byte) 1));
            }
          }
        } catch (final Throwable e) {
          LOGGER.error("Unable to set underline or strikeout"
              + " (probably on a non-Windows platform). ", e);
        }
      }
      font = new Font(display, fontData);
      fontCache.put(fontName, font);
    }
    return font;
  }

  /**
   * Gets a {@link Font} based on the name of an existing font, height and
   * style. Windows-specific strikeout and underline flags are also supported.
   *
   * @param baseFont
   *          the base font of the new font.
   * @param size
   *          the size of the font
   * @param style
   *          the style of the font
   * @param strikeout
   *          the strikeout flag (warning: Windows only)
   * @param underline
   *          the underline flag (warning: Windows only)
   * @return {@link Font} The font matching the name, height, style, strikeout
   *         and underline
   */
  public static Font getFont(Font baseFont, int size, int style,
      boolean strikeout, boolean underline) {
    return getFont(Display.getCurrent(), baseFont, size, style, strikeout,
        underline);
  }

  /**
   * Gets a {@link Font} based on the name of an existing font, height and
   * style. Windows-specific strikeout and underline flags are also supported.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the base font of the new font.
   * @param size
   *          the size of the font
   * @param style
   *          the style of the font
   * @param strikeout
   *          the strikeout flag (warning: Windows only)
   * @param underline
   *          the underline flag (warning: Windows only)
   * @return {@link Font} The font matching the name, height, style, strikeout
   *         and underline
   */
  public static Font getFont(Display display, Font baseFont, int size,
      int style, boolean strikeout, boolean underline) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    return getFont(display, name, size, style, strikeout, underline);
  }

  /**
   * Gets a bold version of the given {@link Font}.
   *
   * @param baseFont
   *          the {@link Font} for which a bold version is desired.
   * @return the bold version of the given {@link Font}.
   */
  public static Font getBoldFont(Font baseFont) {
    return getBoldFont(Display.getCurrent(), baseFont);
  }

  /**
   * Gets a bold version of the given {@link Font}.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} for which a bold version is desired.
   * @return the bold version of the given {@link Font}.
   */
  public static Font getBoldFont(Display display, Font baseFont) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    final int style = data.getStyle();
    return getFont(display, name, height, style | SWT.BOLD,
        (style & STRIKEOUT) != 0,
        (style & UNDERLINE) != 0);
  }

  /**
   * Gets a italic version of the given {@link Font}.
   *
   * @param baseFont
   *          the {@link Font} for which a bold version is desired.
   * @return the italic version of the given {@link Font}.
   */
  public static Font getItalicFont(Font baseFont) {
    return getItalicFont(Display.getCurrent(), baseFont);
  }

  /**
   * Gets a italic version of the given {@link Font}.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} for which a bold version is desired.
   * @return the italic version of the given {@link Font}.
   */
  public static Font getItalicFont(Display display, Font baseFont) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    final int style = data.getStyle();
    return getFont(display, name, height, style | SWT.ITALIC,
        (style & STRIKEOUT) != 0,
        (style & UNDERLINE) != 0);
  }

  /**
   * Gets a normal version of the given {@link Font}.
   *
   * @param baseFont
   *          the {@link Font} for which a normal version is desired.
   * @return the normal version of the given {@link Font}.
   */
  public static Font getNormalFont(Font baseFont) {
    return getNormalFont(Display.getCurrent(), baseFont);
  }

  /**
   * Gets a normal version of the given {@link Font}.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} for which a normal version is desired.
   * @return the normal version of the given {@link Font}.
   */
  public static Font getNormalFont(Display display, Font baseFont) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    final int style = data.getStyle();
    return getFont(display, name, height, SWT.NORMAL,
        (style & STRIKEOUT) != 0,
        (style & UNDERLINE) != 0);
  }

  /**
   * Changes the style of a given {@link Font} and returns a new font.
   *
   * @param baseFont
   *          the {@link Font} based on which the new font is created.
   * @param style
   *          the style of the new font.
   * @param strikeout
   *          the strikeout flag (warning: Windows only) of the new font.
   * @param underline
   *          the underline flag (warning: Windows only) of the new font.
   * @return the new font based on the given {@link Font} with the specified
   *         style.
   */
  public static Font changeFontStyle(Font baseFont, int style,
      boolean strikeout, boolean underline) {
    return changeFontStyle(Display.getCurrent(), baseFont, style,
        strikeout, underline);
  }

  /**
   * Changes the style of a given {@link Font} and returns a new font.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} based on which the new font is created.
   * @param style
   *          the style of the new font.
   * @param strikeout
   *          the strikeout flag (warning: Windows only) of the new font.
   * @param underline
   *          the underline flag (warning: Windows only) of the new font.
   * @return the new font based on the given {@link Font} with the specified
   *         style.
   */
  public static Font changeFontStyle(Display display, Font baseFont, int style,
      boolean strikeout, boolean underline) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    return getFont(display, name, height, style, strikeout, underline);
  }

  /**
   * Changes the size of a given {@link Font} and returns a new font.
   *
   * @param baseFont
   *          the {@link Font} whose size is to be changed.
   * @param newSize
   *          the new size.
   * @return a new {@link Font} object whose name and style is the same as the
   *         base fond and whose size is the specified new size.
   */
  public static Font changeFontSize(Font baseFont, int newSize) {
    return changeFontSize(Display.getCurrent(), baseFont, newSize);
  }

  /**
   * Changes the size of a given {@link Font} and returns a new font.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} whose size is to be changed.
   * @param newSize
   *          the new size.
   * @return a new {@link Font} object whose name and style is the same as the
   *         base fond and whose size is the specified new size.
   */
  public static Font changeFontSize(Display display, Font baseFont, int newSize) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int style = data.getStyle();
    return getFont(display, name, newSize, style, (style & STRIKEOUT) != 0,
        (style & UNDERLINE) != 0);
  }

  /**
   * Adjusts the size of a given {@link Font} and returns a new font.
   *
   * @param baseFont
   *          the {@link Font} whose size is to be changed.
   * @param sizeDiff
   *          the difference between the new size and the old size, that is, the
   *          new size of the new font should be (oldSize + sizeDiff). Note that
   *          this argument could be negative.
   * @return a new {@link Font} object whose name and style is the same as the
   *         base fond and whose size is the specified new size.
   */
  public static Font adjustFontSize(Font baseFont, int sizeDiff) {
    return adjustFontSize(Display.getCurrent(), baseFont, sizeDiff);
  }

  /**
   * Adjusts the size of a given {@link Font} and returns a new font.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} whose size is to be changed.
   * @param sizeDiff
   *          the difference between the new size and the old size, that is, the
   *          new size of the new font should be (oldSize + sizeDiff). Note that
   *          this argument could be negative.
   * @return a new {@link Font} object whose name and style is the same as the
   *         base fond and whose size is the specified new size.
   */
  public static Font adjustFontSize(Display display, Font baseFont, int sizeDiff) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int size = data.getHeight();
    final int style = data.getStyle();
    return getFont(display, name, size + sizeDiff, style, (style & STRIKEOUT) != 0,
        (style & UNDERLINE) != 0);
  }


  /**
   * Adjusts the size and style of a given {@link Font} and returns a new font.
   *
   * @param baseFont
   *          the {@link Font} whose size is to be changed.
   * @param sizeDiff
   *          the difference between the new size and the old size, that is, the
   *          new size of the new font should be (oldSize + sizeDiff). Note that
   *          this argument could be negative.
   * @param style
   *          the style of the new font.
   * @param strikeout
   *          the strikeout flag of the new font (warning: Windows only).
   * @param underline
   *          the underline flag of the new font (warning: Windows only).
   * @return a new {@link Font} object whose name and style is the same as the
   *         base fond and whose size is the specified new size.
   */
  public static Font adjustFont(Font baseFont, int sizeDiff, int style,
      boolean strikeout, boolean underline) {
    return adjustFont(Display.getCurrent(), baseFont, sizeDiff, style,
        strikeout, underline);
  }

  /**
   * Adjusts the size of a given {@link Font} and returns a new font.
   *
   * @param display
   *          a specified display.
   * @param baseFont
   *          the {@link Font} whose size is to be changed.
   * @param sizeDiff
   *          the difference between the new size and the old size, that is, the
   *          new size of the new font should be (oldSize + sizeDiff). Note that
   *          this argument could be negative.
   * @param style
   *          the style of the new font.
   * @param strikeout
   *          the strikeout flag of the new font (warning: Windows only).
   * @param underline
   *          the underline flag of the new font (warning: Windows only).
   * @return a new {@link Font} object whose name and style is the same as the
   *         base fond and whose size is the specified new size.
   */
  public static Font adjustFont(Display display, Font baseFont, int sizeDiff,
      int style, boolean strikeout, boolean underline) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int size = data.getHeight();
    return getFont(display, name, size + sizeDiff, style, strikeout, underline);
  }

  /**
   * Dispose all of the cached {@link Font}'s.
   */
  public static void disposeFonts() {
    // clear fonts
    for (final Font font : fontCache.values()) {
      font.dispose();
    }
    fontCache.clear();
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Cursor
  //
  // //////////////////////////////////////////////////////////////////////////

  /**
   * Maps IDs to cursors.
   */
  private static Map<Integer, Cursor> cursorCache = new HashMap<Integer, Cursor>();

  /**
   * Gets the system cursor matching the specific ID.
   *
   * @param id
   *          int The ID value for the cursor
   * @return Cursor The system cursor matching the specific ID
   */
  public static Cursor getCursor(int id) {
    return getCursor(Display.getCurrent(), id);
  }

  /**
   * Gets the system cursor matching the specific ID.
   *
   * @param display
   *          a specified display.
   * @param id
   *          int The ID value for the cursor
   * @return Cursor The system cursor matching the specific ID
   */
  public static Cursor getCursor(final Display display, int id) {
    final Integer key = Integer.valueOf(id);
    Cursor cursor = cursorCache.get(key);
    if (cursor == null) {
      cursor = new Cursor(display, id);
      cursorCache.put(key, cursor);
    }
    return cursor;
  }

  /**
   * Dispose all of the cached cursors.
   */
  public static void disposeCursors() {
    for (final Cursor cursor : cursorCache.values()) {
      cursor.dispose();
    }
    cursorCache.clear();
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // General
  //
  // //////////////////////////////////////////////////////////////////////////

  /**
   * Dispose of cached objects and their underlying OS resources. This should
   * only be called when the cached objects are no longer needed (e.g. on
   * application shutdown).
   */
  public static void dispose() {
    disposeColors();
    disposeImages();
    disposeFonts();
    disposeCursors();
  }
}