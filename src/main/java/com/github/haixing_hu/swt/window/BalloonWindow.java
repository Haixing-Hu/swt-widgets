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

package com.github.haixing_hu.swt.window;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public static final int DEFAULT_PREFERRED_ANCHOR = SWT.BOTTOM | SWT.RIGHT;

  public static final boolean DEFAULT_AUTO_ANCHOR = true;

  public static final int DEFAULT_MARGIN_LEFT = 12;

  public static final int DEFAULT_MARGIN_RIGHT = 12;

  public static final int DEFAULT_MARGIN_TOP = 5;

  public static final int DEFAULT_MARGIN_BOTTOM = 10;

  public static final int DEFAULT_TITLE_SPACING = 3;

  public static final int DEFAULT_TITLE_WIDGET_SPACING = 8;

  private static final int SCREEN_MARGIN_WIDTH = 16;

  private static final int SCREEN_MARGIN_HEIGHT = 20;

  private static final int CORNER_RADIUS = 5;

  private static final int ANCHOR_CORNER_OFFSET = 10;

  private static final int ANCHOR_SIZE = 20;

  private static final int ANCHOR_MIN_SPACE = ((CORNER_RADIUS + ANCHOR_CORNER_OFFSET) * 2) + ANCHOR_SIZE;

  private static final int BALLOON_MARGIN = 10;

  private static final int OUTER_OFFSET = 2;

  private static final Logger LOGGER = LoggerFactory.getLogger(BalloonWindow.class);

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
  private final int style;
  private final Shell shell;
  private Canvas titleIcon;
  private Label titleLabel;
  private ToolBar closeButton;
  private final Composite contents;
  private final Listener globalListener;
  private final Listener shellListener;
  private final Listener parentListener;
  private Image closeIcon = null;

  private final ArrayList<Object> selectionControls = new ArrayList<Object>();
  private boolean addedGlobalListener = false;
  private final ArrayList<Listener> selectionListeners = new ArrayList<Listener>();
  private Point contentsSize;   //  internal use
  private Point titleSize;      //  internal use

  /**
   * Constructs a new balloon window with the default style.
   *
   * @param parent
   *    the parent shell of the new balloon window.
   */
  public BalloonWindow(Shell parent) {
    this(parent, DEFAULT_STYLE);
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
   *    <li><code>SWT.CLOSE</code>: if presented, the close button on the right
   *    of the title will be shown.</li>
   *    <li><code>SWT.ON_TOP</code>: if presented, the balloon window will be always
   *    on the top of other windows.</li>
   *    </ul>
   */
  public BalloonWindow(final Shell parent, final int style) {
    this.style = style;
    final int shellStyle = (style & (SWT.ON_TOP | SWT.TOOL)) | SWT.NO_TRIM;
    shell = new Shell(parent, shellStyle);
    final Display display = shell.getDisplay();
    shell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
    shell.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));

    contents = new Composite(shell, SWT.NONE);
    contents.setBackground(shell.getBackground());
    contents.setForeground(shell.getForeground());

    selectionControls.add(shell);
    if (titleIcon != null) {
      selectionControls.add(titleIcon);
    }
    if (titleLabel != null) {
      selectionControls.add(titleLabel);
    }
    if (closeButton != null) {
      selectionControls.add(closeButton);
    }
    selectionControls.add(contents);

    //  The global listener is used to dispatch the mouse down event to
    //  all controls in the selectionControls list.
    //  If there is no close button, any mouse down event happened on one
    //  of the selection control will close the balloon window.
    globalListener = new Listener() {
      @Override
      public void handleEvent(Event event) {
        onMouseDown(event);
      }
    };

    //  add the listener to the shell
    shellListener = new Listener() {
      @Override
      public void handleEvent(Event event) {
        switch (event.type) {
        case SWT.Dispose:
          onDispose(event);
          break;
        case SWT.Show:
          onShow(event);
          break;
        case SWT.Hide:
          onHide(event);
          break;
        }
      }
    };
    shell.addListener (SWT.Dispose, shellListener);
    shell.addListener (SWT.Paint, shellListener);
    shell.addListener (SWT.Show, shellListener);
    shell.addListener (SWT.Hide, shellListener);
    shell.addListener (SWT.MouseDown, shellListener);

    //  dispose this balloon window if its parent is disposed.
    parentListener = new Listener() {
      @Override
      public void handleEvent(Event event) {
        shell.dispose();
      }
    };
    parent.addListener(SWT.Dispose, parentListener);
  }

  private void onDispose(Event event) {
    LOGGER.debug("onDispose()");
    final Control parent = shell.getParent ();
    parent.removeListener (SWT.Dispose, parentListener);
    shell.removeListener (SWT.Dispose, shellListener);
    shell.notifyListeners (SWT.Dispose, event);
    event.type = SWT.None;
    //  unregister the global listener
    if (addedGlobalListener) {
      shell.getDisplay().removeFilter(SWT.MouseDown, globalListener);
      addedGlobalListener = false;
    }
    //  dispose the resources
    if (closeIcon != null) {
      closeIcon.dispose();
      closeIcon = null;
    }
  }

  private void onShow(Event event) {
    LOGGER.debug("onShow()");
    if (! addedGlobalListener) {
      shell.getDisplay().addFilter(SWT.MouseDown, globalListener);
      addedGlobalListener = true;
    }
  }

  private void onHide(Event event) {
    LOGGER.debug("onHide()");
    if (addedGlobalListener) {
      shell.getDisplay().removeFilter(SWT.MouseDown, globalListener);
      addedGlobalListener = false;
    }
  }

  private void onClose(Event event) {
    LOGGER.debug("onClose()");
    shell.close();
  }

  private void onMouseDown(Event event) {
    LOGGER.debug("onMouseDown()");
    final Widget w = event.widget;
    for (int i = selectionControls.size() - 1; i >= 0; i--) {
      if (selectionControls.get(i) == w) {
        if (closeButton != null) {
          for (int j = selectionListeners.size() - 1; j >= 0; j--) {
            selectionListeners.get(j).handleEvent(event);
          }
        } else {
          //  if there is no close button on the title bar of this balloon
          //  window, click any of the selection control will close this
          //  balloon window.
          onClose(event);
        }
        event.doit = false;
      }
    }
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
   * <li><code>SWT.TOP</code></li>
   * <li><code>SWT.RIGHT</code></li>
   * <li><code>SWT.BITTOM</code></li>
   * <li><code>SWT.LEFT</code></li>
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
    case SWT.TOP:
    case SWT.BOTTOM:
    case SWT.RIGHT:
    case SWT.LEFT:
      break;
    default:
      throw new IllegalArgumentException("Illegal anchor value " + anchor);
    }
    preferredAnchor = anchor;
  }

  /**
   * Sets whether this balloon will automatically reset the anchor if the
   * preferred anchor is not suitable.
   * <p>
   * If the `autoAnchor` property is set to true, the balloon window will
   * automatically reset the anchor if the preferred anchor will cause a part of
   * the window outside the screen border.
   *
   * @param autoAnchor
   *          Indicates whether this balloon will automatically reset the anchor
   *          if the preferred anchor is not suitable.
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

  public void setTitleWidgetSpacing(int titleWidgetSpacing) {
    this.titleWidgetSpacing = titleWidgetSpacing;
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
    onClose(null);
  }

  private void prepareForOpen() {
    contents.pack();
    contentsSize = contents.getSize();
    createTitle();
    calculateTitleSize();
    if (titleSize.x > contentsSize.x) {
      contentsSize.x = titleSize.x;
      contents.setSize(contentsSize);
    }
    contentsSize.y += titleSize.y;

    LOGGER.debug("Title size is {}", titleSize);
    LOGGER.debug("Contents size is {}", contentsSize);
    final int anchor = calculateAnchor();
    final Point shellSize = setShellSize(anchor);
    putShellChildren(anchor, shellSize);
    setShellShape(anchor, shellSize);
    setShellLocation(anchor, shellSize);
  }

  private void createTitle() {
    final Image icon = shell.getImage();
    if ((titleIcon == null) && (icon != null)) {
      titleIcon = createTitleIcon(shell, icon);
    }
    if (titleLabel == null) {
      titleLabel = createTitleLabel(shell, shell.getText());
    } else {
      titleLabel.setText(shell.getText());
    }
    if ((closeButton == null) && ((style & SWT.CLOSE) != 0)) {
      closeButton = createCloseButton(shell);
    }
  }

  private Canvas createTitleIcon(final Composite parent, final Image icon) {
    LOGGER.debug("Creating the title icon.");
    final Canvas canvas = new Canvas(parent, SWT.NONE);
    canvas.setBackground(parent.getBackground());
    canvas.setBounds(icon.getBounds());
    canvas.addListener(SWT.Paint, new Listener() {
      @Override
      public void handleEvent(Event event) {
        event.gc.drawImage(icon, 0, 0);
      }
    });
    return canvas;
  }

  private Label createTitleLabel(Composite parent, String text) {
    LOGGER.debug("Creating the title label.");
    final Label label = new Label(parent, SWT.NONE);
    label.setBackground(parent.getBackground());
    label.setForeground(parent.getForeground());
    //  set the bold font of the title text
    label.setFont(SWTResourceManager.getBoldFont(parent.getFont()));
    //  set the title text
    label.setText(text);
    return label;
  }

  private ToolBar createCloseButton(Composite parent) {
    LOGGER.debug("Creating the close button.");
    final ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);
    toolbar.setBackground(parent.getBackground());
    toolbar.setForeground(parent.getForeground());
    final ToolItem item = new ToolItem(toolbar, SWT.PUSH);
    final Image icon = createCloseImage(parent.getDisplay(),
          parent.getBackground(), parent.getForeground());
    item.setImage(icon);
    item.addListener(SWT.Dispose, new Listener() {
      @Override
      public void handleEvent(Event event) {
        icon.dispose();
      }
    });
    item.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        onClose(event);
      }
    });
    return toolbar;
  }

  private void calculateTitleSize() {
    if ((style & SWT.TITLE) != 0) {
      titleLabel.pack();
      titleSize = titleLabel.getSize();
      if (titleIcon != null) {
        final Point iconSize = titleIcon.getSize();
        titleSize.x += iconSize.x + titleWidgetSpacing;
        titleSize.y = Math.max(titleSize.y, iconSize.y);
      }
      if (closeButton != null) {
        closeButton.pack();
        final Point closeSize = closeButton.getSize();
        titleSize.x += closeSize.x + titleWidgetSpacing;
        titleSize.y = Math.max(titleSize.y, closeSize.y);
      }
      titleSize.y += titleSpacing;
    } else {
      titleSize = new Point(0, 0);
    }
  }

  private int calculateAnchor() {
    int anchor = preferredAnchor;
    if ((anchor != SWT.NONE) && autoAnchor && (locX != Integer.MIN_VALUE)) {
      final Rectangle screen = shell.getDisplay().getClientArea();
      final int marginWidth = marginLeft + marginRight;
      final int marginHeight = marginTop + marginBottom;
      if ((anchor & SWT.LEFT) != 0) {
        if (((locX + contentsSize.x + marginWidth) - SCREEN_MARGIN_WIDTH) >= (screen.x + screen.width)) {
          anchor = (anchor - SWT.LEFT) + SWT.RIGHT;
        }
      } else {  // RIGHT
        if (((locX - contentsSize.x - marginWidth) + SCREEN_MARGIN_WIDTH) < screen.x) {
          anchor = (anchor - SWT.RIGHT) + SWT.LEFT;
        }
      }
      if ((anchor & SWT.TOP) != 0) {
        if ((locY + contentsSize.y + SCREEN_MARGIN_HEIGHT + marginHeight)
              >= (screen.y + screen.height)) {
          anchor = (anchor - SWT.TOP) + SWT.BOTTOM;
        }
      } else {  // BOTTOM
        if ((locY - contentsSize.y - SCREEN_MARGIN_HEIGHT - marginHeight) < screen.y) {
          anchor = (anchor - SWT.BOTTOM) + SWT.TOP;
        }
      }
    }
    LOGGER.debug("Set anchor to {}", anchor);
    return anchor;
  }

  private Point setShellSize(int anchor) {
    final int marginWidth = marginLeft + marginRight;
    final int marginHeight = marginTop + marginBottom;
    Point shellSize;
    if (anchor == SWT.NONE) {
      shellSize = new Point(contentsSize.x + marginWidth,
          contentsSize.y + marginHeight);
      shellSize.x = Math.max(shellSize.x, ANCHOR_MIN_SPACE + marginWidth);
      shellSize.y = Math.max(shellSize.y, BALLOON_MARGIN + marginHeight);
    } else if ((anchor & (SWT.TOP | SWT.BOTTOM)) != 0) {
      //  the anchor is at the top or bottom
      shellSize = new Point(contentsSize.x +marginWidth,
          contentsSize.y + marginHeight + ANCHOR_SIZE);
      shellSize.x = Math.max(shellSize.x, ANCHOR_MIN_SPACE + marginWidth);
      shellSize.y = Math.max(shellSize.y, ANCHOR_SIZE + BALLOON_MARGIN + marginHeight);
    } else if ((anchor & (SWT.LEFT | SWT.RIGHT)) != 0) {
      //  the anchor is at the left or right
      shellSize = new Point(contentsSize.x +marginWidth + ANCHOR_SIZE,
          contentsSize.y + marginHeight);
      shellSize.x = Math.max(shellSize.x, ANCHOR_SIZE + BALLOON_MARGIN + marginWidth);
      shellSize.y = Math.max(shellSize.y, ANCHOR_MIN_SPACE + marginHeight);
    } else {
      //  impossible situation
      shellSize = new Point(0, 0);
      SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    }
    LOGGER.debug("Set shell size to {}", shellSize);
    shell.setSize(shellSize);
    return shellSize;
  }

  private void putShellChildren(int anchor, Point shellSize) {
    final int offsetTop = marginTop + (((anchor & SWT.TOP) != 0) ? ANCHOR_SIZE : 0);
    final int offsetLeft = marginLeft + ((anchor == SWT.LEFT) ? ANCHOR_SIZE : 0);
    final int offsetRight = marginRight + ((anchor == SWT.RIGHT) ? ANCHOR_SIZE : 0);
    if ((style & SWT.TITLE) != 0) {
      final int realTitleHeight = titleSize.y - titleSpacing;
      if (titleIcon != null) {
        final Point iconSize = titleIcon.getSize();
        titleIcon.setLocation(offsetLeft,
            offsetTop + ((realTitleHeight - iconSize.y) / 2));
        LOGGER.debug("Set title icon at {}", titleIcon.getLocation());
        final Point labelSize = titleLabel.getSize();
        titleLabel.setLocation(offsetLeft + iconSize.x + titleWidgetSpacing,
            offsetTop + ((realTitleHeight - labelSize.y) / 2));
        LOGGER.debug("Set title label at {}", titleLabel.getLocation());
      } else {
        final Point labelSize = titleLabel.getSize();
        titleLabel.setLocation(offsetLeft,
            offsetTop + ((realTitleHeight - labelSize.y) / 2));
        LOGGER.debug("Set title label at {}", titleLabel.getLocation());
      }
      if (closeButton != null) {
        final Point closeSize = closeButton.getSize();
        closeButton.setLocation(shellSize.x - offsetRight - closeSize.x,
            offsetTop + ((realTitleHeight - closeSize.y) / 2));
        LOGGER.debug("Set close button at {}", closeButton.getLocation());
      }
    }
    final int contentsLocY = titleSize.y + offsetTop;
    LOGGER.debug("Set contents at {}, {}", offsetLeft, contentsLocY);
    contents.setLocation(offsetLeft, contentsLocY);
  }

  private void setShellShape(int anchor, Point shellSize) {
    //  set the shape of the shell
    final int[] shape = createOutline(shellSize, anchor, true);
    final Region region = new Region();
    region.add(shape);
    shell.setRegion(region);
    shell.addListener(SWT.Dispose, new Listener() {
      @Override
      public void handleEvent(Event event) {
        region.dispose();
      }
    });
    //  draw the border of the shell
    final int[] border = createOutline(shellSize, anchor, false);
    shell.addListener(SWT.Paint, new Listener() {
      @Override
      public void handleEvent(Event event) {
        LOGGER.debug("onPaint()");
        event.gc.drawPolygon(border);
      }
    });
  }

  private void setShellLocation(int anchor, Point shellSize) {
    final Rectangle screen = shell.getDisplay().getClientArea();
    if (locX == Integer.MIN_VALUE) {
      return;
    }
    final Point shellLoc = new Point(locX, locY);
    switch (anchor) {
    case SWT.LEFT | SWT.TOP:
      shellLoc.x -= (CORNER_RADIUS | ANCHOR_CORNER_OFFSET);
      break;
    case SWT.TOP:
      shellLoc.x -= shellSize.x / 2;
      break;
    case SWT.RIGHT | SWT.TOP:
      shellLoc.x = (shellLoc.x - shellSize.x) + (CORNER_RADIUS | ANCHOR_CORNER_OFFSET);
      break;
    case SWT.RIGHT:
      shellLoc.x -= shellSize.x;
      shellLoc.y -= shellSize.y / 2;
      break;
    case SWT.RIGHT | SWT.BOTTOM:
      shellLoc.x = (shellLoc.x - shellSize.x) + (CORNER_RADIUS | ANCHOR_CORNER_OFFSET);
      shellLoc.y = (shellLoc.y - shellSize.y);
      break;
    case SWT.BOTTOM:
      shellLoc.x -= shellSize.x / 2;
      shellLoc.y = (shellLoc.y - shellSize.y);
      break;
    case SWT.LEFT | SWT.BOTTOM:
      shellLoc.x -= (CORNER_RADIUS | ANCHOR_CORNER_OFFSET);
      shellLoc.y = (shellLoc.y - shellSize.y);
      break;
    case SWT.LEFT:
      shellLoc.y -= shellSize.y / 2;
      break;
    default:
      break;
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
    LOGGER.debug("createOutline: {}, {}, {}", size, anchor, outer);
    final int o = outer ? OUTER_OFFSET : 0;
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
          w - 17 - o, 2, w - 17 - o, 1, w - 16 - o, 1, w - 16 - o, 0, w - 16, 0,
          w - 16, 20,
          // top and top right
          w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22, w - 3, 23,
          w - 2, 23, w - 2, 25, w - 1, 25,
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
          32 + o, 17, 32 + o, 18, 33 + o, 18, 33 + o, 19, 34 + o, 19, 34 + o, 20,
          // top and top right
          w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22, w - 3, 23,
          w - 2, 23, w - 2, 25, w - 1, 25,
          // right and bottom right
          w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4, w - 3, h - 3,
          w - 4, h - 3, w - 4, h - 2, w - 6, h - 2, w - 6, h - 1,
          // bottom and bottom left
          5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4, 1, h - 4,
          1, h - 6, 0, h - 6,
          // left and top left
          0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21 };
    case SWT.TOP:
      return new int[] {
          // top and anchor
          5, 20, (w / 2) - 10 - o, 20, (w / 2) - o, 0, (w / 2), 0, (w / 2) + 10, 20,
          //  top right
          w - 6, 20, w - 6, 21, w - 4, 21, w - 4, 22, w - 3, 22,  w - 3, 23,
          w - 2, 23, w - 2, 25, w - 1, 25,
          // right and bottom right
          w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4, w - 3, h - 3,
          w - 4, h - 3, w - 4, h - 2, w - 6, h - 2, w - 6, h - 1,
          // bottom and bottom left
          5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4, 1, h - 4,
          1, h - 6, 0, h - 6,
          // left and top left
          0, 25, 1, 25, 1, 23, 2, 23, 2, 22, 3, 22, 3, 21, 5, 21};
    case SWT.BOTTOM:
      return new int[] {
          // top and top right
          5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2, w - 3, 3,
          w - 2, 3, w - 2, 5, w - 1, 5,
          // right and bottom right
          w - 1, h - 26, w - 2, h - 26, w - 2, h - 24, w - 3, h - 24,
          w - 3, h - 23, w - 4, h - 23, w - 4, h - 22, w - 6, h - 22,
          w - 6, h - 21,
          // bottom and anchor
          (w / 2) + 10, h - 21, (w / 2), h - 1, (w / 2) - 10, h - 21,
          // bottom left
          5, h - 21, 5, h - 22, 3, h - 22, 3, h - 23, 2, h - 23, 2, h - 24,
          1, h - 24, 1, h - 26, 0, h - 26,
          // left and top left
          0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
    case SWT.LEFT:
      return new int[] {
          // top and top right
          25, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2, w - 3, 3,
          w - 2, 3, w - 2, 5, w - 1, 5,
          // right and bottom right
          w - 1, h - 6, w - 2, h - 6, w - 2, h - 4, w - 3, h - 4, w - 3, h - 3,
          w - 4, h - 3, w - 4, h - 2, w - 6, h - 2, w - 6, h - 1,
          // bottom and bottom left
          25, h - 1, 25, h - 2, 23, h - 2, 23, h - 3, 22, h - 3, 22, h - 4, 21, h - 4,
          21, h - 6, 20, h - 6,
          // left and anchor
          20, (h / 2) + 10, 0, h / 2, 0, (h / 2) - o, 20, (h / 2) - 10 - o,
          // top left
          20, 5, 21, 5, 21, 3, 22, 3, 22, 2, 23, 2, 23, 1, 25, 1 };
    case SWT.RIGHT:
      return new int[] {
          // top and top right
          5, 0, w - 26, 0, w - 26, 1, w - 24, 1, w - 24, 2, w - 23, 2, w - 23, 3,
          w - 22, 3, w - 22, 5, w - 21, 5,
          //  right and anchor
          w - 21, (h / 2) - 10 - o, w - 1, (h / 2) - o, w - 1, h / 2,  w - 21, (h / 2) + 10,
          // bottom right
          w - 21, h - 6, w - 22, h - 6, w - 22, h - 4, w - 23, h - 4, w - 23, h - 3,
          w - 24, h - 3, w - 24, h - 2, w - 26, h - 2, w - 26, h - 1,
          // bottom and bottom left
          5, h - 1, 5, h - 2, 3, h - 2, 3, h - 3, 2, h - 3, 2, h - 4, 1, h - 4,
          1, h - 6, 0, h - 6,
          // left and top left
          0, 5, 1, 5, 1, 3, 2, 3, 2, 2, 3, 2, 3, 1, 5, 1 };
    default:
      return new int[] {
          // top and top right
          5, 0, w - 6, 0, w - 6, 1, w - 4, 1, w - 4, 2, w - 3, 2, w - 3, 3,
          w - 2, 3, w - 2, 5, w - 1, 5,
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
