package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.actions;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.IStructureDeclaration;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.cview.CView;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.IntroducePImplRefactoringRunner;
import org.eclipse.cdt.ui.refactoring.actions.RefactoringAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.views.contentoutline.ContentOutline;

@SuppressWarnings("restriction")
public class IntroducePImplAction extends RefactoringAction {

	public IntroducePImplAction(String label) {
		super(label);
	}

	public void run(IShellProvider shellProvider, ICElement elem) {
		if (elem instanceof ISourceReference) {
			ITextSelection sel = null;

			IWorkbenchPart part = ((IViewSite) shellProvider).getWorkbenchWindow().getActivePage().getActivePart();
			// Call from Project-Explorer or C/C++ Projects
			if (part instanceof ProjectExplorer || part instanceof CView) {
				if (elem.getResource().getFileExtension().equals("h")) {
					sel = new TextSelection(0, 0);
				}
			}
			// Call from Outline
			if (part instanceof ContentOutline) {
				// Find class of selected declaration
				while (!(elem instanceof IStructureDeclaration) && elem.getParent() != null && !(elem.getParent() instanceof IWorkingCopy)) {
					elem = elem.getParent();
				}
				// Get selection from active Editor
				for (IWorkbenchPage page : ((IViewSite) shellProvider).getWorkbenchWindow().getPages()) {
					if (sel == null) {
						if (page.getActiveEditor() != null) {
							sel = (ITextSelection) page.getActiveEditor().getEditorSite().getSelectionProvider().getSelection();
						} else {
							sel = new TextSelection(0, 0);
						}
					} else {
						// Editor found
						break;
					}
				}
				ICElement parent = elem;
				while (!(parent instanceof IWorkingCopy)){
					parent = parent.getParent();
				}
			}
			new IntroducePImplRefactoringRunner(sel, elem, shellProvider).run();
		}
	}

	public void run(IShellProvider shellProvider, IWorkingCopy wc, ITextSelection selection) {
		new IntroducePImplRefactoringRunner(selection, wc, shellProvider).run();
	}
}
