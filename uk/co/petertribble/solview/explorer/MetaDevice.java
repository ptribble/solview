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

import java.util.Set;
import java.util.HashSet;

/**
 * Represent an SVM metadevice.
 * @author Peter Tribble
 * @version 1.0
 */
public class MetaDevice {

    /**
     * A stripe of disks.
     */
    public static final int STRIPE = 1;

    /**
     * A disk concatenation.
     */
    public static final int CONCAT = 2;

    /**
     * A mirror.
     */
    public static final int MIRROR = 3;

    /**
     * A raid5 stripe.
     */
    public static final int RAID5 = 4;

    /**
     * A soft partition.
     */
    public static final int SOFTPART = 5;

    /**
     * A trans (logging) device. No longer used, but included for completeness.
     */
    public static final int TRANS = 99;

    private String name;
    private int metatype;
    // private int interlace;
    private MetaDevice parent;
    private Set <MetaDevice> children;
    private Set <String> components;

    /**
     * Create a new MetaDevice object.
     *
     * @param name the name of the metadevice
     * @param metatype the type of the metadevice
     */
    public MetaDevice(String name, int metatype) {
	this.name = name;
	this.metatype = metatype;
	children = new HashSet <MetaDevice> ();
	components = new HashSet <String> ();
    }

    /**
     * Get the name of this metadevice.
     *
     * @return the name of this MetaDevice
     */
    public String getName() {
	return name;
    }

    /**
     * Get the type of this metadevice.
     *
     * @return the type of this MetaDevice
     */
    public int getType() {
	return metatype;
    }

    /**
     * Get the type of this metadevice, as a String.
     *
     * @return the string representation of the type of this MetaDevice
     */
    public String getTypeAsString() {
	String t;
	switch (metatype) {
	    case STRIPE:
		t = "Stripe";
		break;
	    case CONCAT:
		t = "Concatenation";
		break;
	    case MIRROR:
		t = "Mirror";
		break;
	    case RAID5:
		t = "Raid-5";
		break;
	    case SOFTPART:
		t = "Soft Partition";
		break;
	    default:
		t = "Unknown";
	}
	return t;
    }

    /**
     * Set the parent of this metadevice.
     *
     * @see #getParent
     *
     * @param parent the parent of this metadevice
     */
    public void setParent(MetaDevice parent) {
	this.parent = parent;
    }

    /**
     * Return the parent of this MetaDevice.
     *
     * @see #setParent
     *
     * @return the parent of this MetaDevice.
     */
    public MetaDevice getParent() {
	return parent;
    }

    /**
     * Add a child metadevice. For example, if a mirror this would be a
     * submirror.
     *
     * @param child a child MetaDevice
     */
    public void addChild(MetaDevice child) {
	children.add(child);
    }

    /**
     * Add a component. This is a disk device, not a MetaDevice.
     *
     * @param component the name of the device to add
     */
    public void addComponent(String component) {
	components.add(component);
    }

    /**
     * Return whether this MetaDevice contains the specified device.
     *
     * @param dev the name of the device to check for
     *
     * @return true if this MetaDevice contains the named device
     */
    public boolean contains(String dev) {
	return components.contains(dev);
    }
}
