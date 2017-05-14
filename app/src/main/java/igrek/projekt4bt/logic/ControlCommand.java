package igrek.projekt4bt.logic;


public class ControlCommand {
	
	/**
	 * kierunek skręcania (-1 - lewo, 1 - prawo, 0 - brak)
	 */
	private int yaw;
	
	/**
	 * kierunek jazdy (1 - przód, -1 - do tyłu, 0 - brak)
	 */
	private int throttle;
	
	/**
	 * moc - prędkość [0 - 1]
	 */
	private float power;
	
	public ControlCommand(int yaw, int throttle, float power) {
		this.yaw = yaw;
		this.throttle = throttle;
		this.power = power;
	}
	
	public int getYaw() {
		return yaw;
	}
	
	public int getThrottle() {
		return throttle;
	}
	
	public float getPower() {
		return power;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ControlCommand))
			return false;
		ControlCommand o2 = (ControlCommand) obj;
		return this.yaw == o2.yaw && this.throttle == o2.throttle && this.power == o2.power;
	}
	
	public boolean changedStrongly(ControlCommand c2) {
		if (this.yaw != c2.yaw)
			return true;
		if (this.throttle != c2.throttle)
			return true;
		if (this.power == 1.0f && c2.power != 1.0f)
			return true;
		if (this.power != 1.0f && c2.power == 1.0f)
			return true;
		float powerDiff = this.power < c2.power ? c2.power - this.power : this.power - c2.power;
		return powerDiff >= 0.15;
	}
}
