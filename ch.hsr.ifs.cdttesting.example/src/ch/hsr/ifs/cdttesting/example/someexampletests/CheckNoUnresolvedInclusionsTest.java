/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik  
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 * 
 * Contributors:
 *     Institute for Software - initial API and implementation
 ******************************************************************************/
package ch.hsr.ifs.cdttesting.example.someexampletests;


import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.junit.Test;

import ch.hsr.ifs.cdttesting.JUnit4RtsTest;

public class CheckNoUnresolvedInclusionsTest extends JUnit4RtsTest implements ILogListener {

	IStatus loggedStatus;
	String loggingPlugin;

	@Override
	public void setUp() throws Exception {
		Plugin plugin = CCorePlugin.getDefault();
		if (plugin != null) {
			plugin.getLog().addLogListener(this);
		}
		super.setUp();
	}

	@Override
	@Test
	public void runTest() throws Throwable {
		assertNotNull(loggedStatus);
		assertNull(loggedStatus.getException());
		assertEquals(CCorePlugin.PLUGIN_ID, loggingPlugin);
		assertEquals(IStatus.INFO, loggedStatus.getSeverity());
		assertTrue(loggedStatus.getMessage().contains("1 declarations; 0 references; 13 unresolved inclusions; 0 syntax errors; 0 unresolved names"));
		assertTrue(loggedStatus.getMessage().startsWith("Indexed 'RegressionTestProject' (1 sources, 13 headers)"));
	}

	@Override
	public void logging(IStatus status, String plugin) {
		loggedStatus = status;
		loggingPlugin = plugin;
	}
}