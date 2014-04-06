package uk.co.petertribble.solview;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import uk.co.petertribble.solview.explorer.SysPanel;
import uk.co.petertribble.jingle.JingleInfoFrame;

/**
 * MultiView - shows Solaris information.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class MultiView extends JFrame implements ActionListener {

    private JMenuItem exitItem;
    private JMenuItem helpItem;
    private JMenuItem licenseItem;
    private String helpfile;

    /**
     * Create a new View.
     *
     * @param stitle a String to use as the window title
     * @param hlabel a String to use for the help menu button
     * @param helpfile a String naming the help file to use
     * @param panel The panel to display
     */
    public MultiView(String stitle, String hlabel, String helpfile,
		String mypixmap, JComponent panel) {
	super(SolViewResources.getString(stitle));
	this.helpfile = helpfile;

	addWindowListener(new winExit());
	getContentPane().add(panel, BorderLayout.CENTER);

	JMenuBar jm = new JMenuBar();

	JMenu jme = new JMenu(SolViewResources.getString("FILE.TEXT"));
	jme.setMnemonic(KeyEvent.VK_F);
	exitItem = new JMenuItem(SolViewResources.getString("FILE.EXIT"),
				KeyEvent.VK_X);
	exitItem.addActionListener(this);
	jme.add(exitItem);

	jm.add(jme);

	JMenu jmh = new JMenu(SolViewResources.getString("HELP.TEXT"));
	jmh.setMnemonic(KeyEvent.VK_H);
	helpItem = new JMenuItem(SolViewResources.getString(hlabel),
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
			.getResource(mypixmap)).getImage());

	setSize(720, 600);
	setVisible(true);
    }

    class winExit extends WindowAdapter {
	public void windowClosing(WindowEvent we) {
	    System.exit(0);
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == exitItem) {
	    System.exit(0);
	}
	if (e.getSource() == helpItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				helpfile, "text/html");
	}
	if (e.getSource() == licenseItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/CDDL.txt", "text/plain");
	}
    }

    /**
     * Create a new view from the command line.
     *
     * @param args command line arguments, the first argument representinmg
     * the view to show.
     */
    public static void main(String args[]) {
	if (args[0].equals("information")) {
	    new MultiView("SOLVIEW.INFO.TEXT", "HELP.ABOUT.INFO",
			"help/infoview.html", "pixmaps/solinfo.png",
			new InfoPanel());
	} else if (args[0].equals("explorer")) {
	    new MultiView("SOLVIEW.EXPL.TEXT", "HELP.ABOUT.EXPL",
			"help/explorer.html", "pixmaps/solexpl.png",
			new SysPanel());
	} else if (args[0].equals("services")) {
	    new MultiView("SOLVIEW.SERV.TEXT", "HELP.ABOUT.SERV",
			"help/serviceview.html", "pixmaps/smfview.png",
			new SmfPanel());
	}
    }
}
