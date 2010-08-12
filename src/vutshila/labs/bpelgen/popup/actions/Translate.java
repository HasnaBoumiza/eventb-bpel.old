package vutshila.labs.bpelgen.popup.actions;

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
import org.eventb.core.IMachineRoot;

import vutshila.labs.bpelgen.core.translation.Translator;

public class Translate implements IObjectActionDelegate {

    @SuppressWarnings("unused")
    private Shell shell;

    /**
     * Constructor for Action1.
     */
    public Translate() {
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
	// XXX: need to use refreshLocal Method
	IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
	ISelection selection = window.getSelectionService().getSelection();
	if (selection instanceof IStructuredSelection) {
	    IStructuredSelection ss = (IStructuredSelection) selection;
	    Object obj = ss.getFirstElement();

	    // handle Event-B machine file from Event-B perspective
	    if (obj instanceof IMachineRoot) {
		IMachineRoot machine = (IMachineRoot) obj;
		IProject project = machine.getEventBProject().getRodinProject()
			.getProject();
		IFile machineFile = project.getFile(machine.getElementName()
			.concat(".bcm"));

		if (!machineFile.exists())
		    System.out.println(machineFile.getName()
			    + " File does not exits");

		Translator.translateEventb(machineFile, machine
			.getElementName());

		System.out.println(machine.getElementName());
	    }

	    // handle Event-B machine file out of Event-B perspective
	    // i.e. .bum .bcm files

	    if (obj instanceof IFile) {
		IFile machineFile = (IFile) obj;

		// XXX: check if extension equals .bcm else load the .bcm file
		if (machineFile.getFileExtension().equals("bcm")) {
		    // String name = machineFile.getName().substring(0,
		    // machineFile.getName().length() - EXT_LEN);
		    String name = machineFile.getName();
		    Translator.translateEventb(machineFile, name);

		} else {
		    IProject project = machineFile.getProject();
		    IFile bcm = project.getFile(machineFile.getName().replace(
			    "bum", "bcm"));
		    if (!bcm.exists())
			System.out.println("file does not exist");
		    else
			Translator.translateEventb(bcm, bcm.getName());
		}

		System.out.println(machineFile.getName());

	    }

	}
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

}