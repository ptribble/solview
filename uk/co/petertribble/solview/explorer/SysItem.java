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

import java.util.Map;
import java.util.HashMap;
import uk.co.petertribble.jkstat.api.Kstat;

/**
 * SysItem - represent an item of hardware.
 * @author Peter Tribble
 * @version 1.2
 */
public class SysItem {

    /*
     * The following are types of items
     */
    public static final int HOST = 1;

    public static final int CPU_CONTAINER = 11;
    public static final int CPU = 12;
    public static final int CPU_CORE = 13;
    public static final int CPU_THREAD = 14;

    public static final int DISK_CONTAINER = 21;
    public static final int DISK = 22;
    public static final int DISK_PARTITION = 23;
    public static final int DISK_METADEVICE = 24;
    public static final int DISK_META_CONTAINER = 25;
    public static final int DISK_IO = 26;

    public static final int NET_CONTAINER = 31;
    public static final int NET_INTERFACE = 32;
    public static final int NET_PROTOCOL = 33;
    public static final int NET_PROTO_IP = 34;
    public static final int NET_PROTO_TCP = 35;
    public static final int NET_PROTO_UDP = 36;
    public static final int NET_STAT = 37;

    public static final int MEM_CONTAINER = 41;
    public static final int MEM_KMEM = 42;
    public static final int MEM_ARCSTAT = 43;

    public static final int FS_CONTAINER = 51;
    public static final int FS_FSSTAT = 52;
    public static final int ZFS_CONTAINER = 53;
    public static final int ZFS_POOL = 54;
    public static final int ZFS_FS = 55;

    public static final int ZONE_CONTAINER = 61;
    public static final int ZONE_ZONE = 62;

    public static final int PROCESS_CONTAINER = 71;

    /*
     * The following are status codes, used by the cell renderer to display
     * the health or quality of this item.
     */

    public static final int OK = 0;
    public static final int FAIL = 1;
    public static final int WARN = 2;
    public static final int UNKNOWN = 3;
    public static final int BLANK = 99;

    private Map <String, Object> attributes;
    private int type;
    private int status = BLANK;

    private Kstat kstat;

    /**
     * A container for a hardware item.
     *
     * @param type the type of item
     */
    public SysItem(int type) {
	this.type = type;
	attributes = new HashMap <String, Object> ();
    }

    /**
     * Return the type of this SysItem.
     *
     * @return the type of this item
     */
    public int getType() {
	return type;
    }

    /**
     * Add an attribute to this item. Attributes are named by their
     * key, and can be any Object. It is the responsibility of the
     * consumer of the attribute to know what the attribute represents.
     *
     * @param key the name of the attribute
     * @param value an Object representing this attribute
     */
    public void addAttribute(String key, Object value) {
	attributes.put(key, value);
    }

    /**
     * Get the named attribute.
     *
     * @param key the name of the desired attribute
     *
     * @return the attribute of the given name
     */
    public Object getAttribute(String key) {
	return attributes.get(key);
    }

    /**
     * Set the kstat associated with this item. This is sufficiently common
     * usage that it is handled explicitly rather than as an attribute.
     *
     * @see #getKstat
     *
     * @param kstat the Kstat to be associated with this item
     */
    public void setKstat(Kstat kstat) {
	this.kstat = kstat;
    }

    /**
     * Get the kstat associated with this item.
     *
     * @see #setKstat
     *
     * @return the Kstat associated with this item
     */
    public Kstat getKstat() {
	return kstat;
    }

    /**
     * Set the status of this item.
     *
     * @see #getStatus
     *
     * @param status the new status of this item
     */
    public void setStatus(int status) {
	this.status = status;
    }

    /**
     * Get the status of this item.
     *
     * @see #setStatus
     *
     * @return the status of this item
     */
    public int getStatus() {
	return status;
    }
}
