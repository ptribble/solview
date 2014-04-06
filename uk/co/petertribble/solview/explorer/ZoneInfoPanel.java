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
import uk.co.petertribble.solview.InfoCommand;

/**
 * ZoneInfoPanel - shows Zone status.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZoneInfoPanel extends InfoPanel {

    /**
     * Display a zone information panel.
     *
     * @param hi The item to display
     */
    public ZoneInfoPanel(SysItem hi) {
	super(hi);

	if (hi.getType() == SysItem.ZONE_CONTAINER) {
	    displaySummary();
	} else if (hi.getType() == SysItem.ZONE_ZONE) {
	    displayZone();
	}

	validate();
    }

    /*
     * Top level summary. List of Zones.
     * FIXME: list -cp and then enhance the table to understand parseable
     * output with specific separators and separate headers
     */
    private void displaySummary() {
	jvp.add(new JLabel("Zone Summary"));
	InfoCommand ic = new InfoCommand("za", "/usr/sbin/zoneadm", "list -cv");
	addText(new CommandTableModel(ic.getOutput()));
    }

    /*
     * Describe a Zone.
     *
     * FIXME implement, and pass zone name
     */
    private void displayZone() {
	jvp.add(new JLabel("Zone Summary"));
	addText(new InfoCommand("zc", "/usr/sbin/zonecfg", "-z info"));
    }
}
