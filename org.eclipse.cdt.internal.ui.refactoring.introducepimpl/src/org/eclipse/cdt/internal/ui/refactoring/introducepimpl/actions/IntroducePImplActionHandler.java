package org.eclipse.cdt.internal.ui.refactoring.introducepimpl.actions;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.internal.ui.cview.CView;
import org.eclipse.cdt.internal.ui.editor.CEditor;
import org.eclipse.cdt.internal.ui.refactoring.introducepimpl.Messages;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.views.contentoutline.ContentOutline;

@SuppressWarnings("restriction")
public class IntroducePImplActionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IntroducePImplAction pimplAction = new IntroducePImplAction(Messages.IntroducePImpl_IntroducePImpl);
		if (window.getActivePage().getActivePart() instanceof CEditor) {
			pimplAction.setEditor(window.getActivePage().getActiveEditor());
			pimplAction.run();
		}
		IWorkbenchPart part = window.getActivePage().getActivePart();
		if (part instanceof ContentOutline || part instanceof CView|| part instanceof ProjectExplorer) {
			IWorkbenchSite site = part.getSite();
			pimplAction.setSite(site);
			ICElement elem= getCElement(site.getSelectionProvider().getSelection());
			pimplAction.updateSelection(elem);
			pimplAction.run();
		}
		return null;
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
}
