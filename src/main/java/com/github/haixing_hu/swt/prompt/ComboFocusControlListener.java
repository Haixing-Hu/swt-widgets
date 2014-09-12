/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *     Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.prompt;

import org.eclipse.swt.widgets.Combo;

/**
 * Focus/Control listener for a Combo widget
 */
class ComboFocusControlListener extends BaseFocusControlListener {

	/**
	 * Constructor
	 *
	 * @param control control on which this listener will be attached
	 */
	public ComboFocusControlListener(final Combo control) {
		super(control);
	}

	@Override
	protected void hidePrompt() {
		((Combo) control).setText("");
	}

	@Override
	protected void highLightPrompt() {
	}

	@Override
	protected void fillPromptText() {
		final String promptText = PromptSupport.getPrompt(control);
		if (promptText != null) {
			control.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					((Combo) ComboFocusControlListener.this.control).setText(promptText);
				}
			});
		}
	}

	@Override
	protected boolean isFilled() {
		final String promptText = PromptSupport.getPrompt(control);
		if ((promptText != null)
		    && promptText.equals(((Combo) control).getText().trim())) {
			return false;
		}
		return !"".equals(((Combo) control).getText().trim());
	}
}
