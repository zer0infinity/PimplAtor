package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test;

import junit.framework.TestSuite;

import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting.DoNothingTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting.RefactoringContentTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.IntroducePImplContextTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock.IntroducePImplRefactoringTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	IntroducePImplRefactoringTest.class,
	IntroducePImplContextTest.class,
	DoNothingTest.class,
	RefactoringContentTest.class,
})
public class IntroducePImplJUnitPluginTestSuite extends TestSuite {
}
