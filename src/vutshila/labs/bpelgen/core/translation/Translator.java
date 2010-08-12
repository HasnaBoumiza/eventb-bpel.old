/**
 * 
 */
package vutshila.labs.bpelgen.core.translation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Ernest Mashele
 * 
 */
public class Translator {

    /**
     * Translates Event-B to BPEL and WSDL
     * 
     * @param filename
     * @param machineName
     * @throws FileNotFoundException
     */
    public static void translateEventb(IFile machineFile, String machineName) {
	IProject project = machineFile.getProject();
	IFile output = project.getFile("Translation.wsdl");

	if (machineFile.exists()) {

	    File file = machineFile.getLocation().toFile();
	    Scanner s;
	    try {
		s = new Scanner(file);
		System.out.println(s.nextLine());
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}
	if (!output.exists()) {
	    byte bytes[] = "Testing 1 2 3".getBytes();
	    InputStream source = new ByteArrayInputStream(bytes);
	    try {
		output.create(source, IResource.FILE, null);
	    } catch (CoreException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	// Testing wdsl packages and dom4j
//	System.out.println("before reading");
//	Document doc = null;
//	Element root = null;
//
//	doc = DocumentHelper.createDocument();
//	root = doc.addElement("process").addAttribute("name", "testing");
//	IFile proc = project.getFile("awesome.bpel");
//	if (!proc.exists()) {
//	    FileWriter procWriter;
//	    XMLWriter writer;
//	    
//	    System.out.println("In awesome");
//
//	    try {
//		procWriter = new FileWriter(proc.getFullPath().toString());
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		writer = new XMLWriter(procWriter, format);
//		writer.write(doc);
//		writer.close();
//		proc.refreshLocal(IResource.DEPTH_ZERO, null);
//		System.out.println("awesome executed");
//	    } catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    } catch (CoreException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	    }
//	}
//	
//	System.out.println("Done!");
    }
}
