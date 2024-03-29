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

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import uk.co.petertribble.jproc.gui.JPinfoTable;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcessSet;
import uk.co.petertribble.jproc.api.JProcessFilter;

/**
 * ProcessInfoPanel - shows Process status.
 * @author Peter Tribble
 * @version 1.0
 */
public class ProcessInfoPanel extends InfoPanel {

    private JPinfoTable jpip;

    /**
     * Display a Process information panel.
     *
     * @param hi The item to display
     */
    public ProcessInfoPanel(SysItem hi) {
	super(hi);

	if (hi.getType() == SysItem.PROCESS_CONTAINER) {
	    displaySummary();
	}

	validate();
    }

    @Override
    public void stopLoop() {
	jpip.stopLoop();
    }

    /*
     * Top level summary. List of Processes.
     */
    private void displaySummary() {
	jvp.add(new JLabel("Running Processes"));
	JProc jproc = new JProc();
	jpip = new JPinfoTable(jproc, new JProcessFilter(), 5);
	jpip.removeColumn("CT");
	jpip.removeColumn("TASK");
	jpip.removeColumn("PROJ");
	jpip.removeColumn("GROUP");
	jpip.removeColumn("ppid");
	JProcessSet jps = new JProcessSet(jproc);
	if (jps.getZones().size() == 1) {
	    jpip.removeColumn("ZONE");
	}
	jvp.add(new JScrollPane(jpip));
    }
}
