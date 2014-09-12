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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * Instances of this class represent a star displayed by the StarRating component.
 *
 * @author Laurent CARON
 * @author Haixing Hu
 */
class Star {
  private static final String STAR_FOLDER = "images/stars/";
	private static final String SMALL_STAR_MARKED_FOCUS = STAR_FOLDER + "mark-focus16.png";
	private static final String SMALL_STAR_MARKED = STAR_FOLDER + "mark16.png";
	private static final String SMALL_STAR_FOCUS = STAR_FOLDER + "focus16.png";
	private static final String SMALL_STAR = STAR_FOLDER + "16.png";
	private static final String BIG_STAR_MARKED_FOCUS = STAR_FOLDER + "mark-focus32.png";
	private static final String BIG_STAR_MARKED = STAR_FOLDER + "mark32.png";
	private static final String BIG_STAR_FOCUS = STAR_FOLDER + "focus32.png";
	private static final String BIG_STAR = STAR_FOLDER + "32.png";

	boolean hover;
	boolean marked;
	Rectangle bounds;
	Image defaultImage;
	Image hoverImage;
	Image selectedImage;
	Image selectedHoverImage;
	private final StarRating parent;

  Star(final StarRating parent) {
    final Display dis = Display.getCurrent();
    final ClassLoader cl = this.getClass().getClassLoader();
    this.parent = parent;

    if (parent.getSizeOfStars() == StarRating.Size.BIG) {
      defaultImage = new Image(dis, cl.getResourceAsStream(BIG_STAR));
      hoverImage = new Image(dis, cl.getResourceAsStream(BIG_STAR_FOCUS));
      selectedImage = new Image(dis, cl.getResourceAsStream(BIG_STAR_MARKED));
      selectedHoverImage = new Image(dis, cl.getResourceAsStream(BIG_STAR_MARKED_FOCUS));
    } else {
      defaultImage = new Image(dis, cl.getResourceAsStream(SMALL_STAR));
      hoverImage = new Image(dis, cl.getResourceAsStream(SMALL_STAR_FOCUS));
      selectedImage = new Image(dis, cl.getResourceAsStream(SMALL_STAR_MARKED));
      selectedHoverImage = new Image(dis, cl.getResourceAsStream(SMALL_STAR_MARKED_FOCUS));
    }
  }

	void dispose() {
		this.defaultImage.dispose();
		this.hoverImage.dispose();
		this.selectedImage.dispose();
		this.selectedHoverImage.dispose();
	}

	void draw(final GC gc, final int x, final int y) {
		Image image;
		if (!this.parent.isEnabled()) {
			image = this.defaultImage;
		} else {
			if (this.marked) {
				if (this.hover) {
					image = this.selectedHoverImage;
				} else {
					image = this.selectedImage;
				}
			} else {
				if (this.hover) {
					image = this.hoverImage;
				} else {
					image = this.defaultImage;
				}
			}
		}
		gc.drawImage(image, x, y);
		final Rectangle rect = image.getBounds();
		this.bounds = new Rectangle(x, y, rect.width, rect.height);
	}
}
