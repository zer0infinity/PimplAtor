package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class IntroducePImplClassInputPage extends UserInputWizardPage {
	
	private IntroducePImplInformation info;

	public IntroducePImplClassInputPage(String name, IntroducePImplInformation info) {
		super(name);
		this.info = info;
	}

	public void createControl(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		setControl(result);

		result.setLayout(new GridLayout());

		Label infoText = new Label(result, SWT.NONE);
		infoText.setText(Messages.IntroducePImpl_ChooseClassInfo);

		for (ICPPASTCompositeTypeSpecifier classSpezifier : info.getClassSpecifiers()){
			addClass(classSpezifier, result, true);
		}
	}

	private void addClass(ICPPASTCompositeTypeSpecifier classSpecifier, Composite result, boolean firstClass) {
		if (firstClass) {
			info.setClassSpecifier(classSpecifier);
		}
		Button classRadioButton = new Button(result, SWT.RADIO);
		classRadioButton.setText(classSpecifier.getName().toString());
		classRadioButton.addSelectionListener(new SpecialSelectionListener(info, classSpecifier));
	}

	private class SpecialSelectionListener implements SelectionListener {

		IntroducePImplInformation info;
		ICPPASTCompositeTypeSpecifier classSpecifier;

		public SpecialSelectionListener(IntroducePImplInformation info,	ICPPASTCompositeTypeSpecifier classSpecifier) {
			this.info = info;
			this.classSpecifier = classSpecifier;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			info.setClassSpecifier(classSpecifier);
		}
	}
}
