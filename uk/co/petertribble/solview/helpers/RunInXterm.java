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

package uk.co.petertribble.solview.helpers;

import java.io.File;
import java.io.IOException;

/**
 * RunInXterm - run the given command in an xterm.
 * @author Peter Tribble
 * @version 1.0
 */
public class RunInXterm {

    private static String xtermbin;
    /*
     * We search all the following locations for a terminal emulator. It
     * must support the -e option. If none of them work, we fall back to
     * an unqualified xterm and cross our fingers.
     */
    private static String[] xsearch = { "/usr/bin/xterm",
					"/usr/openwin/bin/xterm",
					"/opt/sfw/bin/xterm",
					"/usr/bin/X11/xterm",
					"/usr/bin/gnome-terminal",
					"/usr/dt/bin/dtterm" };

    static {
	for (String s : xsearch) {
	    if (new File(s).exists()) {
		xtermbin = s;
		break;
	    }
	    if (xtermbin == null) {
		xtermbin = "xterm";
	    }
	}
    }

    /**
     * Run a command in an xterm window.
     *
     * @param cmd The command to run
     */
    public RunInXterm(String cmd) {
	try {
	    Runtime.getRuntime().exec(xtermbin + " -e " + cmd,
			(String[]) null, new File("/tmp"));
	} catch (IOException ioe) {}
    }
}
