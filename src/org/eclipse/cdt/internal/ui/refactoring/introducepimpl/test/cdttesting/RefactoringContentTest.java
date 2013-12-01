package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting;

import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplInformation;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplRefactoring;
import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.junit.Test;

import ch.hsr.ifs.cdttesting.rts.junit4.CDTProjectJUnit4RtsTest;

@SuppressWarnings("restriction")
public class RefactoringContentTest extends CDTProjectJUnit4RtsTest {
	
	private IntroducePImplInformation info;
	
	@Override
	@Test
	public void runTest() throws Throwable {
		IFile refFile = project.getFile(activeFileName);
		ICElement element = CoreModel.getDefault().create(refFile);
		
		IntroducePImplRefactoring refactoring = new IntroducePImplRefactoring(element, selection, info);
		CRefactoringContext context = new CRefactoringContext(refactoring);
		refactoring.setContext(context);
		
		RefactoringStatus checkInitialConditions = refactoring.checkInitialConditions(NULL_PROGRESS_MONITOR);
		assertTrue(checkInitialConditions.isOK());
		RefactoringStatus checkFinalConditions = refactoring.checkFinalConditions(NULL_PROGRESS_MONITOR);
		assertTrue(checkFinalConditions.isOK());
		RefactoringStatus checkAllConditions = refactoring.checkAllConditions(NULL_PROGRESS_MONITOR);
		assertTrue(checkAllConditions.isOK());
		Change createChange = refactoring.createChange(NULL_PROGRESS_MONITOR);
		RefactoringStatus finalConditions = refactoring.checkFinalConditions(NULL_PROGRESS_MONITOR);
		assertTrue(finalConditions.isOK());
		createChange.perform(NULL_PROGRESS_MONITOR);
	}
	
	@Override
	protected void configureTest(Properties properties) {
		info = new IntroducePImplInformation();
		String classType = properties.getProperty("classType", "struct");
		if (classType.equals("class")){
			info.setClassType(ICPPASTCompositeTypeSpecifier.k_class);
		} else {
			info.setClassType(ICPPASTCompositeTypeSpecifier.k_struct);
		}
		
		String pointerType = properties.getProperty("pointerType", "standard");
		if (pointerType.equals("shared")){
			info.setPointerType(IntroducePImplInformation.PointerType.SHARED);
		} else if(pointerType.equals("unique")) {
			info.setPointerType(IntroducePImplInformation.PointerType.UNIQUE);
		} else {
			info.setPointerType(IntroducePImplInformation.PointerType.STANDARD);
		}
		
		String libraryType = properties.getProperty("libraryType","boost");
		if (libraryType.equals("std")){
			info.setLibraryType(IntroducePImplInformation.LibraryType.STD);
		} else {
			info.setLibraryType(IntroducePImplInformation.LibraryType.BOOST);
		}
		
		String copyType = properties.getProperty("copyType", "deep");
		if (copyType.equals("shallow")){
			info.setCopyType(IntroducePImplInformation.CopyType.SHALLOW);
		} else if (copyType.equals("nocopy")){
			info.setCopyType(IntroducePImplInformation.CopyType.NOCOPY);
		} else if (copyType.equals("noncopyable")) {
			info.setCopyType(IntroducePImplInformation.CopyType.NONCOPYABLE);
		} else {
			info.setCopyType(IntroducePImplInformation.CopyType.DEEP);
		}
		
		info.setClassNameImpl(properties.getProperty("classNameImpl", "Impl"));
		info.setPointerNameImpl(properties.getProperty("pointerNameImpl", "_impl"));
	}
}
