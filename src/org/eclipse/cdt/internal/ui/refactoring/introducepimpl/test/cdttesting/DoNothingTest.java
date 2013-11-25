package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting;

import org.junit.Test;

import ch.hsr.ifs.cdttesting.rts.junit4.CDTProjectJUnit4RtsTest;
import ch.hsr.ifs.cdttesting.rts.junit4.RunFor;

@RunFor(rtsFile = "./resources/org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting//DoNothing.rts")
public class DoNothingTest extends CDTProjectJUnit4RtsTest {
	
	@Override
	@Test
	public void runTest() throws Throwable {
		assertEquals("Example.h", activeFileName);
		assertEquals("Example.cpp", activeFileName);
	}
}
