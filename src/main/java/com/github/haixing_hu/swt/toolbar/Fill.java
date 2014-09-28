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

package com.github.haixing_hu.swt.toolbar;

import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.github.haixing_hu.lang.Argument;

/**
 * An action used as a filling space between tool items.
 *
 * @author Haixing Hu
 */
public class Fill extends Separator {

  /**
   * The ID of this action.
   */
  public static final String ID = "FILL";

  /**
   * The default width of the space.
   */
  public static final int DEFAULT_WIDTH = 20;

  private int width = DEFAULT_WIDTH;

  /**
   * Creates a filling space between tool items with the default width.
   *
   * @see {@link #DEFAULT_WIDTH}
   */
  public Fill() {
    super();
  }

  /**
   * Creates a filling space between tool items with the specified width.
   *
   * @param width
   *          the width of the space.
   */
  public Fill(int width) {
    super();
    this.width = Argument.requirePositive("width", width);
  }

  /**
   * Gets the width.
   *
   * @return the width.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the width.
   *
   * @param width
   *          the new width to set.
   */
  public void setWidth(int width) {
    this.width = Argument.requirePositive("width", width);
  }

  @Override
  public void fill(ToolBar toolbar, int index) {
    ToolItem item;
    if (index >= 0) {
      item = new ToolItem(toolbar, SWT.SEPARATOR_FILL, index);
    } else {
      item = new ToolItem(toolbar, SWT.SEPARATOR_FILL);
    }
    item.setWidth(width);
  }
}
