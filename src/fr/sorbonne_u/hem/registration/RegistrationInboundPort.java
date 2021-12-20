package fr.sorbonne_u.hem.registration;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegistrationInboundPort
extends		AbstractInboundPort
implements	RegistrationCI
{
	private static final long serialVersionUID = 1L;

	public	RegistrationInboundPort(ComponentI owner)
	throws Exception
	{
		super(RegistrationCI.class, owner);
	}

	public	RegistrationInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, RegistrationCI.class, owner);
	}

	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception {
		return this.getOwner().handleRequest(
				o -> ((RegistrationImplementation)o).register(uid, controlPortURI, path2xmlControlAdapter));
	}
}
