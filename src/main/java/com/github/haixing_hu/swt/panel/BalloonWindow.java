/*******************************************************************************
 * Copyright (c) 2004 Stefan Zeiger and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Zeiger (szeiger@novocode.com) - initial API and implementation
 *     Haixing Hu (https://github.com/Haixing-Hu/) - modify the code.
 *******************************************************************************/

package com.github.haixing_hu.swt.panel;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import com.github.haixing_hu.swt.utils.SWTResourceManager;

/**
 * A Shell wrapper which creates balloon popup windows.
 *
 * <p>
 * By default, a balloon window has no title bar or system controls. The
 * following styles are supported:
 * </p>
 *
 * <ul>
 * <li>SWT.ON_TOP - Keep the window on top of other windows</li>
 * <li>SWT.TOOL - Add a drop shadow to the window (on supported platforms)</li>
 * <li>SWT.CLOSE - Show a "close" control on the title bar (implies SWT.TITLE)</li>
 * <li>SWT.TITLE - Show a title bar</li>
 * </ul>
 *
 * @author Stefan Zeiger (szeiger@novocode.com)
 * @author Haixing Hu (https://github.com/Haixing-Hu/)
 * @since Jul 2, 2004
 */
public class BalloonWindow {

  public static final int DEFAULT_STYLE = SWT.ON_TOP | SWT.TOOL | SWT.TITLE | SWT.CLOSE;

  public static final RGB DEFAULT_BACKGROUND_COLOR = new RGB(0xFF, 0xFF, 0xE1);

  public static final RGB DEFAULT_FORGROUND_COLOR = new RGB(0, 0, 0);

  public static final int DEFAULT_PREFERRED_ANCHOR = SWT.BOTTOM | SWT.RIGHT;

  public static final boolean DEFAULT_AUTO_ANCHOR = true;

  public static final int DEFAULT_MARGIN_LEFT = 12;

  public static final int DEFAULT_MARGIN_RIGHT = 12;

  public static final int DEFAULT_MARGIN_TOP = 5;

  public static final int DEFAULT_MARGIN_BOTTOM = 10;

  public static final int DEFAULT_TITLE_SPACING = 3;

  public static final int DEFAULT_TITLE_WIDGET_SPACING = 8;

  /**
   * The default value of the color of the drop shadow.
   */
  public static final RGB DEFAULT_SHADOW_COLOR = new RGB(0,0,0);

  /**
   * The default value of the radius of the drop shadow in pixels.
   */
  public static final int DEFAULT_SHADOW_RADIUS = 24;

  /**
   * The default value of the radius of the highlight area of the drop shadow,
   * in pixels.
   */
  public static final int DEFAULT_SHADOW_HIGHLIGHT_RADIUS = 16;

  /**
   * The default value of the opacity of the shadow, between 0 and 255.
   */
  public static final int DEFAULT_SHADOW_OPACITY = 100;

  private final Shell shell;
  private final Composite contents;
  private Label titleLabel;
  private Canvas titleImageLabel;
  private final int style;
  private int preferredAnchor = DEFAULT_PREFERRED_ANCHOR;
  private boolean autoAnchor = DEFAULT_AUTO_ANCHOR;
  private int locX = Integer.MIN_VALUE;
  private int locY = Integer.MIN_VALUE;
  private int marginLeft = DEFAULT_MARGIN_LEFT;
  private int marginRight = DEFAULT_MARGIN_RIGHT;
  private int marginTop = DEFAULT_MARGIN_TOP;
  private int marginBottom = DEFAULT_MARGIN_BOTTOM;
  private int titleSpacing = DEFAULT_TITLE_SPACING;
  private int titleWidgetSpacing = DEFAULT_TITLE_WIDGET_SPACING;

  private int shadowRadius = DEFAULT_SHADOW_RADIUS;
  private int shadowHighlightRadius = DEFAULT_SHADOW_HIGHLIGHT_RADIUS;
  private int shadowOpacity = DEFAULT_SHADOW_OPACITY;
  private Color shadowColor;

  private ToolBar systemControlsBar;
  private final ArrayList<Object> selectionControls = new ArrayList<Object>();
  private boolean addedGlobalListener;
  private final ArrayList<Listener> selectionListeners = new ArrayList<Listener>();
  private Point contentsSize;
  private Point titleSize;

  /**
   * Constructs a new balloon window with the default style.
   *
   * @param parent
   *    the parent shell of the new balloon window.
   */
  public BalloonWindow(Shell parent) {
    this(null, parent, DEFAULT_STYLE);
  }

  /**
   * Constructs a new balloon window.
   *
   * @param parent
   *    the parent shell of the new balloon window.
   * @param style
   *    the SWT windows style of the new balloon window. Supported styles are
   *    <ul>
   *    <li><code>SWT.TITLE</code>: if presented, the title will be shown.</li>
   *    <li><code>SWT.CLOSe</code>: if presented, the close button on the right
   *    of the title will be shown.</li>
   *    <li><code>SWT.ON_TOP</code>: if presented, the balloon window will be always
   *    on the top of other windows.</li>
   *    </ul>
   */
  public BalloonWindow(Shell parent, int style) {
    this(null, parent, style);
  }

  /**
   * Constructs a new balloon window with the default style.
   *
   * @param display
   *    the display for the new balloon window.
   * @param style
   *    the SWT windows style of the new balloon window.
   */
  public BalloonWindow(Display display) {
    this(display, null, DEFAULT_STYLE);
  }

  /**
   * Constructs a new balloon window.
   *
   * @param display
   *    the display for the new balloon window.
   * @param style
   *    the SWT windows style of the new balloon window. Supported styles are
   *    <ul>
   *    <li><code>SWT.TITLE</code>: if presented, the title will be shown.</li>
   *    <li><code>SWT.CLOSe</code>: if presented, the close button on the right
   *    of the title will be shown.</li>
   *    <li><code>SWT.ON_TOP</code>: if presented, the balloon window will be always
   *    on the top of other windows.</li>
   *    </ul>
   */
  public BalloonWindow(Display display, int style) {
    this(display, null, style);
  }

  private BalloonWindow(Display display, Shell parent, final int style) {
    this.style = style;
    final int shellStyle = (style & (SWT.ON_TOP | SWT.TOOL)) | SWT.NO_TRIM;
    if (display != null) {
      shell = new Shell(display, shellStyle);
    } else {
      shell = new Shell(parent, shellStyle);
      display = shell.getDisplay();
    }
    shadowColor = SWTResourceManager.getColor(display, DEFAULT_SHADOW_COLOR);
    contents = new Composite(shell, SWT.NONE);
    shell.setBackground(SWTResourceManager.getColor(display, DEFAULT_BACKGROUND_COLOR));
    shell.setForeground(SWTResourceManager.getColor(display, DEFAULT_FORGROUND_COLOR));
    contents.setBackground(shell.getBackground());
    contents.setForeground(shell.getForeground());

    selectionControls.add(shell);
    selectionControls.add(contents);

    final Listener globalListener = new Listener() {
      @Override
      public void handleEvent(Event event) {
        final Widget w = event.widget;
        for (int i = selectionControls.size() - 1; i >= 0; i--) {
          if (selectionControls.get(i) == w) {
            if ((style & SWT.CLOSE) != 0) {
              for (int j = selectionListeners.size() - 1; j >= 0; j--) {
                selectionListeners.get(j).handleEvent(event);
              }
            } else {
              shell.close();
            }
            event.doit = false;
          }
        }
      }
    };

    shell.addListener(SWT.Show, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (! addedGlobalListener) {
          shell.getDisplay().addFilter(SWT.MouseDown, globalListener);
          addedGlobalListener = true;
        }
      }
    });

    shell.addListener(SWT.Hide, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (addedGlobalListener) {
          shell.getDisplay().removeFilter(SWT.MouseDown, globalListener);
          addedGlobalListener = false;
        }
      }
    });

    shell.addListener(SWT.Dispose, new Listener() {
      @Override
      public void handleEvent(Event event) {
        if (addedGlobalListener) {
          shell.getDisplay().removeFilter(SWT.MouseDown, globalListener);
          addedGlobalListener = false;
        }
      }
    });
  }

  /**
   * Adds a control to the list of controls which close the balloon window. The
   * background, title image and title text are included by default.
   */
  public void addSelectionControl(Control c) {
    selectionControls.add(c);
  }

  public void addListener(int type, Listener l) {
    if (type == SWT.Selection) {
      selectionListeners.add(l);
    }
  }

  /**
   * Set the location of the anchor of this balloon window, i.e., the location
   * of the "arrow" shape of this balloon window.
   *
   * <p>
   * The location of the anchor must be one of the following values:
   * <ul>
   * <li><code>SWT.NONE</code></li>
   * <li><code>SWT.LEFT | SWT.TOP</code></li>
   * <li><code>SWT.RIGHT | SWT.TOP</code></li>
   * <li><code>SWT.LEFT | SWT.BOTTOM</code></li>
   * <li><code>SWT.RIGHT | SWT.BOTTOM</code></li>
   * </ul>
   *
   * @param anchor
   *          The location of the anchor of this balloon window.
   */
  public void setAnchor(int anchor) {
    switch (anchor) {
    case SWT.NONE:
    case SWT.LEFT | SWT.TOP:
    case SWT.RIGHT | SWT.TOP:
    case SWT.LEFT | SWT.BOTTOM:
    case SWT.RIGHT | SWT.BOTTOM:
      break;
    default:
      throw new IllegalArgumentException("Illegal anchor value " + anchor);
    }
    preferredAnchor = anchor;
  }

  /**
   * Sets whether this balloon will automatically place the anchor.
   *
   * @param autoAnchor
   *    whether this balloon will automatically place the anchor.
   */
  public void setAutoAnchor(boolean autoAnchor) {
    this.autoAnchor = autoAnchor;
  }

  /**
   * Sets the location of this balloon window.
   * <p>
   * <b>NOTE:</b>The location is relative to the display.
   *
   * @param x
   *    the x coordinate of the point specifies the location relative to the
   *    display.
   * @param y
   *    the y coordinate of the point specifies the location relative to the
   *    display.
   */
  public void setLocation(int x, int y) {
    locX = x;
    locY = y;
  }

  /**
   * Sets the location of this balloon window.
   * <p>
   * <b>NOTE:</b>The location is relative to the display.
   *
   * @param p
   *    the point specifies the location relative to the display.
   */
  public void setLocation(Point p) {
    locX = p.x;
    locY = p.y;
  }

  /**
   * Sets the text of the title of this balloon window.
   *
   * @param text
   *    The text of the title of this balloon window.
   */
  public void setTitleText(String text) {
    shell.setText(text);
  }

  /**
   * Sets the image of the title of this balloon window. The image will be
   * displayed at the upper-left of the title.
   *
   * @param image
   *          the image of the title of this balloon window.
   */
  public void setTitleImage(Image image) {
    shell.setImage(image);
  }

  public void setMargins(int marginLeft, int marginRight, int marginTop,
      int marginBottom) {
    this.marginLeft = marginLeft;
    this.marginRight = marginRight;
    this.marginTop = marginTop;
    this.marginBottom = marginBottom;
  }

  public void setMargins(int marginX, int marginY) {
    setMargins(marginX, marginX, marginY, marginY);
  }

  public void setMargins(int margin) {
    setMargins(margin, margin, margin, margin);
  }

  public void setTitleSpacing(int titleSpacing) {
    this.titleSpacing = titleSpacing;
  }

  public void setTitleWidgetSpacing(int titleImageSpacing) {
    titleWidgetSpacing = titleImageSpacing;
  }

  /**
   * Gets the shadowRadius.
   *
   * @return the shadowRadius.
   */
  public int getShadowRadius() {
    return shadowRadius;
  }

  /**
   * Sets the shadowRadius.
   *
   * @param shadowRadius the new shadowRadius to set.
   */
  public void setShadowRadius(int shadowRadius) {
    this.shadowRadius = shadowRadius;
  }

  /**
   * Gets the shadowHighlightRadius.
   *
   * @return the shadowHighlightRadius.
   */
  public int getShadowHighlightRadius() {
    return shadowHighlightRadius;
  }

  /**
   * Sets the shadowHighlightRadius.
   *
   * @param shadowHighlightRadius the new shadowHighlightRadius to set.
   */
  public void setShadowHighlightRadius(int shadowHighlightRadius) {
    this.shadowHighlightRadius = shadowHighlightRadius;
  }

  /**
   * Gets the shadowOpacity.
   *
   * @return the shadowOpacity.
   */
  public int getShadowOpacity() {
    return shadowOpacity;
  }

  /**
   * Sets the shadowOpacity.
   *
   * @param shadowOpacity the new shadowOpacity to set.
   */
  public void setShadowOpacity(int shadowOpacity) {
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
   * @param shadowColor the new shadowColor to set.
   */
  public void setShadowColor(Color shadowColor) {
    this.shadowColor = shadowColor;
  }

  public Shell getShell() {
    return shell;
  }

  public Composite getContents() {
    return contents;
  }

  public void setVisible(boolean visible) {
    if (visible) {
      prepareForOpen();
    }
    shell.setVisible(visible);
  }

  /**
   * Opens and shows this balloon window.
   * <p>
   * <b>NOTE:</b> before open this balloon window, the location should be set
   * by calling the {@link #setLocation(Point)} function.
   */
  public void open() {
    prepareForOpen();
    shell.open();
  }

  /**
   * Closes and disposes this balloon window.
   */
  public void close() {
    shell.close();
  }

  private void prepareForOpen() {
    contentsSize = contents.getSize();
    titleSize = new Point(0, 0);
    if ((style & SWT.TITLE) != 0) {
      createTitle();
    }
    final int anchor = calculateAnchor();
    final Point shellSize = setShellSize(anchor);
    setChildrenLocations(anchor, shellSize);
    setShellShape(anchor, shellSize);
    setShellLocation(anchor, shellSize);
  }

  private void createTitle() {
    // create the title text label
    if (titleLabel == null) {
      titleLabel = new Label(shell, SWT.NONE);
      titleLabel.setBackground(shell.getBackground());
      titleLabel.setForeground(shell.getForeground());
      final Font titleFont = SWTResourceManager.getBoldFont(shell.getDisplay(),
          shell.getFont());
      titleLabel.setFont(titleFont);
      selectionControls.add(titleLabel);
    }
    final String titleText = shell.getText();
    titleLabel.setText(titleText == null ? "" : titleText);
    titleLabel.pack();
    titleSize = titleLabel.getSize();
    //  create the title image label
    final Image titleImage = shell.getImage();
    if ((titleImageLabel == null) && (titleImage != null)) {
      titleImageLabel = new Canvas(shell, SWT.NONE);
      titleImageLabel.setBackground(shell.getBackground());
      titleImageLabel.setBounds(titleImage.getBounds());
      titleImageLabel.addListener(SWT.Paint, new Listener() {
        @Override
        public void handleEvent(Event event) {
          event.gc.drawImage(titleImage, 0, 0);
        }
      });
      final Point imgSize = titleImageLabel.getSize();
      titleSize.x += imgSize.x + titleWidgetSpacing;
      if (imgSize.y > titleSize.y) {
        titleSize.y = imgSize.y;
      }
      selectionControls.add(titleImageLabel);
    }
    //  create the title close button
    if ((systemControlsBar == null) && ((style & SWT.CLOSE) != 0)) {
      final Display display = shell.getDisplay();
      final Color closeFG = shell.getForeground();
      final Color closeBG = shell.getBackground();
      final Image closeImage = createCloseImage(display, closeBG, closeFG);
      shell.addListener(SWT.Dispose, new Listener() {
        @Override
        public void handleEvent(Event event) {
          closeImage.dispose();
        }
      });
      systemControlsBar = new ToolBar(shell, SWT.FLAT | SWT.NO_FOCUS);
      systemControlsBar.setBackground(closeBG);
      systemControlsBar.setForeground(closeFG);
      final ToolItem closeItem = new ToolItem(systemControlsBar, SWT.PUSH);
      closeItem.setImage(closeImage);
      closeItem.addListener(SWT.Selection, new Listener() {
        @Override
        public void handleEvent(Event event) {
          shell.close();
        }
      });
      systemControlsBar.pack();
      final Point closeSize = systemControlsBar.getSize();
      titleSize.x += closeSize.x + titleWidgetSpacing;
      if (closeSize.y > titleSize.y) {
        titleSize.y = closeSize.y;
      }
    }

    titleSize.y += titleSpacing;
    if (titleSize.x > contentsSize.x) {
      contentsSize.x = titleSize.x;
      contents.setSize(contentsSize.x, contentsSize.y);
    }
    contentsSize.y += titleSize.y;
  }

  private int calculateAnchor() {
    int anchor = preferredAnchor;
    if ((anchor != SWT.NONE) && autoAnchor && (locX != Integer.MIN_VALUE)) {
      final Rectangle screen = shell.getDisplay().getClientArea();
      if ((anchor & SWT.LEFT) != 0) {
        if (((locX + contentsSize.x + marginLeft + marginRight) - 16)
              >= (screen.x + screen.width)) {
          anchor = (anchor - SWT.LEFT) + SWT.RIGHT;
        }
      } else // RIGHT
      {
        if (((locX - contentsSize.x - marginLeft - marginRight) + 16) < screen.x) {
          anchor = (anchor - SWT.RIGHT) + SWT.LEFT;
        }
      }
      if ((anchor & SWT.TOP) != 0) {
        if ((locY + contentsSize.y + 20 + marginTop + marginBottom)
              >= (screen.y + screen.height)) {
          anchor = (anchor - SWT.TOP) + SWT.BOTTOM;
        }
      } else // BOTTOM
      {
        if ((locY - contentsSize.y - 20 - marginTop - marginBottom) < screen.y) {
          anchor = (anchor - SWT.BOTTOM) + SWT.TOP;
        }
      }
    }
    return anchor;
  }

  private Point setShellSize(int anchor) {
    Point shellSize;
    if (anchor == SWT.NONE) {
      shellSize = new Point(contentsSize.x + marginLeft + marginRight,
          contentsSize.y + marginTop + marginBottom);
    } else {
       shellSize = new Point(contentsSize.x + marginLeft + marginRight,
           contentsSize.y + marginTop + marginBottom + 20);
    }
    if (shellSize.x < (54 + marginLeft + marginRight)) {
      shellSize.x = 54 + marginLeft + marginRight;
    }
    if (anchor == SWT.NONE) {
      if (shellSize.y < (10 + marginTop + marginBottom)) {
        shellSize.y = 10 + marginTop + marginBottom;
      }
    } else {
      if (shellSize.y < (30 + marginTop + marginBottom)) {
        shellSize.y = 30 + marginTop + marginBottom;
      }
    }
    shell.setSize(shellSize);
    return shellSize;
  }

  private void setChildrenLocations(int anchor, Point shellSize) {
    final int titleLocY = marginTop + (((anchor & SWT.TOP) != 0) ? 20 : 0);
    contents.setLocation(marginLeft, titleSize.y + titleLocY);
    if ((style & SWT.TITLE) != 0) {
      final int realTitleHeight = titleSize.y - titleSpacing;
      if (titleImageLabel != null) {
        titleImageLabel.setLocation(marginLeft,
            titleLocY + ((realTitleHeight - titleImageLabel.getSize().y) / 2));
        titleLabel.setLocation(
            marginLeft + titleImageLabel.getSize().x + titleWidgetSpacing,
            titleLocY + ((realTitleHeight - titleLabel.getSize().y) / 2));
      } else {
        titleLabel.setLocation(marginLeft,
            titleLocY + ((realTitleHeight - titleLabel.getSize().y) / 2));
      }
      if (systemControlsBar != null) {
        systemControlsBar.setLocation(
            shellSize.x - marginRight - systemControlsBar.getSize().x,
            titleLocY + ((realTitleHeight - systemControlsBar.getSize().y) / 2));
      }
    }
  }

  private void setShellShape(int anchor, Point shellSize) {
    final int[] shape = createOutline(shellSize, anchor, true);
    final int[] border = createOutline(shellSize, anchor, false);

    //  draw the shape of the balloon window
    final Region region = new Region();
    region.add(shape);
    shell.setRegion(region);
    shell.addListener(SWT.Dispose, new Listener() {
      @Override
      public void handleEvent(Event event) {
        region.dispose();
      }
    });

    shell.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        // draw the border of the balloon window
        e.gc.drawPolygon(border);
      }
    });
  }

  private void setShellLocation(int anchor, Point shellSize) {
    final Rectangle screen = shell.getDisplay().getClientArea();
    if (locX != Integer.MIN_VALUE) {
      final Point shellLoc = new Point(locX, locY);
      if ((anchor & SWT.BOTTOM) != 0) {
        shellLoc.y = (shellLoc.y - shellSize.y) + 1;
      }
      if ((anchor & SWT.LEFT) != 0) {
        shellLoc.x -= 15;
      } else if ((anchor & SWT.RIGHT) != 0) {
        shellLoc.x = (shellLoc.x - shellSize.x) + 16;
      }
      if (autoAnchor) {
        if (shellLoc.x < screen.x) {
          shellLoc.x = screen.x;
        } else if (shellLoc.x > ((screen.x + screen.width) - shellSize.x)) {
          shellLoc.x = (screen.x + screen.width) - shellSize.x;
        }

        if (anchor == SWT.NONE) {
          if (shellLoc.y < screen.y) {
            shellLoc.y = screen.y;
          } else if (shellLoc.y > ((screen.y + screen.height) - shellSize.y)) {
            shellLoc.y = (screen.y + screen.height) - shellSize.y;
          }
        }
      }
      shell.setLocation(shellLoc);
    }
  }

  /**
   * Creates the outline of the polygon shape of the balloon window.
   *
   * @param size
   *    The size of the balloon window.
   * @param anchor
   *    The anchor position of the balloon window.
   * @param outer
   *    Indicates whether this outline is the outer outline or the inner outline.
   * @return
   *    an array of alternating x and y values which are the vertices of the
   *    polygon.
   */
  private static int[] createOutline(Point size, int anchor, boolean outer) {
    final int o = outer ? 1 : 0;
    final int w = size.x + o;
    final int h = size.y + o;

    switch (anchor) {
    case SWT.RIGHT | SWT.BOTTOM:
      return new int[] {
          // top and top right
          5, 0, w - 6, 0, w - 6, 1,
          w - 4,
          1,
          w - 4,
          2,
          w - 3,
          2,
          w - 3,
          3,
          w - 2,
          3,
          w - 2,
          5,
          w - 1,
          5,
          // right and bottom right
          w - 1, h - 26, w - 2, h - 26, w - 2,
          h - 24,
          w - 3,
          h - 24,
          w - 3,
          h - 23,
          w - 4,
          h - 23,
          w - 4,
          h - 22,
          w - 6,
          h - 22,
          w - 6,
          h - 21,
          // bottom with anchor
          w - 16, h - 21, w - 16, h - 1, w - 16 - o, h - 1, w - 16 - o, h - 2,
          w - 17 - o, h - 2, w - 17 - o, h - 3, w - 18 - o, h - 3, w - 18 - o,
          h - 4, w - 19 - o, h - 4, w - 19 - o, h - 5, w - 20 - o, h - 5,
          w - 20 - o, h - 6, w - 21 - o, h - 6, w - 21 - o, h - 7, w - 22 - o,
          h - 7, w - 22 - o, h - 8, w - 23 - o, h - 8, w - 23 - o, h - 9,
          w - 24 - o, h - 9, w - 24 - o, h - 10, w - 25 - o, h - 10,
          w - 25 - o, h - 11, w - 26 - o, h - 11, w - 26 - o, h - 12,
          w - 27 - o, h - 12, w - 27 - o, h - 13, w - 28 - o, h - 13,
          w - 28 - o, h - 14, w - 29 - o, h - 14, w - 29 - o, h - 15,
          w - 30 - o, h - 15, w - 30 - o, h - 16, w - 31 - o, h - 16,
          w - 31 - o, h - 17, w - 32 - o, h - 17, w - 32 - o, h - 18,
          w - 33 - o, h - 18, w - 33 - o, h - 19, w - 34 - o, h - 19,
          w - 34 - o, h - 20, w - 35 - o, h - 20, w - 35 - o, h - 21,
          // bottom left
          5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2, h - 24, 1,
          h - 24, 1, h - 26, 0, h - 26,
          // left and top left
          0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
    case SWT.LEFT | SWT.BOTTOM:
      return new int[] {
          // top and top right
          5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4,
          2,
          w - 3,
          2,
          w - 3,
          3,
          w - 2,
          3,
          w - 2,
          5,
          w - 1,
          5,
          // right and bottom right
          w - 1, h - 26, w - 2, h - 26, w - 2, h - 24, w - 3, h - 24,
          w - 3,
          h - 23,
          w - 4,
          h - 23,
          w - 4,
          h - 22,
          w - 6,
          h - 22,
          w - 6,
          h - 21,
          // bottom with anchor
          34 + o, h - 21, 34 + o, h - 20, 33 + o, h - 20, 33 + o, h - 19,
          32 + o, h - 19, 32 + o, h - 18, 31 + o, h - 18, 31 + o, h - 17,
          30 + o, h - 17, 30 + o, h - 16, 29 + o, h - 16, 29 + o, h - 15,
          28 + o, h - 15, 28 + o, h - 14, 27 + o, h - 14, 27 + o, h - 13,
          26 + o, h - 13, 26 + o, h - 12, 25 + o, h - 12, 25 + o, h - 11,
          24 + o, h - 11, 24 + o, h - 10, 23 + o, h - 10, 23 + o, h - 9,
          22 + o, h - 9, 22 + o, h - 8, 21 + o, h - 8, 21 + o, h - 7, 20 + o,
          h - 7, 20 + o, h - 6, 19 + o, h - 6, 19 + o, h - 5, 18 + o, h - 5,
          18 + o, h - 4, 17 + o, h - 4, 17 + o, h - 3, 16 + o, h - 3, 16 + o,
          h - 2, 15 + o, h - 2, 15, h - 1, 15, h - 21,
          // bottom left
          5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2, h - 24, 1,
          h - 24, 1, h - 26, 0, h - 26,
          // left and top left
          0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
    case SWT.RIGHT | SWT.TOP:
      return new int[] {
          // top with anchor
          5, 20, w - 35 - o, 20, w - 35 - o, 19, w - 34 - o, 19, w - 34 - o,
          18, w - 33 - o, 18, w - 33 - o, 17, w - 32 - o, 17, w - 32 - o, 16,
          w - 31 - o, 16, w - 31 - o, 15, w - 30 - o, 15, w - 30 - o, 14,
          w - 29 - o, 14, w - 29 - o, 13, w - 28 - o, 13, w - 28 - o, 12,
          w - 27 - o, 12, w - 27 - o, 11, w - 26 - o, 11, w - 26 - o, 10,
          w - 25 - o, 10, w - 25 - o, 9, w - 24 - o, 9, w - 24 - o, 8,
          w - 23 - o, 8, w - 23 - o, 7, w - 22 - o, 7, w - 22 - o, 6,
          w - 21 - o, 6, w - 21 - o, 5, w - 20 - o, 5, w - 20 - o, 4,
          w - 19 - o, 4, w - 19 - o, 3, w - 18 - o, 3, w - 18 - o, 2,
          w - 17 - o, 2, w - 17 - o, 1, w - 16 - o, 1, w - 16 - o, 0, w - 16,
          0,
          w - 16,
          20,
          // top and top right
          w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22, w - 3, 23,
          w - 2, 23, w - 2, 25, w - 1,
          25,
          // right and bottom right
          w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4, w - 3, h - 3,
          w - 4, h - 3, w - 4, h - 2, w - 6, h - 2, w - 6, h - 1,
          // bottom and bottom left
          5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4, 1, h - 4,
          1, h - 6, 0, h - 6,
          // left and top left
          0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
    case SWT.LEFT | SWT.TOP:
      return new int[] {
          // top with anchor
          5, 20, 15, 20, 15, 0, 15 + o, 0, 16 + o, 1, 16 + o, 2, 17 + o, 2,
          17 + o, 3, 18 + o, 3, 18 + o, 4, 19 + o, 4, 19 + o, 5, 20 + o, 5,
          20 + o, 6, 21 + o, 6, 21 + o, 7, 22 + o, 7, 22 + o, 8, 23 + o, 8,
          23 + o, 9, 24 + o, 9, 24 + o, 10, 25 + o, 10, 25 + o, 11, 26 + o, 11,
          26 + o, 12, 27 + o, 12, 27 + o, 13, 28 + o, 13, 28 + o, 14, 29 + o,
          14, 29 + o, 15, 30 + o, 15, 30 + o, 16, 31 + o, 16, 31 + o, 17,
          32 + o, 17, 32 + o, 18, 33 + o, 18, 33 + o, 19, 34 + o,
          19,
          34 + o,
          20,
          // top and top right
          w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22, w - 3, 23,
          w - 2, 23, w - 2, 25, w - 1,
          25,
          // right and bottom right
          w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4, w - 3, h - 3,
          w - 4, h - 3, w - 4, h - 2, w - 6, h - 2, w - 6, h - 1,
          // bottom and bottom left
          5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4, 1, h - 4,
          1, h - 6, 0, h - 6,
          // left and top left
          0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
    default:
      return new int[] {
          // top and top right
          5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2, w - 3, 3,
          w - 2, 3, w - 2, 5, w - 1,
          5,
          // right and bottom right
          w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4, w - 3, h - 3,
          w - 4, h - 3, w - 4, h - 2, w - 6, h - 2, w - 6, h - 1,
          // bottom and bottom left
          5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4, 1, h - 4,
          1, h - 6, 0, h - 6,
          // left and top left
          0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
    }
  }

  /**
   * Creates the image of the closing button on the title bar of the balloon
   * window.
   *
   * @param display
   *    a specified display.
   * @param bg
   *    the background color.
   * @param fg
   *    the foreground color.
   * @return
   *    the image of the closing button on the title bar of the balloon window.
   */
  private static final Image createCloseImage(Display display, Color bg,
      Color fg) {
    final int size = 11, off = 1;
    final Image image = new Image(display, size, size);
    final GC gc = new GC(image);
    gc.setBackground(bg);
    gc.fillRectangle(image.getBounds());
    gc.setForeground(fg);
    gc.drawLine(0 + off, 0 + off, size - 1 - off, size - 1 - off);
    gc.drawLine(1 + off, 0 + off, size - 1 - off, size - 2 - off);
    gc.drawLine(0 + off, 1 + off, size - 2 - off, size - 1 - off);
    gc.drawLine(size - 1 - off, 0 + off, 0 + off, size - 1 - off);
    gc.drawLine(size - 1 - off, 1 + off, 1 + off, size - 1 - off);
    gc.drawLine(size - 2 - off, 0 + off, 0 + off, size - 2 - off);
    /*
     * gc.drawLine(1, 0, size-2, 0); gc.drawLine(1, size-1, size-2, size-1);
     * gc.drawLine(0, 1, 0, size-2); gc.drawLine(size-1, 1, size-1, size-2);
     */
    gc.dispose();
    return image;
  }
}
