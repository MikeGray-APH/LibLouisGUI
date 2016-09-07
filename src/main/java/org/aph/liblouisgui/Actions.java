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

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.UnsupportedEncodingException;

public class Actions
{
	private final Shell parentShell;
	private final TextTranslate textTranslate;

	public Actions(Shell parentShell, TextTranslate textTranslate)
	{
		this.parentShell = parentShell;
		this.textTranslate = textTranslate;

		Menu menuBar = new Menu(parentShell, SWT.BAR);

		ToolBar toolBar = new ToolBar(parentShell, SWT.HORIZONTAL | SWT.FLAT);

		toolBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		parentShell.setMenuBar(menuBar);

		Menu menu;
		MenuItem item;

		String mod1KeyName = "Ctrl+";
		String mod2KeyName = "Shift+";
		if(System.getProperty("os.name").toLowerCase().startsWith("mac"))
		{
			mod1KeyName = "⌘";
			mod2KeyName = "⇧";
		}

		//   LibLouis menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&LibLouis");
		item.setMenu(menu);

		new SetLibLouisPathDialogAction().addToMenuAndToolBar(menu, toolBar, "Set LibLouis Path", 0, true);
		new SetDirectoryPathDialogAction().addToMenuAndToolBar(menu, toolBar, "Set Data Path", 0, true);

		//   translate menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Translate");
		item.setMenu(menu);

		new TranslateAction().addToMenuAndToolBar(menu, toolBar, "translate", 0, true);
	}

	private final class SetLibLouisPathDialogAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
			fileDialog.setFileName(LibLouis.getLibraryPath());
			String fileName = fileDialog.open();
			if(fileName == null)
				return;

			LibLouis.loadLibrary(fileName);
		}
	}

	private final class SetDirectoryPathDialogAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			DirectoryDialog directoryDialog = new DirectoryDialog(parentShell, SWT.OPEN);
			String directoryName = directoryDialog.open();
			if(directoryName == null)
				return;

			LibLouis.setDataPath(directoryName);
		}
	}

	private final class TranslateAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			String inputString = textTranslate.getText();

			byte inputBytes[];
			try
			{
				inputBytes = inputString.getBytes("UTF-16LE");
			}
			catch(UnsupportedEncodingException exception)
			{
				Message.messageError("Input Error", exception, true);
				return;
			}

			Memory inbuf = new Memory(inputBytes.length);
			inbuf.write(0, inputBytes, 0, inputBytes.length);
			IntByReference inlen = new IntByReference(inputBytes.length);

			Memory outbuf = new Memory(inputBytes.length * 2);
			IntByReference outlen = new IntByReference(inputBytes.length * 2);

			int result = LibLouis.lou_translateString("en-ueb-g2.ctb", inbuf, inlen, outbuf, outlen, null, null, 0);

			int length = outlen.getValue();
			byte outputBytes[] = outbuf.getByteArray(0, length * 2);
			String outputString;
			try
			{
				outputString = new String(outputBytes, "UTF-16LE");
			}
			catch(UnsupportedEncodingException exception)
			{
				Message.messageError("Output Error", exception, true);
				return;
			}

			MessageBox messageBox = new MessageBox(parentShell, SWT.ICON_INFORMATION | SWT.OK);
			messageBox.setMessage(outputString);
			messageBox.open();

		}
	}

	private static class BaseAction implements SelectionListener
	{
		protected MenuItem menuItem;
		protected ToolItem toolItem;
		protected boolean enabled;

		void addToMenuAndToolBar(Menu menu, ToolBar toolBar, String tag, int accelerator, boolean enabled)
		{
			this.enabled = enabled;

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(tag);
			if(accelerator != 0)
				menuItem.setAccelerator(accelerator);
			menuItem.addSelectionListener(this);
			menuItem.setEnabled(enabled);

			toolItem = new ToolItem(toolBar, SWT.PUSH);
			//TODO:  add icon instead
			toolItem.setText(tag);
			toolItem.addSelectionListener(this);
			toolItem.setEnabled(enabled);
		}

		void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
			menuItem.setEnabled(enabled);
			toolItem.setEnabled(enabled);
		}

		boolean isEnabled()
		{
			return enabled;
		}

		@Override
		public void widgetSelected(SelectionEvent ignored){}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}
	}
}
