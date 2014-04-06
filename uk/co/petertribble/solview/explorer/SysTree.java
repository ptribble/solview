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
import javax.swing.*;
import javax.swing.tree.*;
import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import uk.co.petertribble.solview.SolViewResources;

/**
 * SysTree - shows a hierarchical hardware view.
 * @author Peter Tribble
 * @version 1.0
 */
public class SysTree extends JTree {

    private JKstat jkstat;

    /**
     * Display a tree of hardware items.
     *
     * @param title the name of the tree
     */
    public SysTree(String title) {
	this(new NativeJKstat(), title);
    }

    /**
     * Display a tree of hardware items.
     *
     * @param title the name of the tree
     * @param jkstat a JKstat object
     */
    public SysTree(JKstat jkstat, String title) {
	this.jkstat = jkstat;

	DefaultMutableTreeNode root = new SysTreeNode(
					new SysItem(SysItem.HOST), title);
	buildTree(root);
	setModel(new DefaultTreeModel(root));
    }

    private void buildTree(DefaultMutableTreeNode root) {
	addProcessors(root);
	addDisks(root);
	addNetworks(root);
	ZFSconfig zconfig = ZFSconfig.getInstance();
	addMemory(root, zconfig);
	addFilesystems(root, zconfig);
	addProcesses(root);
	addZones(root);
    }

    private void addProcessors(DefaultMutableTreeNode root) {
	/*
	 * Add a node for each processor.
	 */
	ProcessorTree proctree = new ProcessorTree(jkstat);
	SysItem hi = new SysItem(SysItem.CPU_CONTAINER);
	hi.addAttribute("ptree", proctree);
	SysTreeNode htn = new SysTreeNode(hi,
				SolViewResources.getString("HARD.CPU"));
	root.add(htn);

	// loop over chips
	for (Long l : proctree.getChips()) {
	    SysItem hi2 = new SysItem(SysItem.CPU);
	    hi2.addAttribute("ptree", proctree);
	    hi2.addAttribute("chip", l);
	    SysTreeNode htnchip = new SysTreeNode(hi2, l.toString());
	    if (proctree.isMulticore()) {
		// multicore, loop over cores
		for (Long ll : proctree.getCores(l)) {
		    SysItem hi3 = new SysItem(SysItem.CPU_CORE);
		    SysTreeNode htncore = new SysTreeNode(hi3, ll.toString());
		    hi3.addAttribute("ptree", proctree);
		    hi3.addAttribute("chip", l);
		    hi3.addAttribute("core", ll);
		    if (proctree.isThreaded()) {
			// multithreaded, loop over threads
			for (Kstat ks : proctree.coreInfoStats(l, ll)) {
			    SysItem hi4 = new SysItem(SysItem.CPU_THREAD);
			    hi4.setKstat(ks);
			    hi4.addAttribute("ptree", proctree);
			    hi4.addAttribute("thread", ks.getInst());
			    hi4.addAttribute("core", ll);
			    hi4.addAttribute("chip", l);
			    htncore.add(new SysTreeNode(hi4,
							ks.getInstance()));
			}
		    } else {
			// single thread
			// we should go round the loop exactly once
			for (Kstat ks : proctree.coreInfoStats(l, ll)) {
			    hi3.setKstat(ks);
			}
		    }
		    htnchip.add(htncore);
		}
	    } else {
		// single core
		if (proctree.isThreaded()) {
		    // multithreaded, loop over threads
		    for (Kstat ks : proctree.chipInfoStats(l)) {
			SysItem hi3 = new SysItem(SysItem.CPU_THREAD);
			hi3.setKstat(ks);
			hi3.addAttribute("ptree", proctree);
			hi3.addAttribute("thread", ks.getInst());
			hi3.addAttribute("chip", l);
			htnchip.add(new SysTreeNode(hi3,
						ks.getInstance()));
		    }
		} else {
		    // single thread
		    for (Kstat ks : proctree.chipInfoStats(l)) {
			hi2.setKstat(ks);
		    }
		}
	    }
	    htn.add(htnchip);
	}
    }

    private void addDisks(DefaultMutableTreeNode root) {
	SysItem diskItem = new SysItem(SysItem.DISK_CONTAINER);
	SysTreeNode htn = new SysTreeNode(diskItem,
				SolViewResources.getString("HARD.DISK"));
	root.add(htn);
	htn.add(new SysTreeNode(new SysItem(SysItem.DISK_IO),
				SolViewResources.getString("HARD.IO")));

	// parent node for metadevices
	// only added if we find any
	SysTreeNode htnmd = null;

	/*
	 * Configuration for metadevices.
	 *
	 * The kstats use mdXX for device names, whereas metastat output uses
	 * dXX and drops the leading "m". We use the kstat version as the name
	 * of the tree node, but SVMconfig uses the other version. The mdmap
	 * to find nodes based on names uses the dXX scheme, hence the
	 * substring(1) calls below which chop off the leading "m".
	 */
	Set <SysTreeNode> disks = new TreeSet <SysTreeNode> ();
	List <SysTreeNode> mds = new ArrayList <SysTreeNode> ();
	Map <String, SysTreeNode> mdmap = new HashMap <String, SysTreeNode> ();

	// Ought to handle SVM metasets

	/*
	 * Here we enumerate disks, which include metadevices. We build
	 * metadevices into their own tree, and each disk acquires its
	 * partitions as children. We build up the list of devices into
	 * the 'disks' Set, which sorts them, and run through that at the
	 * end to add all the nodes to the tree.
	 */
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("disk");
	KstatSet kss = new KstatSet(jkstat, ksf);
	for (Kstat ks : kss.getKstats()) {
	    if (ks.getModule().equals("md")) {
		SysItem hi = new SysItem(SysItem.DISK_METADEVICE);
		hi.setKstat(ks);
		if (htnmd == null) {
		    htnmd = new SysTreeNode(
				new SysItem(SysItem.DISK_META_CONTAINER),
				SolViewResources.getString("HARD.MD"));
		}
		SysTreeNode stn = new SysTreeNode(hi, ks.getName());
		mdmap.put(ks.getName().substring(1), stn);
		mds.add(stn);
	    } else {
		SysItem hi = new SysItem(SysItem.DISK);
		hi.setKstat(ks);
		SysTreeNode htndisk = new SysTreeNode(hi, ks.getName());
		disks.add(htndisk);
		// add partitions
		KstatFilter ksfp = new KstatFilter(jkstat);
		ksfp.setFilterClass("partition");
		ksfp.addFilter(ks.getModule() + ":" + ks.getInstance() + "::");
		KstatSet kssp = new KstatSet(jkstat, ksfp);
		for (Kstat ksp : new TreeSet <Kstat> (kssp.getKstats())) {
		    SysItem hi3 = new SysItem(SysItem.DISK_PARTITION);
		    hi3.setKstat(ksp);
		    htndisk.add(new SysTreeNode(hi3, ksp.getName()));
		}
	    }
	}

	diskItem.addAttribute("ndisks", disks.size());

	// structure the metadevices into a tree
	if (htnmd != null) {
	    for (SysTreeNode stn : mds) {
		MetaDevice md = SVMconfig.getInstance()
				.getDevice(stn.toString().substring(1));
		if (md == null || md.getParent() == null) {
		    htnmd.add(stn);
		} else {
		    // child, find the parent
		    SysTreeNode pn = mdmap.get(md.getParent().getName());
		    if (pn == null) {
			htnmd.add(stn);
		    } else {
			pn.add(stn);
		    }
		}
	    }
	    disks.add(htnmd);
	}

	// now add the entries to the tree, so they end up sorted
	for (SysTreeNode stn : disks) {
	    htn.add(stn);
	}
    }

    private void addNetworks(DefaultMutableTreeNode root) {
	SysTreeNode htn = new SysTreeNode(new SysItem(SysItem.NET_CONTAINER),
				SolViewResources.getString("HARD.NETWORK"));
	root.add(htn);

	// enumerate networks
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("net");
	ksf.addFilter(":::rbytes64");
	ksf.addNegativeFilter("::mac");
	for (Kstat ks : new TreeSet <Kstat> (ksf.getKstats())) {
	    SysItem hi = new SysItem(SysItem.NET_INTERFACE);
	    hi.setKstat(ks);
	    htn.add(new SysTreeNode(hi, ks.getName()));
	}

	// add network protocols
	SysTreeNode htnp = new SysTreeNode(new SysItem(SysItem.NET_PROTOCOL),
				SolViewResources.getString("HARD.NETPROTO"));
	htn.add(htnp);

	htnp.add(new SysTreeNode(new SysItem(SysItem.NET_PROTO_IP), "ip"));
	htnp.add(new SysTreeNode(new SysItem(SysItem.NET_PROTO_TCP), "tcp"));
	htnp.add(new SysTreeNode(new SysItem(SysItem.NET_PROTO_UDP), "udp"));

	// netstat -an
	htn.add(new SysTreeNode(new SysItem(SysItem.NET_STAT), "netstat"));
    }

    private void addMemory(DefaultMutableTreeNode root, ZFSconfig zconfig) {
	SysTreeNode htn = new SysTreeNode(new SysItem(SysItem.MEM_CONTAINER),
				SolViewResources.getString("HARD.MEMORY"));
	root.add(htn);

	htn.add(new SysTreeNode(new SysItem(SysItem.MEM_KMEM),
				SolViewResources.getString("HARD.KMEM")));

	if (!zconfig.pools().isEmpty()) {
	    htn.add(new SysTreeNode(new SysItem(SysItem.MEM_ARCSTAT),
				SolViewResources.getString("HARD.ZMEM")));
	}
    }

    private void addFilesystems(DefaultMutableTreeNode root,
				ZFSconfig zconfig) {
	SysTreeNode htn = new SysTreeNode(new SysItem(SysItem.FS_CONTAINER),
				SolViewResources.getString("HARD.FS"));
	root.add(htn);

	htn.add(new SysTreeNode(new SysItem(SysItem.FS_FSSTAT),
				SolViewResources.getString("HARD.FSSTAT")));

	if (!zconfig.pools().isEmpty()) {
	    SysTreeNode zn = new SysTreeNode(new SysItem(SysItem.ZFS_CONTAINER),
				SolViewResources.getString("HARD.ZFS"));
	    htn.add(zn);
	    for (Zpool zpool : zconfig.pools()) {
		addPool(zn, zpool);
	    }
	}
    }

    /*
     * Add a pool, then its datasets.
     */
    private void addPool(SysTreeNode stn, Zpool zp) {
	SysItem zpitem = new SysItem(SysItem.ZFS_POOL);
	zpitem.addAttribute("zpool", zp);
	SysTreeNode ptn = new SysTreeNode(zpitem, zp.getName());
	stn.add(ptn);
	for (Zfilesys zfs : zp.parents()) {
	    addZnode(ptn, zfs);
	}
    }

    /*
     * Add a zfs dataset, and then call recursively to add all that
     * dataset's children
     */
    private void addZnode(SysTreeNode stn, Zfilesys zfs) {
	SysItem zitem = new SysItem(SysItem.ZFS_FS);
	zitem.addAttribute("zfs", zfs);
	SysTreeNode ztn = new SysTreeNode(zitem, zfs.getName());
	stn.add(ztn);
	for (Zfilesys zfs2 : zfs.children()) {
	    addZnode(ztn, zfs2);
	}
    }

    /*
     * Add processes
     */
    private void addProcesses(DefaultMutableTreeNode root) {
	root.add(new SysTreeNode(new SysItem(SysItem.PROCESS_CONTAINER),
				SolViewResources.getString("HARD.PS")));
    }

    /*
     * Add zones
     */
    private void addZones(DefaultMutableTreeNode root) {
	root.add(new SysTreeNode(new SysItem(SysItem.ZONE_CONTAINER),
				SolViewResources.getString("HARD.ZONES")));
    }
}
