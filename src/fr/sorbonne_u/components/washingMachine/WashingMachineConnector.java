package fr.sorbonne_u.components.washingMachine;


import java.time.Duration;
import java.time.LocalTime;
import java.util.Timer;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.interfaces.PlanningEquipmentControlCI;

//-----------------------------------------------------------------------------
/**
* The class <code>WashingMachineConnector</code> implements a connector for the
* {@code WashingMachineCI} component interface.
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant	true
* </pre>
* 
* <p>Created on : 2021-10-16</p>
* 
* @author	<a href="kaoutar.nhaila@etu.sorbonne-universite.fr">NHAILA Kaoutar</a>
*/

public class WashingMachineConnector extends AbstractConnector implements PlanningEquipmentControlCI {

	public	WashingMachineConnector()
	{
		super();
	}

	@Override
	public boolean on() throws Exception {
		return ((WashingMachineCI)this.offering).isRunning();
	}

	@Override
	public boolean switchOn() throws Exception {
		((WashingMachineCI)this.offering).startWashingMachine();
		return true;
	}

	@Override
	public boolean switchOff() throws Exception {
		((WashingMachineCI)this.offering).stopWashingMachine();
		return true;
	}

	@Override
	public int maxMode() throws Exception {
		return 1;
	}

	@Override
	public boolean upMode() throws Exception {
		return false;
	}

	@Override
	public boolean downMode() throws Exception {
		return false;
	}

	@Override
	public boolean setMode(int modeIndex) throws Exception {
		Program program = Program.COTON;
		
		switch(modeIndex) {
		  case 2:
			  program = Program.COTONCI;
		  case 3:
			  program = Program.MIX40C;
		  case 4:
			  program = Program.SYNTETHETIQUES;
		  case 5:
			  program = Program.COUETTE;
	       default:
	    	   break;
		}
		
		((WashingMachineCI)this.offering).setMode(program);
		return true;
	}

	@Override
	public int currentMode() throws Exception {
		Program program = ((WashingMachineCI)this.offering).getMode();
		
		switch(program) {
		  case COTON:
		    return 1;
		  case COTONCI:
		    return 2;
		  case MIX40C:
			  return 3;
		  case SYNTETHETIQUES:
			  return 4;
		  case COUETTE:
			  return 5;
	       default:
	    	   return 6;
		}
	}

	@Override
	public boolean hasPlan() throws Exception {
		return true;
	}

	@Override
	public LocalTime startTime() throws Exception {
		LocalTime now = LocalTime.now();
		return now.plusMinutes(3);
	}

	@Override
	public Duration duration() throws Exception {
		return ((WashingMachineCI)this.offering).getCurrentDuration();
	}

	@Override
	public LocalTime deadline() throws Exception {
		LocalTime now = LocalTime.now();
		return now.plusMinutes(1);
	}

	@Override
	public boolean postpone(Duration d) throws Exception {
		((WashingMachineCI)this.offering).stopWashingMachine();
		
		Timer t = new Timer();
		t.schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	try {
							switchOn();
						} catch (Exception e) {
							e.printStackTrace();
						}
		                t.cancel();
		            }
		        }, 
		        d.toSecondsPart() 
		);
		
		return true;
	}

	@Override
	public boolean cancel() throws Exception {
		((WashingMachineCI)this.offering).stopWashingMachine();
		return true;
	}
	
	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setSpinningNumber(int)
	 */
	
	public void setSpinningNumber(int target) throws Exception {
		((WashingMachineCI)this.offering).setSpinningNumber(target);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getSpinningNumber()
	 */
	
	
	public int getSpinningNumber() throws Exception {
		return ((WashingMachineCI)this.offering).getSpinningNumber();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentSpinningNumber()
	 */
	
	
	public int getCurrentSpinningNumber() throws Exception {
		return ((WashingMachineCI)this.offering).getCurrentSpinningNumber();
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#setDuration(Duration)
	 */
	
	
	public void setDuration(Duration duration) throws Exception {
		((WashingMachineCI)this.offering).setDuration(duration);
	}

	/**
	 * @see fr.sorbonne_u.components.washingMachine.WashingMachineCI#getCurrentDuration()
	 */
	
	public Duration getCurrentDuration() throws Exception {
		return ((WashingMachineCI)this.offering).getCurrentDuration();
	}
}
