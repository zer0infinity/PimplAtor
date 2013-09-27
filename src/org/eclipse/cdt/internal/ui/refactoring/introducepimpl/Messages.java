package org.eclipse.cdt.internal.ui.refactoring.introducepimpl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.cdt.internal.ui.refactoring.introducepimpl.messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public static String IntroducePImpl_ChooseClassMsg;
	public static String IntroducePImpl_ChooseClassInfo;
	public static String IntroducePImpl_ChooseDetailMsg;
	public static String IntroducePImpl_IntroducePImpl;
	public static String IntroducePImpl_ClassType;
	public static String IntroducePImpl_PointerType;
	public static String IntroducePImpl_LibraryType;
	public static String IntroducePImpl_CopyType;
	public static String IntroducePImpl_Struct;
	public static String IntroducePImpl_Class;
	public static String IntroducePImpl_StandardPtr;
	public static String IntroducePImpl_SharedPtr;
	public static String IntroducePImpl_Boost;
	public static String IntroducePImpl_TR1;
	public static String IntroducePImpl_DeepCopy;
	public static String IntroducePImpl_ShallowCopy;
	public static String IntroducePImpl_NoCopy;
	public static String IntroducePImpl_NonCopyable;
	public static String IntroducePImpl_SelectionInvalid;
	public static String IntroducePImpl_ClassNameWarning;
	public static String IntroducePImpl_ClassNameEmpty;
	public static String IntroducePImpl_PointerNameWarning;
	public static String IntroducePImpl_PointerNameEmpty;
	public static String IntroducePImpl_ClassName;
	public static String IntroducePImpl_PointerName;
	public static String IntroducePImpl_ClassNameError;
	public static String IntroducePImpl_PointerNameError;
	public static String IntroducePImpl_HeaderFileNotFound;
	public static String IntroducePImpl_FunctionInOneFile;
	public static String IntroducePImpl_TooManyCppFiles;
	public static String IntroducePImpl_NoDefinitionFound;
	public static String IntroducePImpl_CppFileCreated;
	public static String IntroducePImpl_EmptyDeclarationFound;
	public static String IntroducePImpl_PublicField;
	
	public static String IntroducePImpl_Rewrite_IncludeInsert;
	public static String IntroducePImpl_Rewrite_NewLineInsertHeader;
	public static String IntroducePImpl_Rewrite_NamespaceInserted;
	public static String IntroducePImpl_Rewrite_HeaderClassReplace;
	public static String IntroducePImpl_Rewrite_ImplClassInsertSource;
	public static String IntroducePImpl_Rewrite_StaticFieldInsertHeader;
	public static String IntroducePImpl_Rewrite_ConstructerInsertHeader;
	public static String IntroducePImpl_Rewrite_ConstructorInsertSource;
	public static String IntroducePImpl_Rewrite_ConstructorInsertImpl;
	public static String IntroducePImpl_Rewrite_DestructorInsertImpl;
	public static String IntroducePImpl_Rewrite_MemberInsertHeader;
	public static String IntroducePImpl_Rewrite_MemberInsertSource;
	public static String IntroducePImpl_Rewrite_MemberInsertImpl;
	public static String IntroducePImpl_Rewrite_PointerInsertHeader;
	public static String IntroducePImpl_Rewrite_BasicConstructorInsertSource;
	public static String IntroducePImpl_Rewrite_BasicConstructorInsertHeader;
	public static String IntroducePImpl_Rewrite_DestructorInsertSource;
	public static String IntroducePImpl_Rewrite_DestructorInsertHeader;
	public static String IntroducePImpl_Rewrite_CopyConstructorInsertSource;
	public static String IntroducePImpl_Rewrite_CopyConstructorInsertHeader;
	public static String IntroducePImpl_Rewrite_PrivateLabelInsertHeader;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
