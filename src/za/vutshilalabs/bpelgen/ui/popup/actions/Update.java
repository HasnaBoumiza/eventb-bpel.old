package za.vutshilalabs.bpelgen.ui.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.translation.WSDLTranslator;

public class Update implements IObjectActionDelegate {

	@SuppressWarnings("unused")
	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public Update() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		// MessageDialog.openInformation(
		// shell,
		// "BPEL generator",
		// "Update was executed.");

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object obj = ss.getFirstElement();

			if (obj instanceof IFile) {

				IFile bpelfile = (IFile) obj;
				IProject project = bpelfile.getProject();
				IRodinProject rodinProject = RodinHelper
						.getRodinProject(project);
				WSDLTranslator wsdlTranslator = new WSDLTranslator();
				IFile wsdlFile = project.getFile("PurchaseOrderM.wsdl");
				try {
					wsdlTranslator.init(wsdlFile, rodinProject);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

}
