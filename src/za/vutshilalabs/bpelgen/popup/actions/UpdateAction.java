package za.vutshilalabs.bpelgen.popup.actions;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jdom.JDOMException;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import za.vutshilalabs.bpelgen.core.RodinHelper;
import za.vutshilalabs.bpelgen.core.translation.BPELTranslator;
import za.vutshilalabs.bpelgen.core.translation.WSDLTranslator;

public class UpdateAction implements IObjectActionDelegate {

	public UpdateAction() {
		super();
	}

	@Override
	public void run(IAction action) {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
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
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				else if (file.getFileExtension().equals("bpel")) {
					BPELTranslator bpelTranslator = new BPELTranslator();
					try {
						bpelTranslator.init(file, rodinProject);
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// ignore

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		targetPart.getSite().getShell();

	}

}
