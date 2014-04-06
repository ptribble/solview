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
import uk.co.petertribble.solview.InfoCommand;

/**
 * SVMconfig - represent the configuration of SVM.
 * @author Peter Tribble
 * @version 1.0
 */
public class SVMconfig {
    private static final SVMconfig INSTANCE = new SVMconfig();

    /*
     * I'm not sure how to handle metasets. Presumably there's a parent class
     * of this one that holds the lists of metasets, or should that be here?
     */
    private Set <MetaDB> metadbs;
    private Set <MetaHS> metahotspares;
    private Set <MetaDevice> metadevices;
    private Map <String, MetaDevice> metamap;

    /**
     * Create a new SVMconfig, to hold details of the SVM configuration.
     */
    private SVMconfig() {
	metadbs = new HashSet <MetaDB> ();
	metadevices = new HashSet <MetaDevice> ();
	metahotspares = new HashSet <MetaHS> ();
	metamap = new HashMap <String, MetaDevice> ();
	parse_metadb();
	parse_metastat();
    }

    /**
     * Get the singleton SVMconfig instance.
     *
     * @return the singleton SVMconfig instance
     */
    public static SVMconfig getInstance() {
	return INSTANCE;
    }

    /**
     * Get the Set of metadevices.
     *
     * @return the Set of metadevices
     */
    public Set <MetaDevice> devices() {
	return new HashSet <MetaDevice> (metadevices);
    }

    /**
     * Get the Set of metadevice databases.
     *
     * @return the Set of metadevice databases
     */
    public Set <MetaDB> databases() {
	return new HashSet <MetaDB> (metadbs);
    }

    /**
     * Get a metadevice by name.
     *
     * @param name the name of the desired MetaDevice
     *
     * @return the named MetaDevice
     */
    public MetaDevice getDevice(String name) {
	return metamap.get(name);
    }

    private void parse_metadb() {
	InfoCommand ic = new InfoCommand("MD", "/usr/sbin/metadb");
	if (ic.exists()) {
	    for (String line : ic.getOutput().split("\n")) {
		String[] ts = line.split("\\s+");
		String ms = ts[ts.length - 1];
		if (ms.startsWith("/")) {
		    boolean gotit = false;
		    for (MetaDB mdb : metadbs) {
			if (ms.equals(mdb.getDevice())) {
			    mdb.addReplica();
			    gotit = true;
			}
		    }
		    if (!gotit) {
			metadbs.add(new MetaDB(ms));
		    }
		}
	    }
	}
    }

    /*
     * Parsing metastat output: we take two passes. In the first, we enumerate
     * the list of devices. In the second, we assign properties to build up
     * relationships.
     */
    private void parse_metastat() {
	InfoCommand ic = new InfoCommand("MD", "/usr/sbin/metastat", "-p");
	if (ic.exists()) {
	    String[] lines = ic.getOutput().split("\n");
	    for (String line : lines) {
		String[] ds = line.split("\\s+");
		/*
		 * Conventionally, metadevices start with d; the only other
		 * possible entries are hot spares starting with hsp.
		 */
		if (ds[0].startsWith("d")) {
		    // metadevice
		    MetaDevice md = parse_metadevice(ds);
		    metadevices.add(md);
		    metamap.put(ds[0], md);
		} else if (ds[0].startsWith("hsp")) {
		    // Hot spare pool
		    metahotspares.add(parse_hotspare(ds));
		} else {
		    System.err.println("Unhandled metastat output\n" + line);
		}
	    }
	    for (String line : lines) {
		if (line.startsWith("d")) {
		    relate_metadevices(line.split("\\s+"));
		}
	    }
	}
    }

    private MetaDevice parse_metadevice(String[] ds) {
	int mtype = 0;
	if (ds[1].equals("-m")) {
	    mtype = MetaDevice.MIRROR;
	} else if (ds[1].equals("-r")) {
	    mtype = MetaDevice.RAID5;
	} else if (ds[1].equals("-p")) {
	    mtype = MetaDevice.SOFTPART;
	} else if (ds[1].equals("1")) {
	    mtype = MetaDevice.STRIPE;
	} else {
	    mtype = MetaDevice.CONCAT;
	}
	return new MetaDevice(ds[0], mtype);
    }

    /*
     * Walk through the metadevices to define relationships
     *
     * FIXME add RAID and CONCAT
     */
    private void relate_metadevices(String[] ds) {
	MetaDevice md = metamap.get(ds[0]);
	if (md == null) {
	    return;
	}
	if (md.getType() == MetaDevice.MIRROR) {
	    /*
	     * format: d0 -m d1 [d2 [d3]] n
	     *
	     * For each submirror, add it as a child of the primary device
	     * and add the primary as the parent of each submirror
	     */
	    for (int i = 2; i < ds.length; i++) {
		MetaDevice md2 = metamap.get(ds[i]);
		if (md2 != null) {
		    md.addChild(md2);
		    md2.setParent(md);
		}
	    }
	}
	if (md.getType() == MetaDevice.STRIPE) {
	    /*
	     * format d0 1 n cXtYdZsA [cXtYdZsA ...]
	     * where there are n disks
	     */
	    int imax = Integer.parseInt(ds[2]);
	    for (int i = 0; i < imax; i++) {
		md.addComponent(ds[i+3]);
	    }
	}
    }

    private MetaHS parse_hotspare(String[] ds) {
	return new MetaHS(ds[0]);
    }
}
