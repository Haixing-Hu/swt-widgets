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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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

  public static void main(String[] args)
  {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setText("BalloonWindow Example");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(340, 250);
    SWTUtils.centerShell(shell);

    final Button button = new Button(shell, SWT.NONE);
    button.setText("Click this button to show a balloon window.");
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
        final BalloonWindow bw = createBalloonWindow(shell.getDisplay());
        final Point loc = button.toDisplay(button.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        bw.setLocation(loc);
        bw.open();
      }
    });

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    SWTResourceManager.dispose();
    display.dispose();
  }

  private static BalloonWindow createBalloonWindow(Display display) {
    final int style =  SWT.ON_TOP | SWT.TITLE | SWT.CLOSE;
    final BalloonWindow bw = new BalloonWindow(display, style);
    bw.setTitleText("This is the title text");
    final Image img = SWTResourceManager.getImage(display,
        BalloonWindowExample.class, "/images/warning.png");
    bw.setTitleImage(img);

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
    bw.setAnchor(SWT.TOP | SWT.LEFT);
    return bw;
  }
}
