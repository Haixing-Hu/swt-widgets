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

import org.eclipse.jface.action.AbstractGroupMarker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A extension of {@link org.eclipse.jface.action.Separator} which supports
 * specifying width.
 *
 * @author Haixing Hu
 */
public class Separator extends AbstractGroupMarker {

  /**
   * The ID of this action.
   */
  public static final String ID = "SEPARATOR";

  private int width = - 1;

  /**
   * Creates a separator which does not start a new group.
   */
  public Separator() {
    super();
  }

  /**
   * Creates a separator which does not start a new group.
   *
   * @param width
   *          the width of the separator.
   */
  public Separator(int width) {
    super();
    this.width = width;
  }

  /**
   * Creates a new separator which also defines a new group having the given
   * group name. The group name must not be <code>null</code> or the empty
   * string. The group name is also used as the item id.
   *
   * @param groupName
   *          the group name of the separator.
   */
  public Separator(String groupName) {
    super(groupName);
  }

  /**
   * Creates a new separator which also defines a new group having the given
   * group name. The group name must not be <code>null</code> or the empty
   * string. The group name is also used as the item id.
   *
   * @param groupName
   *          the group name of the separator.
   * @param width
   *          the width of the separator.
   */
  public Separator(String groupName, int width) {
    super(groupName);
    this.width = width;
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
    this.width = width;
  }

  @Override
  public void fill(Menu menu, int index) {
    if (index >= 0) {
      new MenuItem(menu, SWT.SEPARATOR, index);
    } else {
      new MenuItem(menu, SWT.SEPARATOR);
    }
  }

  @Override
  public void fill(ToolBar toolbar, int index) {
    ToolItem item;
    if (width <= 0) {
      if (index >= 0) {
        item = new ToolItem(toolbar, SWT.SEPARATOR, index);
      } else {
        item = new ToolItem(toolbar, SWT.SEPARATOR);
      }
    } else { // width > 0
      if (index >= 0) {
        item = new ToolItem(toolbar, SWT.SEPARATOR, index);
      } else {
        item = new ToolItem(toolbar, SWT.SEPARATOR);
      }
      item.setWidth(width);
    }
  }

  /**
   * The <code>Separator</code> implementation of this
   * <code>IContributionItem</code> method returns <code>true</code>
   */
  @Override
  public boolean isSeparator() {
    return true;
  }
}
