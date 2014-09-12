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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.github.haixing_hu.lang.DesktopApi;
import com.github.haixing_hu.swt.utils.SWTResourceManager;
import com.github.haixing_hu.swt.utils.SWTUtils;

/**
 * This snippet demonstrates the {@link HyperlinkLabel} widget.
 *
 * @author Haixing Hu
 */
public class HyperlinkLabelExample {

  /**
   * @param args
   */
  public static void main(final String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);

    shell.setText("HyperlinkLabel Label Example");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(640, 350);

    final Label label1 = new Label(shell, SWT.NONE);
    label1.setText("Contact me at ");
    label1.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

    final HyperlinkLabel link1 = new HyperlinkLabel(shell,
        SWT.SINGLE | SWT.BORDER | SWT.NO_FOCUS);
    link1.setText("szeiger@novocode.com");
    link1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    link1.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        DesktopApi.mail(link1.getText());
      }
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        DesktopApi.mail(link1.getText());
      }
    });

    final Label label2 = new Label(shell, SWT.NONE);
    label2.setText("Google: ");
    label2.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

    final HyperlinkLabel link2 = new HyperlinkLabel(shell,
        SWT.SINGLE | SWT.BORDER | SWT.NO_FOCUS);
    link2.setText("https://www.google.com");
    link2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    link2.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        DesktopApi.open(link2.getText());
      }
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        DesktopApi.open(link2.getText());
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
