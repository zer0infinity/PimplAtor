package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test;

import junit.framework.TestSuite;

import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting.DoNothingTest;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting.RefactoringContentTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DoNothingTest.class,
	RefactoringContentTest.class,
})
public class IntroducePImplCDTSuite extends TestSuite {
}
