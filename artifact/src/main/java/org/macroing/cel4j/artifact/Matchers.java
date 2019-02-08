/*
 * The MIT License (MIT)
 * 
 * Copyright 2018 - 2019 J&#246;rgen Lundgren
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.macroing.cel4j.artifact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Matchers {
	public static final String NAME_IMPORT_STATEMENT;
	public static final String NAME_PACKAGE_STATEMENT;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final Pattern PATTERN_IMPORT_STATEMENT;
	private static final Pattern PATTERN_PACKAGE_STATEMENT;
	private static final Pattern PATTERN_SUBSTITUTION_VARIABLE;
	private static final Pattern PATTERN_WHITE_SPACE;
	private static final String REGEX_IDENTIFIER;
	private static final String REGEX_IMPORT_STATEMENT;
	private static final String REGEX_PACKAGE_STATEMENT;
	private static final String REGEX_SUBSTITUTION_VARIABLE;
	private static final String REGEX_WHITE_SPACE;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Matchers() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	static {
		NAME_IMPORT_STATEMENT = "ImportStatement";
		NAME_PACKAGE_STATEMENT = "PackageStatement";
		
		REGEX_IDENTIFIER = "(?!(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|false|finally|final|float|for|if|goto|implements|import|instanceof|interface|int|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throws|throw|transient|true|try|void|volatile|while)([^\\p{javaJavaIdentifierPart}]|$))\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
		REGEX_IMPORT_STATEMENT = String.format("(?<%s>import(\\s+static)?\\s+%s(\\s*\\.\\s*%s)*?(\\s*\\.\\s*\\*)?\\s*;)", NAME_IMPORT_STATEMENT, REGEX_IDENTIFIER, REGEX_IDENTIFIER);
		REGEX_PACKAGE_STATEMENT = String.format("package\\s+(?<%s>%s(\\s*\\.\\s*%s)*?)\\s*;", NAME_PACKAGE_STATEMENT, REGEX_IDENTIFIER, REGEX_IDENTIFIER);
		REGEX_SUBSTITUTION_VARIABLE = String.format("\\$(%s)", REGEX_IDENTIFIER);
		REGEX_WHITE_SPACE = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";
		
		PATTERN_IMPORT_STATEMENT = Pattern.compile(REGEX_IMPORT_STATEMENT);
		PATTERN_PACKAGE_STATEMENT = Pattern.compile(REGEX_PACKAGE_STATEMENT);
		PATTERN_SUBSTITUTION_VARIABLE = Pattern.compile(REGEX_SUBSTITUTION_VARIABLE);
		PATTERN_WHITE_SPACE = Pattern.compile(REGEX_WHITE_SPACE);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static Matcher newImportStatementMatcher(final CharSequence input) {
		return PATTERN_IMPORT_STATEMENT.matcher(input);
	}
	
	public static Matcher newPackageStatementMatcher(final CharSequence input) {
		return PATTERN_PACKAGE_STATEMENT.matcher(input);
	}
	
	public static Matcher newSubstitutionVariableMatcher(final CharSequence input) {
		return PATTERN_SUBSTITUTION_VARIABLE.matcher(input);
	}
	
	public static Matcher newWhiteSpaceMatcher(final CharSequence input) {
		return PATTERN_WHITE_SPACE.matcher(input);
	}
}