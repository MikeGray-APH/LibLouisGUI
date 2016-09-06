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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class Actions
{
	private final Shell parentShell;

	public Actions(Shell parentShell)
	{
		this.parentShell = parentShell;

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

		//   translate menu
		menu = new Menu(menuBar);
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Translate");
		item.setMenu(menu);

		new TranslateAction().addToMenuAndToolBar(menu, toolBar, "translate", 0, true);
	}

	private static class TranslateAction extends BaseAction
	{
		@Override
		public void widgetSelected(SelectionEvent ignored)
		{
			System.out.println("pressed");
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
