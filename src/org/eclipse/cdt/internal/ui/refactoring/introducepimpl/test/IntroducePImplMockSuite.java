package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test;

import junit.framework.TestSuite;

import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.DeclarationFinderTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.IntroducePImplRefactoringTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.SelectionHelperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	IntroducePImplRefactoringTest.class,
	DeclarationFinderTest.class,
	SelectionHelperTest.class,
})
public class IntroducePImplMockSuite extends TestSuite {
}
