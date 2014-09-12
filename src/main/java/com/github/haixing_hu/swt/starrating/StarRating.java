/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *     Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.starrating;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Instances of this class provide a rating element.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>HORIZONTAL</dd>
 * <dd>VERTICAL</dd>
 * <p>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * The implementation was modified by Haixing Hu in order to change the behavior
 * of clicking a star:
 * <ul>
 * <li>If the star has not been selected, click on it will select it and all
 * starts below it.</li>
 * <li>If the star has been selected and is the highest selected star, click on
 * it will deselect it.</li>
 * <li>If the star has been selected and is not the highest selected star, click
 * on it will deselect all selected stars above it, but does not deselect it.
 * </ul>
 *
 * @author Laurent CARON
 * @author Haixing Hu
 */
public class StarRating extends Canvas {

  /**
   * The default value of the maximum number of stars.
   */
  public static final int DEFAULT_MAX_NUMBERS_OF_STARS = 5;

  private static final int SIZE_SMALL = 16;
  private static final int SIZE_BIG = 32;

  public enum Size {
    SMALL, BIG
  };

  private final Size sizeOfStars;
  private final int maxNumberOfStars;
  private final int orientation;
  private final Star stars[];
  private final List<SelectionListener> selectionListeners;
  private int currentNumberOfStars;

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance, with the default maximum number of
   * stars ({@link #DEFAULT_MAX_NUMBERS_OF_STARS}).
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must be
   * built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
   * constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent
   *          a composite control which will be the parent of the new instance
   *          (cannot be null)
   * @param style
   *          the style of control to construct
   * @param sizeOfStar
   *          the size of the stars.
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              </ul>
   * @see #DEFAULT_MAX_NUMBERS_OF_STARS
   */
  public StarRating(final Composite parent, final int style, Size sizeOfStar) {
    this(parent, style, sizeOfStar, DEFAULT_MAX_NUMBERS_OF_STARS);
  }

  /**
   * Constructs a new instance of this class given its parent and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in class
   * <code>SWT</code> which is applicable to instances of this class, or must be
   * built by <em>bitwise OR</em>'ing together (that is, using the
   * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
   * constants. The class description lists the style constants that are
   * applicable to the class. Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent
   *          a composite control which will be the parent of the new instance
   *          (cannot be null)
   * @param style
   *          the style of control to construct
   * @param sizeOfStar
   *          the size of the stars.
   * @param maxNumberOfStars
   *          the maximum number of stars.
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              </ul>
   *
   */
  public StarRating(final Composite parent, final int style,
      Size sizeOfStar, int maxNumberOfStars) {
    super(parent, checkStyle(style) | SWT.DOUBLE_BUFFERED);
    this.sizeOfStars = sizeOfStar;
    this.maxNumberOfStars = maxNumberOfStars;
    if ((style & SWT.VERTICAL) != 0) {
      orientation = SWT.VERTICAL;
    } else {
      orientation = SWT.HORIZONTAL;
    }
    stars = new Star[maxNumberOfStars];
    selectionListeners = new ArrayList<SelectionListener>();
    currentNumberOfStars = 0;

    initStars();
    initListeners();
  }

  private static int checkStyle(int style) {
    if ((style & SWT.VERTICAL) != 0) {
      style = style & ~ SWT.VERTICAL;
    }

    if ((style & SWT.HORIZONTAL) != 0) {
      style = style & ~ SWT.HORIZONTAL;
    }
    return style;
  }

  private void initStars() {
    for (int i = 0; i < maxNumberOfStars; i++) {
      stars[i] = new Star(this);
    }
  }

  private void initListeners() {
    final Listener listener = new Listener() {
      @Override
      public void handleEvent(final Event event) {
        switch (event.type) {
        case SWT.MouseEnter:
        case SWT.MouseMove:
          onMouseEnterOrMove(event);
          break;
        case SWT.MouseExit:
          onMouseExit(event);
          break;
        case SWT.MouseUp:
          onMouseUp(event);
          break;
        case SWT.Paint:
          onMousePaint(event);
          break;
        case SWT.Dispose:
          onDispose(event);
          break;
        }
      }
    };

    final int[] events = new int[] { SWT.MouseEnter, SWT.MouseMove,
        SWT.MouseExit, SWT.MouseUp, SWT.Paint, SWT.Dispose };
    for (final int event : events) {
      addListener(event, listener);
    }
  }

  private void onMouseEnterOrMove(final Event event) {
    for (final Star star : stars) {
      star.hover = false;
    }

    for (final Star star : stars) {
      final boolean mouseHover = star.bounds.contains(event.x, event.y);
      star.hover = true;
      if (mouseHover) {
        break;
      }
    }
    redraw();
    update();
  }

  private void onMouseExit(final Event event) {
    for (final Star star : stars) {
      star.hover = false;
    }
    redraw();
    update();
  }

  private void onMouseUp(final Event event) {
    for (int i = 0; i < maxNumberOfStars; i++) {
      final Star star = stars[i];
      final boolean selected = star.bounds.contains(event.x, event.y);
      if (selected) {
        // modified by Haixing Hu.
        // check whether this star is the highest selected star
        if ((i + 1) == currentNumberOfStars) {
          // if this star is the highest selected star, click on it will deselect it.
          setCurrentNumberOfStars(i);
        } else {
          //  otherwise, click on it will select it and all stars below it, and
          //  deselect all starts above it.
          setCurrentNumberOfStars(i + 1);
        }
        fireSelectionEvent();
        redraw();
        update();
        break;
      }
    }
  }

  private void fireSelectionEvent() {
    final Event event = new Event();
    event.widget = this;
    event.display = getDisplay();
    event.item = this;
    event.type = SWT.Selection;
    for (final SelectionListener selectionListener : selectionListeners) {
      selectionListener.widgetSelected(new SelectionEvent(event));
    }
  }

  private void onMousePaint(final Event event) {
    final GC gc = event.gc;
    int x = 0, y = 0;

    for (final Star star : stars) {
      star.draw(gc, x, y);
      if (orientation == SWT.VERTICAL) {
        y += sizeOfStars.equals(Size.BIG) ? SIZE_BIG : SIZE_SMALL;
      } else {
        x += sizeOfStars.equals(Size.BIG) ? SIZE_BIG : SIZE_SMALL;
      }
    }
  }

  private void onDispose(final Event event) {
    for (final Star star : stars) {
      star.dispose();
    }
  }

  /**
   * Adds the listener to the collection of listeners who will be notified when
   * the control is selected by the user, by sending it one of the messages
   * defined in the <code>SelectionListener</code> interface.
   * <p>
   * <code>widgetDefaultSelected</code> is not called.
   * </p>
   *
   * @param listener
   *          the listener which should be notified when the control is selected
   *          by the user,
   *
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   * @see SelectionListener
   * @see #removeSelectionListener
   * @see SelectionEvent
   */
  public void addSelectionListener(final SelectionListener listener) {
    checkWidget();
    if (listener == null) {
      SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }
    this.selectionListeners.add(listener);
  }

  /**
   * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
   */
  @Override
  public Point computeSize(final int wHint, final int hHint,
      final boolean changed) {
    if (orientation == SWT.VERTICAL) {
      return computeSizeVertical();
    }
    return computeSizeHorizontal();
  }

  private Point computeSizeVertical() {
    final int width = sizeOfStars.equals(Size.BIG) ? SIZE_BIG : SIZE_SMALL;
    final int height = maxNumberOfStars * width;
    return new Point(width + (getBorderWidth() * 2), height
        + (getBorderWidth() * 2));
  }

  private Point computeSizeHorizontal() {
    final int height = sizeOfStars.equals(Size.BIG) ? SIZE_BIG : SIZE_SMALL;
    final int width = maxNumberOfStars * height;
    return new Point(width + (getBorderWidth() * 2), height
        + (getBorderWidth() * 2));
  }

  /**
   * @return the number of selected stars
   *
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   */
  public final int getCurrentNumberOfStars() {
    return currentNumberOfStars;
  }

  /**
   * @return the maximum number of stars that is displayed by this component
   *
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   */
  public final int getMaxNumberOfStars() {
    return maxNumberOfStars;
  }

  /**
   * @return the orientation of this widget (SWT.VERTICAL or SWT.HORIZONTAL)
   *
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   */
  @Override
  public final int getOrientation() {
    return orientation;
  }

  /**
   * @return the size of stars
   *
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   */
  public final Size getSizeOfStars() {
    return sizeOfStars;
  }

  /**
   * Removes the listener from the collection of listeners who will be notified
   * when the control is selected by the user.
   *
   * @param listener
   *          the listener which should no longer be notified
   *
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   * @see SelectionListener
   * @see #addSelectionListener
   */
  public void removeSelectionListener(final SelectionListener listener) {
    checkWidget();
    if (listener == null) {
      SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }
    this.selectionListeners.remove(listener);
  }

  /**
   * Set the current number of stars
   *
   * @param currentNumberOfStars
   *          current number of stars
   *
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_INVALID_ARGUMENT - if the number of star is negative
   *              or greater than the maximum number of stars</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the receiver</li>
   *              </ul>
   *
   */
  public void setCurrentNumberOfStars(final int currentNumberOfStars) {
    checkWidget();
    if ((currentNumberOfStars < 0) || (currentNumberOfStars > maxNumberOfStars)) {
      SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    }
    this.currentNumberOfStars = currentNumberOfStars;
    for (int i = 0; i < currentNumberOfStars; ++i) {
      stars[i].marked = true;
    }
    for (int i = currentNumberOfStars; i < maxNumberOfStars; ++i) {
      stars[i].marked = false;
    }
  }
}
