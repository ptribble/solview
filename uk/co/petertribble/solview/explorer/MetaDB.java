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
 * Represent a Solaris metadevice database.
 * @author Peter Tribble
 * @version 1.0
 */
public class MetaDB {

    private String devname;
    private int count;

    /**
     * Create a new MetaDB to represent an SVM metadevice database.
     *
     * @param devname the name of the device underlying this metadb
     */
    public MetaDB(String devname) {
	this.devname = devname;
	count = 1;
    }

    /**
     * Add another replica to this metadb.
     */
    public void addReplica() {
	count++;
    }

    /**
     * Get the device underlying this metadb.
     *
     * @return the name of the device underlying this metadb
     */
    public String getDevice() {
	return devname;
    }

    /**
     * Get how many replicas are contained in this metadb.
     *
     * @return the number of replicas in this metadb
     */
    public int getCount() {
	return count;
    }

    /**
     * Check to see if a given device matches this metadb. The name is checked
     * against both the short device name and the /dev/dsk version of the name.
     *
     * @param dev the device name to match with this metadb
     *
     * @return true if the given device has the same name as this metadb
     */
    public boolean contains(String dev) {
	return (devname == null) ? false :
		(devname.equals(dev) || devname.equals("/dev/dsk/" + dev));
    }
}
