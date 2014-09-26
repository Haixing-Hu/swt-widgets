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

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * An extension to {@link org.eclipse.jface.action.Action}, providing the
 * following enhancements:
 * <ul>
 * <li>add the visibility control</li>
 * <li>add a property which indicate whether this action will display a dialog.</li>
 * </ul>
 *
 * @author Haixing Hu
 */
public abstract class ActionEx extends org.eclipse.jface.action.Action {

  private boolean visible = true;
  private boolean showDialog = false;
  private boolean showImage = true;

  /**
   * Creates a new action with no text and no image.
   * <p>
   * Configure the action later using the set methods.
   * </p>
   */
  protected ActionEx() {
    super();
  }

  /**
   * Creates a new action with the given text and no image. Calls the zero-arg
   * constructor, then <code>setText</code>.
   *
   * @param text
   *          the string used as the text for the action, or <code>null</code>
   *          if there is no text
   * @see #setText
   */
  protected ActionEx(String text) {
    super(text);
  }

  /**
   * Creates a new action with the given text and image. Calls the zero-arg
   * constructor, then <code>setText</code> and <code>setImageDescriptor</code>.
   *
   * @param text
   *          the action's text, or <code>null</code> if there is no text
   * @param image
   *          the action's image, or <code>null</code> if there is no image
   * @see #setText
   * @see #setImageDescriptor
   */
  protected ActionEx(String text, ImageDescriptor image) {
    super(text, image);
  }

  /**
   * Creates a new action with the given text and style.
   *
   * @param text
   *          the action's text, or <code>null</code> if there is no text
   * @param style
   *          one of <code>AS_PUSH_BUTTON</code>, <code>AS_CHECK_BOX</code>,
   *          <code>AS_DROP_DOWN_MENU</code>, <code>AS_RADIO_BUTTON</code>, and
   *          <code>AS_UNSPECIFIED</code>.
   */
  protected ActionEx(String text, int style) {
    super(text, style);
  }

  /**
   * Tests whether this action is visible.
   *
   * @return <code>true</code> if this action is visible; <code>false</code>
   *         otherwise.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets whether this action's visibility.
   *
   * @param visible
   *          <code>true</code> if this action should be visible;
   *          <code>false</code> otherwise.
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Tests whether this action will show a dialog.
   *
   * @return <code>true</code> if this action will show a dialog;
   *         <code>false</code> otherwise.
   */
  public boolean isShowDialog() {
    return showDialog;
  }

  /**
   * Sets whether this action will show a dialog.
   *
   * @return <code>true</code> if this action will show a dialog;
   *         <code>false</code> otherwise.
   */
  public void setShowDialog(boolean showDialog) {
    this.showDialog = showDialog;
  }

  /**
   * Gets whether the menu item or tool bar item created by this action will
   * display its image.
   *
   * @return true if the menu item or tool bar item created by this action will
   *         display its image; false otherwise.
   */
  public boolean isShowImage() {
    return showImage;
  }

  /**
   * Sets whether the menu item or tool bar item created by this action will
   * display its image.
   *
   * @param showImage
   *          true if the menu item or tool bar item created by this action will
   *          display its image; false otherwise.
   */
  public void setShowImage(boolean showImage) {
    this.showImage = showImage;
  }
}
