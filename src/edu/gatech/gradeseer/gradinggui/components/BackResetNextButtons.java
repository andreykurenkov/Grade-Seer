package edu.gatech.gradeseer.gradinggui.components;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class BackResetNextButtons extends JPanel {
	private final JButton back;
	private final JButton reset;
	private final JButton next;

	public BackResetNextButtons(String back, boolean reset, boolean vertical, String next) {
		this.setLayout(new GridLayout(vertical ? 3 : 1, vertical ? 1 : 3));
		this.back = new JButton(new ImageIcon(back));
		this.add(this.back);
		if (reset) {
			this.reset = new JButton("Reset");
			this.add(this.reset);
		} else {
			this.reset = new JButton("");
		}
		this.next = new JButton(new ImageIcon(next));
		this.add(this.next);

	}

	public BackResetNextButtons(String back, boolean vertical, String next) {
		this(back, false, vertical, next);
	}

	public BackResetNextButtons(boolean hasReset, boolean vertical) {
		this("Res/" + (vertical ? "UpArrow.png" : "LeftArrow.png"), hasReset, vertical, "Res/"
				+ (vertical ? "DownArrow.png" : "RightArrow.png"));
	}

	public BackResetNextButtons() {
		this(true, false);
	}

	public void addBackActionListener(ActionListener add) {
		back.addActionListener(add);
	}

	public void addNextActionListener(ActionListener add) {
		next.addActionListener(add);
	}

	public void addResetActionListener(ActionListener add) {
		if (reset != null)
			reset.addActionListener(add);
	}

	public void setResetText(String setTo) {
		this.reset.setText(setTo);
	}

	public void setBorders(Border setTo) {
		back.setBorder(setTo);
		if (reset != null)
			reset.setBorder(setTo);
		next.setBorder(setTo);
	}
}
