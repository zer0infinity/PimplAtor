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
	NodeHelperTest.class,
	IntroducePImplRefactoringTest.class,
	DeclarationFinderTest.class,
	SelectionHelperTest.class,
//	DoNothingTest.class,
//	RefactoringContentTest.class,
})
public class IntroducePImplTestSuite extends TestSuite {
}
