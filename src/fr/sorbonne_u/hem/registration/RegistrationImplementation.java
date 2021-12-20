package fr.sorbonne_u.hem.registration;

public interface RegistrationImplementation {

	public boolean	register(
			String uid,
			String controlPortURI,
			String path2xmlControlAdapter
			) throws Exception;
}
