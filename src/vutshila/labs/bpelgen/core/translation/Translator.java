/**
 * 
 */
package vutshila.labs.bpelgen.core.translation;

import org.eclipse.core.resources.IFile;

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
     */
    public static void translateEventb(IFile machineFile, String machineName) {
	System.out.println("Translation");
	EventBTranslator translator = new EventBTranslator();
	System.out.println(translator.toString());
	//translator.loadMachineDocument(machineFile, machineName);
	//translator.createEventB();
	// see what you can do with InputStream
    }
}
