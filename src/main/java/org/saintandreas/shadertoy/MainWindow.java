package org.saintandreas.shadertoy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MainWindow {
  private static final String DEFAULT_FRAGMENT_SHADER = 
      "void main(void)\n" +
      "{\n" +
      "  vec2 uv = gl_FragCoord.xy / iResolution.xy;\n" +
      "  gl_FragColor = vec4(uv,0.5+0.5*sin(iGlobalTime),1.0);\n" +
      "}\n";

  protected Shell shell;
  private Text text;
  private String fragmentShader = DEFAULT_FRAGMENT_SHADER;
  private ShaderToyWindow renderWindow = new ShaderToyWindow(); 

  /**
   * Launch the application.
   * @param args
   */
  public static void main(String[] args) {
    try {
      MainWindow window = new MainWindow();
      window.open();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Open the window.
   */
  public void open() {
    Display display = Display.getDefault();
    createContents();
    shell.open();
    shell.layout();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
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
    
    text = new Text(shell, SWT.BORDER | SWT.MULTI);
    GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
    gd_text.heightHint = 20;
    gd_text.widthHint = 689;
    text.setLayoutData(gd_text);
    text.setText(fragmentShader);
    
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
    

    GridData gd_channel0 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
    gd_channel0.heightHint = 125;

    Button channel0 = new Button(shell, SWT.NONE);
    channel0.setLayoutData(gd_channel0);

    Button channel1 = new Button(shell, SWT.NONE);
    channel1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

    Button channel2 = new Button(shell, SWT.NONE);
    channel2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

    Button channel3 = new Button(shell, SWT.NONE);
    channel3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
  }

  protected void onRun() {
  }
}
