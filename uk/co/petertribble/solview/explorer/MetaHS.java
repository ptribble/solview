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

/**
 * Represent a Solaris Volume Manager hot spare.
 * @author Peter Tribble
 * @version 1.0
 */
public class MetaHS {

    private String name;

    /**
     * Create a new MetaHS object.
     *
     * @param name the name of this hot spare
     */
    public MetaHS(String name) {
	this.name = name;
    }

    /**
     * Get the name of this hot spare.
     *
     * @return the name of this hot spare
     */
    public String getName() {
	return name;
    }
}
