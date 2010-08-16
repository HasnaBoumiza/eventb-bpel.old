package vutshila.labs.bpelgen.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IMachineRoot;

import vutshila.labs.bpelgen.core.translation.BPELwriter;
import vutshila.labs.bpelgen.core.translation.WSDLwriter;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class Translate implements IWorkbenchWindowActionDelegate {
    @SuppressWarnings("unused")
    private IWorkbenchWindow window;

    /**
     * The constructor.
     */
    public Translate() {
    }

    /**
     * The action has been activated. The argument of the method represents the
     * 'real' action sitting in the workbench UI.
     * 
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action) {
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
		// Create BPEL file
		IFile bpelFile = project.getFile(machine.getElementName()
			.concat(".bpel"));
		BPELwriter writer = new BPELwriter();
		writer.init(machine);
		writer.createFile(bpelFile);
		
		// Create WSDL file
		IFile wsdlFile = project.getFile(machine.getElementName().concat(".wsdl"));
		WSDLwriter wsdlWriter = new WSDLwriter(machine);
		wsdlWriter.createFile(wsdlFile);
		System.out.println("hello, world");
		// Validate files
	    }

	    // handle Event-B machine file out of Event-B perspective
	    // i.e. .bum .bcm files

	    if (obj instanceof IFile) {
		IFile machineFile = (IFile) obj;

//		if (machineFile.getFileExtension().equals("bcm")) {
//		    
//		    String name = machineFile.getName();
//		    Translator.translateEventb(machineFile, name);
//
//		} else {
//		    IProject project = machineFile.getProject();
//		    IFile bcm = project.getFile(machineFile.getName().replace(
//			    "bum", "bcm"));
//		    if (!bcm.exists())
//			System.out.println("file does not exist");
//		    else
//			Translator.translateEventb(bcm, bcm.getName());
//		}
		

		System.out.println(machineFile.getName());

	    }

	}
    }

    /**
     * Selection in the workbench has been changed. We can change the state of
     * the 'real' action here if we want, but this can only happen after the
     * delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {

    }

    /**
     * We can use this method to dispose of any system resources we previously
     * allocated.
     * 
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {
    }

    /**
     * We will cache window object in order to be able to provide parent shell
     * for the message dialog.
     * 
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
	this.window = window;
    }
}