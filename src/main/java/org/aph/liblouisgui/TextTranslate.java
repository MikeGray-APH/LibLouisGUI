/* Copyright (C) 2016 American Printing House for the Blind Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aph.liblouisgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;

public class TextTranslate
{
	private final Shell parentShell;
	private final StyledText textText, textBraille;
	private StyledText textCurrent;

	public TextTranslate(Shell parentShell)
	{
		this.parentShell = parentShell;

		Composite composite = new Composite(parentShell, 0);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		textText = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		textText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		textText.addFocusListener(new FocusHandler(textText));

		textBraille = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		textBraille.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		textBraille.addFocusListener(new FocusHandler(textBraille));
	}

	public void cut()
	{
		textCurrent.cut();
	}

	public void copy()
	{
		textCurrent.copy();
	}

	public void paste()
	{
		textCurrent.paste();
	}

	public String getText()
	{
		return textText.getText();
	}

	public void setText(String string)
	{
		textText.setText(string);
	}

	public String[] getTextLines()
	{
		ArrayList<String> arrayList = new ArrayList<>();
		for(int i = 0; i < textText.getLineCount(); i++)
			arrayList.add(textText.getLine(i));

		return arrayList.toArray(new String[arrayList.size()]);
	}

	public void setTextLines(String[] lines)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if(lines.length > 0)
		{
			stringBuilder.append(lines[0]);
			for(int i = 1; i < lines.length; i++)
				stringBuilder.append("\n" + lines[i]);
		}

		textText.setText(stringBuilder.toString());
	}

	public String getBraille()
	{
		return textBraille.getText();
	}

	public void setBraille(String string)
	{
		textBraille.setText(string);
	}

	public String[] getBrailleLines()
	{
		ArrayList<String> arrayList = new ArrayList<>();
		for(int i = 0; i < textBraille.getLineCount(); i++)
			arrayList.add(textBraille.getLine(i));

		return arrayList.toArray(new String[arrayList.size()]);
	}

	public void setBrailleLines(String[] lines)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if(lines.length > 0)
		{
			stringBuilder.append(lines[0]);
			for(int i = 1; i < lines.length; i++)
				stringBuilder.append("\n" + lines[i]);
		}

		textBraille.setText(stringBuilder.toString());
	}

	private final class FocusHandler implements FocusListener
	{
		private final StyledText source;

		private FocusHandler(StyledText source)
		{
			this.source = source;
		}

		@Override
		public void focusGained(FocusEvent ignored)
		{
			textCurrent = source;
		}

		@Override
		public void focusLost(FocusEvent ignored){}
	}
}
