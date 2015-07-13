package edu.gatech.gradeseer.gui.helpers;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;

public class ConstraintsHelper {
	public static final Insets defInsets = new Insets(0, 0, 0, 0);
	public static final double defWeight = 1;
	public static final int defPad = 0;

	private ConstraintsHelper() {
	};

	public static GridBagConstraints makeConstraints(int x, int y, int fill, double weightx, double weighty, int width,
			int height, int anchor) {
		GridBagConstraints c = ConstraintsHelper.makeConstraints(x, y, fill, weightx, weighty, width, height);
		c.anchor = anchor;
		return c;
	}

	public static GridBagConstraints makeConstraints(int x, int y, int fill, double weightx, double weighty, int width,
			int height) {
		GridBagConstraints c = ConstraintsHelper.makeConstraints(x, y, fill, weightx, weighty);
		c.gridwidth = width;
		c.gridheight = height;
		return c;
	}

	public static GridBagConstraints makeConstraints(int x, int y, int fill, double weightx, double weighty) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.fill = fill;
		c.weightx = weightx;
		c.weighty = weighty;
		return c;
	}

	public static void addGroup(JPanel addTo, Component[] add, int[][] intVars, double[][] doubleVars) {
		for (int i = 0; i < add.length; i++) {
			addTo.add(add[i],
					makeConstraints(intVars[i][0], intVars[i][1], intVars[i][2], doubleVars[i][0], doubleVars[i][1]));
		}
	}
}
