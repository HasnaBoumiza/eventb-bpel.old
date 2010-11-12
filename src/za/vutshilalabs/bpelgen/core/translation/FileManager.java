/**
 * 
 */
package za.vutshilalabs.bpelgen.core.translation;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import za.vutshilalabs.bpelgen.core.IGlobalConstants;

/**
 * Handle file creation, name clash resolution, and file association in Event-B.
 * 
 * @author Mashele Ernest<mashern@tuks.co.za>
 * 
 * 
 */
public class FileManager {

	/**
	 * Check if the file name is not in use and unused one.
	 * 
	 * @param context
	 * @param project
	 * @return
	 */
	public static String getWSDLName(IContextRoot context, IRodinProject project) {

		String contextName = context.getElementName();
		String suggestedName = contextName
				.concat(IGlobalConstants.WSDL_EXTENSION);
		IResource[] nonRodin;
		ArrayList<String> names = new ArrayList<String>(0);
		int instances = 0;
		boolean safe = true;
		try {
			nonRodin = project.getNonRodinResources();
			for (IResource r : nonRodin) {
				if (r.getName().equals(suggestedName)) {
					safe = false;
				} else if (r.getName().startsWith(contextName)) {
					instances++;
					names.add(r.getName());
				}
			}

			if (!safe || instances > 0) {
				String result = contextName + (instances + 1)
						+ IGlobalConstants.WSDL_EXTENSION;
				if (!names.contains(result)) {
					return result;
				} else {
					result = contextName + (instances + 2)
							+ IGlobalConstants.WSDL_EXTENSION;
					return result;
				}
			} else {
				return suggestedName;
			}
		} catch (RodinDBException e) {
			System.out.println("failed reading Non-Rodin resources.");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Check if the file name is not in use and unused one.
	 * 
	 * @param machine
	 * @param project
	 * @return
	 */
	public static String getBPELName(IMachineRoot machine, IRodinProject project) {

		String contextName = machine.getElementName();
		String suggestedName = contextName
				.concat(IGlobalConstants.BPEL_EXTENSION);
		IResource[] nonRodin;
		ArrayList<String> names = new ArrayList<String>(0);

		int instances = 0;
		boolean safe = true;
		try {
			nonRodin = project.getNonRodinResources();
			for (IResource r : nonRodin) {
				if (r.getName().equals(suggestedName)) {
					safe = false;
				} else if (r.getName().startsWith(contextName)) {
					instances++;
					names.add(r.getName());
				}
			}

			if (!safe || instances > 0) {
				String result = contextName + (instances + 1)
						+ IGlobalConstants.BPEL_EXTENSION;
				if (!names.contains(result)) {
					return result;
				} else {
					result = contextName + (instances + 2)
							+ IGlobalConstants.BPEL_EXTENSION;
					return result;
				}
			} else {
				return suggestedName;
			}
		} catch (RodinDBException e) {
			System.out.println("failed reading Non-Rodin resources.");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Search for Event-B files, return safe
	 * 
	 * @param file
	 * @param project
	 * @return
	 */
	public static String getFilename(IFile file, IRodinProject project) {
		IRodinFile[] rodinFiles = null;
		ArrayList<String> names = new ArrayList<String>(0);

		try {
			rodinFiles = project.getRodinFiles();
			int count = 0;
			boolean safe = true;

			String ext = "";

			if (file.getFileExtension().equals("wsdl")) {
				ext = "buc";
			} else {
				ext = "bum";
			}

			String suggestedName = file.getName().substring(0,
					file.getName().indexOf(IGlobalConstants.PERIOD));
			for (IRodinFile rodinFile : rodinFiles) {
				String localName = rodinFile.getElementName().substring(
						0,
						rodinFile.getElementName().indexOf(
								IGlobalConstants.PERIOD));
				if (localName.equals(suggestedName)) {
					names.add(localName);
					safe = false;
				} else if (localName.startsWith(suggestedName)) {
					names.add(localName);
					if (rodinFile.getElementName().endsWith(ext))
						count++;
				}
			}

			if (!safe || count > 0) {
				String result = suggestedName + (count + 1);
				if (!names.contains(result)) {
					return result;
				} else {
					return suggestedName + (count + 2);
				}
			} else {
				return suggestedName;
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
