package uk.co.petertribble.solview;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import uk.co.petertribble.jingle.JingleMultiFrame;
import uk.co.petertribble.jingle.JingleInfoFrame;
import uk.co.petertribble.solview.explorer.SysPanel;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import uk.co.petertribble.jkstat.demo.KstatToolsMenu;

/**
 * SolView - shows Solaris information.
 * @author Peter Tribble
 * @version 1.0
 */
public class SolView extends JFrame implements ActionListener {

    private JMenuItem exitItem;
    private JMenuItem cloneItem;
    private JMenuItem closeItem;
    private JMenuItem helpItem;
    private JMenuItem licenseItem;

    public SolView() {
	super("Solview");

	addWindowListener(new winExit());

	JTabbedPane jtp = new JTabbedPane();
	getContentPane().add(jtp, BorderLayout.CENTER);

	JMenuBar jm = new JMenuBar();

	jtp.add(SolViewResources.getString("SOLVIEW.SERV.TEXT"),
		new SmfPanel());
	jtp.add(SolViewResources.getString("SOLVIEW.INFO.TEXT"),
		new InfoPanel());
	jtp.add(SolViewResources.getString("SOLVIEW.EXPL.TEXT"),
		new SysPanel());

	JMenu jme = new JMenu(SolViewResources.getString("FILE.TEXT"));
	jme.setMnemonic(KeyEvent.VK_F);
	cloneItem = new JMenuItem(SolViewResources.getString("FILE.NEWBROWSER"),
				KeyEvent.VK_B);
	cloneItem.addActionListener(this);
	jme.add(cloneItem);
	closeItem = new JMenuItem(SolViewResources.getString("FILE.CLOSEWIN"),
				KeyEvent.VK_W);
	closeItem.addActionListener(this);
	jme.add(closeItem);
	exitItem = new JMenuItem(SolViewResources.getString("FILE.EXIT"),
				KeyEvent.VK_X);
	exitItem.addActionListener(this);
	jme.add(exitItem);

	JingleMultiFrame.register(this, closeItem);

	jm.add(jme);
	jm.add(new SolToolsMenu());

	/*
	 * Add a menu of Kstat tools
	 */
	KstatToolsMenu ktm = new KstatToolsMenu(new NativeJKstat());
	ktm.setMnemonic(KeyEvent.VK_K);
	jm.add(ktm);

	JMenu jmh = new JMenu(SolViewResources.getString("HELP.TEXT"));
	jmh.setMnemonic(KeyEvent.VK_H);
	helpItem = new JMenuItem(
			SolViewResources.getString("HELP.ABOUT.SOLVIEW"),
			KeyEvent.VK_A);
	helpItem.addActionListener(this);
	jmh.add(helpItem);
	licenseItem = new JMenuItem(
			SolViewResources.getString("HELP.LICENSE.TEXT"),
			KeyEvent.VK_L);
	licenseItem.addActionListener(this);
	jmh.add(licenseItem);

	jm.add(jmh);
	setJMenuBar(jm);

	setIconImage(new ImageIcon(this.getClass().getClassLoader()
			.getResource("pixmaps/solview.png")).getImage());

	setSize(720, 600);
	setVisible(true);
    }

    class winExit extends WindowAdapter {
	public void windowClosing(WindowEvent we) {
	    JingleMultiFrame.unregister(SolView.this);
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == cloneItem) {
	    new SolView();
	}
	if (e.getSource() == closeItem) {
	    JingleMultiFrame.unregister(this);
	}
	if (e.getSource() == exitItem) {
	    System.exit(0);
	}
	if (e.getSource() == helpItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/index.html", "text/html");
	}
	if (e.getSource() == licenseItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/CDDL.txt", "text/plain");
	}
    }

    /**
     * Create a new solview application from the command line.
     *
     * @param args command line arguments, ignored
     */
    public static void main(String args[]) {
	new SolView();
    }
}
