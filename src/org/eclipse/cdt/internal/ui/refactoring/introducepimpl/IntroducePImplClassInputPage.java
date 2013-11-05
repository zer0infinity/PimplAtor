package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public class IntroducePImplClassInputPage extends IntroducePImplBaseInputPage {

	private CRefactoringContext context;

	public IntroducePImplClassInputPage(String name, IntroducePImplInformation info, CRefactoringContext context) {
		super(name, info);
		this.context = context;
	}
	
	@Override
	public void dispose() {
		context.dispose();
	}

	public void createControl(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		setControl(result);

		result.setLayout(new GridLayout());

		Label infoText = new Label(result, SWT.NONE);
		infoText.setText(Messages.IntroducePImpl_ChooseClassInfo);

		boolean firstClass = true;
		for (ICPPASTCompositeTypeSpecifier classSpezifier : info.getClassSpecifiers()){
			addClass(classSpezifier, result, firstClass);
		}
	}

	private void addClass(ICPPASTCompositeTypeSpecifier classSpezifier, Composite result, boolean firstClass) {
		if (firstClass) {
			// Initialize ClassSpezifier in Info with first class
			// occurrence
			info.setClassSpecifier(classSpezifier);
			firstClass = false;
		}
		Button classRadioButton = new Button(result, SWT.RADIO);
		classRadioButton.setText(classSpezifier.getName().toString());
		classRadioButton.addSelectionListener(new SpecialSelectionListener(classRadioButton, info, classSpezifier));
	}

	class SpecialSelectionListener implements SelectionListener {

		Button control;
		IntroducePImplInformation info;
		ICPPASTCompositeTypeSpecifier classSpecifier;

		public SpecialSelectionListener(Button control, IntroducePImplInformation info,
				ICPPASTCompositeTypeSpecifier classSpecifier) {
			super();
			this.control = control;
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
