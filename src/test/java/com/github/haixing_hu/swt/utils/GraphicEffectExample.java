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

package com.github.haixing_hu.swt.utils;

import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.github.haixing_hu.io.IoUtils;

/**
 * A code snippet for the graphic effects provided by the
 * {@link SWTGraphicUtils} class.
 *
 * @author Haixing Hu
 */
public class GraphicEffectExample {

  public static void main(String args[]) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setText("Image Effects");
    shell.setLayout(new GridLayout());
    final CTabFolder folder = new CTabFolder(shell, SWT.NONE);
    folder.setLayoutData(new GridData(GridData.FILL_BOTH));
    folder.setLayout(new FillLayout());
    folder.setSimple(false);
    final EffectPanel emboss = new EffectPanel(display, shell, folder, "Emboss");
    final EffectPanel blur = new EffectPanel(display, shell, folder, "Blur");
    final EffectPanel glow = new EffectPanel(display, shell, folder, "Glow");
    final EffectPanel shadow = new EffectPanel(display, shell, folder,
        "Drop Shadow");

    // Emboss Panel
    new Label(emboss.parametersGroup, SWT.NONE).setText("Gray level: ");
    final Spinner embossGrayLevel = new Spinner(emboss.parametersGroup,
        SWT.NONE);
    embossGrayLevel.setMaximum(255);
    embossGrayLevel.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        final ImageData imageData = EffectPanel.originalImageData;
        if (imageData == null) {
          return;
        }
        emboss.filteredImageData = SWTGraphicUtils.emboss(imageData,
            ((Spinner) event.widget).getSelection());
        emboss.canvas.redraw();
      }
    });

    // Blur Panel
    new Label(blur.parametersGroup, SWT.NONE).setText("Blur radius: ");
    final Spinner blurRadius = new Spinner(blur.parametersGroup, SWT.NONE);
    blurRadius.setMaximum(9999);
    blurRadius.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        final ImageData imageData = EffectPanel.originalImageData;
        if (imageData == null) {
          return;
        }
        blur.filteredImageData = SWTGraphicUtils.blur(imageData,
            ((Spinner) event.widget).getSelection());
        blur.canvas.redraw();
      }
    });

    // Glow Panel
    new Label(glow.parametersGroup, SWT.NONE).setText("Glow radius: ");
    final Spinner glowRadius = new Spinner(glow.parametersGroup, SWT.NONE);
    glowRadius.setMaximum(9999);
    new Label(glow.parametersGroup, SWT.NONE).setText("Highlight radius: ");
    final Spinner glowHighlightRadius = new Spinner(glow.parametersGroup,
        SWT.NONE);
    new Label(glow.parametersGroup, SWT.NONE).setText("Opacity: ");
    final Spinner glowOpacity = new Spinner(glow.parametersGroup, SWT.NONE);
    glowOpacity.setMaximum(255);
    glowOpacity.setSelection(255);
    new Label(glow.parametersGroup, SWT.NONE).setText("Glow color: ");
    final Canvas glowColor = new Canvas(glow.parametersGroup, SWT.NONE);
    glowColor.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
    glowRadius.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        doGlow(glow, glowColor.getBackground(), glowRadius,
            glowHighlightRadius, glowOpacity);
      }
    });
    glowHighlightRadius.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        doGlow(glow, glowColor.getBackground(), glowRadius,
            glowHighlightRadius, glowOpacity);
      }
    });
    glowOpacity.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        doGlow(glow, glowColor.getBackground(), glowRadius,
            glowHighlightRadius, glowOpacity);
      }
    });
    glowColor.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseUp(MouseEvent e) {
        final RGB rgb = new ColorDialog(shell).open();
        if (rgb == null) {
          return;
        }
        glowColor.setBackground(new Color(display, rgb));
        doGlow(glow, glowColor.getBackground(), glowRadius,
            glowHighlightRadius, glowOpacity);
      }
    });

    // Drop Shadow Panel
    new Label(shadow.parametersGroup, SWT.NONE).setText("Shadow radius: ");
    final Spinner shadowRadius = new Spinner(shadow.parametersGroup, SWT.NONE);
    shadowRadius.setMaximum(100);
    new Label(shadow.parametersGroup, SWT.NONE).setText("Highlight radius: ");
    final Spinner shadowHighlightRadius = new Spinner(shadow.parametersGroup,
        SWT.NONE);
    new Label(shadow.parametersGroup, SWT.NONE).setText("Opacity: ");
    final Spinner shadowOpacity = new Spinner(shadow.parametersGroup, SWT.NONE);
    shadowOpacity.setMaximum(255);
    shadowOpacity.setSelection(255);
    new Label(shadow.parametersGroup, SWT.NONE).setText("Shadow color: ");
    final Canvas shadowColor = new Canvas(shadow.parametersGroup, SWT.NONE);
    shadowColor.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
    shadowRadius.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        doDropShadow(shadow, shadowColor.getBackground(), shadowRadius,
            shadowHighlightRadius, shadowOpacity);
      }
    });
    shadowHighlightRadius.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        doDropShadow(shadow, shadowColor.getBackground(), shadowRadius,
            shadowHighlightRadius, shadowOpacity);
      }
    });
    shadowOpacity.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        doDropShadow(shadow, shadowColor.getBackground(), shadowRadius,
            shadowHighlightRadius, shadowOpacity);
      }
    });
    shadowColor.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseUp(MouseEvent e) {
        final RGB rgb = new ColorDialog(shell).open();
        if (rgb == null) {
          return;
        }
        shadowColor.setBackground(new Color(display, rgb));
        doDropShadow(shadow, shadowColor.getBackground(), shadowRadius,
            shadowHighlightRadius, shadowOpacity);
      }
    });

    // Shared file selection button
    // This is always visible because it is part of the Shell, not the
    // CTabFolder
    final Button button = new Button(shell, SWT.PUSH);
    button.setText("Select Image File");
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        final FileDialog dialog = new FileDialog(shell);
        dialog.setFilterExtensions(new String[] { "*.jpg;*.bmp;*.gif;*.png" });
        final String fileName = dialog.open();
        if (fileName != null) {
          try {
            EffectPanel.originalImageData = new ImageData(fileName);
            final Composite composite = ((Composite) folder.getSelection().getControl());
            final Control[] children = composite.getChildren();
            children[1].redraw(); // force canvas to be redrawn
          } catch (final RuntimeException e) {
            System.err.println("Error while opening file: " + fileName);
          }
        }
      }
    });

    shell.open();
    while (! shell.isDisposed()) {
      if (! display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }

  private static void doGlow(EffectPanel panel, Color color, Spinner radius,
      Spinner highlightRadius, Spinner opacity) {
    final ImageData imageData = EffectPanel.originalImageData;
    if (imageData == null) {
      return;
    }
    panel.filteredImageData = SWTGraphicUtils.glow(imageData, color,
        radius.getSelection(), highlightRadius.getSelection(),
        opacity.getSelection());
    panel.canvas.redraw();
  }

  private static void doDropShadow(EffectPanel panel, Color color,
      Spinner radius, Spinner highlightRadius, Spinner opacity) {
    final ImageData imageData = EffectPanel.originalImageData;
    if (imageData == null) {
      return;
    }
    panel.filteredImageData = SWTGraphicUtils.dropShadow(imageData, color,
        radius.getSelection(), highlightRadius.getSelection(),
        opacity.getSelection());
    panel.canvas.redraw();
  }

  /**
   * Represents a tab of a CTabFolder. Handles layout, resizing, and painting of
   * images.
   */
  static class EffectPanel {
    CTabItem tab;
    Group parametersGroup; // group of image effect settings
    Canvas canvas; // for image
    public static ImageData originalImageData;
    ImageData lastOriginalImageData; // used to track when originalImageData
                                     // changes
    ImageData filteredImageData;

    public EffectPanel(final Display display, final Shell shell,
        CTabFolder folder, String title) {
      tab = new CTabItem(folder, SWT.NONE);
      tab.setText(title);
      final Composite tabControlComposite = new Composite(folder, SWT.BORDER);
      tabControlComposite.setLayout(new GridLayout());
      tab.setControl(tabControlComposite);
      parametersGroup = new Group(tabControlComposite, SWT.NONE);
      parametersGroup.setText("Parameters");
      parametersGroup.setLayout(new GridLayout(2, false));
      canvas = new Canvas(tabControlComposite, SWT.DOUBLE_BUFFERED);
      canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
      canvas.addPaintListener(new PaintListener() {
        @Override
        public void paintControl(PaintEvent e) {
          if (originalImageData == null) {
            return;
          }
          if ((filteredImageData == null)
              || (lastOriginalImageData != originalImageData)) {
            filteredImageData = lastOriginalImageData = originalImageData;
          }
          final String text = tab.getText();
          if (text.equals("Glow") || text.equals("Drop Shadow")) {
            shell.setSize(Math.max(500, filteredImageData.width + 50),
                Math.max(500, filteredImageData.height + 325));
          } else {
            shell.setSize(Math.max(500, filteredImageData.width + 50),
                Math.max(500, filteredImageData.height + 125));
          }
          final Image image = new Image(display, filteredImageData);
          e.gc.drawImage(image, 0, 0);
          image.dispose();
        }
      });
      //  load the default image
      final ClassLoader cl = this.getClass().getClassLoader();
      final InputStream is = cl.getResourceAsStream("images/blur.jpg");
      if (is != null) {
        try {
          originalImageData = new ImageData(is);
        } finally {
          IoUtils.closeQuietly(is);
        }
      }
    }
  }
}
