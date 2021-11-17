package fr.sorbonne_u.components.waterHeater.mil.events;


import fr.sorbonne_u.components.waterHeater.mil.WaterHeaterElectricityModel;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;


public class			SwitchOffWaterHeater
extends		AbstractWaterHeaterEvent
{

	private static final long serialVersionUID = 1L;

	
	public SwitchOffWaterHeater(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return false;
	}

	
	@Override
	public void			executeOn(AtomicModel model)
	{
		assert	model instanceof WaterHeaterElectricityModel;

		WaterHeaterElectricityModel m = ((WaterHeaterElectricityModel)model);
		
		if (m.getState() != WaterHeaterElectricityModel.State.OFF) {
			m.setState(WaterHeaterElectricityModel.State.OFF);
			m.toggleConsumptionHasChanged();
		}
	}
}
// -----------------------------------------------------------------------------
