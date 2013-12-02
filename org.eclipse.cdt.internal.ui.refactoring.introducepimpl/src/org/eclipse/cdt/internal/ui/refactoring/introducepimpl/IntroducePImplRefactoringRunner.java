package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringRunner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;


@SuppressWarnings("restriction")
public class IntroducePImplRefactoringRunner extends RefactoringRunner {

	public IntroducePImplRefactoringRunner(ISelection selection, ICElement element, IShellProvider shellProvider) {
		super(element, selection, shellProvider, null);
	}

	@Override
	public void run() {
		IntroducePImplInformation info = new IntroducePImplInformation();
		CRefactoring refactoring = new IntroducePImplRefactoring(element, selection, info);
		CRefactoringContext context = new CRefactoringContext(refactoring);
		refactoring.setContext(context);
		IntroducePImplRefactoringWizard wizard = new IntroducePImplRefactoringWizard(refactoring, info);
		RefactoringWizardOpenOperation operator = new RefactoringWizardOpenOperation(wizard);
		try {
			operator.run(shellProvider.getShell(), refactoring.getName());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			context.dispose();
		}
	}
}