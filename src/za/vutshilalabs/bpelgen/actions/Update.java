package za.vutshilalabs.bpelgen.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.translation.BPELTranslator;
import za.vutshilalabs.bpelgen.core.translation.WSDLTranslator;

/**
 * 
 * @author Mashele Ernest<mashern@tuks.co.za>
 * 
 */
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

			if (obj instanceof IFile) {

				IFile file = (IFile) obj;
				IProject project = file.getProject();
				IRodinProject rodinProject = RodinHelper
						.getRodinProject(project);

				if (file.getFileExtension().equals("wsdl")) {

					WSDLTranslator wsdlTranslator = new WSDLTranslator();
					try {
						wsdlTranslator.init(file, rodinProject);
					} catch (RodinDBException e) {
						e.printStackTrace();
					}
				}

				else if (file.getFileExtension().equals("bpel")) {

					BPELTranslator bpelTranslator = new BPELTranslator();
					try {
						bpelTranslator.init(file, rodinProject);
					} catch (RodinDBException e) {
						e.printStackTrace();
					}
				}

			}

		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
