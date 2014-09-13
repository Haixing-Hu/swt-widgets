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
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TypedListener;

/**
 * The extension of the {@link Label} widget which could response to the mouse
 * single click event.
 *
 * @author Haixing Hu
 */
public class ClickableLabel extends Label {

  private boolean mousePressed;

  public ClickableLabel(Composite parent, int style) {
    super(parent, style);
    mousePressed = false;
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        mousePressed= true;
      }
      @Override
      public void mouseUp(MouseEvent e) {
        if (mousePressed) {
          mousePressed = false;
          final Event event = new Event();
          event.widget = e.widget;
          event.type = SWT.Selection;
          event.widget.notifyListeners(SWT.Selection, event);
        }
      }
    });
  }

  @Override
  protected void checkSubclass () {
    //  make this method empty in order to avoid prohibiting the subclass
    //  of Label.
  }


  /**
   * Adds the listener to receive the selection (click) events.
   *
   * @param listener
   *          the listener to be added.
   * @exception SWTError
   *              (ERROR_THREAD_INVALID_ACCESS) when called from the wrong
   *              thread
   * @exception SWTError
   *              (ERROR_WIDGET_DISPOSED) when the widget has been disposed
   * @exception SWTError
   *              (ERROR_NULL_ARGUMENT) when listener is null
   */
  public void addSelectionListener(SelectionListener listener) {
    checkWidget();
    if (listener == null) {
      SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }
    final TypedListener typedListener = new TypedListener(listener);
    addListener(SWT.Selection, typedListener);
    addListener(SWT.DefaultSelection, typedListener);
  }
}
