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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.UnsupportedEncodingException;

public class Actions
{
	private final Shell parentShell;
	private final Settings settings;
	private final TextTranslate textTranslate;

	public Actions(Shell parentShell, Settings settings, TextTranslate textTranslate)
	{
		this.parentShell = parentShell;
		this.settings = settings;
		this.textTranslate = textTranslate;

		Menu menuBar = new Menu(parentShell, SWT.BAR);
		parentShell.setMenuBar(menuBar);

		ToolBar toolBar = new ToolBar(parentShell, SWT.HORIZONTAL | SWT.FLAT);
		toolBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		toolBar.moveAbove(null);

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

		new SetLibLouisPathDialogAction().addToMenu(menu, "Set LibLouis Path", 0, true);
		new SetTablePathDialogAction().addToMenu(menu, "Set Table Path", 0, true);
		new SetTableListAction().addToMenu(menu, "Set tables", 0, true);

		//   edit menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Edit");
		item.setMenu(menu);

		//   cut, copy, and paste accelerators are handled by StyledText.
		new CutAction().addToMenu(menu, "Cut\t" + mod1KeyName + "X", 0, true);
		new CopyAction().addToMenu(menu, "Copy\t" + mod1KeyName + "C", 0, true);
		new PasteAction().addToMenu(menu, "Paste\t" + mod1KeyName + "V", 0, true);

		//   translate menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Translate");
		item.setMenu(menu);

		new TranslateTextAction().addToMenuAndToolBar(menu, toolBar, "translate", 0, true);
		new TranslateBrailleAction().addToMenuAndToolBar(menu, toolBar, "back translate", 0, true);
	}

	private final class SetLibLouisPathDialogAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			FileDialog fileDialog = new FileDialog(parentShell, SWT.OPEN);
			fileDialog.setFileName(settings.libraryFileName);
			String fileName = fileDialog.open();
			if(fileName == null)
				return;

			settings.libraryFileName = fileName;
			LibLouis.loadLibrary(fileName);
		}
	}

	private final class SetTablePathDialogAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			DirectoryDialog directoryDialog = new DirectoryDialog(parentShell, SWT.OPEN);
			String directoryName = directoryDialog.open();
			if(directoryName == null)
				return;

			settings.tablePath = directoryName;
			LibLouis.lou_setDataPath(directoryName);
		}
	}

	private final class SetTableListAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new SetTableListDialog();
		}
	}

	private final class SetTableListDialog implements SelectionListener, KeyListener
	{
		private final Shell shell;
		private final Text text;
		private final Button okButton;
		private final Button cancelButton;

		private SetTableListDialog()
		{
			shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			shell.setText("tables");
			shell.setLayout(new GridLayout(1, true));

			text = new Text(shell, SWT.SINGLE | SWT.LEFT);
			text.setText(settings.tableList);
			text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
			text.addKeyListener(this);

			Composite composite = new Composite(shell, 0);
			composite.setLayout(new GridLayout(2, true));
			composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

			okButton = new Button(composite, SWT.PUSH);
			okButton.setText("OK");
			okButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			okButton.addSelectionListener(this);

			cancelButton = new Button(composite, SWT.PUSH);
			cancelButton.setText("Cancel");
			cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			cancelButton.addSelectionListener(this);

			shell.pack();
			shell.open();
		}

		@Override
		public void widgetSelected(SelectionEvent event)
		{
			if(event.widget == okButton)
				settings.tableList = text.getText();
			shell.dispose();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}

		@Override
		public void keyPressed(KeyEvent event)
		{
			if(event.keyCode == '\r' || event.keyCode == '\n')
			{
				settings.tableList = text.getText();
				shell.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent ignored){}
	}

	private class CutAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.cut();
		}
	}

	private class CopyAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.copy();
		}
	}

	private class PasteAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			textTranslate.paste();
		}
	}

	private final class TranslateTextAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			String inputString = textTranslate.getTextText();
			if(inputString.length() == 0)
				return;

			String outputString = null;
			try
			{
				outputString = LibLouis.translateString(settings.tableList, inputString, inputString.length() * 3, null, null, 0);
			}
			catch(UnsupportedEncodingException exception)
			{
				Message.messageError("UnsupportedEncodingException", exception, true);
			}
			catch(UnsatisfiedLinkError error)
			{
				Message.messageError("UnsatisfiedLinkError", error, true);
			}

			textTranslate.setTextBraille(outputString);
		}
	}

	private final class TranslateBrailleAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			String inputString = textTranslate.getTextBraille();
			if(inputString.length() == 0)
				return;

			String outputString = null;
			try
			{
				outputString = LibLouis.backTranslateString(settings.tableList, inputString, inputString.length() * 3, null, null, 0);
			}
			catch(UnsupportedEncodingException exception)
			{
				Message.messageError("UnsupportedEncodingException", exception, true);
			}
			catch(UnsatisfiedLinkError error)
			{
				Message.messageError("UnsatisfiedLinkError", error, true);
			}

			textTranslate.setTextText(outputString);
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

		void addToMenu(Menu menu, String tag, int accelerator, boolean enabled)
		{
			this.enabled = enabled;

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(tag);
			if(accelerator != 0)
				menuItem.setAccelerator(accelerator);
			menuItem.addSelectionListener(this);
			menuItem.setEnabled(enabled);
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
