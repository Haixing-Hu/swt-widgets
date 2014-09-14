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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * Code snippet for the Tooltip class.
 *
 * @author Haixing Hu
 */
public class Snippet225 {

  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    Image image = null;
    final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
    tip.setMessage("Here is a message for the user. When the message is too "
        + "long it wraps. I should say something cool but nothing "
        + "comes to my mind.");
    final Tray tray = display.getSystemTray();
    if (tray != null) {
      final TrayItem item = new TrayItem(tray, SWT.NONE);
      image = display.getSystemImage(SWT.ICON_INFORMATION);
      item.setImage(image);
      tip.setText("Notification from a tray item");
      item.setToolTip(tip);
    } else {
      tip.setText("Notification from anywhere");
      tip.setLocation(400, 400);
    }
    final Button button = new Button(shell, SWT.PUSH);
    button.setText("Press for balloon tip");
    button.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        tip.setVisible(true);
      }
    });
    final Rectangle clientArea = shell.getClientArea();
    button.setLocation(clientArea.x, clientArea.y);
    button.pack();
    shell.setBounds(50, 50, 300, 200);
    shell.open();
    while (! shell.isDisposed()) {
      if (! display.readAndDispatch()) {
        display.sleep();
      }
    }
    if (image != null) {
      image.dispose();
    }
    display.dispose();
  }
}
