package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringRunner;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

/**
 * @author Andrea Berweger
 * 
 */
@SuppressWarnings("restriction")
public class IntroducePImplRefactoringRunner extends RefactoringRunner {

	public IntroducePImplRefactoringRunner(ISelection selection, ICElement element, IShellProvider shellProvider) {
		super(element, selection, shellProvider, null);
	}

	@Override
	public void run() {
		IntroducePImplInformation info = new IntroducePImplInformation();
		CRefactoring refactoring = new IntroducePImplRefactoring(selection, element, info);
		IntroducePImplRefactoringWizard wizard = new IntroducePImplRefactoringWizard(refactoring, info);
		RefactoringWizardOpenOperation operator = new RefactoringWizardOpenOperation(wizard);

		try {
//			refactoring.lockIndex();
			operator.run(shellProvider.getShell(), refactoring.getName());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (CoreException e) {
			CUIPlugin.log(e);
		} finally {
//			refactoring.unlockIndex();
		}
	}
}
