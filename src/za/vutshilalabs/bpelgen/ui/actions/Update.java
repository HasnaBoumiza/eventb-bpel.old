package za.vutshilalabs.bpelgen.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.rodinp.core.IRodinProject;

import za.vutshilalabs.bpelgen.core.EBConstant;
import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.translation.WSDLTranslator;

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

			if (obj instanceof IFile
					&& ((IFile) obj).getFileExtension().equals(
							EBConstant.BPEL_EXTENSION)) {
				IRodinProject rodinProject = RodinHelper
						.getRodinProject(((IFile) obj).getProject());
				// Create WSDL
				IFile wsdlFile = ((IFile) obj).getProject().getFile(
						"PurchaseOrderM.wsdl");
				WSDLTranslator wsdlTranslator = new WSDLTranslator();
				try {
					wsdlTranslator.init(wsdlFile, rodinProject);
				} catch (Exception e) {
					System.err.printf(
							"failed creating Context file. exception: %s", e
									.getMessage());
				}
				// TODO (mashern) Create Machine after wsdl
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
