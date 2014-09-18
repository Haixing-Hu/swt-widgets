/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *     Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.github.haixing_hu.swt.utils.SWTGraphicUtils;

/**
 * Instances of this class are controls located on the top of a shell. They
 * display a blurred version of the content of the shell
 */
public class BlurredPanel {
  private static final String BLURED_PANEL_KEY = BlurredPanel.class.getName();

  private final Shell parent;
  private int radius;
  private Shell panel;
  private Canvas canvas;

  /**
   * Constructs a new instance of this class given its parent.
   *
   * @param shell
   *          a shell that will be the parent of the new instance (cannot be
   *          null)
   * @exception IllegalArgumentException
   *              <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *              </ul>
   * @exception SWTException
   *              <ul>
   *              <li>ERROR_WIDGET_DISPOSED - if the parent has been disposed</li>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              thread that created the parent</li>
   *              </ul>
   */
  public BlurredPanel(final Shell shell) {
    if (shell == null) {
      SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }

    if (shell.isDisposed()) {
      SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    }
    parent = shell;
    if (shell.getData(BLURED_PANEL_KEY) != null) {
      throw new IllegalArgumentException(
          "This shell has already an infinite panel attached on it !");
    }
    shell.setData(BLURED_PANEL_KEY, this);
    radius = 2;
  }

  /**
   * Show the blurred panel
   */
  public void show() {
    if (parent.isDisposed()) {
      SWT.error(SWT.ERROR_WIDGET_DISPOSED);
    }

    panel = new Shell(parent, SWT.APPLICATION_MODAL | SWT.NO_TRIM);
    panel.setLayout(new FillLayout());

    panel.addListener(SWT.KeyUp, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        event.doit = false;
      }
    });

    canvas = new Canvas(panel, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
    canvas.addPaintListener(new PaintListener() {

      @Override
      public void paintControl(final PaintEvent e) {
        paintCanvas(e);
      }
    });

    panel.setBounds(panel.getDisplay()
        .map(parent, null, parent.getClientArea()));
    panel.open();

  }

  /**
   * Paint the canvas that holds the panel
   *
   * @param e
   *          {@link PaintEvent}
   */
  private void paintCanvas(final PaintEvent e) {
    // Paint the panel
    e.gc.drawImage(createBlurredImage(), 0, 0);
  }

  private Image createBlurredImage() {
    final Display display = parent.getDisplay();
    final Point parentSize = parent.getSize();
    final Image image = new Image(display,parentSize.x, parentSize.y);
    final GC gc = new GC(parent);
    gc.copyArea(image, 0, 0);
    gc.dispose();
    final ImageData data = SWTGraphicUtils.blur(image.getImageData(), radius);
    image.dispose();
    return new Image(display, data);
  }

  /**
   * Hide the panel
   */
  public void hide() {
    if (parent.isDisposed()) {
      SWT.error(SWT.ERROR_WIDGET_DISPOSED);
    }

    if ((panel == null) || panel.isDisposed()) {
      return;
    }

    panel.dispose();
  }

  /**
   * @return the radius of the blur effect
   */
  public int getRadius() {
    return radius;
  }

  /**
   * @param radius
   *          the radius to set
   */
  public void setRadius(final int radius) {
    this.radius = radius;
  }

}
