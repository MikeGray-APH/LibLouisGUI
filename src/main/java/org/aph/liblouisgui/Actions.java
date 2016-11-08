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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Actions
{
	private final Shell parentShell;
	private final Settings settings;
	private final TextTranslate textTranslate;
	private final Label tableListLabel;

	public Actions(Shell parentShell, Settings settings, TextTranslate textTranslate, Label tableListLabel)
	{
		this.parentShell = parentShell;
		this.settings = settings;
		this.textTranslate = textTranslate;
		this.tableListLabel = tableListLabel;

		tableListLabel.setText("Tables:  " + settings.tableList);

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
		new EditTablePathAction().addToMenu(menu, "Edit Table Path", 0, true);
		new EditTableListAction().addToMenu(menu, "Edit Tables", 0, true);

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
		new TranslateBrailleAction().addToMenuAndToolBar(menu, toolBar, "back-translate", 0, true);
		new ConvertToUnicode().addToMenuAndToolBar(menu, toolBar, "unicode", 0, true);
		new ConvertToAscii().addToMenuAndToolBar(menu, toolBar, "ascii", 0, true);
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

			try
			{
				LibLouis.loadLibrary(fileName);
			}
			catch(UnsatisfiedLinkError error)
			{
				Message.messageError("Invalid liblouis library:  " + settings.libraryFileName, error, true);
				return;
			}

			settings.libraryFileName = fileName;
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
			try
			{
				LibLouis.lou_setDataPath(directoryName);
			}
			catch(UnsatisfiedLinkError error)
			{
				Message.messageError("Invalid liblouis library:  " + settings.libraryFileName, error, true);
				return;
			}
		}
	}

	private final class EditTablePathAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new EditTablePathDialog();
		}
	}

	private final class EditTablePathDialog implements SelectionListener, KeyListener
	{
		private final Shell shell;
		private final Text text;
		private final Button okButton;
		private final Button cancelButton;

		private EditTablePathDialog()
		{
			shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			shell.setText("tables");
			shell.setLayout(new GridLayout(1, true));

			text = new Text(shell, SWT.SINGLE | SWT.LEFT);
			text.setText(settings.tablePath);
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

		private void setTablePath(String tablePath)
		{
			settings.tablePath = tablePath;
			try
			{
				LibLouis.lou_setDataPath(tablePath);
			}
			catch(UnsatisfiedLinkError error)
			{
				Message.messageError("Invalid liblouis library:  " + settings.libraryFileName, error, true);
				return;
			}
		}

		@Override
		public void widgetSelected(SelectionEvent event)
		{
			if(event.widget == okButton)
				setTablePath(text.getText());
			shell.dispose();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent ignored){}

		@Override
		public void keyPressed(KeyEvent event)
		{
			if(event.keyCode == '\r' || event.keyCode == '\n')
			{
				setTablePath(text.getText());
				shell.dispose();
			}
		}

		@Override
		public void keyReleased(KeyEvent ignored){}
	}

	private final class EditTableListAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			new EditTableListDialog();
		}
	}

	private final class EditTableListDialog implements SelectionListener, KeyListener
	{
		private final Shell shell;
		private final Text text;
		private final Button okButton;
		private final Button cancelButton;

		private EditTableListDialog()
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
			tableListLabel.setText("Tables:  " + settings.tableList);
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
				tableListLabel.setText("Tables:  " + settings.tableList);
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
			String inputLines[] = textTranslate.getTextLines();
			if(inputLines.length == 0)
				return;

			ArrayList<String> outputLines = new ArrayList<>();

			for(String inputLine : inputLines)
			{
				if(inputLine.length() == 0)
				{
					outputLines.add("");
					continue;
				}

				String outputLine = "";
				try
				{
					outputLine = LibLouis.translateString(settings.tableList, inputLine, inputLine.length() * 3, null, null, 0);
				}
				catch(UnsupportedEncodingException exception)
				{
					Message.messageError("UnsupportedEncodingException", exception, true);
					return;
				}
				catch(UnsatisfiedLinkError error)
				{
					Message.messageError("Invalid liblouis library:  " + settings.libraryFileName, error, true);
					return;
				}
				outputLines.add(outputLine);
			}

			textTranslate.setBrailleLines(outputLines.toArray(new String[outputLines.size()]));
		}
	}

	private final class TranslateBrailleAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			String inputLines[] = textTranslate.getBrailleLines();
			if(inputLines.length == 0)
				return;

			ArrayList<String> outputLines = new ArrayList<>();

			for(String inputLine : inputLines)
			{
				if(inputLine.length() == 0)
				{
					outputLines.add("");
					continue;
				}

				String outputLine = "";
				try
				{
					outputLine = LibLouis.backTranslateString(settings.tableList, inputLine, inputLine.length() * 3, null, null, 0);
				}
				catch(UnsupportedEncodingException exception)
				{
					Message.messageError("UnsupportedEncodingException", exception, true);
					return;
				}
				catch(UnsatisfiedLinkError error)
				{
					Message.messageError("Invalid liblouis library:  " + settings.libraryFileName, error, true);
					return;
				}
				outputLines.add(outputLine);
			}

			textTranslate.setTextLines(outputLines.toArray(new String[outputLines.size()]));
		}
	}

	private final String asciiString =   " A1B'K2L@CIF/MSP\"E3H9O6R^DJG>NTQ,*5<-U8V.%[$+X!&;:4\\0Z7(_?W]#Y)=";

	private final class ConvertToUnicode extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			char chars[] = textTranslate.getBraille().toUpperCase().toCharArray();

			for(int i = 0; i < chars.length; i++)
			if(chars[i] >= 0x20 && chars[i] <= 0x5f)
				chars[i] = ((char)(0x2800 + asciiString.indexOf(chars[i])));

			textTranslate.setBraille(new String(chars));
		}
	}

	private final class ConvertToAscii extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			char chars[] = textTranslate.getBraille().toUpperCase().toCharArray();

			for(int i = 0; i < chars.length; i++)
			if(chars[i] >= 0x2800 && chars[i] <= 0x283f)
				chars[i] = asciiString.charAt(chars[i] - 0x2800);

			textTranslate.setBraille(new String(chars));
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
