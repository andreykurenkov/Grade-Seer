package edu.gatech.gradeseer.gui.components;

//PDFViewer.java

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

public class PDFViewer extends JPanel {
	private int pagenum;
	private JLabel label;
	private File pdfFile;
	private PdfDecoder pdfDecoder;

	public PDFViewer(File file) throws PdfException {
		super(new BorderLayout());
		changeToFile(file);
		final BackResetNextButtons backNext = new BackResetNextButtons(false, false);
		backNext.getBackButton().setEnabled(false);
		if (pdfDecoder.getPageCount() == 1)
			backNext.getNextButton().setEnabled(false);
		backNext.addBackActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setPage(pagenum - 1);
				backNext.getNextButton().setEnabled(true);
				if (pagenum == 1)
					backNext.getBackButton().setEnabled(false);
			}
		});
		backNext.addNextActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setPage(pagenum + 1);
				backNext.getBackButton().setEnabled(true);
				if (pagenum == pdfDecoder.getPageCount())
					backNext.getNextButton().setEnabled(false);
			}
		});
		this.add(new JScrollPane(label), BorderLayout.CENTER);
		this.add(backNext, BorderLayout.SOUTH);
	}

	public void changeToFile(File file) throws PdfException {
		if (file != null) {
			pdfFile = file;
			pdfDecoder = new PdfDecoder(true);
			pdfDecoder.openPdfFile(file.getAbsolutePath());
			label = new JLabel();
			setPage(1);
		}
	}

	private void setPage(int setTo) {
		pagenum = setTo;
		try {
			pdfDecoder.decodePage(pagenum);
			label.setIcon(new ImageIcon(pdfDecoder.getPageAsImage(pagenum)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
