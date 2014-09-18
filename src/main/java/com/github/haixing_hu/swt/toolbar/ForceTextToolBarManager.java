/******************************************************************************
 *
 * Copyright (c) 2014  Haixing Hu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
