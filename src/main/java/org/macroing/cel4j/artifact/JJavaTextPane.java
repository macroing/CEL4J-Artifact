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

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

final class JJavaTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;
	private static final Pattern PATTERN_1 = Pattern.compile("\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|false|finally|final|float|for|if|goto|implements|import|instanceof|interface|int|long|new|null|package|private|protected|public|return|short|static|strictf|super|switch|synchronized|this|throws|throw|transient|true|try|void|volatile|while)\\b");
	private static final Pattern PATTERN_2 = Pattern.compile("\"(\\\\[\\\\\"'()bfnrt]|[^\\\\\"])*\"");
	private static final Pattern PATTERN_3 = Pattern.compile("(//|#).*$", Pattern.MULTILINE);
	private static final StyleContext STYLE_CONTEXT = StyleContext.getDefaultStyleContext();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AttributeSet attributeSet1 = doCreateAttributeSet1();
	private final AttributeSet attributeSet2 = doCreateAttributeSet2();
	private final AttributeSet attributeSet3 = doCreateAttributeSet3();
	private final AttributeSet attributeSet4 = doCreateAttributeSet4();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public JJavaTextPane() {
		addCaretListener(new CaretListenerImpl());
		setMargin(new Insets(10,10,10,10));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static AttributeSet doCreateAttributeSet1() {
		final AttributeSet attributeSet0 = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
		final AttributeSet attributeSet1 = STYLE_CONTEXT.addAttribute(attributeSet0, StyleConstants.FontFamily, Font.MONOSPACED);
		final AttributeSet attributeSet2 = STYLE_CONTEXT.addAttribute(attributeSet1, StyleConstants.FontSize, Integer.valueOf(16));
		
		return attributeSet2;
	}
	
	private static AttributeSet doCreateAttributeSet2() {
		final AttributeSet attributeSet0 = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, new Color(127, 0, 85));
		final AttributeSet attributeSet1 = STYLE_CONTEXT.addAttribute(attributeSet0, StyleConstants.FontFamily, Font.MONOSPACED);
		final AttributeSet attributeSet2 = STYLE_CONTEXT.addAttribute(attributeSet1, StyleConstants.FontSize, Integer.valueOf(16));
		final AttributeSet attributeSet3 = STYLE_CONTEXT.addAttribute(attributeSet2, StyleConstants.Bold, Boolean.TRUE);
		
		return attributeSet3;
	}
	
	private static AttributeSet doCreateAttributeSet3() {
		final AttributeSet attributeSet0 = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
		final AttributeSet attributeSet1 = STYLE_CONTEXT.addAttribute(attributeSet0, StyleConstants.FontFamily, Font.MONOSPACED);
		final AttributeSet attributeSet2 = STYLE_CONTEXT.addAttribute(attributeSet1, StyleConstants.FontSize, Integer.valueOf(16));
		
		return attributeSet2;
	}
	
	private static AttributeSet doCreateAttributeSet4() {
		final AttributeSet attributeSet0 = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, new Color(63, 127, 95));
		final AttributeSet attributeSet1 = STYLE_CONTEXT.addAttribute(attributeSet0, StyleConstants.FontFamily, Font.MONOSPACED);
		final AttributeSet attributeSet2 = STYLE_CONTEXT.addAttribute(attributeSet1, StyleConstants.FontSize, Integer.valueOf(16));
		
		return attributeSet2;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final class CaretListenerImpl implements CaretListener {
		public CaretListenerImpl() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		public void caretUpdate(final CaretEvent e) {
			final
			SwingWorker<Void, Void> swingWorker = new SwingWorkerImpl();
			swingWorker.execute();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final class SwingWorkerImpl extends SwingWorker<Void, Void> {
		public SwingWorkerImpl() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		protected Void doInBackground() {
			try {
				doMatch(JJavaTextPane.this.getDocument().getText(0, JJavaTextPane.this.getDocument().getLength()));
			} catch(final BadLocationException e) {
//				Do nothing.
			}
			
			return null;
		}
		
		@Override
		protected void done() {
//			Do nothing.
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private void doMatch(final String string){
			doResetStyledDocument();
			doMatchPattern1(string);
			doMatchPattern2(string);
			doMatchPattern3(string);
		}
		
		@SuppressWarnings("synthetic-access")
		private void doMatchPattern1(final String string) {
			final Matcher matcher = PATTERN_1.matcher(string);
			
			while(matcher.find()) {
				JJavaTextPane.this.getStyledDocument().setCharacterAttributes(matcher.start(1), matcher.end(1) - matcher.start(1), JJavaTextPane.this.attributeSet2, true);
			}
		}
		
		@SuppressWarnings("synthetic-access")
		private void doMatchPattern2(final String string) {
			final Matcher matcher = PATTERN_2.matcher(string);
			
			while(matcher.find()) {
				JJavaTextPane.this.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), JJavaTextPane.this.attributeSet3, true);
			}
		}
		
		@SuppressWarnings("synthetic-access")
		private void doMatchPattern3(final String string) {
			final Matcher matcher = PATTERN_3.matcher(string);
			
			while(matcher.find()) {
				JJavaTextPane.this.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), JJavaTextPane.this.attributeSet4, true);
			}
		}
		
		@SuppressWarnings("synthetic-access")
		private void doResetStyledDocument() {
			JJavaTextPane.this.getStyledDocument().setCharacterAttributes(0, JJavaTextPane.this.getDocument().getLength(), JJavaTextPane.this.attributeSet1, true);
		}
	}
}