
package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.jmock;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplInformation;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplRefactoring;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;

public class IntroducePImplRefactoringTest extends MockObjectTestCase {
	
	private static final NullProgressMonitor PM = new NullProgressMonitor();

	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();
	
	private IntroducePImplRefactoring refactoring;
	private IntroducePImplInformation info;
	
	@Override
	protected void setUp() throws Exception {
		final IWorkingCopy iwcopy = context.mock(IWorkingCopy.class);
		final ISelection selection = context.mock(ISelection.class);
		context.checking(new Expectations() {{
			allowing(iwcopy).getTranslationUnit();
			allowing(iwcopy).getSourceRange();
			oneOf(selection);
		}});
		info = new IntroducePImplInformation();
		refactoring = new IntroducePImplRefactoring(iwcopy, selection, info);
	}
	
	public void testCheckInitialCondition() {
		RefactoringStatus status = refactoring.checkInitialConditions(PM);
		assertTrue(status.isOK());
	}
	
	@SuppressWarnings("restriction")
	public void testCheckFinalCondition() {
		mockClassSpecifier();
		RefactoringStatus status = null;
		try {
			status = refactoring.checkFinalConditions(PM);
			assertTrue(status.isOK());
		} catch (OperationCanceledException | CoreException e) {
			e.printStackTrace();
			assertFalse(status.isOK());
		}
	}

	private void mockClassSpecifier() {
		final ICPPASTCompositeTypeSpecifier classSpecifier = context.mock(ICPPASTCompositeTypeSpecifier.class);
		context.checking(new Expectations() {{
			allowing(classSpecifier).getName(); will(returnValue(IASTName.class));
			allowing(classSpecifier).getParent().getParent(); will(returnValue(IASTNode.class));
			allowing(classSpecifier).getDeclarations(with(any(Boolean.class)));
		}});
		info.setClassSpecifier(classSpecifier);
	}
	
	public void testCheckAllConditions() {
		mockClassSpecifier();
		RefactoringStatus status = null;
		try {
			status = refactoring.checkAllConditions(PM);
			assertTrue(status.isOK());
		} catch (OperationCanceledException | CoreException e) {
			e.printStackTrace();
			assertFalse(status.isOK());
		}
	}
}
