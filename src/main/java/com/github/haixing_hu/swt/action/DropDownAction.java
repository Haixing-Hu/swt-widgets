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

package com.github.haixing_hu.swt.action;

import java.util.Collection;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.haixing_hu.swt.menu.MenuManagerEx;
import com.github.haixing_hu.swt.menu.SubMenuCreator;

/**
 * A drop-down style action which contains sub-actions.
 *
 * @author Haixing Hu
 */
public class DropDownAction extends ActionEx {

  /**
   * The key for the separator item.
   */
  public static final String SEPARATOR_KEY = SubMenuCreator.SEPARATOR_KEY;

  protected final IActionManager actionManager;
  protected final SubMenuCreator subMenuCreator;
  protected final Logger logger;

  /**
   * Constructs an action.
   *
   * @param id
   *          the id of the new action.
   * @param actionManager
   *          a map from the ID of an action to the action itself, used as a manager
   *          of actions.
   * @param subActionIds
   *          the IDs of the sub-actions in the drop-down menu of the new
   *          action.
   */
  public DropDownAction(String id, IActionManager actionManager, String[] subActionIds) {
    super(id, IAction.AS_DROP_DOWN_MENU);
    super.setId(id);
    this.actionManager = actionManager;
    subMenuCreator = new SubMenuCreator(actionManager, subActionIds, false);
    setMenuCreator(subMenuCreator);
    logger = LoggerFactory.getLogger(this.getClass());
  }

  @Override
  public void run() {
    logger.warn("Should call runWithEvent() for the action: {}", getId());
  }

  @Override
  public void runWithEvent(Event event) {
    final IMenuCreator mc = getMenuCreator();
    if (mc == null) {
      logger.error("No menu creator for action: {}", getId());
      return;
    }
    if (event.widget instanceof ToolItem) {
      final ToolItem ti = (ToolItem) event.widget;
      final Menu menu = mc.getMenu(ti.getParent());
      //  calculate the position where to display the drop-down menu
      Point point = new Point(event.x, event.y);
      final Rectangle rect = ti.getBounds();
      point.x += rect.x;
      point.y += rect.y + rect.height;
      point = ti.getParent().toDisplay(point);
      // position the menu below the drop down item
      menu.setLocation(point.x, point.y);
      menu.setVisible(true);
    } else {
      logger.error("Cannot create pop-menu for action: {}", getId());
    }
  }

  /**
   * Updates the menu items of all the drop-down menus for this action.
   *
   */
  public final void update() {
    final Collection<MenuManagerEx> menus = subMenuCreator.getMenuManagers();
    for (final MenuManagerEx menu : menus) {
      menu.update();
    }
  }

  /**
   * Incrementally builds all the drop-down menus from this action,
   * but does not to any of their sub-menus.
   *
   * @param force
   *          <code>true</code> means update even if not dirty, and
   *          <code>false</code> for normal incremental updating
   * @see #updateAll(boolean)
   */
  public final void update(boolean force) {
    final Collection<MenuManagerEx> menus = subMenuCreator.getMenuManagers();
    for (final MenuManagerEx menu : menus) {
      menu.update(force);
    }
  }

  /**
   * Incrementally builds all the drop-down menus from this action.
   * and does so recursively for all sub-menus.
   *
   * @param force
   *          <code>true</code> means update even if not dirty, and
   *          <code>false</code> for normal incremental updating.
   * @see #update(boolean)
   */
  public final void updateAll(boolean force) {
    final Collection<MenuManagerEx> menus = subMenuCreator.getMenuManagers();
    for (final MenuManagerEx menu : menus) {
      menu.updateAll(force);
    }
  }

  /**
   * Set the visibility of a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action whose visibility is to be set.
   * @param visible
   *          the visibility to be set.
   */
  private final void setSubActionVisible(String id, boolean visible) {
    //  first, set the visibility for the action
    final ActionEx action = actionManager.get(id);
    if (action == null) {
      logger.error("Cannot found the sub-action: {}", id);
      return;
    }
    action.setVisible(visible);
    //  then, set the visibility for the contribution items
    final Collection<MenuManagerEx> menus = subMenuCreator.getMenuManagers();
    for (final MenuManagerEx menu : menus) {
      final IContributionItem item = menu.find(id);
      if (item != null) {
        item.setVisible(visible);
      } else {
        logger.error("Cannot found the sub-action: {}", id);
      }
    }
  }

  /**
   * Hides a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action to be hidden.
   */
  public final void hideSubAction(String id) {
    logger.trace("Hide the sub-action: {}", id);
    setSubActionVisible(id, false);
  }

  /**
   * Shows a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action to be shown.
   */
  public final void showSubAction(String id) {
    logger.trace("Show the sub-action: {}", id);
    setSubActionVisible(id, true);
  }

  /**
   * Hides sub-actions.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param ids
   *          the IDs of sub-actions to be hidden.
   */
  public final void hideSubActions(String ... ids) {
    logger.trace("Hide the sub-actions: {}", (Object[])ids);
    for (final String id : ids) {
      setSubActionVisible(id, false);
    }
  }

  /**
   * Shows sub-actions.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param ids
   *          the IDs of sub-actions to be shown.
   */
  public final void showSubActions(String ... ids) {
    logger.trace("Show the sub-actions: {}", (Object[])ids);
    for (final String id : ids) {
      setSubActionVisible(id, true);
    }
  }

  /**
   * Set the checking status of a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action whose checking status is to be set.
   * @param checked
   *          the checking status to be set.
   */
  private final void setSubActionChecked(String id, boolean checked) {
    final Collection<MenuManagerEx> menus = subMenuCreator.getMenuManagers();
    for (final MenuManagerEx menu : menus) {
      final IContributionItem item = menu.find(id);
      if (item != null) {
        final IAction action = ((ActionContributionItem) item).getAction();
        action.setChecked(checked);
      } else {
        logger.error("Cannot found the sub-action: {}", id);
      }
    }
  }

  /**
   * Unchecks a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action to be set to unchecked.
   */
  public final void uncheckSubAction(String id) {
    logger.trace("Uncheck the sub-action: {}", id);
    setSubActionChecked(id, false);
  }

  /**
   * Checks a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action to be set to checked.
   */
  public final void checkSubAction(String id) {
    logger.trace("Check the sub-action: {}", id);
    setSubActionChecked(id, true);
  }

  /**
   * Unchecks sub-actions.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param ids
   *          the IDs of sub-actions to be set to unchecked.
   */
  public final void uncheckSubActions(String ... ids) {
    logger.trace("Uncheck the sub-actions: {}", (Object[])ids);
    for (final String id : ids) {
      setSubActionChecked(id, false);
    }
  }

  /**
   * Checks sub-actions.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param ids
   *          the IDs of sub-actions to be set to checked.
   */
  public final void checkSubActions(String ... ids) {
    logger.trace("Check the sub-actions: {}", (Object[])ids);
    for (final String id : ids) {
      setSubActionChecked(id, true);
    }
  }

  /**
   * Set the ability status of a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action whose ability status is to be set.
   * @param enabled
   *          the ability status to be set.
   */
  private final void setSubActionEnabled(String id, boolean enabled) {
    final Collection<MenuManagerEx> menus = subMenuCreator.getMenuManagers();
    for (final MenuManagerEx menu : menus) {
      final IContributionItem item = menu.find(id);
      if (item != null) {
        final IAction action = ((ActionContributionItem) item).getAction();
        action.setEnabled(enabled);
      } else {
        logger.error("Cannot found the sub-action: {}", id);
      }
    }
  }


  /**
   * Disables a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action to be disabled.
   */
  public final void disableSubAction(String id) {
    logger.trace("Disable the sub-action: {}", id);
    setSubActionEnabled(id, false);
  }

  /**
   * Enables a sub-action.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param id
   *          the ID of the sub-action to be enabled.
   */
  public final void enableSubAction(String id) {
    logger.trace("Enable the sub-action: {}", id);
    setSubActionEnabled(id, true);
  }

  /**
   * Disables sub-actions.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param ids
   *          the IDs of sub-actions to be disabled.
   */
  public final void disableSubActions(String ... ids) {
    logger.trace("Disable the sub-actions: {}", (Object[])ids);
    for (final String id : ids) {
      setSubActionEnabled(id, false);
    }
  }

  /**
   * Enables sub-actions.
   *
   * <b>NOTE:</b> After calling this function, the {@link #update(true)}
   * or {@link #updateAll(true)} must be called in order to rebuild all
   * the drop-down menus created by this action.
   *
   * @param ids
   *          the IDs of sub-actions to be enabled.
   */
  public final void enableSubActions(String ... ids) {
    logger.trace("Enable the sub-actions: {}", (Object[])ids);
    for (final String id : ids) {
      setSubActionEnabled(id, true);
    }
  }
}
