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

import javax.swing.table.*;

/**
 * A Table Model representing the output from a command.
 * @author Peter Tribble
 * @version 1.0
 */
public class CommandTableModel extends AbstractTableModel {

    private String[] columnNames;
    private String[][] data;
    private int nrows;

    /**
     * Create a CommandTableModel from the given input. It is assumed
     * that the input String consists of multiple lines, with the first
     * line containing the headers and subsequent lines containing data.
     * It is further assumed that all lines contain the same number of
     * data fields.
     *
     * @param input The command output that we're going to convert to tabular
     * form
     */
    public CommandTableModel(String input) {
	this(input, 0);
    }

    /**
     * Create a CommandTableModel from the given input. It is assumed
     * that the input String consists of multiple lines, with the first
     * line containing the headers and subsequent lines containing data.
     * It is further assumed that all lines contain the same number of
     * data fields.
     *
     * @param input The command output that we're going to convert to tabular
     * form
     * @param colmax The maximum number of columns. Columns beyond this
     * will be combined.
     */
    public CommandTableModel(String input, int colmax) {
	String[] rows = input.split("\n");
	columnNames = rows[0].split("\\s+", colmax);
	nrows = rows.length - 1;
	data = new String[nrows][columnNames.length];
	for (int i = 1; i < rows.length; i++) {
	    String[] items = rows[i].split("\\s+", colmax);
	    // and if not the right length?
	    if (items.length == columnNames.length) {
		data[i-1] = items;
	    }
	}
    }

    public int getColumnCount() {
	return columnNames.length;
    }

    public int getRowCount() {
	return nrows;
    }

    public Object getValueAt(int row, int col) {
	return data[row][col];
    }

    @Override
    public String getColumnName(int col) {
	return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int c) {
	return String.class;
    }
}
