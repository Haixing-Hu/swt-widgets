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

package com.github.haixing_hu.swt.menu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.haixing_hu.swt.action.ActionEx;
import com.github.haixing_hu.swt.action.IActionManager;
import com.github.haixing_hu.swt.toolbar.Separator;

/**
 * A {@link IMenuCreator} which create a sub-menu.
 *
 * @author Haixing Hu
 */
public class SubMenuCreator implements IMenuCreator {

  private final IActionManager actionManager;
  private final String[] subActionIds;
  private final boolean showImage;
  private final Map<Object, MenuManagerEx> subMenuCache;
  private final Logger logger;

  /**
   * Constructs a sub-menu creator.
   *
   * @param actionManager
   *          a map from the ID of the action to the action object.
   * @param subActionIds
   *          an array of IDs of the sub-action which will be used to create the
   *          menu items in the sub-menus created by this creator.
   * @param showImage
   *          indicates whether the sub-menu created by this creator should
   *          display the image on its menu items.
   */
  public SubMenuCreator(IActionManager actionManager, String[] subActionIds,
      boolean showImage) {
    if ((actionManager == null) || (subActionIds == null)) {
      SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }
    this.actionManager = actionManager;
    this.subActionIds = subActionIds;
    this.showImage = showImage;
    subMenuCache = new HashMap<Object, MenuManagerEx>();
    logger = LoggerFactory.getLogger(SubMenuCreator.class);
  }

  @Override
  public void dispose() {
    for (final Map.Entry<Object, MenuManagerEx> entry : subMenuCache.entrySet()) {
      final MenuManagerEx menuManager = entry.getValue();
      menuManager.dispose();
    }
    subMenuCache.clear();
  }

  @Override
  public Menu getMenu(Control parent) {
    logger.debug("Getting the submenu for parent: {}", parent);
    MenuManagerEx menuManager = subMenuCache.get(parent);
    if (menuManager == null) {
      logger.debug("Creating a submenu for parent: {}", parent);
      menuManager = new MenuManagerEx(showImage);
      addActions(menuManager);
      menuManager.createContextMenu(parent);
      subMenuCache.put(parent, menuManager);
    }
    return menuManager.getMenu();
  }

  @Override
  public Menu getMenu(Menu parent) {
    logger.debug("Getting the submenu for parent: {}", parent);
    MenuManagerEx menuManager = subMenuCache.get(parent);
    if (menuManager == null) {
      logger.debug("Creating a submenu for parent: {}", parent);
      menuManager = new MenuManagerEx(showImage);
      addActions(menuManager);
      menuManager.createSubMenu(parent);
      subMenuCache.put(parent, menuManager);
    }
    return menuManager.getMenu();
  }

  /**
   * Gets all menu managers created by this creator.
   *
   * @return the collection of all menu managers created by this creator.
   */
  public Collection<MenuManagerEx> getMenuManagers() {
    return subMenuCache.values();
  }

  private void addActions(MenuManagerEx menuManager) {
    for (final String id : subActionIds) {
      if (id.equals(Separator.ID)) {
        logger.debug("Adding a separator to the sub-menu: {}", id);
        menuManager.add(new Separator());
      } else {
        final ActionEx action = actionManager.get(id);
        if (action != null) {
          logger.debug("Adding an action to the sub-menu: {}", id);
          menuManager.add(action);
        } else {
          logger.error("Cannot found the action in the action manager: {}", id);
        }
      }
    }
  }
}
