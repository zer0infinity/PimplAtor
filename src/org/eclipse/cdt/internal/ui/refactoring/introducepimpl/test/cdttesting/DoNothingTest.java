package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.test.cdttesting;

import java.util.Properties;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplInformation;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplRefactoring;
import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.junit.Test;

import ch.hsr.ifs.cdttesting.rts.junit4.CDTProjectJUnit4RtsTest;

@SuppressWarnings("restriction")
public class DoNothingTest extends CDTProjectJUnit4RtsTest {
	
	private IntroducePImplInformation info;
	
	@Override
	public void setUp() throws Exception {
		info = new IntroducePImplInformation();
	}
	
	@Override
	@Test
	public void runTest() throws Throwable {
		IFile refFile = project.getFile(activeFileName);
		ICElement element = CoreModel.getDefault().create(refFile); 
		IntroducePImplRefactoring refactoring = new IntroducePImplRefactoring(element, selection, info);
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
		super.configureTest(properties);
	}
}
