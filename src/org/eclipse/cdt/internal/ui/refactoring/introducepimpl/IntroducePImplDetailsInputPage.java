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

public class IntroducePImplDetailsInputPage extends UserInputWizardPage {

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
	private Button boostRadioButton;
	private Button cpp11RadioButton;

	private Pattern classnameDiscouraged = Pattern.compile("[A-Z].*");
	private Pattern pointernameDiscouraged = Pattern.compile("(_|[a-z]).*");
	private Pattern nameError = Pattern.compile("(_|[A-Z]|[a-z]|[0-9])*", Pattern.UNICODE_CASE);
	
	private IntroducePImplInformation info;

	public IntroducePImplDetailsInputPage(String name, IntroducePImplInformation info) {
		super(name);
		this.info = info;
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
		createPointerNameLabel(result, textData);
		createClassTypeGroup(result);
		createPointerTypeGroup(result);
		createCopyTypeGroup(result);

		classNameText.setText(info.getClassSpecifier().getName().toString()	+ "Impl");
		pointerNameText.setText("_impl");
	}

	private void createPointerNameLabel(Composite result, GridData textData) {
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

		createStructRadioButton(classTypeGroup);
		createClassRadioButton(classTypeGroup);
	}

	private void createClassRadioButton(Group classTypeGroup) {
		Button classRadioButton = new Button(classTypeGroup, SWT.RADIO);
		classRadioButton.setText(Messages.IntroducePImpl_Class);
		classRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setClassType(ICPPASTCompositeTypeSpecifier.k_class);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createStructRadioButton(Group classTypeGroup) {
		Button structRadioButton = new Button(classTypeGroup, SWT.RADIO);
		structRadioButton.setText(Messages.IntroducePImpl_Struct);
		structRadioButton.setSelection(true);
		structRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setClassType(ICPPASTCompositeTypeSpecifier.k_struct);
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

		createStandardRadioButton(ptrTypeGroup);
		createSharedButton(ptrTypeGroup);
		createUniqueButton(ptrTypeGroup);

		createLibraryTypeGroup(ptrTypeGroup);
	}

	private void createUniqueButton(Group ptrTypeGroup) {
		uniqueButton = new Button(ptrTypeGroup, SWT.RADIO);
		uniqueButton.setText(Messages.IntroducePImpl_UniquePtr);
		uniqueButton.setSelection(true);
		uniqueButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setPointerType(IntroducePImplInformation.PointerType.UNIQUE);
				shallowRadioButton.setEnabled(true);
				boostRadioButton.setEnabled(false);
				cpp11RadioButton.setEnabled(false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createSharedButton(Group ptrTypeGroup) {
		sharedButton = new Button(ptrTypeGroup, SWT.RADIO);
		sharedButton.setText(Messages.IntroducePImpl_SharedPtr);
		sharedButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setPointerType(IntroducePImplInformation.PointerType.SHARED);
				shallowRadioButton.setEnabled(true);
				boostRadioButton.setEnabled(true);
				cpp11RadioButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createStandardRadioButton(Group ptrTypeGroup) {
		standardRadioButton = new Button(ptrTypeGroup, SWT.RADIO);
		standardRadioButton.setText(Messages.IntroducePImpl_StandardPtr);
		standardRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setPointerType(IntroducePImplInformation.PointerType.STANDARD);
				shallowRadioButton.setEnabled(false);
				shallowRadioButton.setSelection(false);
				if(!noCopyRadioButton.getSelection()
						&& !nonCopyableRadioButton.getSelection())
					deepRadioButton.setSelection(true);
				boostRadioButton.setEnabled(false);
				cpp11RadioButton.setEnabled(false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createLibraryTypeGroup(Composite parent) {
		Group libraryTypeGroup = new Group(parent, SWT.NONE);
		libraryTypeGroup.setText(Messages.IntroducePImpl_LibraryType);
		libraryTypeGroup.setLayoutData(getGroupBox());
		libraryTypeGroup.setLayout(new GridLayout());
		
		createBoostRadioButton(libraryTypeGroup);
		createCpp11RadioButton(libraryTypeGroup);
	}

	private void createCpp11RadioButton(Group libraryTypeGroup) {
		cpp11RadioButton = new Button(libraryTypeGroup, SWT.RADIO);
		cpp11RadioButton.setText(Messages.IntroducePImpl_STD);
		cpp11RadioButton.setEnabled(false);
		cpp11RadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setLibraryType(IntroducePImplInformation.LibraryType.STD);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createBoostRadioButton(Group libraryTypeGroup) {
		boostRadioButton = new Button(libraryTypeGroup, SWT.RADIO);
		boostRadioButton.setText(Messages.IntroducePImpl_Boost);
		boostRadioButton.setSelection(true);
		boostRadioButton.setEnabled(false);
		boostRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setLibraryType(IntroducePImplInformation.LibraryType.BOOST);
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

		createDeepRadioButton(copyTypeGroup);
		createShallowRadioButton(copyTypeGroup);
		createNoCopyRadioButton(copyTypeGroup);
		createNonCopyableRadioButton(copyTypeGroup);
	}

	private void createNonCopyableRadioButton(Group copyTypeGroup) {
		nonCopyableRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		nonCopyableRadioButton.setText(Messages.IntroducePImpl_NonCopyable);
		nonCopyableRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.NONCOPYABLE);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createNoCopyRadioButton(Group copyTypeGroup) {
		noCopyRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		noCopyRadioButton.setText(Messages.IntroducePImpl_NoCopy);
		noCopyRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.NOCOPY);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createShallowRadioButton(Group copyTypeGroup) {
		shallowRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		shallowRadioButton.setText(Messages.IntroducePImpl_ShallowCopy);
		shallowRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.SHALLOW);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createDeepRadioButton(Group copyTypeGroup) {
		deepRadioButton = new Button(copyTypeGroup, SWT.RADIO);
		deepRadioButton.setText(Messages.IntroducePImpl_DeepCopy);
		deepRadioButton.setSelection(true);
		deepRadioButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				info.setCopyType(IntroducePImplInformation.CopyType.DEEP);
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

	private boolean isNameOk(Pattern patternDiscouraged, String text, String emptyMsg, String errorMsg,	String warningMsg) {
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
