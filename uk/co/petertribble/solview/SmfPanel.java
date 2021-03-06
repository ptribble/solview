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

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * An smf informational panel, containing a list of smf services for which
 * information can be shown on the left and a panel showing the information
 * about those services on the right.
 */
public class SmfPanel extends JPanel {

    private SmfInfoPanel sip;

    /**
     * Display an smf information panel.
     */
    public SmfPanel() {
	setLayout(new BorderLayout());

	SmfUtils smfutil = new SmfUtils();
	SmfList plist = new SmfList(smfutil);
	plist.addMouseListener(mouseListener);
	plist.addKeyListener(keyListener);

	final SmfTree ptree = new SmfTree(smfutil);
	ptree.addTreeSelectionListener(new TreeSelectionListener() {
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    ptree.getLastSelectedPathComponent();

		if ((node != null) && node.isLeaf()) {
		    setInfo((SmfService) node.getUserObject());
		}
	    }
	});

	JTabbedPane jtp = new JTabbedPane();

	jtp.add(SolViewResources.getString("SMF.LIST"), new JScrollPane(plist));
	jtp.add(SolViewResources.getString("SMF.TREE"), new JScrollPane(ptree));

	sip = new SmfInfoPanel(smfutil);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		jtp, sip);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(180);
	add(psplit);
    }

    private void setInfo(SmfService svc) {
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	sip.setInfo(svc);
	setCursor(c);
    }

    MouseListener mouseListener = new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
	    JList source = (JList) e.getSource();
	    setInfo((SmfService) source.getSelectedValue());
	}
    };

    KeyListener keyListener = new KeyAdapter() {
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		JList source = (JList) e.getSource();
		setInfo((SmfService) source.getSelectedValue());
	    }
	}
    };
}
