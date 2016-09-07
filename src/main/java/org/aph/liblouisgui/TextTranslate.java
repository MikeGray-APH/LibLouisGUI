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

	public String getTextText()
	{
		return textText.getText();
	}

	public void setTextText(String string)
	{
		textText.setText(string);
	}

	public String getTextBraille()
	{
		return textBraille.getText();
	}

	public void setTextBraille(String string)
	{
		textBraille.setText(string);
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
