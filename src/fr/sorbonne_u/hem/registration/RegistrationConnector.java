package fr.sorbonne_u.hem.registration;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RegistrationConnector
extends		AbstractConnector
implements	RegistrationCI {

	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		return ((RegistrationCI)this.offering).register(uid, controlPortURI, path2xmlControlAdapter);
	}
}
