/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *     Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.github.haixing_hu.lang.SystemUtils;
import com.github.haixing_hu.swt.utils.SWTResourceManager;

/**
 * This abstract class if the mother of DialogMessageArea and DialogFooterArea classes
 */
abstract class DialogArea {
  private static final String MORE_DETAILS_IMAGE = "/images/more-details.png";
  private static final String FEWER_DETAILS_IMAGE = "/images/fewer-details.png";
  private static final String WINDOWS_DEFAULT_FONT = "Segoe UI";
  private static final String MAC_OS_DEFAULT_FONT = "Lucida Grande";
  protected final Dialog parent;
  private boolean initialised;

  /**
   * Constructor
   *
   * @param parent
   *          parent dialog
   */
  public DialogArea(final Dialog parent) {
    this.parent = parent;
  }

  /**
   * Render the content of an area
   */
  abstract void render();

  /**
   * @return the initialised field
   */
  boolean isInitialised() {
    return this.initialised;
  }

  /**
   * @param initialised
   *          the initialised value to set
   */
  void setInitialised(final boolean initialised) {
    this.initialised = initialised;
  }

  /**
   * @return the normal font used by the dialog box
   */
  protected Font getNormalFont() {
    if (SystemUtils.IS_OS_MAC) {
      return SWTResourceManager.getFont(MAC_OS_DEFAULT_FONT, 11, SWT.NONE);
    } else {
      return SWTResourceManager.getFont(WINDOWS_DEFAULT_FONT, 9, SWT.NONE);
    }
  }

  /**
   * @return the bigger font used by the dialog box
   */
  protected Font getBiggerFont() {
    if (SystemUtils.IS_OS_MAC) {
      return SWTResourceManager.getFont(MAC_OS_DEFAULT_FONT, 13, SWT.NONE);
    } else {
      return SWTResourceManager.getFont(WINDOWS_DEFAULT_FONT, 11, SWT.NONE);
    }
  }


  /**
   * @return the title's color (blue)
   */
  protected Color getTitleColor() {
    final Color color = SWTResourceManager.getColor(35, 107, 178);
    return color;
  }

  /**
   * @return the grey color
   */
  protected Color getGreyColor() {
    final Color color = SWTResourceManager.getColor(240, 240, 240);
    return color;
  }

  /**
   * @return the image "fewer details"
   */
  protected Image getFewerDetailsImage() {
    return SWTResourceManager.getImage(this.getClass(), FEWER_DETAILS_IMAGE);
  }

  /**
   * @return the image "more details"
   */
  protected Image getMoreDetailsImage() {
    return SWTResourceManager.getImage(this.getClass(), MORE_DETAILS_IMAGE);
  }

}
