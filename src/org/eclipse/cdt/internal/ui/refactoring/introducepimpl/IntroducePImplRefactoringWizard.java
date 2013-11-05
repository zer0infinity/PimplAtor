package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

/**
 * The wizard page for Introduce PImpl Refactoring, creates the UI page.
 */
@SuppressWarnings("restriction")
public class IntroducePImplRefactoringWizard extends RefactoringWizard {

	private final IntroducePImplInformation info;
	private CRefactoringContext context;
	
	public IntroducePImplRefactoringWizard(Refactoring refactoring, IntroducePImplInformation info, CRefactoringContext context) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
		this.info = info;
		this.context = context;
	}

	protected void addUserInputPages() {
		if(info.getClassSpecifiers().size() > 1 && info.getClassSpecifier() == null){
			IntroducePImplClassInputPage chooseClassPage = new IntroducePImplClassInputPage(Messages.IntroducePImpl_IntroducePImpl, this.info, context);
			chooseClassPage.setTitle(Messages.IntroducePImpl_IntroducePImpl);
			chooseClassPage.setMessage(Messages.IntroducePImpl_ChooseClassMsg);
			addPage(chooseClassPage);
		}
				
		IntroducePImplDetailsInputPage chooseDetailsPage = new IntroducePImplDetailsInputPage(Messages.IntroducePImpl_IntroducePImpl, this.info, context);
		chooseDetailsPage.setTitle(Messages.IntroducePImpl_IntroducePImpl);
		chooseDetailsPage.setMessage(Messages.IntroducePImpl_ChooseDetailMsg);
		addPage(chooseDetailsPage);
	}
}
