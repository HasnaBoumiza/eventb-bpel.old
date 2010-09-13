package vutshila.labs.bpelgen.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eventb.core.IContextRoot;

import vutshila.labs.bpelgen.core.translation.ContextTranslator;

public class Update implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	@Override
	public void dispose() {

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	@Override
	public void run(IAction action) {
		ISelection selection = window.getSelectionService().getSelection();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object obj = ss.getFirstElement();

			// handle Event-B machine file from Event-B perspective
			if (obj instanceof IContextRoot) {
				IContextRoot context = (IContextRoot) obj;
				
				// Move to translator
				ContextTranslator trans = new ContextTranslator();
				trans.init(null, context);
				trans.createTypes();				
			}

		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
