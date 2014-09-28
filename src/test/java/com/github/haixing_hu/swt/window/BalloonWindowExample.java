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

package com.github.haixing_hu.swt.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.github.haixing_hu.swt.utils.SWTResourceManager;
import com.github.haixing_hu.swt.utils.SWTUtils;

/**
 * This snippet demonstrates the {@link BalloonWindow} widget.
 *
 * @author Haixing Hu
 */
public class BalloonWindowExample {

  public static void main(String[] args) {

    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setText("BalloonWindow Example");
    shell.setLayout(new GridLayout(1, false));
    shell.setSize(640, 350);
    SWTUtils.centerShell(shell);

    createButton(shell, SWT.NONE);
    createButton(shell, SWT.LEFT | SWT.TOP);
    createButton(shell, SWT.RIGHT | SWT.TOP);
    createButton(shell, SWT.LEFT | SWT.BOTTOM);
    createButton(shell, SWT.RIGHT | SWT.BOTTOM);
    createButton(shell, SWT.TOP);
    createButton(shell, SWT.BOTTOM);
    createButton(shell, SWT.LEFT);
    createButton(shell, SWT.RIGHT);

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    SWTResourceManager.dispose();
    display.dispose();
  }

  private static String anchorToString(int anchor) {
    final StringBuilder builder = new StringBuilder();
    if ((anchor & SWT.TOP) != 0) {
      builder.append(" | SWT.TOP");
    }
    if ((anchor & SWT.LEFT) != 0) {
      builder.append(" | SWT.LEFT");
    }
    if ((anchor & SWT.BOTTOM) != 0) {
      builder.append(" | SWT.BOTTOM");
    }
    if ((anchor & SWT.RIGHT) != 0) {
      builder.append(" | SWT.RIGHT");
    }
    if (builder.length() > 0) {
      return builder.substring(2);
    } else {
      return "SWT.NONE";
    }
  }

  private static void createButton(final Shell shell, final int anchor) {
    final Button button = new Button(shell, SWT.NONE);
    button.setText("BalloonWindows's anchor is: " + anchorToString(anchor));
    button.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        onClick();
      }
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        onClick();
      }
      public void onClick() {
        System.err.println("Button clicked.");
        //  we should recompute the location of the label before show the balloon
        //  window
        final BalloonWindow bw = createBalloonWindow(shell, anchor);
        final Point loc = button.toDisplay(button.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        bw.setLocation(loc);
        bw.open();
      }
    });
  }

  private static BalloonWindow createBalloonWindow(Shell shell, int anchor) {
    final int style =  SWT.TITLE;
    final BalloonWindow bw = new BalloonWindow(shell, style);
    bw.setTitle("This is the title text");
    final Display display = shell.getDisplay();
    bw.setImage(display.getSystemImage(SWT.ICON_WARNING));
    final Label label = new Label(bw.getContents(), SWT.WRAP);
    label.setText("You can add any widgets to the contents composite of the "
        + "balloon window. You can add any widgets to the contents composite of the "
        + "balloon window. You can add any widgets to the contents composite of the "
        + "balloon window. You can add any widgets to the contents composite of the "
        + "balloon window. You can add any widgets to the contents composite of the "
        + "balloon window. ");
    label.setSize(label.computeSize(300, SWT.DEFAULT));
    label.setBackground(bw.getShell().getBackground());
    bw.getContents().setSize(label.getSize());
    bw.addSelectionControl(label);
    bw.setAnchor(anchor);
    return bw;
  }
}
