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

import java.util.*;
import java.io.File;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * Parse and extract data from the /etc/mnttab file.
 * @author Peter Tribble
 * @version 1.0
 */
public class Mnttab {

    private Map <String, String> devmap;
    private Map <String, String> fsmap;
    private Map <String, String> fstypemap;
    private Map <String, List <String>> optmap;
    private List <String> fsList;
    private long modified;

    /**
     * Parse the list of mounted filesystems.
     */
    public Mnttab() {
	devmap = new HashMap <String, String> ();
	fsmap = new HashMap <String, String> ();
	fstypemap = new HashMap <String, String> ();
	optmap = new HashMap <String, List <String>> ();
	fsList = new ArrayList <String> ();
	update();
    }

    /**
     * Update the list of mount entries. If the mnttab hasn't been modified,
     * then does nothing, else parses the mnttab completely.
     */
    public void update() {
	File f = new File("/etc/mnttab");
	if (f.lastModified() == modified) {
	    return;
	}
	modified = f.lastModified();
	for (String line : JumbleFile.getLines(f)) {
	    StringTokenizer st = new StringTokenizer(line);
	    String sdev = st.nextToken();
	    String sname = st.nextToken();
	    String stype = st.nextToken();
	    String soptions = st.nextToken();
	    // ignore timestamp
	    List <String> vopt = Arrays.asList(soptions.split(","));
	    // parsed, add to data structures
	    devmap.put(sname, sdev);
	    fsmap.put(sdev, sname);
	    fstypemap.put(sname, stype);
	    optmap.put(sname, vopt);
	    fsList.add(sname);
	}
    }

    /**
     * Returns the String representation of the device for the given filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The device the given filesystem is mounted on
     */
    public String getDevice(String fs) {
	return devmap.get(fs);
    }

    /**
     * Returns the String representation of the filesystem for the given device.
     * Multiple filesystems may be mounted on the same device (for example
     * swap), and in that case only one is returned.
     *
     * @param device The device of interest
     *
     * @return The filesystem mounted on the given device
     */
    public String getFs(String device) {
	return fsmap.get(device);
    }

    /**
     * Returns the String representation of the filesystem type of the given
     * filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The type of the given filesystem
     */
    public String getFsType(String fs) {
	return fstypemap.get(fs);
    }

    /**
     * Returns a List of Strings which are the mount options for the given
     * filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The mount options of the given filesystem
     */
    public List <String> getOptions(String fs) {
	return optmap.get(fs);
    }

    /**
     * Find the mount option that matches the key and return its
     * value. Mount options are either single words, or pairs of
     * words separated by an = sign. If the mount option is not
     * present, return null.
     */
    private String getOptionValue(String sf, String s) {
	String sval = null;
	for (String si : getOptions(sf)) {
	    if (si.startsWith(s)) {
		int i = si.indexOf("=");
		if (i > 0) {
		    sval = si.substring(i+1);
		}
	    }
	}
	return sval;
    }

    /**
     * Return the zone the filesystem is mounted in. If not specified, return
     * the literal String "global".
     *
     * @param fs The filesystem of interest
     *
     * @return The name of the zone that the given filesystem is mounted in.
     */
    public String getZoneName(String fs) {
	String sz = getOptionValue(fs, "zone");
	return (sz == null) ? "global" : sz;
    }

    /**
     * Return the device id of the given filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The device id of the given filesystem
     */
    public String getDeviceID(String fs) {
	return getOptionValue(fs, "dev");
    }

    /**
     * Return true or false depending on whether the filesystem has
     * the ignore flag set. If no filesystem matches, return false.
     *
     * @param fs The filesystem of interest
     *
     * @return Whether the given filesystem has the ignore flag set
     */
    public boolean getIgnore(String fs) {
	List <String> v = optmap.get(fs);
	return (v == null) ? false : v.contains("ignore");
    }

    /**
     * Return the first filesystem with the given deviceid.
     *
     * @param s A device id
     *
     * @return The name of the first filesystem with the given device id
     */
    public String getFSforDevice(String s) {
	for (String fs : fsList) {
	    if (s.equals(getDeviceID(fs))) {
		return fs;
	    }
	}
	// we didn't find it
	return null;
    }

    /**
     * Return a list of zones that have filesystems mounted.
     *
     * @return A List of zones that have mounted filesystems
     */
    public List <String> getZoneList() {
	List <String> v = new ArrayList <String> ();
	for (String fs : fsList) {
	    String zn = getZoneName(fs);
	    if (!v.contains(zn)) {
		v.add(zn);
	    }
	}
	return v;
    }

    /**
     * Return a list of filesystems in a given zone.
     *
     * @param myzone The zone name to be queried
     *
     * @return A List of filesystems that the given zone has mounted
     */
    public List <String> getFSforZone(String myzone) {
	List <String> v = new ArrayList <String> ();
	for (String fs : fsList) {
	    if (getZoneName(fs).equals(myzone)) {
		v.add(fs);
	    }
	}
	return v;
    }

    /**
     * Return a list of mounted fstypes.
     *
     * @return A List of filesystem types that are currently mounted
     */
    public List <String> getFstypeList() {
	List <String> v = new ArrayList <String> ();
	for (String s : fstypemap.values()) {
	    if (!v.contains(s)) {
		v.add(s);
	    }
	}
	return v;
    }

    /**
     * Return a list of filesystems of the given fstype.
     *
     * @param ftype The requested filesystem type
     *
     * @return A List of filesystems of the given filesystem type
     */
    public List <String> getFSforFstype(String ftype) {
	List <String> v = new ArrayList <String> ();
	for (String s : fstypemap.keySet()) {
	    String t = fstypemap.get(s);
	    if (t.equals(ftype)) {
		v.add(s);
	    }
	}
	return v;
    }

    /**
     * Return a list of device ids of the given fstype.
     *
     * @param ftype The requested filesystem type
     *
     * @return A List of device IDs for the given filesystem type
     */
    public List <String> getIDforFstype(String ftype) {
	List <String> v = new ArrayList <String> ();
	for (String s : fstypemap.keySet()) {
	    String t = fstypemap.get(s);
	    if (t.equals(ftype)) {
		String dev = devmap.get(s);
		if (!v.contains(dev)) {
		    v.add(dev);
		}
	    }
	}
	return v;
    }
}
