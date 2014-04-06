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

import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import uk.co.petertribble.jkstat.demo.JKdemo;

/**
 * Show ZFS ARC (adaptive replacement cache) statistics.
 * @author Peter Tribble
 * @version 1.0
 */
public class ArcStat extends JKdemo {

    private ArcStatPanel mainPanel;

    /**
     * Create an ArcStat demo.
     */
    public ArcStat() {
	this(new NativeJKstat(), true);
    }

    /**
     * Create an ArcStat demo.
     *
     * @param jkstat a JKstat object
     * @param standalone if false, indicates that this demo is being called
     * from another application
     */
    public ArcStat(JKstat jkstat, boolean standalone) {
	super("ArcStat", 5, standalone);

	mainPanel = new ArcStatPanel(jkstat, 5);
	setContentPane(mainPanel);

	pack();
	setVisible(true);
    }

    /**
     * Start the timer loop, so that the accessory updates itself.
     */
    public void startLoop() {
	mainPanel.startLoop();
    }

    @Override
    public void stopLoop() {
	mainPanel.stopLoop();
    }

    @Override
    public void setDelay(int i) {
	mainPanel.setDelay(i);
	setLabelDelay(i);
    }

    /**
     * Create a standalone ArcStat demo application.
     *
     * @param args Command line arguments, unused
     */
    public static void main(String[] args) {
	new ArcStat();
    }
}
