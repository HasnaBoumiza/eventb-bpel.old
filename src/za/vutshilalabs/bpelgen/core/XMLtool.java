package za.vutshilalabs.bpelgen.core;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import za.vutshilalabs.bpelgen.BpelgenPlugin;

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

	public Document getDocument() {
		return doc;
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

			try {
				builder = factory.newDocumentBuilder();
				doc = builder.parse(file.getLocation().toFile());
				doc.getDocumentElement().normalize();
			} catch (ParserConfigurationException e) {
				BpelgenPlugin.logError(e, e.getMessage());
			} catch (SAXException e) {
				BpelgenPlugin.logError(e, e.getMessage());
			} catch (IOException e) {
				BpelgenPlugin.logError(e, e.getMessage());
			}

		}
	}
}
