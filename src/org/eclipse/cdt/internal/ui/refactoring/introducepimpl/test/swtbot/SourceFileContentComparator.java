package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.swtbot;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *	SWTBot with Cygwin GCC 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class SourceFileContentComparator {

	private static final String PROJECTNAME = "TestPImplRefactoring";
	private static final String CYGWIN_GCC = "Cygwin GCC";
	private static final String HEADERFILE = "Test.h";
	private static final String SOURCEFILE = "Test.cpp";
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
	}
	
	private void createCPPProject() {
		bot.menu("File").menu("Project...").click();
		SWTBotShell shell = bot.shell("New Project");
		shell.activate();
		bot.tree().expandNode("C/C++").select("C++ Project");
		bot.button("Next >").click();
		bot.textWithLabel("Project name:").setText(PROJECTNAME);
		bot.tableWithLabel("Toolchains:").select(CYGWIN_GCC);
		bot.button("Finish").click();
	}
	
	private void createCPPFiles() throws CoreException {
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECTNAME);
		assertTrue(CYGWIN_GCC + " needed", project.exists());
		IFile headerFile = project.getFile(HEADERFILE);
		assertTrue(headerFile.exists());
		headerFile.create(getHeaderContent(), true, null);
		IFile sourceFile = project.getFile(SOURCEFILE);
		assertTrue(sourceFile.exists());
		sourceFile.create(getSourceContent(), true, null);
	}
	
	private InputStream getHeaderContent() {
		String content =
				"#ifndef TESTPIMPLREFACTORING_H_\n" +
				"#define TESTPIMPLREFACTORING_H_\n\n" +
				"class Example {" +
				"public:" +
				"\tvoid foo();" +
				"private:" +
				"\tint getNumber();" +
				"};\n" +
				"#endif /* TESTPIMPLREFACTORING_H_ */";
		return new ByteArrayInputStream(content.getBytes());
	}
	
	private InputStream getSourceContent() {
		String content =
				"#include \"Test.h\"\n\n" + 
				"void Example::foo() {\n" +
				"\tint i = getNumber();\n" +
				"}\n\n" +
				"int Example::getNumber() {\n" +
				"\treturn 3;\n" +
				"}\n\n" +
				"int main() {\n" +
				"\tExample ex;\n" +
				"\tex.foo();\n" +
				"}";
		return new ByteArrayInputStream(content.getBytes());
	}
	
	private String getPimplHeaderContent() {
		String content =
				"#ifndef TESTPIMPLREFACTORING_H_\n" +
				"#define TESTPIMPLREFACTORING_H_\n\n" +
				"class Example {" +
				"public:" +
				"\tvoid foo();" +
				"private:" +
				"\tstruct ExampleImpl* _impl;" +
				"};\n" +
				"#endif /* TESTPIMPLREFACTORING_H_ */";
		return null;
	}
	
	private String getPimplSourceContent() {
		String content =
				"#include \"Test.h\"\n\n" + 
				"void Example::foo() {\n" +
				"\tint i = getNumber();\n" +
				"}\n\n" +
				"int Example::getNumber() {\n" +
				"\treturn 3;\n" +
				"}\n\n" +
				"int main() {\n" +
				"\tExample ex;\n" +
				"\tex.foo();\n" +
				"}";
		return null;
	}
	
	@Test
	public void testPImplRefactoring() throws Exception {
		createCPPProject();
		createCPPFiles();
		
		bot.menu("Refactor").menu("Introduce PImpl...").click();
		bot.radioWithLabel("struct");
		bot.radioWithLabel("standard ptr");
		bot.radioWithLabel("deep copy");
		bot.button("Finish").click();
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return false;
			}
			@Override
			public String getFailureMessage() {
				return null;
			}
		});
		
		String[] defaultValue = { "struct", "standard ptr", "deep copy" };
		String[] implClass = { "struct", "class" };
		String[] pointerType = { "standard", "shared_ptr", "unique_ptr" };
		String[] copyOptions = { "deep copy", "shallow copy", "no copy", "noncopyable" };
		
	}

	@AfterClass
	public static void sleep() {
		bot.sleep(2000);
	}
}
