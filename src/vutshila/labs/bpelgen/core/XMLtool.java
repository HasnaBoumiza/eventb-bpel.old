package vutshila.labs.bpelgen.core;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import vutshila.labs.bpelgen.BpelgenPlugin;

/**
 * 
 * @author Ernest Mashele
 * 
 */

public class XMLtool {

    private Document doc;

    public XMLtool(boolean createNew, IFile file) {
	init(createNew, file);
    }

    /**
     * Load or create a new DOM document
     * 
     * @param createNew
     * @param file
     */
    private void init(boolean createNew, IFile file) {
	DocumentBuilder builder;

	if (createNew) {
	    try {
		builder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
		doc = builder.newDocument();

	    } catch (ParserConfigurationException e) {
		BpelgenPlugin.logError(e, e.getMessage());
	    }
	}

	else {
	    DocumentBuilderFactory factory;

	    factory = DocumentBuilderFactory.newInstance();
	    factory.setValidating(true); // Check errors
	    try {
		doc = factory.newDocumentBuilder().parse(
			file.getLocation().toFile());
	    } catch (ParserConfigurationException e) {
		BpelgenPlugin.logError(e, e.getMessage());
	    } catch (SAXException e) {
		BpelgenPlugin.logError(e, e.getMessage());
	    } catch (IOException e) {
		BpelgenPlugin.logError(e, e.getMessage());
	    }

	}
    }

    public Document getDocument() {
	return doc;
    }
}
