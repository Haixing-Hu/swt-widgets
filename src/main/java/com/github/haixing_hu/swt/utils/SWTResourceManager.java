/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *    Haixing Hu (starfish.hu at gmail dot com)  - Modification for personal use.
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

  private static Logger LOGGER = LoggerFactory.getLogger(SWTResourceManager.class);

  // //////////////////////////////////////////////////////////////////////////
  //
  // Color
  //
  // //////////////////////////////////////////////////////////////////////////
  private static Map<RGB, Color> m_colorMap = new HashMap<RGB, Color>();

  /**
   * Returns the system {@link Color} matching the specific ID.
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
   * Returns a {@link Color} given its red, green and blue component values.
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
   * Returns a {@link Color} given its RGB value.
   *
   * @param rgb
   *          the {@link RGB} value of the color
   * @return the {@link Color} matching the RGB value
   */
  public static Color getColor(RGB rgb) {
    Color color = m_colorMap.get(rgb);
    if (color == null) {
      final Display display = Display.getCurrent();
      color = new Color(display, rgb);
      m_colorMap.put(rgb, color);
    }
    return color;
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

  /**
   * Dispose of all the cached {@link Color}'s.
   */
  public static void disposeColors() {
    for (final Color color : m_colorMap.values()) {
      color.dispose();
    }
    m_colorMap.clear();
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Image
  //
  // //////////////////////////////////////////////////////////////////////////
  /**
   * Maps image paths to images.
   */
  private static Map<String, Image> m_imageMap = new HashMap<String, Image>();

  /**
   * Returns an {@link Image} encoded by the specified {@link InputStream}.
   *
   * @param stream
   *          the {@link InputStream} encoding the image data
   * @return the {@link Image} encoded by the specified input stream
   */
  protected static Image getImage(InputStream stream) throws IOException {
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
   * Returns an {@link Image} stored in the file at the specified path.
   *
   * @param path
   *          the path to the image file
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(String path) {
    Image image = m_imageMap.get(path);
    if (image == null) {
      try {
        image = getImage(new FileInputStream(path));
        m_imageMap.put(path, image);
      } catch (final Exception e) {
        LOGGER.error("Failed to load the image from path: {}", path, e);
        image = getMissingImage();
        m_imageMap.put(path, image);
      }
    }
    return image;
  }

  /**
   * Returns an {@link Image} stored in the file at the specified path relative
   * to the specified class.
   *
   * @param clazz
   *          the {@link Class} relative to which to find the image
   * @param path
   *          the path to the image file, if starts with <code>'/'</code>
   * @return the {@link Image} stored in the file at the specified path
   */
  public static Image getImage(Class<?> clazz, String path) {
    final String key = clazz.getName() + '|' + path;
    Image image = m_imageMap.get(key);
    if (image == null) {
      try {
        image = getImage(clazz.getResourceAsStream(path));
        m_imageMap.put(key, image);
      } catch (final Exception e) {
        LOGGER.error("Failed to load the image from resource: {}", path, e);
        image = getMissingImage();
        m_imageMap.put(key, image);
      }
    }
    return image;
  }

  private static final int MISSING_IMAGE_SIZE = 10;

  /**
   * @return the small {@link Image} that can be used as placeholder for missing
   *         image.
   */
  private static Image getMissingImage() {
    final Image image = new Image(Display.getCurrent(), MISSING_IMAGE_SIZE,
        MISSING_IMAGE_SIZE);
    //
    final GC gc = new GC(image);
    gc.setBackground(getColor(SWT.COLOR_RED));
    gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
    gc.dispose();
    //
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
   * Returns an {@link Image} composed of a base image decorated by another
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
   * Returns an {@link Image} composed of a base image decorated by another
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
    //
    Image result = decoratedMap.get(decorator);
    if (result == null) {
      final Rectangle bib = baseImage.getBounds();
      final Rectangle dib = decorator.getBounds();
      //
      result = new Image(Display.getCurrent(), bib.width, bib.height);
      //
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
      //
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
      for (final Image image : m_imageMap.values()) {
        image.dispose();
      }
      m_imageMap.clear();
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

  private static final Map<String, Font> m_fontMap = new HashMap<String, Font>();

  /**
   * The font style constant indicating an strikeout font
   * (value is 1&lt;&lt;2).
   */
  public static final int STRIKEOUT = 1 << 2;

  /**
   * The font style constant indicating an underline font
   * (value is 1&lt;&lt;3).
   */
  public static final int UNDERLINE = 1 << 3;


  private static String getFontName(String name, int size, int style) {
    final String fontName = name + '|' + size + '|' + style;
    return fontName;
  }

  /**
   * Returns a {@link Font} based on its name, height and style.
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
    return getFont(name, height, style, false, false);
  }


  /**
   * Returns a {@link Font} based on the name of an existing font, height and
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
    return getFont(baseFont, size, style, false, false);
  }

  /**
   * Returns a {@link Font} based on its name, height and style.
   * Windows-specific strikeout and underline flags are also supported.
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
    if (strikeout) {
      style |= STRIKEOUT;
    }
    if (underline) {
      style |= UNDERLINE;
    }
    final String fontName = getFontName(name, size, style);
    Font font = m_fontMap.get(fontName);
    if (font == null) {
      final FontData fontData = new FontData(name, size, style);
      if (strikeout || underline) {
        try {
          final Class<?> logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT");
          final Object logFont = FontData.class.getField("data").get(fontData);
          if ((logFont != null) && (logFontClass != null)) {
            if (strikeout) {
              logFontClass.getField("lfStrikeOut").set(logFont, Byte.valueOf((byte) 1));
            }
            if (underline) {
              logFontClass.getField("lfUnderline").set(logFont, Byte.valueOf((byte) 1));
            }
          }
        } catch (final Throwable e) {
          LOGGER.error("Unable to set underline or strikeout"
              + " (probably on a non-Windows platform). ", e);
        }
      }
      font = new Font(Display.getCurrent(), fontData);
      m_fontMap.put(fontName, font);
    }
    return font;
  }


  /**
   * Returns a {@link Font} based on the name of an existing font, height and
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
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    return getFont(name, size, style, strikeout, underline);
  }

  /**
   * Returns a bold version of the given {@link Font}.
   *
   * @param baseFont
   *          the {@link Font} for which a bold version is desired
   * @return the bold version of the given {@link Font}
   */
  public static Font getBoldFont(Font baseFont) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    final int style = data.getStyle();
    return getFont(name, height, SWT.BOLD,
              (style & STRIKEOUT) != 0,
              (style & UNDERLINE) != 0);
  }

  /**
   * Returns a italic version of the given {@link Font}.
   *
   * @param baseFont
   *          the {@link Font} for which a bold version is desired
   * @return the italic version of the given {@link Font}
   */
  public static Font getItalicFont(Font baseFont) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    final int style = data.getStyle();
    return getFont(name, height, SWT.ITALIC,
              (style & STRIKEOUT) != 0,
              (style & UNDERLINE) != 0);
  }

  /**
   * Returns a normal version of the given {@link Font}.
   *
   * @param baseFont
   *          the {@link Font} for which a normal version is desired
   * @return the normal version of the given {@link Font}
   */
  public static Font getNormalFont(Font baseFont) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int height = data.getHeight();
    final int style = data.getStyle();
    return getFont(name, height, SWT.NORMAL,
              (style & STRIKEOUT) != 0,
              (style & UNDERLINE) != 0);
  }

  /**
   * Changes the size of a given {@link Font}.
   *
   * @param baseFont
   *    the {@link Font} whose size is to be changed.
   * @param newSize
   *    the new size.
   * @return
   *    a new {@link Font} object whose name and style is the same as the base
   *    fond and whose size is the specified new size.
   */
  public static Font changeFontSize(Font baseFont, int newSize) {
    final FontData fontDatas[] = baseFont.getFontData();
    final FontData data = fontDatas[0];
    final String name = data.getName();
    final int style = data.getStyle();
    return getFont(name, newSize, style,
              (style & STRIKEOUT) != 0,
              (style & UNDERLINE) != 0);
  }



  /**
   * Dispose all of the cached {@link Font}'s.
   */
  public static void disposeFonts() {
    // clear fonts
    for (final Font font : m_fontMap.values()) {
      font.dispose();
    }
    m_fontMap.clear();
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Cursor
  //
  // //////////////////////////////////////////////////////////////////////////
  /**
   * Maps IDs to cursors.
   */
  private static Map<Integer, Cursor> m_idToCursorMap = new HashMap<Integer, Cursor>();

  /**
   * Returns the system cursor matching the specific ID.
   *
   * @param id
   *          int The ID value for the cursor
   * @return Cursor The system cursor matching the specific ID
   */
  public static Cursor getCursor(int id) {
    final Integer key = Integer.valueOf(id);
    Cursor cursor = m_idToCursorMap.get(key);
    if (cursor == null) {
      cursor = new Cursor(Display.getDefault(), id);
      m_idToCursorMap.put(key, cursor);
    }
    return cursor;
  }

  /**
   * Dispose all of the cached cursors.
   */
  public static void disposeCursors() {
    for (final Cursor cursor : m_idToCursorMap.values()) {
      cursor.dispose();
    }
    m_idToCursorMap.clear();
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