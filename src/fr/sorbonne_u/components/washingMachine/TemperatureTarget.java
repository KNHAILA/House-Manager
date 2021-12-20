package fr.sorbonne_u.components.washingMachine;

public enum TemperatureTarget {
	FROID(10),
	MEDIUM_20(20),
	MEDIUM_30(20),
	MEDIUM_40(20),
	MEDIUM_60(20),
	HIGH(95);
	
	private final int value;

	TemperatureTarget(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
