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
package com.github.haixing_hu.swt.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.haixing_hu.swt.utils.Messages;
import com.github.haixing_hu.swt.utils.SWTUtils;

/**
 * Instances of this class are dialog box This component was inspired by the
 * Oxbow Project (http://code.google.com/p/oxbow/) by Eugene Ryzhikov.
 *
 * @author Laurent CARON
 * @author Haixing Hu
 */
public class Dialog {

  public static final String MESSAGE_OK = "dialog.ok";

  public static final String MESSAGE_CANCEL = "dialog.cancel";

  public static final String MESSAGE_CLOSE = "dialog.close";

  public static final String MESSAGE_YES = "dialog.yes";

  public static final String MESSAGE_NO = "dialog.no";

  public static final String MESSAGE_INFORMATION = "dialog.information";

  public static final String MESSAGE_WARNING = "dialog.warning";

  public static final String MESSAGE_EXCEPTION = "dialog.exception";

  public static final String MESSAGES_APPLICATION_ERROR = "dialog.application-error";

  public static final String MESSAGE_CHOICE = "dialog.choice";

  public static final String MESSAGE_INPUT = "dialog.input";

  public static final String MESSAGE_SELECT = "dialog.select";

  public static final String MESSAGE_FEWER_DETAILS = "dialog.fewer-details";

  public static final String MESSAGE_MORE_DETAILS = "dialog.more-details";

  /**
   * Types of opal dialog
   */
  public enum Type {
    CLOSE,
    YES_NO,
    OK,
    OK_CANCEL,
    SELECT_CANCEL,
    NO_BUTTON,
    OTHER,
    NONE
  }

  public enum Option {
    CENTER_ON_SCREEN,
    CENTER_ON_DIALOG
  }

  private Option centerPolicy = Option.CENTER_ON_SCREEN;

  private String title;
  Type buttonType;
  private final MessageArea messageArea;
  private final FooterArea footerArea;
  final Shell shell;

  private int minimumWidth = 300;
  private int minimumHeight = 150;

  /**
   * Constructor
   */
  public Dialog() {
    this(null);
  }

  /**
   * Constructor
   *
   * @param resizable
   *          if <code>true</code>, the window is resizable
   */
  public Dialog(final boolean resizable) {
    this(null, resizable);
  }

  /**
   * Constructor
   *
   * @param parent
   *          parent shell
   */
  public Dialog(final Shell parent) {
    this(parent, false);
  }

  /**
   * Constructor
   *
   * @param parent
   *          parent shell
   * @param resizable
   *          if <code>true</code>, the window is resizable
   */
  public Dialog(final Shell parent, final boolean resizable) {
    if (parent == null) {
      shell = new Shell(Display.getCurrent(), SWT.DIALOG_TRIM
          | SWT.APPLICATION_MODAL | (resizable ? SWT.RESIZE : SWT.NONE));
    } else {
      shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
          | (resizable ? SWT.RESIZE : SWT.NONE));
      if (parent.getImage() != null) {
        shell.setImage(parent.getImage());
      }
    }
    messageArea = new MessageArea(this);
    footerArea = new FooterArea(this);
  }

  /**
   * Show the dialog box
   *
   * @return the index of the selected button
   */
  public int show() {
    final GridLayout gd = new GridLayout(1, true);
    gd.horizontalSpacing = 0;
    gd.verticalSpacing = 0;
    gd.marginHeight = gd.marginWidth = 0;
    shell.setLayout(gd);

    messageArea.render();
    footerArea.render();
    if (title != null) {
      shell.setText(title);
    }
    pack();
    shell.open();

    final Display display = shell.getDisplay();
    while (! shell.isDisposed()) {
      if (! display.readAndDispatch()) {
        display.sleep();
      }
    }

    return footerArea.getSelectedButton();
  }

  /**
   * Close the dialog box
   */
  public void close() {
    shell.dispose();

  }

  /**
   * Compute the size of the shell
   */
  void pack() {

    final Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);

    if (preferredSize.x < minimumWidth) {
      preferredSize.x = minimumWidth;
    }
    if (preferredSize.y < minimumHeight) {
      preferredSize.y = minimumHeight;
    }
    final int centerX;
    final int centerY;
    if ((centerPolicy == Option.CENTER_ON_SCREEN)
        || (shell.getParent() == null)) {
      final Rectangle bounds = SWTUtils.getMonitorBounds(shell);
      centerX = bounds.x + ((bounds.width - preferredSize.x) / 2);
      centerY = bounds.y + ((bounds.height - preferredSize.y) / 2);
    } else {
      final Shell parent = (Shell) shell.getParent();
      final Point parentLocation = parent.getLocation();
      final Point parentSize = parent.getSize();
      centerX = parentLocation.x + ((parentSize.x - preferredSize.x) / 2);
      centerY = parentLocation.y + ((parentSize.y - preferredSize.y) / 2);
    }
    shell.setBounds(centerX, centerY, preferredSize.x, preferredSize.y);
  }

  /**
   * Create a dialog box that asks a question
   *
   * @param title
   *          title of the dialog box
   * @param text
   *          text of the question
   * @param defaultValue
   *          default value of the input
   * @return the value typed by the user
   */
  public static String ask(final String title, final String text,
      final String defaultValue) {
    return ask(null, title, text, defaultValue);
  }

  /**
   * Create a dialog box that asks a question
   *
   * @shell parent shell
   * @param title
   *          title of the dialog box
   * @param text
   *          text of the question
   * @param defaultValue
   *          default value of the input
   * @return the value typed by the user
   */
  public static String ask(final Shell shell, final String title,
      final String text, final String defaultValue) {
    final Dialog dialog = new Dialog(shell);
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGE_INPUT));
    dialog.getMessageArea()
          .setTitle(title)
          .setText(text)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION))
          .addTextBox(defaultValue);
    dialog.setButtonType(Type.OK_CANCEL);
    if (dialog.show() == 0) {
      return dialog.getMessageArea().getTextBoxValue();
    } else {
      return null;
    }
  }

  /**
   * Create a dialog box that displays an error message
   *
   * @param title
   *          title of the dialog box
   * @param errorMessage
   *          Error message
   */
  public static void error(final String title, final String errorMessage) {
    error(null, title, errorMessage);
  }

  /**
   * Create a dialog box that displays an error message
   *
   * @param shell
   *          parent shell
   * @param title
   *          title of the dialog box
   * @param errorMessage
   *          Error message
   */
  public static void error(final Shell shell, final String title,
      final String errorMessage) {
    final Dialog dialog = new Dialog(shell);
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGES_APPLICATION_ERROR));
    dialog.getMessageArea()
          .setTitle(title)
          .setText(errorMessage)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_ERROR));
    dialog.setButtonType(Type.OK);
    dialog.show();
  }

  /**
   * Create a dialog box that inform the user
   *
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   */
  public static void inform(final String title, final String text) {
    inform(null, title, text);
  }

  /**
   * Create a dialog box that inform the user
   *
   * @param shell
   *          parent shell
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   */
  public static void inform(final Shell shell, final String title,
      final String text) {
    final Dialog dialog = new Dialog(shell);
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGE_INFORMATION));
    dialog.getMessageArea()
          .setTitle(title)
          .setText(text)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_INFORMATION));
    dialog.setButtonType(Type.CLOSE);
    dialog.show();
  }

  /**
   * Create a dialog box that asks the user a confirmation
   *
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @return <code>true</code> if the user confirmed, <code>false</code>
   *         otherwise
   */
  public static boolean isConfirmed(final String title, final String text) {
    return isConfirmed(null, title, text, - 1);
  }

  /**
   * Create a dialog box that asks the user a confirmation
   *
   * @param shell
   *          parent shell
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @return <code>true</code> if the user confirmed, <code>false</code>
   *         otherwise
   */
  public static boolean isConfirmed(final Shell shell, final String title,
      final String text) {
    return isConfirmed(shell, title, text, - 1);
  }

  /**
   * Create a dialog box that asks the user a confirmation. The button "yes" is
   * not enabled before timer seconds
   *
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @param timer
   *          number of seconds before enabling the yes button
   * @return <code>true</code> if the user confirmed, <code>false</code>
   *         otherwise
   */
  public static boolean isConfirmed(final String title, final String text,
      final int timer) {
    return isConfirmed(null, title, text, timer);
  }

  /**
   * Create a dialog box that asks the user a confirmation. The button "yes" is
   * not enabled before timer seconds
   *
   * @param shell
   *          parent shell
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @param timer
   *          number of seconds before enabling the yes button
   * @return <code>true</code> if the user confirmed, <code>false</code>
   *         otherwise
   */
  public static boolean isConfirmed(final Shell shell, final String title,
      final String text, final int timer) {
    final Dialog dialog = new Dialog(shell);
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGE_WARNING));
    dialog.getMessageArea()
          .setTitle(title)
          .setText(text)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));
    dialog.getFooterArea()
          .setTimer(timer)
          .setTimerIndexButton(0);
    dialog.setButtonType(Type.YES_NO);
    return dialog.show() == 0;
  }

  /**
   * Create a dialog box with a radio choice
   *
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @param defaultSelection
   *          index of the default selection
   * @param values
   *          values to display
   * @return the index of the selection
   */
  public static int radioChoice(final String title, final String text,
      final int defaultSelection, final String... values) {
    return radioChoice(null, title, text, defaultSelection, values);
  }

  /**
   * Create a dialog box with a radio choice
   *
   * @param shell
   *          parent shell
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @param defaultSelection
   *          index of the default selection
   * @param values
   *          values to display
   * @return the index of the selection
   */
  public static int radioChoice(final Shell shell, final String title,
      final String text, final int defaultSelection, final String... values) {
    final Dialog dialog = new Dialog(shell);
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGE_CHOICE));
    dialog.getMessageArea()
          .setTitle(title)
          .setText(text)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION))
          .addRadioButtons(defaultSelection, values);
    dialog.setButtonType(Type.SELECT_CANCEL);
    if (dialog.show() == 0) {
      return dialog.getMessageArea().getRadioChoice();
    } else {
      return - 1;
    }
  }

  /**
   * Display a dialog box with an exception
   *
   * @param exception
   *          exception to display
   */
  public static void showException(final Throwable exception) {
    final Dialog dialog = new Dialog();
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGE_EXCEPTION));
    final String exmsg = exception.getMessage();
    final String className = exception.getClass().getName();
    final boolean noMessage = (exmsg == null) || (exmsg.trim().length() == 0);
    dialog.getMessageArea()
          .setTitle(noMessage ? className : exmsg)
          .setText(noMessage ? "" : className)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_ERROR))
          .setException(exception);
    dialog.getFooterArea()
          .setExpanded(true);
    dialog.setButtonType(Type.CLOSE);
    dialog.show();
  }

  /**
   * Create a dialog box with a choice
   *
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @param defaultSelection
   *          index of the default selection
   * @param items
   *          items to display
   * @return the index of the selected value
   */
  public static int choice(final String title, final String text,
      final int defaultSelection, final ChoiceItem... items) {
    return choice(null, title, text, defaultSelection, items);
  }

  /**
   * Create a dialog box with a choice
   *
   * @param shell
   *          parent shell
   * @param title
   *          title of the dialog box
   * @param text
   *          text to display
   * @param defaultSelection
   *          index of the default selection
   * @param items
   *          items to display
   * @return the index of the selected value
   */
  public static int choice(final Shell shell, final String title,
      final String text, final int defaultSelection, final ChoiceItem... items) {
    final Dialog dialog = new Dialog(shell);
    final Messages messages = Messages.getInstance();
    dialog.setTitle(messages.get(MESSAGE_CHOICE));
    dialog.getMessageArea()
          .setTitle(title)
          .setText(text)
          .setIcon(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION))
          .addChoice(defaultSelection, items);
    dialog.setButtonType(Type.NONE);
    dialog.show();
    return dialog.getMessageArea().getChoice();
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *          the title to set
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * @return the buttonType
   */
  public Type getButtonType() {
    return buttonType;
  }

  /**
   * Sets the type of the button.
   *
   * @param type
   *          the buttonType to set
   */
  public void setButtonType(final Type type) {
    buttonType = type;
    final Messages messages = Messages.getInstance();
    switch (type) {
    case CLOSE:
      footerArea.setButtonLabels(messages.get(MESSAGE_CLOSE))
                .setDefaultButtonIndex(0);
      break;
    case NO_BUTTON:
      break;
    case OK:
      footerArea.setButtonLabels(messages.get(MESSAGE_OK))
                .setDefaultButtonIndex(0);
      break;
    case OK_CANCEL:
      footerArea.setButtonLabels(messages.get(MESSAGE_OK),
                                 messages.get(MESSAGE_CANCEL))
                .setDefaultButtonIndex(- 1);
      break;
    case SELECT_CANCEL:
      footerArea.setButtonLabels(messages.get(MESSAGE_SELECT),
                                 messages.get(MESSAGE_CANCEL))
                .setDefaultButtonIndex(- 1);
      break;
    case YES_NO:
      footerArea.setButtonLabels(messages.get(MESSAGE_YES),
                                 messages.get(MESSAGE_NO))
                .setDefaultButtonIndex(0);
      break;
    default:
      break;
    }

  }

  /**
   * @return the messageArea
   */
  public MessageArea getMessageArea() {
    return messageArea;
  }

  /**
   * @return the footerArea
   */
  public FooterArea getFooterArea() {
    return footerArea;
  }

  /**
   * @return the shell
   */
  public Shell getShell() {
    return shell;
  }

  /**
   * @return the index of the selected button
   */
  public int getSelectedButton() {
    return getFooterArea().getSelectedButton();
  }

  /**
   * @return the selection state of the checkbox
   */
  public boolean getCheckboxValue() {
    return footerArea.getCheckBoxValue();
  }

  /**
   * @return the minimum width of the dialog box
   */
  public int getMinimumWidth() {
    return minimumWidth;
  }

  /**
   * @param minimumWidth
   *          the minimum width of the dialog box to set
   */
  public void setMinimumWidth(final int minimumWidth) {
    this.minimumWidth = minimumWidth;
  }

  /**
   * @return the minimum height of the dialog box
   */
  public int getMinimumHeight() {
    return minimumHeight;
  }

  /**
   * @param minimumHeight
   *          the minimum height of the dialog box to set
   */
  public void setMinimumHeight(final int minimumHeight) {
    this.minimumHeight = minimumHeight;
  }

  /**
   * @return the center policy (Dialog centered on screen or centered in the
   *         center of the parent window)
   */
  public Option getCenterPolicy() {
    return centerPolicy;
  }

  /**
   * @param centerPolicy
   *          center policy
   */
  public void setCenterPolicy(final Option centerPolicy) {
    this.centerPolicy = centerPolicy;
  }

}
