package com.demo1;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.SHELLEXECUTEINFO;
import org.eclipse.swt.internal.win32.TCHAR;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class cmdText {

    private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"
    private Composite composite = null;
    private Button button = null;

    /**
     * This method initializes composite
     *
     */
    private void createComposite() {
        composite = new Composite(sShell, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setBounds(new Rectangle(3, 41, 418, 195));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Display display = Display.getDefault();
        cmdText thisClass = new cmdText();
        thisClass.createSShell();
        thisClass.sShell.open();
        while (!thisClass.sShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    /**
     * This method initializes sShell
     */
    private void createSShell() {
        sShell = new Shell();
        sShell.setText("Shell");
        createComposite();
        sShell.setSize(new Point(434, 270));
        sShell.setLayout(null);
        button = new Button(sShell, SWT.NONE);
        button.setText("启动");
        button.setBounds(new Rectangle(10, 4, 110, 22));
        button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                try {
                    startCMD();  //启动cmd程序
                  } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void executeProg(String fileName) throws Exception {
        int hHeap = (int) OS.GetProcessHeap();
        TCHAR buffer = new TCHAR(0, fileName, true);
        int byteCount = buffer.length() * TCHAR.sizeof;
        int lpFile = (int) OS.HeapAlloc(hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
        OS.MoveMemory(lpFile, buffer, byteCount);
        SHELLEXECUTEINFO info = new SHELLEXECUTEINFO();
        info.cbSize = SHELLEXECUTEINFO.sizeof;
        info.lpFile = lpFile;
        // 隐藏启动
        info.nShow = OS.SW_HIDE;
        boolean result = OS.ShellExecuteEx(info);
        if (lpFile != 0)
            OS.HeapFree(hHeap, 0, lpFile);
        if (result == false)
            throw new Exception("启动失败!");
    }

    protected void startCMD() throws Exception {
        // "cmd.exe"为待启动的程序名
        executeProg("cmd.exe");

        // 等待cmd.exe启动并且初始化完毕，需要根据实际情况调整sleep的时间
        Thread.sleep(1000);

        // "cmd"为被嵌套程序窗口的ClassName(Win32级别)，可以使用Spy++等工具查看
        int cmd= (int) OS.FindWindow(new TCHAR(0, "ConsoleWindowClass", true), null);

        // &~WS_BORDER去掉内嵌程序边框，这样看起来更像一个内嵌的程序。如果需要显示边框，则将这两行代码删除
        int oldStyle = OS.GetWindowLong(cmd, OS.GWL_STYLE);
        OS.SetWindowLong(cmd, OS.GWL_STYLE, oldStyle & ~OS.WS_BORDER);

        // composite为承载被启动程序的控件
        OS.SetParent(cmd, composite.handle);
        // 窗口最大化
        OS.SendMessage(cmd, OS.WM_SYSCOMMAND, OS.SC_MAXIMIZE, 0);
    }
}
