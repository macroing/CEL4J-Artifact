/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
 * 
 * This file is part of org.macroing.cel4j.artifact.
 * 
 * org.macroing.cel4j.artifact is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.macroing.cel4j.artifact is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.macroing.cel4j.artifact. If not, see <http://www.gnu.org/licenses/>.
 */
package org.macroing.cel4j.artifact;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.PrintStream;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

final class GUI implements Runnable {
	private final JFrame jFrame = new JFrame();
	private final JMenu jMenu1 = new JMenu();
	private final JMenuBar jMenuBar = new JMenuBar();
	private final JMenuItem jMenuItem1 = new JMenuItem();
	private final JScrollPane jScrollPane1 = new JScrollPane();
	private final JScrollPane jScrollPane2 = new JScrollPane();
	private final JSplitPane jSplitPane = new JSplitPane();
	private final JTextArea jTextArea = new JTextArea();
	private final JTextAreaOutputStreamDecorator jTextAreaOutputStreamDecoratorErr = new JTextAreaOutputStreamDecorator(this.jTextArea, System.err);
	private final JTextAreaOutputStreamDecorator jTextAreaOutputStreamDecoratorOut = new JTextAreaOutputStreamDecorator(this.jTextArea, System.out);
	private final JTextPane jTextPane = new JJavaTextPane();
	private final JToolBar jToolBar = new JToolBar();
	private final String extension;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private GUI(final String extension) {
		this.extension = extension;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void run() {
		doConfigure();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void start(final String extension) {
		doStart(Objects.requireNonNull(extension, "extension == null"));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doConfigure() {
		doConfigureJFrame();
		doConfigureJMenu1();
		doConfigureJMenuBar();
		doConfigureJMenuItem1();
		doConfigureJScrollPane1();
		doConfigureJScrollPane2();
		doConfigureJSplitPane();
		doConfigureJTextArea();
		doConfigureJTextPane();
		doConfigureJToolBar();
		doConfigureSystemErr();
		doConfigureSystemOut();
		doConfigureThread();
	}
	
	private void doConfigureJFrame() {
		this.jFrame.getContentPane().setLayout(new BorderLayout());
		this.jFrame.getContentPane().add(this.jToolBar, BorderLayout.NORTH);
		this.jFrame.getContentPane().add(this.jSplitPane, BorderLayout.CENTER);
		this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.jFrame.setJMenuBar(this.jMenuBar);
		this.jFrame.setSize(800, 600);
		this.jFrame.setTitle("Artifact - v. 0.1-beta");
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setVisible(true);
	}
	
	private void doConfigureJMenu1() {
		this.jMenu1.add(this.jMenuItem1);
		this.jMenu1.setText("File");
	}
	
	private void doConfigureJMenuBar() {
		this.jMenuBar.add(this.jMenu1);
	}
	
	private void doConfigureJMenuItem1() {
		this.jMenuItem1.addActionListener(ArtifactUtilities.newExitActionListener(this.jFrame));
		this.jMenuItem1.setText("Exit");
	}
	
	private void doConfigureJScrollPane1() {
		this.jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.jScrollPane1.setViewportView(this.jTextPane);
	}
	
	private void doConfigureJScrollPane2() {
		this.jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.jScrollPane2.setViewportView(this.jTextArea);
	}
	
	private void doConfigureJSplitPane() {
		this.jSplitPane.setDividerLocation(300);
		this.jSplitPane.setLeftComponent(this.jScrollPane1);
		this.jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.jSplitPane.setRightComponent(this.jScrollPane2);
	}
	
	private void doConfigureJTextArea() {
		this.jTextArea.setBackground(Color.BLACK);
		this.jTextArea.setEditable(false);
		this.jTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
		this.jTextArea.setForeground(Color.WHITE);
		this.jTextArea.setSelectedTextColor(Color.BLACK);
		this.jTextArea.setSelectionColor(Color.WHITE);
	}
	
	private void doConfigureJTextPane() {
		this.jTextPane.getActionMap().put("Evaluate", new EvaluatingAction(this.jTextPane, this.extension));
		this.jTextPane.getInputMap().put(KeyStroke.getKeyStroke("F5"), "Evaluate");
	}
	
	private void doConfigureJToolBar() {
		this.jToolBar.add(new EvaluatingAction(this.jTextPane, this.extension)).setIcon(ArtifactUtilities.createIcon("Evaluate.png", "Evaluate"));
		this.jToolBar.setFloatable(false);
	}
	
	private void doConfigureSystemErr() {
		System.setErr(new PrintStream(this.jTextAreaOutputStreamDecoratorErr));
	}
	
	private void doConfigureSystemOut() {
		System.setOut(new PrintStream(this.jTextAreaOutputStreamDecoratorOut));
	}
	
	private void doConfigureThread() {
		Thread.setDefaultUncaughtExceptionHandler(ArtifactUtilities.newUncaughtExceptionHandler(this.jTextArea));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void doStart(final String extension) {
		SwingUtilities.invokeLater(new GUI(extension));
	}
}