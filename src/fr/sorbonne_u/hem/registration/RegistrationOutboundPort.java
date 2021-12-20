package fr.sorbonne_u.hem.registration;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RegistrationOutboundPort 
extends		AbstractOutboundPort
implements	RegistrationCI {

	private static final long serialVersionUID = 1L;
	

	public	RegistrationOutboundPort(ComponentI owner)
	throws Exception
	{
		super(RegistrationCI.class, owner);
	}

	public	RegistrationOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, RegistrationCI.class, owner);
	}

	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		return ((RegistrationCI)this.getConnector()).register(uid, controlPortURI, path2xmlControlAdapter);
	}
}
