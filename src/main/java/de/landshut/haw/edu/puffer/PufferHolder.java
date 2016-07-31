package de.landshut.haw.edu.puffer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.landshut.haw.edu.model.TransmissionObject;

public class PufferHolder {
	
	public static BlockingQueue<TransmissionObject> INPUT_PUFFER = new LinkedBlockingQueue<TransmissionObject>();
	
//	public static BlockingQueue<TransmissionObject> HOLD_PUFFER = new LinkedBlockingQueue<TransmissionObject>();
}
