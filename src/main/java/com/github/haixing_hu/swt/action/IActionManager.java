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

/**
 * The interface for action managers, which is a map from the action's ID to the
 * action.
 *
 * @author Haixing Hu
 */
public interface IActionManager {

  /**
   * Tests whether this action manager is empty.
   *
   * @return <code>true</code> if this action manager is empty;
   *         <code>false</code> otherwise.
   */
  public boolean isEmpty();

  /**
   * Gets the number of action in this action manager.
   *
   * @return the number of actions in this action manager.
   */
  public int size();

  /**
   * Tests whether this action manager contains an action.
   *
   * @param id
   *          the ID of the action to be tested.
   * @return <code>true</code> if the action with the specified ID was contained
   *         in this action manager; <code>false</code> otherwise.
   */
  public boolean contains(String id);

  /**
   * Gets the action by its ID.
   *
   * @param id
   *          the ID of the action to be get.
   * @return the action corresponding to the ID, or <code>null</code> if no such
   *         action.
   */
  public ActionEx get(String id);

  /**
   * Gets all actions in this action manager.
   *
   * @return the collection of all actions in this action manager.
   */
  public Collection<ActionEx> getAll();

  /**
   * Adds an action to this action manager.
   *
   * @param action
   *          an action.
   */
  public void add(ActionEx action);

  /**
   * Removes an action from this action manager.
   *
   * @param id
   *          the ID of the action to be removed.
   * @return the action removed by this function, or <code>null</code> if no
   *         such action.
   */
  public ActionEx remove(String id);

  /**
   * Clears all actions in this action manager.
   */
  public void clear();
}
