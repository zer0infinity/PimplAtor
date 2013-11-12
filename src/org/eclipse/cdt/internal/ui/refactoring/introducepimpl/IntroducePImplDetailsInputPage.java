package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import java.util.regex.Pattern;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class IntroducePImplDetailsInputPage extends IntroducePImplBaseInputPage {

	private Text classNameText;
	private Text pointerNameText;
	// Copy Type Group
	private Button deepRadioButton;
	private Button shallowRadioButton;
	private Button noCopyRadioButton;
	private Button nonCopyableRadioButton;
	// Pointer Type Group
	private Button standardRadioButton;
	private Button sharedButton;
	private Button uniqueButton;
	// Library Group
	private Group libraryTypeGroup;
	private Button boostRadioButton;
	private Button cpp11RadioButton;
	// Class Type Group
	private Button structRadioButton;
	private Button classRadioButton;

	private Pattern classnameDiscouraged = Pattern.compile("[A-Z].*");
	private Pattern pointernameDiscouraged = Pattern.compile("(_|[a-z]).*");
	private Pattern nameError = Pattern.compile("(_|[A-Z]|[a-z]|[0-9])*", Pattern.UNICODE_CASE);

	public IntroducePImplDetailsInputPage(String name, IntroducePImplInformation info) {
		super(name, info);
	}
	
	public void createControl(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		setControl(result);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		result.setLayout(layout);

		GridData textData = new GridData(GridData.FILL_HORIZONTAL);
		textData.grabExcessHorizontalSpace = true;

		createClassNameLabel(result, textData);

		CreatePointerNameLabel(result, textData);

		createClassTypeGroup(result);

		createPointerTypeGroup(result);

		createCopyTypeGroup(result);

		classNameText.setText(info.getClassSpecifier().getName().toString()	+ "Impl");
		pointerNameText.setText("_impl");
	}

	private void CreatePointerNameLabel(Composite result, GridData textData) {
		Label pointerNameLabel = new Label(result, SWT.NONE);
		pointerNameLabel.setText(Messages.IntroducePImpl_PointerName + ":");
		pointerNameText = new Text(result, SWT.NONE);
		pointerNameText.setLayoutData(textData);
		pointerNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setMessages();
			}
		});
	}

	private void createClassNameLabel(Composite result, GridData textData) {
		Label classNameLabel = new Label(result, SWT.NONE);
		classNameLabel.setText(Messages.IntroducePImpl_ClassName + ":");
		classNameText = new Text(result, SWT.NONE);
		classNameText.setLayoutData(textData);
		classNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setMessages();
			}
		});
	}

	private GridData getGroupBox() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 2;
		return gridData;
	}

	private void createClassTypeGroup(Composite parent) {
		Group classTypeGroup = new Group(parent, SWT.NONE);
		classTypeGroup.setText(Messages.IntroducePImpl_ClassType);
		classTypeGroup.setLayoutData(getGroupBox());
		classTypeGroup.setLayout(new GridLayout());

		structRadioButton = new Button(classTypeGroup, SWT.RADIO);
		structRadioButton.setText(Messages.IntroducePImpl_Struct);
		structRadioButton.setSelection(true);
		structRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setClassType(ICPPASTCompositeTypeSpecifier.k_struct);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		classRadioButton = new Button(classTypeGroup, SWT.RADIO);
		classRadioButton.setText(Messages.IntroducePImpl_Class);
		classRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setClassType(ICPPASTCompositeTypeSpecifier.k_class);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createPointerTypeGroup(Composite parent) {
		Group ptrTypeGroup = new Group(parent, SWT.NONE);
		ptrTypeGroup.setText(Messages.IntroducePImpl_PointerType);
		ptrTypeGroup.setLayoutData(getGroupBox());
		ptrTypeGroup.setLayout(new GridLayout());

		standardRadioButton = new Button(ptrTypeGroup, SWT.RADIO);
		standardRadioButton.setText(Messages.IntroducePImpl_StandardPtr);
		standardRadioButton.setSelection(true);
		standardRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setPointerType(IntroducePImplInformation.PointerType.standard);
				shallowRadioButton.setEnabled(false);
				if(shallowRadioButton.getSelection())
					shallowRadioButton.setSelection(false);
					if(!noCopyRadioButton.getSelection() && !nonCopyableRadioButton.getSelection())
						deepRadioButton.setSelection(true);
				libraryTypeGroup.setVisible(false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		sharedButton = new Button(ptrTypeGroup, SWT.RADIO);
		sharedButton.setText(Messages.IntroducePImpl_SharedPtr);
		sharedButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setPointerType(IntroducePImplInformation.PointerType.shared);
				shallowRadioButton.setEnabled(true);
				libraryTypeGroup.setVisible(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		uniqueButton = new Button(ptrTypeGroup, SWT.RADIO);
		uniqueButton.setText(Messages.IntroducePImpl_UniquePtr);
		uniqueButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setPointerType(IntroducePImplInformation.PointerType.unique);
				shallowRadioButton.setEnabled(true);
				libraryTypeGroup.setVisible(false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		createLibraryTypeGroup(ptrTypeGroup);
	}

	private void createLibraryTypeGroup(Composite parent) {
		libraryTypeGroup = new Group(parent, SWT.NONE);
		libraryTypeGroup.setText(Messages.IntroducePImpl_LibraryType);
		libraryTypeGroup.setLayoutData(getGroupBox());
		libraryTypeGroup.setVisible(false);
		libraryTypeGroup.setLayout(new GridLayout());
		boostRadioButton = new Button(libraryTypeGroup, SWT.RADIO);
		boostRadioButton.setText(Messages.IntroducePImpl_Boost);
		boostRadioButton.setSelection(true);
		boostRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setLibraryType(IntroducePImplInformation.LibraryType.boost);
				shallowRadioButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		cpp11RadioButton = new Button(libraryTypeGroup, SWT.RADIO);
		cpp11RadioButton.setText(Messages.IntroducePImpl_STD);
		cpp11RadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setLibraryType(IntroducePImplInformation.LibraryType.std);
				shallowRadioButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createCopyTypeGroup(Composite parent) {
		Group copyTypeGroup = new Group(parent, SWT.NONE);
		copyTypeGroup.setText(Messages.IntroducePImpl_CopyType);
		copyTypeGroup.setLayoutData(getGroupBox());
		copyTypeGroup.setLayout(new GridLayout());

		deepRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		deepRadioButton.setText(Messages.IntroducePImpl_DeepCopy);
		deepRadioButton.setSelection(true);
		deepRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.deep);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		shallowRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		shallowRadioButton.setText(Messages.IntroducePImpl_ShallowCopy);
		shallowRadioButton.setEnabled(false);
		shallowRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.shallow);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		noCopyRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		noCopyRadioButton.setText(Messages.IntroducePImpl_NoCopy);
		noCopyRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.nocopy);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		nonCopyableRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		nonCopyableRadioButton.setText(Messages.IntroducePImpl_NonCopyable);
		nonCopyableRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.noncopyable);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void setMessages() {
		setPageComplete();
		if (isNameOk(classnameDiscouraged, classNameText.getText(), Messages.IntroducePImpl_ClassNameEmpty,
				Messages.IntroducePImpl_ClassNameError, Messages.IntroducePImpl_ClassNameWarning)) {
			isNameOk(pointernameDiscouraged, pointerNameText.getText(), Messages.IntroducePImpl_PointerNameEmpty,
					Messages.IntroducePImpl_PointerNameError, Messages.IntroducePImpl_PointerNameWarning);
		}
	}

	private boolean isNameOk(Pattern patternDiscouraged, String text, String emptyMsg, String errorMsg,
			String warningMsg) {
		if (text.equals("")) {
			setErrorMessage(emptyMsg);
			return false;
		} else {
			if (!nameError.matcher(text).matches()) {
				setErrorMessage(errorMsg);
				return false;
			} else {
				setErrorMessage(null);
				if (!patternDiscouraged.matcher(text).matches()) {
					setMessage(warningMsg, UserInputWizardPage.WARNING);
					return false;
				} else {
					setMessage(null);
					return true;
				}
			}
		}
	}

	private void setPageComplete() {
		String className = classNameText.getText();
		String pointerName = pointerNameText.getText();
		if ((!className.equals("") && nameError.matcher(className).matches())
				&& (!pointerName.equals("") && nameError.matcher(pointerName).matches())) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	public IWizardPage getNextPage() {
		info.setClassNameImpl(classNameText.getText());
		info.setPointerNameImpl(pointerNameText.getText());
		return super.getNextPage();
	}
}
