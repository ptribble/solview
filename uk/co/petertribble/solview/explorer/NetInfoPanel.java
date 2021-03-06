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
import uk.co.petertribble.jkstat.gui.AccessoryNetPanel;
import uk.co.petertribble.jkstat.gui.KstatTable;
import uk.co.petertribble.jkstat.gui.KstatBaseChart;
import uk.co.petertribble.jkstat.gui.KstatChart;
import uk.co.petertribble.solview.InfoCommand;
import java.util.List;
import java.util.Arrays;
import org.jfree.chart.ChartPanel;

/**
 * NetInfoPanel - shows Network status and information.
 * @author Peter Tribble
 * @version 1.0
 *
 */
public class NetInfoPanel extends InfoPanel {

    private AccessoryNetPanel acp;
    private JKstat jkstat;
    private KstatTable kt;
    private KstatBaseChart kbc;

    /**
     * Display a network information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public NetInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	switch (hi.getType()) {
	    case SysItem.NET_INTERFACE:
		displayInterface(hi.getKstat());
		break;
	    case SysItem.NET_PROTO_IP:
		displayProto("ip");
		break;
	    case SysItem.NET_PROTO_TCP:
		displayProto("tcp");
		break;
	    case SysItem.NET_PROTO_UDP:
		displayProto("udp");
		break;
	    case SysItem.NET_STAT:
		displayNetstat();
		break;
	    case SysItem.NET_CONTAINER:
		displaySummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (acp != null) {
	    acp.stopLoop();
	}
	if (kt != null) {
	    kt.stopLoop();
	}
	if (kbc != null) {
	    kbc.stopLoop();
	}
    }

    /*
     * Top level summary.
     */
    private void displaySummary() {
	jvp.add(new JLabel("Interfaces: Output from ifconfig -a"));

	addText(new InfoCommand("IF", "/usr/sbin/ifconfig", "-a"));
    }

    /*
     * netstat
     */
    private void displayNetstat() {
	jvp.add(new JLabel("Output from netstat -an"));

	addText(new InfoCommand("IF", "/usr/bin/netstat", "-an"));
    }

    /*
     * A network interface
     */
    private void displayInterface(Kstat ks) {
	if (ks != null) {
	    jvp.add(new JLabel("Details of Network Interface " + ks.getName()));
	    addAccessory(ks);
	}
    }

    /*
     * Generic protocol handler.
     */
    private void displayProto(String proto) {
	jvp.add(new JLabel("Network protocol: " + proto));
	kt = new KstatTable(proto, "0", proto, 5, jkstat);
	jvp.add(new JScrollPane(kt));
    }

    /*
     * Add an accessory if we can.
     * NOTE: we've already checked for ks being non-null above
     */
    private void addAccessory(Kstat ks) {
	acp = new AccessoryNetPanel(ks, 5, jkstat);
	jvp.add(acp);
	List <String> statistics = Arrays.asList("rbytes64", "obytes64");
	kbc = new KstatChart(jkstat, ks, statistics, true);
	jvp.add(new ChartPanel(kbc.getChart()));
    }
}
