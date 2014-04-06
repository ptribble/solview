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

package uk.co.petertribble.solview;

import java.util.*;

/**
 * InfoCommandList - a List of Solaris information commands.
 * @author Peter Tribble
 * @version 1.0
 */
public class InfoCommandList extends Vector <InfoCommand> {

    // a backing hash so we can look up commands by description
    private Map <String, InfoCommand> infoMap;

    /**
     * Construct a List of available commands.
     * Note that it's actually a Vector.
     */
    public InfoCommandList() {
	infoMap = new HashMap <String, InfoCommand> ();
	// add all the possible commands here
	addCommand(SolViewResources.getString("INFO.CORE"),
		"/usr/bin/coreadm");
	addCommand(SolViewResources.getString("INFO.DF"),
		"/usr/sbin/df", "-kl");
	addCommand(SolViewResources.getString("INFO.IOSTAT"),
		"/usr/bin/iostat", "-En");
	addCommand(SolViewResources.getString("INFO.HOSTID"),
		"/usr/bin/hostid");
	addCommand(SolViewResources.getString("INFO.IFCONFIG"),
		"/usr/sbin/ifconfig", "-a");
	addCommand(SolViewResources.getString("INFO.INETADM"),
		"/usr/sbin/inetadm");
	addCommand(SolViewResources.getString("INFO.ISA"),
		"/usr/bin/isainfo", "-v");
	addCommand(SolViewResources.getString("INFO.META"),
		"/usr/sbin/metastat");
	addCommand(SolViewResources.getString("INFO.MDDB"),
		"/usr/sbin/metadb", "-i");
	addCommand(SolViewResources.getString("INFO.MOD"),
		"/usr/sbin/modinfo");
	addCommand(SolViewResources.getString("INFO.NSCD"),
		"/usr/sbin/nscd", "-g");
	addCommand(SolViewResources.getString("INFO.NTP"),
		"/usr/sbin/ntpq", "-p");
	addCommand(SolViewResources.getString("INFO.PKGS"),
		"/usr/bin/pkginfo");
	addCommand(SolViewResources.getString("INFO.PATCH"),
		"/usr/bin/showrev", "-p");
	addCommand(SolViewResources.getString("INFO.EEPROM"),
		"/usr/sbin/eeprom");
	addCommand(SolViewResources.getString("INFO.PRTCONF"),
		"/usr/sbin/prtconf");
	addCommand(SolViewResources.getString("INFO.PRTDIAG"),
		"/usr/sbin/prtdiag");
	addCommand(SolViewResources.getString("INFO.PRTFRU"),
		"/usr/sbin/prtfru");
	addCommand(SolViewResources.getString("INFO.PRTPICL"),
		"/usr/sbin/prtpicl");
	addCommand(SolViewResources.getString("INFO.PSRINFO"),
		"/usr/sbin/psrinfo", "-v");
	addCommand(SolViewResources.getString("INFO.ROUTE"), "/sbin/routeadm");
	addCommand(SolViewResources.getString("INFO.RPC"),
		"/usr/bin/rpcinfo", "-s");
	addCommand(SolViewResources.getString("INFO.SHARE"), "/usr/sbin/share");
	addCommand(SolViewResources.getString("INFO.SMBIOS"),
		"/usr/sbin/smbios");
	addCommand(SolViewResources.getString("INFO.SWAP"),
		"/usr/sbin/swap", "-l");
	addCommand(SolViewResources.getString("INFO.UNAME"),
		"/usr/bin/uname", "-a");
	addCommand(SolViewResources.getString("INFO.UP"), "/usr/bin/uptime");
	addCommand(SolViewResources.getString("INFO.ZPOOL"),
		"/usr/sbin/zpool", "status");
	addCommand(SolViewResources.getString("INFO.ZFS"),
		"/usr/sbin/zfs", "list");
	addCommand(SolViewResources.getString("INFO.ZONES"),
		"/usr/sbin/zoneadm", "list -icv");
    }

    private void addCommand(String text, String cmd) {
	addCommand(text, cmd, (String) null);
    }

    private void addCommand(String text, String cmd, String args) {
	addCommand(new InfoCommand(text, cmd, args));
    }

    private void addCommand(InfoCommand infocmd) {
	if (infocmd.exists() &&
			!infoMap.containsKey(infocmd.toString())) {
	    infoMap.put(infocmd.toString(), infocmd);
	    add(infocmd);
	}
    }

    /**
     * Return the InfoCommand of the given name. Used by the jsp interface.
     *
     * @param s the name of the InfoCommand to look up
     *
     * @return the InfoCommand matching the given name
     */
    public InfoCommand getCmd(String s) {
	return infoMap.get(s);
    }

    /**
     * Return a html formatted table representing the output of the given
     * command.
     *
     * @param infocmd the InfoCommand to display the result of
     *
     * @return a String in html format representing the given command
     */
    public String infoTable(InfoCommand infocmd) {
	StringBuilder sb = new StringBuilder();
	sb.append(SolViewResources.getString("INFO.OUTPUT.TEXT"));
	sb.append(" <b>").append(infocmd.getFullCmd());
	sb.append("</b>\n<hr>\n<pre>");
	sb.append(infocmd.getOutput());
	sb.append("</pre>");
	return sb.toString();
    }
}
