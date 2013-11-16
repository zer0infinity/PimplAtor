package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

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
		IntroducePImplBaseInputPage classPage = new IntroducePImplDetailsInputPage(Messages.IntroducePImpl_IntroducePImpl, info);
		classPage.setMessage(Messages.IntroducePImpl_ChooseDetailMsg);
		if(info.getClassSpecifiers().size() > 1 && info.getClassSpecifier() == null){
			classPage = new IntroducePImplClassInputPage(Messages.IntroducePImpl_IntroducePImpl, info);
			classPage.setMessage(Messages.IntroducePImpl_ChooseClassMsg);
		}
		classPage.setTitle(Messages.IntroducePImpl_IntroducePImpl);
		addPage(classPage);
	}
}
