package za.vutshilalabs.bpelgen.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import za.vutshilalabs.bpelgen.Activator;

/**
 * 
 * @author mashern
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.TNS_WS, "http://www.example.org");
		store.setDefault(PreferenceConstants.WSDL_NS_PREFIX, "tns");
		store.setDefault(PreferenceConstants.OPTIONS, "generate");

	}

}
