package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting;

import org.junit.Test;

import ch.hsr.ifs.cdttesting.rts.junit4.CDTProjectJUnit4RtsTest;

public class RefactoringContentTest extends CDTProjectJUnit4RtsTest {
	
	@Override
	@Test
	public void runTest() throws Throwable {
		assertEquals("Example.h", activeFileName);
	}
}
