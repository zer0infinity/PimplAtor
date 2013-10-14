package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;

/**
 * @author Andrea Berweger
 */
public abstract class IntroducePImplBaseInputPage extends UserInputWizardPage {

	protected IntroducePImplInformation info;

	public IntroducePImplBaseInputPage(String name, IntroducePImplInformation info) {
		super(name);
		this.info = info;
	}
}
