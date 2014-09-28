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

import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.haixing_hu.swt.toolbar.Separator;
import com.github.haixing_hu.swt.toolbar.Fill;

/**
 * Utility functions for actions.
 *
 * @author Haixing Hu
 */
public class ActionUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionUtils.class);

  public static void addActions(ContributionManager cm, IActionManager am,
      String[] actionIds) {
    for (final String id : actionIds) {
      if ((id == null) || (id.length() == 0)) {
        continue;
      }
      if (Separator.ID.equals(id)) {
        cm.add(new Separator());
      } else if (Fill.ID.equals(id)) {
        cm.add(new Fill());
      } else {
        final IAction action = am.get(id);
        if (action == null) {
          LOGGER.error("Unknown action: {}", id);
        } else {
          cm.add(action);
        }
      }
    }
  }
}
