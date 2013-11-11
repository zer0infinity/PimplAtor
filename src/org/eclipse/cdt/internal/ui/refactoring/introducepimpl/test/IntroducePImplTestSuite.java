package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test;

import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.DeclarationFinderTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.IntroducePImplRefactoringTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IntroducePImplTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("org.eclipse.cdt.internal.ui.refactoring.introducepimpl tests");
		suite.addTestSuite(NodeHelperTest.class);
		suite.addTestSuite(IntroducePImplRefactoringTest.class);
		suite.addTestSuite(DeclarationFinderTest.class);
		return suite;
	}
}
