package fr.sorbonne_u.components.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class FanInboundPort extends AbstractInboundPort implements FanCI {

	private static final long serialVersionUID = 1L;

	public	FanInboundPort(ComponentI owner) throws Exception
	{
		super(FanCI.class, owner);
	}

	public	FanInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FanCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.fan.FanCI#getState()
	 */
	@Override
	public State	getState() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<State>() {
					@Override
					public State call() throws Exception {
						return ((FanImplementation)
					this.getServiceOwner()).getState();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.Fan.FanCI#getMode()
	 */
	@Override
	public Mode	getMode() throws Exception
	{
		return this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<Mode>() {
				@Override
				public Mode call() throws Exception {
					return ((FanImplementation)
					this.getServiceOwner()).getMode();
				}
			});
	}

	/**
	 * @see fr.sorbonne_u.components.fan.FanCI#turnOn()
	 */
	@Override
	public void	turnOn() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((FanImplementation)
						this.getServiceOwner()).turnOn();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.fan.FanCI#turnOff()
	 */
	@Override
	public void	turnOff() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((FanImplementation)
						this.getServiceOwner()).turnOff();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.fan.FanCI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((FanImplementation)
						this.getServiceOwner()).setHigh();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.fan.FanCI#setLow()
	 */
	@Override
	public void			setLow() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((FanImplementation)
						this.getServiceOwner()).setLow();
						return null;
					}
				});
	}
}
