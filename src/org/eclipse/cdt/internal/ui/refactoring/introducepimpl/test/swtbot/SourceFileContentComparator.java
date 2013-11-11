package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.swtbot;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class SourceFileContentComparator {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
	}

	@Test
	public void canCreateANewJavaProject() throws Exception {
		bot.menu("Refactor").menu("Introduce PImpl...").click();

		// SWTBotShell shell = bot.shell("New Project");
		// shell.activate();
		// bot.tree().expandNode("Java").select("Java Project");
		// bot.button("Next >").click();
		// bot.textWithLabel("Project name:").setText("MyFirstProject");
		// bot.button("Finish").click();

		// bot.button("Login").click();
		// SWTBotMenu fileMenu = bot.menu("File");
		// Assert.assertNotNull(fileMenu);
		// SWTBotMenu exitMenu = fileMenu.menu("Another Exit");
		// Assert.assertNotNull(exitMenu);
		// exitMenu.click();
	}

	@AfterClass
	public static void sleep() {
		bot.sleep(2000);
	}
}
