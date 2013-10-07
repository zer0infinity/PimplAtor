package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.actions;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.internal.ui.cview.CView;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.views.contentoutline.ContentOutline;

public class IntroducePImplViewActionDelegate implements IEditorActionDelegate {
	private IEditorPart targetEditor;

	@Override
	public void run(IAction action) {
		IntroducePImplAction pimplAction = new IntroducePImplAction(Messages.IntroducePImpl_IntroducePImpl);
		if(targetEditor.getSite().getPage().getActivePart() instanceof CEditor) {
			pimplAction.setEditor(targetEditor.getSite().getPage().getActiveEditor());
			pimplAction.run();
		}
		IWorkbenchPart part = targetEditor.getSite().getPage().getActivePart();
		if (part instanceof ContentOutline || part instanceof CView|| part instanceof ProjectExplorer) {
			IWorkbenchSite site = part.getSite();
			pimplAction.setSite(site);
			ICElement elem= getCElement(site.getSelectionProvider().getSelection());
			pimplAction.updateSelection(elem);
			pimplAction.run();
		}
	}
	
	private ICElement getCElement(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss= (IStructuredSelection) selection;
			if (ss.size() == 1) {
				Object o= ss.getFirstElement();
				if (o instanceof ICElement && o instanceof ISourceReference) {
					return (ICElement) o;
				}
			}
		}
		return null;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
	}
}
