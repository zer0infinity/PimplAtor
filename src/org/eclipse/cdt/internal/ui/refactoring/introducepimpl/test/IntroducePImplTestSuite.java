package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test;

import junit.framework.TestSuite;

import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.junittest.NodeHelperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	NodeHelperTest.class,
})
public class IntroducePImplTestSuite extends TestSuite {
}
