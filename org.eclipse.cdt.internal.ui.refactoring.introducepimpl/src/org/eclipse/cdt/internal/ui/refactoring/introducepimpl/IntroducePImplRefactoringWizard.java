package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.ui.IntroducePImplClassInputPage;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.ui.IntroducePImplDetailsInputPage;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

/**
 * The wizard page for Introduce PImpl Refactoring, creates the UI page.
 */
public class IntroducePImplRefactoringWizard extends RefactoringWizard {

	private final IntroducePImplInformation info;
	
	public IntroducePImplRefactoringWizard(Refactoring refactoring, IntroducePImplInformation info) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
		this.info = info;
	}

	protected void addUserInputPages() {
		if(info.getClassSpecifiers().size() > 1 && info.getClassSpecifier() == null){
			IntroducePImplClassInputPage chooseClassPage = new IntroducePImplClassInputPage(Messages.IntroducePImpl_IntroducePImpl, info);
			chooseClassPage.setTitle(Messages.IntroducePImpl_IntroducePImpl);
			chooseClassPage.setMessage(Messages.IntroducePImpl_ChooseClassMsg);
			addPage(chooseClassPage);
		}
				
		IntroducePImplDetailsInputPage chooseDetailsPage = new IntroducePImplDetailsInputPage(Messages.IntroducePImpl_IntroducePImpl, info);
		chooseDetailsPage.setTitle(Messages.IntroducePImpl_IntroducePImpl);
		chooseDetailsPage.setMessage(Messages.IntroducePImpl_ChooseDetailMsg);
		addPage(chooseDetailsPage);
	}
}
