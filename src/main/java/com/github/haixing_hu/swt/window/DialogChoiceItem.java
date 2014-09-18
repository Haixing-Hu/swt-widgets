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

/**
 * Instances of this class are choice items used by the choice widget.
 */
public class DialogChoiceItem {

  private final String instruction;
  private final String text;

  /**
   * Constructor
   *
   * @param instruction
   *          instruction of the choice
   * @param text
   *          text displayed under the instruction
   */
  public DialogChoiceItem(final String instruction, final String text) {
    this.instruction = instruction;
    this.text = text;
  }

  /**
   * Constructor
   *
   * @param instruction
   *          instruction
   */
  public DialogChoiceItem(final String instruction) {
    this(instruction, null);
  }

  /**
   * @return the instruction
   */
  public String getInstruction() {
    return instruction;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  };

}
