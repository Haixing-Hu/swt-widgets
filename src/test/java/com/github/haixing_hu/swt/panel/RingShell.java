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

package com.github.haixing_hu.swt.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RingShell {
  int[] createCircle(int radius, int centerX, int centerY) {
    final int[] points = new int[360 * 2];
    for (int i = 0; i < 360; i++) {
      points[i * 2] = centerX + (int) (radius * Math.cos(Math.toRadians(i)));
      points[(i * 2) + 1] = centerY
          + (int) (radius * Math.sin(Math.toRadians(i)));
    }
    return points;
  }

  Point originalPosition = null;

  public RingShell() {
    final Display display = new Display();
    final Shell shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
    shell.setBackground(display.getSystemColor(SWT.COLOR_DARK_MAGENTA));
    final Region region = new Region();
    region.add(createCircle(100, 100, 100));
    region.subtract(createCircle(50, 100, 100));
    shell.setRegion(region);
    // Make the shell movable by using the mouse pointer.
    shell.addMouseListener(new MouseListener() {
      @Override
      public void mouseDoubleClick(MouseEvent e) {
        shell.dispose(); // Double click to dispose the shell.
      }

      @Override
      public void mouseDown(MouseEvent e) {
        originalPosition = new Point(e.x, e.y);
      }

      @Override
      public void mouseUp(MouseEvent e) {
        originalPosition = null;
      }
    });
    shell.addMouseMoveListener(new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
        if (originalPosition == null) {
          return;
        }
        final Point point = display.map(shell, null, e.x, e.y);
        shell.setLocation(point.x - originalPosition.x, point.y
            - originalPosition.y);
        System.out.println("Moved from: " + originalPosition + " to " + point);
      }
    });
    final Rectangle regionBounds = region.getBounds();
    shell.setSize(regionBounds.width, regionBounds.height);
    shell.open();

    // Set up the event loop.
    while (! shell.isDisposed()) {
      if (! display.readAndDispatch()) {
        // If no more entries in event queue
        display.sleep();
      }
    }
    display.dispose();
    region.dispose();
  }

  public static void main(String[] args) {
    new RingShell();
  }
}
