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

import java.io.File;
import uk.co.petertribble.solview.helpers.RunCommand;

/**
 * InfoCommand - a command to show Solaris information.
 * @author Peter Tribble
 * @version 1.0
 */
public class InfoCommand {

    private String text;
    private String cmd;
    private String fullcmd;

    /**
     * Create an informational command.
     *
     * @param text The name of the command
     * @param cmd The full path to the command
     */
    public InfoCommand(String text, String cmd) {
	this(text, cmd, (String) null);
    }

    /**
     * Create an informational command, with arguments.
     *
     * @param text The name of the command
     * @param cmd The full path to the command
     * @param args The arguments to the command
     */
    public InfoCommand(String text, String cmd, String args) {
	this.text = text;
	this.cmd = cmd;
	fullcmd = (args == null) ? cmd : cmd + " " + args;
    }

    /**
     * Override toString() to give the informational name.
     */
    @Override
    public String toString() {
	return text;
    }

    /**
     * Return the full command executed for this informational command,
     * including any arguments.
     *
     * @return The full command including any arguments
     */
    public String getFullCmd() {
	return fullcmd;
    }

    /**
     * Return whether the filename corresponding to the pathname exists.
     *
     * @return true if the command exists
     */
    public boolean exists() {
	return (new File(cmd)).exists();
    }

    /**
     * Return the textual output from executing this informational command.
     *
     * @return The output from running this command
     */
    public String getOutput() {
	return (exists()) ? (new RunCommand(fullcmd)).getOut() :
	    "Command not found";
    }
}
