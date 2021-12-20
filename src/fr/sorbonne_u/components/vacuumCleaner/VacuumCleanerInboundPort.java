package fr.sorbonne_u.components.vacuumCleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class VacuumCleanerInboundPort extends AbstractInboundPort implements VacuumCleanerCI {

	private static final long serialVersionUID = 1L;

	public	VacuumCleanerInboundPort(ComponentI owner) throws Exception
	{
		super(VacuumCleanerCI.class, owner);
	}

	public	VacuumCleanerInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, VacuumCleanerCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#getState()
	 */
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<VacuumCleanerState>() {
					@Override
					public VacuumCleanerState call() throws Exception {
						return ((VacuumCleanerImplementation)
					this.getServiceOwner()).getState();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#getMode()
	 */
	@Override
	public VacuumCleanerMode	getMode() throws Exception
	{
		return this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<VacuumCleanerMode>() {
				@Override
				public VacuumCleanerMode call() throws Exception {
					return ((VacuumCleanerImplementation)
					this.getServiceOwner()).getMode();
				}
			});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((VacuumCleanerImplementation)
						this.getServiceOwner()).turnOn();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((VacuumCleanerImplementation)
						this.getServiceOwner()).turnOff();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((VacuumCleanerImplementation)
						this.getServiceOwner()).setHigh();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.hem2021e1.equipments.VacuumCleaner.VacuumCleanerCI#setLow()
	 */
	@Override
	public void			setLow() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((VacuumCleanerImplementation)
						this.getServiceOwner()).setLow();
						return null;
					}
				});
	}
}

