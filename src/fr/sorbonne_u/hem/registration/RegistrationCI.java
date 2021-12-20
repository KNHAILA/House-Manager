package fr.sorbonne_u.hem.registration;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

//-----------------------------------------------------------------------------
public interface		RegistrationCI
extends		RegistrationImplementation, OfferedCI, RequiredCI
{
	@Override
	public boolean register(String uid, String controlPortURI, String path2xmlControlAdapter) throws Exception;
}