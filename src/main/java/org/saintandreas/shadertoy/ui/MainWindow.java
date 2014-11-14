package org.saintandreas.shadertoy.ui;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.saintandreas.resources.ResourceManager;
import org.saintandreas.shadertoy.ShaderToyWindow;
import org.saintandreas.shadertoy.data.ChannelInput;
import org.saintandreas.shadertoy.data.Shaders;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.oculusvr.capi.Hmd;

public class MainWindow {
  private ShaderToyWindow renderWindow = new ShaderToyWindow();
  protected Shell shell;
  private Text text;
  private String currentFile;
  
  /**
   * Launch the application.
   * @param args
   */
  public static void main(String[] args) {
    Hmd.initialize();
    try {
      MainWindow window = new MainWindow();
      window.open();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Hmd.shutdown();
  }

  public MainWindow() {
  }
  /**
   * Open the window.
   */
  public void open() {
    Display display = Display.getDefault();
    createContents();
    shell.open();
    shell.layout();
    renderWindow.create();
    renderWindow.setFragmentSource(ResourceManager.getAsString(Shaders.FRAGMENT_SHADER));
    while (!shell.isDisposed()) {
      while (display.readAndDispatch()) {};
      renderWindow.onFrame();
    }
    renderWindow.destroy();
  }

  class MyTextureSelectionAdapter extends SelectionAdapter {
    final int channel;
    
    MyTextureSelectionAdapter(int channel) {
      this.channel = channel;
    }
    
    @Override
    public void widgetSelected(SelectionEvent e) {
      ChannelInput result = (ChannelInput) new SelectTexture(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL).open();
      if (null != result) {
        Image image = SWTResourceManager.getImage(result.thumbnail);
        ((Button)e.getSource()).setImage(image);
        renderWindow.setTextureSource(result, channel);
      }
    }
  }
  /**
   * Create contents of the window.
   */
  protected void createContents() {
    shell = new Shell();
    shell.setSize(643, 494);
    shell.setText("ShaderToy Java");
    shell.setLayout(new GridLayout(4, true));
    
    text = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
    Font terminalFont = JFaceResources.getFont(JFaceResources.TEXT_FONT);
    text.setFont(terminalFont);
    GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
    gd_text.heightHint = 20;
    gd_text.widthHint = 689;
    text.setLayoutData(gd_text);
    text.setText(ResourceManager.getAsString(Shaders.FRAGMENT_SHADER));
    
    Button btnRun = new Button(shell, SWT.NONE);
    btnRun.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        onRun();
      }
    });
    btnRun.setText("Run");
    new Label(shell, SWT.NONE);
    new Label(shell, SWT.NONE);
    new Label(shell, SWT.NONE);
    
     

    for (int i = 0; i < 4; ++i) {
      Button channel = new Button(shell, SWT.NONE);
      GridData gd_channel0 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
      gd_channel0.heightHint = 125;
      channel.addSelectionListener(new MyTextureSelectionAdapter(i));
      channel.setLayoutData(gd_channel0);
    }
    Menu menu = new Menu(shell, SWT.BAR);
    shell.setMenuBar(menu);

    MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
    mntmFile.setText("File");

    Menu menu_1 = new Menu(mntmFile);
    mntmFile.setMenu(menu_1);

    MenuItem mntmNew = new MenuItem(menu_1, SWT.NONE);
    mntmNew.setText("New");

    MenuItem mntmOpen = new MenuItem(menu_1, SWT.NONE);
    mntmOpen.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterExtensions(new String [] {"*.fs"});
        //dialog.setFilterPath("c:\\temp");
        String result = dialog.open();
        if (null != result) {
          currentFile = result;
          try {
            String source = Files.toString(new File(currentFile), Charsets.UTF_8);
            text.setText(source);
          } catch (IOException e1) {
          }
        }
      }
    });
    mntmOpen.setText("Open File");
    
    new MenuItem(menu_1, SWT.SEPARATOR);
    
    MenuItem mntmSave = new MenuItem(menu_1, SWT.NONE);
    mntmSave.setText("Save");
    
    MenuItem mntmSaveAs = new MenuItem(menu_1, SWT.NONE);
    mntmSaveAs.setText("Save as...");
    
    MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
    mntmExit.setText("Exit");
  }

  protected void onRun() {
    String newFragmentShaderSource = text.getText();
    renderWindow.setFragmentSource(newFragmentShaderSource);
  }
}
