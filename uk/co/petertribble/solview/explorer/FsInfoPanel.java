/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

package uk.co.petertribble.solview.explorer;

import javax.swing.*;
import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.solview.InfoCommand;

/**
 * FsInfoPanel - shows Filesystem status.
 * @author Peter Tribble
 * @version 1.0
 */
public class FsInfoPanel extends InfoPanel {

    private JKstat jkstat;
    private JFSstatPanel fsPanel;

    /**
     * Display a file system information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public FsInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	switch (hi.getType()) {
	    case SysItem.FS_FSSTAT:
		displayFS();
		break;
	    case SysItem.ZFS_POOL:
		displayZpool();
		break;
	    case SysItem.ZFS_FS:
		displayZFS();
		break;
	    case SysItem.FS_CONTAINER:
		displaySummary();
		break;
	    case SysItem.ZFS_CONTAINER:
		displayZSummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (fsPanel != null) {
	    fsPanel.stopLoop();
	}
    }

    /*
     * Top level summary. just df
     *
     * The last column header says "Mounted on" so we need to explicitly
     * specify the number of columns.
     */
    private void displaySummary() {
	jvp.add(new JLabel("Filesystem Usage Summary"));
	InfoCommand ic = new InfoCommand("df", "/usr/sbin/df", "-h");
	addText(new CommandTableModel(ic.getOutput(), 6));
    }

    /*
     * Top level summary. just zpool status
     */
    private void displayZSummary() {
	jvp.add(new JLabel("ZFS Pool Summary"));
	InfoCommand ic = new InfoCommand("zp", "/usr/sbin/zpool", "list");
	addText(new CommandTableModel(ic.getOutput()));
    }

    /*
     * Zpool details
     */
    private void displayZpool() {
	Zpool zp = (Zpool) hi.getAttribute("zpool");
	jvp.add(new JLabel("Details of ZFS pool " + zp.getName()));
	JPanel jzp = new JPanel();
	jzp.setLayout(new BoxLayout(jzp, BoxLayout.PAGE_AXIS));

	InfoCommand ic = new InfoCommand("zp", "/usr/sbin/zpool",
					"status " + zp.getName());
	jzp.add(new SysCmdPanel(ic));

	ic = new InfoCommand("zp", "/usr/sbin/zpool",
					"get all " + zp.getName());
	JTable jt = new JTable(new CommandTableModel(ic.getOutput()));
	jzp.add(jt.getTableHeader());
	jzp.add(jt);

	jvp.add(new JScrollPane(jzp));
    }

    /*
     * fsstat
     */
    private void displayFS() {
	jvp.add(new JLabel("Filesystem statistics"));
	fsPanel = new JFSstatPanel(jkstat, 5);
	jvp.add(new JScrollPane(fsPanel));
    }

    /*
     * ZFS filesystem properties
     *
     * The readable form of the date has embedded spaces, so the zfs
     * command reorders the columns so the property goes last and the
     * table explicitly has 4 columns.
     */
    private void displayZFS() {
	Zfilesys zfs = (Zfilesys) hi.getAttribute("zfs");
	jvp.add(new JLabel("ZFS Filesystem properties for " + zfs.getName()));
	InfoCommand ic = new InfoCommand("zf", "/usr/sbin/zfs",
		"get -o name,property,source,value all " + zfs.getName());
	addText(new CommandTableModel(ic.getOutput(), 4));
    }
}
