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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.ToolBar;

/**
 * An extension of {@link ToolBarManager} which forces displaying text on tool items or buttons,
 * even if an image is present.
 *
 * @author Haixing Hu
 */
public class ForceTextToolBarManager extends ToolBarManager {

  public ForceTextToolBarManager() {
    super();
  }

  public ForceTextToolBarManager(int style) {
    super(style);
  }

  public ForceTextToolBarManager(ToolBar toolBar) {
    super(toolBar);
  }

  /**
   * Adds an action to this tool bar manager.
   * <p>
   * <b>NOTE:</b> Overrides the {@link ContributionManager#add(IAction)} method
   * in order to force displaying text in each tool item.
   *
   * @param action
   *          The action to be added.
   */
  @Override
  public void add(IAction action) {
    Assert.isNotNull(action, "Action must not be null"); //$NON-NLS-1$
    final ActionContributionItem item = new ActionContributionItem(action);
    item.setMode(ActionContributionItem.MODE_FORCE_TEXT);
    add(item);
  }

}
