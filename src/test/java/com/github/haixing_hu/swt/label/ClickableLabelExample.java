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

package com.github.haixing_hu.swt.label;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.haixing_hu.swt.dialog.Dialog;
import com.github.haixing_hu.swt.utils.SWTResourceManager;
import com.github.haixing_hu.swt.utils.SWTUtils;

/**
 * This snippet demonstrates the {@link ClickableLabel} widget.
 *
 * @author Haixing Hu
 */
public class ClickableLabelExample {

  public static void main(final String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);

    shell.setText("ClickableLabel Example");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(640, 350);

    final ClickableLabel label = new ClickableLabel(shell, SWT.NONE);
    label.setText("Click this label to display a balloon window.");
    label.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
    label.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        onClick();
      }
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        onClick();
      }
      private void onClick() {
        Dialog.inform("Click Event", "The label has been clicked.");
      }
    });

    SWTUtils.centerShell(shell);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    SWTResourceManager.dispose();
    display.dispose();
  }

}
