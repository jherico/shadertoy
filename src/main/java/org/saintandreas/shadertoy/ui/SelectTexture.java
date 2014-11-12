package org.saintandreas.shadertoy.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.saintandreas.resources.Resource;
import org.saintandreas.resources.ResourceManager;
import org.saintandreas.shadertoy.data.Data;

public class SelectTexture extends Dialog {

  protected Object result;
  protected Shell shell;

  /**
   * Create the dialog.
   * 
   * @param parent
   * @param style
   */
  public SelectTexture(Shell parent, int style) {
    super(parent, style);
    setText("SWT Dialog");
  }

  /**
   * Open the dialog.
   * 
   * @return the result
   */
  public Object open() {
    createContents();
    shell.open();
    shell.layout();
    Display display = getParent().getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return result;
  }

  private void fillButtons(Composite composite, Resource[] resources, RowData rowData) {
    for (Resource res : resources) {
      Button button = new Button(composite, SWT.NONE);
//      button.setText(res.getPath());
//      button.setData(res);
      try {
        button.setImage(new Image(shell.getDisplay(), ResourceManager.getAsInputStream(res)));
      } catch (SWTException e) {
        System.out.println(res.getPath());
      }
      button.setLayoutData(new RowData(64, 64));
      button.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          result = res;
          shell.close();
        }
      });
    }
  }

  /**
   * Create contents of the dialog.
   */
  private void createContents() {
    shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE);
    shell.setSize(800, 800);
    shell.setText(getText());
    shell.setLayout(new FillLayout(SWT.VERTICAL));

    {
      Composite misc = new Composite(shell, SWT.NONE);
      misc.setLayout(new RowLayout(SWT.HORIZONTAL));
      Label label = new Label(misc, SWT.NONE);
      label.setText("Misc");
    }

    {
      Composite textures = new Composite(shell, SWT.NONE);
      textures.setLayout(new RowLayout(SWT.HORIZONTAL));
      Label label = new Label(textures, SWT.NONE);
      label.setText("Textures");
      fillButtons(textures, Data.TEXTURES, new RowData(64, 64));
    }

    {
      Composite videos = new Composite(shell, SWT.NONE);
      videos.setLayout(new RowLayout(SWT.HORIZONTAL));
      Label label = new Label(videos, SWT.NONE);
      label.setText("Videos");
      fillButtons(videos, Data.VIDEOS, new RowData(128, 72));
    }

    {
      Composite cubemaps = new Composite(shell, SWT.NONE);
      cubemaps.setLayout(new RowLayout(SWT.HORIZONTAL));
      Label label = new Label(cubemaps, SWT.NONE);
      label.setText("Cubemaps");
      fillButtons(cubemaps, Data.CUBEMAPS, new RowData(64, 64));
    }

    {
      Composite music = new Composite(shell, SWT.NONE);
      music.setLayout(new RowLayout(SWT.HORIZONTAL));
      Label label = new Label(music, SWT.NONE);
      label.setText("Music");
    }

  }
}
