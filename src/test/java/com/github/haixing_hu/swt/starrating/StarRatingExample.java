/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *     Haixing Hu (https://github.com/Haixing-Hu/)  - Modification for personal use.
 *******************************************************************************/
package com.github.haixing_hu.swt.starrating;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.github.haixing_hu.swt.starrating.StarRating;
import com.github.haixing_hu.swt.utils.SWTResourceManager;

/**
 * A simple example for the StarRating widget.
 *
 * @author Laurent CARON
 * @author Haixing Hu
 */
public class StarRatingExample {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("StarRating Example");

		shell.setLayout(new GridLayout(2, false));

		createHorizontal(shell, true);
		createHorizontal(shell, false);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(8, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		createVertical(composite, true);
		createVertical(composite, false);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		SWTResourceManager.dispose();
		display.dispose();

	}

	private static void createHorizontal(final Shell shell, final boolean enabled) {
		for (final StarRating.Size size : StarRating.Size.values()) {
			final Label label = new Label(shell, SWT.NONE);
			label.setText("Horizontal " + (enabled ? "enabled" : "disabled") + " size=" + size.toString());
			label.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

			final StarRating sr = new StarRating(shell, SWT.NONE,
			    size, 5 + (enabled ? 1 : 0));
			final GridData gd = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
			sr.setLayoutData(gd);
			sr.setEnabled(enabled);
		}
	}

	private static void createVertical(final Composite composite, final boolean enabled) {
		for (final StarRating.Size size : StarRating.Size.values()) {
			final Label label = new Label(composite, SWT.NONE);
			label.setText("Vertical " + (enabled ? "enabled" : "disabled") + " size=" + size.toString());
			label.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));

			final StarRating sr = new StarRating(composite, SWT.VERTICAL | SWT.BORDER,
			    size, 5 + (enabled ? 1 : 0));
			sr.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
			sr.setEnabled(enabled);
		}
	}

}
