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
import java.awt.*;
import java.awt.event.*;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * An informational panel, containing a list of items for which information
 * can be shown on the left and a panel showing the informational output on
 * the right.
 */
public class InfoPanel extends JPanel {

    private JingleTextPane tp;
    private InfoCommandList iclist;

    /**
     * Display an information panel.
     */
    public InfoPanel() {
	setLayout(new BorderLayout());
	iclist = new InfoCommandList();

	JList ilist = new InfoJList(iclist);
	ilist.addMouseListener(mouseListener);
	ilist.addKeyListener(keyListener);

	tp = new JingleTextPane();
	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(ilist), new JScrollPane(tp));
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(150);
	add(psplit);
    }

    private void setInfo(InfoCommand ic) {
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	tp.setText(iclist.infoTable(ic));
	setCursor(c);
    }

    MouseListener mouseListener = new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
	    JList source = (JList) e.getSource();
	    setInfo((InfoCommand) source.getSelectedValue());
	}
    };

    KeyListener keyListener = new KeyAdapter() {
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		JList source = (JList) e.getSource();
		setInfo((InfoCommand) source.getSelectedValue());
	    }
	}
    };
}
