package vutshila.labs.bpelgen.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import vutshila.labs.bpelgen.core.translation.Translator;
import vutshila.labs.bpelgen.core.translation.WSDLTranslator;

public class Update implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public Update() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
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
				// Translator.translateEventb(machineFile);
				IWorkspaceRoot wroot = project.getWorkspace().getRoot();
				IRodinDB rodinDB = RodinCore.valueOf(wroot);
				IRodinProject rodinProject = rodinDB.getRodinProject(project
						.getName());

				WSDLTranslator wsdl = new WSDLTranslator();
				try {
					System.out.println("hooray!");
					wsdl.init(bpelfile.getName().substring(0,
							bpelfile.getName().indexOf(".")), rodinProject);
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

}
