package igrek.projekt4bt.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.ParcelUuid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import igrek.projekt4bt.dispatcher.AbstractEvent;
import igrek.projekt4bt.dispatcher.EventDispatcher;
import igrek.projekt4bt.dispatcher.IEventConsumer;
import igrek.projekt4bt.dispatcher.IEventObserver;
import igrek.projekt4bt.events.BTDataReceivedEvent;
import igrek.projekt4bt.events.ConnectButtonEvent;
import igrek.projekt4bt.events.ControlMotorsEvent;
import igrek.projekt4bt.events.DisconnectButtonEvent;
import igrek.projekt4bt.events.ReloadButtonEvent;
import igrek.projekt4bt.events.ShootButtonEvent;
import igrek.projekt4bt.events.ShowInfoEvent;
import igrek.projekt4bt.events.StatusButtonEvent;
import igrek.projekt4bt.events.TestButtonEvent;
import igrek.projekt4bt.logger.Logs;
import igrek.projekt4bt.logic.ControlCommand;

public class BTAdapter implements IEventObserver {
	
	private final String DEVICE_NAME = "HC-06";
	
	private OutputStream outputStream;
	private InputStream inStream;
	
	private BluetoothSocket socket;
	
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	volatile boolean stopWorker;
	
	public BTAdapter() {
		registerEvents();
	}
	
	@Override
	public void registerEvents() {
		EventDispatcher.registerEventObserver(ConnectButtonEvent.class, this);
		EventDispatcher.registerEventObserver(DisconnectButtonEvent.class, this);
		EventDispatcher.registerEventObserver(TestButtonEvent.class, this);
		EventDispatcher.registerEventObserver(StatusButtonEvent.class, this);
		EventDispatcher.registerEventObserver(ShootButtonEvent.class, this);
		EventDispatcher.registerEventObserver(ReloadButtonEvent.class, this);
		EventDispatcher.registerEventObserver(ControlMotorsEvent.class, this);
	}
	
	@Override
	public void onEvent(AbstractEvent event) {
		
		event.bind(ConnectButtonEvent.class, new IEventConsumer<ConnectButtonEvent>() {
			@Override
			public void accept(ConnectButtonEvent e) {
				showInfo("Connecting to bluetooth device...");
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						connect();
					}
				});
			}
		});
		
		event.bind(DisconnectButtonEvent.class, new IEventConsumer<DisconnectButtonEvent>() {
			@Override
			public void accept(DisconnectButtonEvent e) {
				showInfo("Disconnecting...");
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						disconnect();
					}
				});
			}
		});
		
		event.bind(TestButtonEvent.class, new IEventConsumer<TestButtonEvent>() {
			@Override
			public void accept(TestButtonEvent e) {
				testConnection();
			}
		});
		
		event.bind(StatusButtonEvent.class, new IEventConsumer<StatusButtonEvent>() {
			@Override
			public void accept(StatusButtonEvent e) {
				showStatus();
			}
		});
		
		event.bind(ShootButtonEvent.class, new IEventConsumer<ShootButtonEvent>() {
			@Override
			public void accept(ShootButtonEvent e) {
				sendShoot();
			}
		});
		
		event.bind(ReloadButtonEvent.class, new IEventConsumer<ReloadButtonEvent>() {
			@Override
			public void accept(ReloadButtonEvent e) {
				sendReload();
			}
		});
		
		event.bind(ControlMotorsEvent.class, new IEventConsumer<ControlMotorsEvent>() {
			@Override
			public void accept(ControlMotorsEvent e) {
				controlMotors(e.getControls());
			}
		});
		
	}
	
	private void showInfo(String message) {
		Logs.info(message);
		EventDispatcher.sendNow(new ShowInfoEvent(message));
	}
	
	private void showError(String message) {
		//TODO inny kolor wiadomości w przypadku błędu
		Logs.error(message);
		EventDispatcher.sendNow(new ShowInfoEvent("ERROR: " + message));
	}
	
	private void connect() {
		
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueAdapter != null && blueAdapter.isEnabled()) {
			
			Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
			
			Logs.info("bonded devices: " + bondedDevices.size());
			for (BluetoothDevice bt : bondedDevices) {
				Logs.info("device: " + bt.getName());
			}
			
			BluetoothDevice device = findDevice(bondedDevices);
			if (device == null) {
				showError("no bonded device named " + DEVICE_NAME + " found");
				return;
			}
			
			try {
				ParcelUuid[] uuids = device.getUuids();
				socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
				socket.connect();
				outputStream = socket.getOutputStream();
				inStream = socket.getInputStream();
				showInfo("Device connected: " + device.getName());
				
				listenForData();
			} catch (IOException e) {
				showError("Failed connecting to device: " + e.getMessage());
			}
			
		} else {
			showError("Bluetooth is disabled.");
		}
	}
	
	public void send(String s) {
		try {
			showInfo("Sending data: " + s);
			if (outputStream == null) {
				showError("No output stream.");
				return;
			}
			outputStream.write((s + "\n").getBytes());
		} catch (IOException e) {
			showError("Sending failed: " + e.getMessage());
		}
	}
	
	private void listenForData() {
		final Handler handler = new Handler();
		final byte delimiter = 10; //This is the ASCII code for a newline character
		
		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted() && !stopWorker) {
					try {
						// TODO disconnect detection
						
						int bytesAvailable = inStream.available();
						if (bytesAvailable > 0) {
							byte[] packetBytes = new byte[bytesAvailable];
							inStream.read(packetBytes);
							for (int i = 0; i < bytesAvailable; i++) {
								byte b = packetBytes[i];
								if (b == delimiter) {
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;
									
									handler.post(new Runnable() {
										public void run() {
											receivedData(data);
										}
									});
								} else {
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					} catch (IOException ex) {
						stopWorker = true;
					}
				}
			}
		});
		
		workerThread.start();
	}
	
	private void receivedData(String data) {
		Logs.debug("BT data received: " + data);
		EventDispatcher.sendNow(new ShowInfoEvent("BT: " + data));
		
		// split received message by line feeds
		for (String line : data.split("\\r|\\n")) {
			if (!line.isEmpty()) {
				receivedPacket(line);
			}
		}
	}
	
	private void receivedPacket(String packet) {
		
		EventDispatcher.sendEvent(new BTDataReceivedEvent(packet));
		
	}
	
	public void disconnect() {
		// TODO if connected
		try {
			stopWorker = true;
			if (outputStream != null)
				outputStream.close();
			if (inStream != null)
				inStream.close();
			if (socket != null)
				socket.close();
			showInfo("Bluetooth device disconnected");
		} catch (IOException e) {
			showError("Disconnecting failed: " + e.getMessage());
		}
	}
	
	private BluetoothDevice findDevice(Set<BluetoothDevice> bondedDevices) {
		for (BluetoothDevice bd : bondedDevices) {
			if (bd.getName().equals(DEVICE_NAME)) {
				return bd;
			}
		}
		return null;
	}
	
	private void testConnection() {
		send("TST");
	}
	
	private void showStatus() {
		//TODO
	}
	
	private void sendShoot() {
		send("SET 5");
		send("RST 7");
	}
	
	private void sendReload() {
		send("SET 7");
		send("RST 5");
	}
	
	private void controlMotors(ControlCommand controls) {
		// skręcanie
		if (controls.getYaw() == -1) {
			send("SET 1");
			send("RST 2");
		} else if (controls.getYaw() == +1) {
			send("RST 1");
			send("SET 2");
		} else {
			send("RST 1");
			send("RST 2");
		}
		// prędkość jazdy przód - tył
		if (controls.getThrottle() == -1) {
			send("SET 3");
			sendPWMSpeed(controls.getPower());
			send("RST 6");
		} else if (controls.getThrottle() == +1) {
			send("RST 3");
			sendPWMSpeed(controls.getPower());
			send("SET 6");
		} else {
			send("RST 3");
			send("RST 4");
			send("RST 6");
		}
	}
	
	private void sendPWMSpeed(float power) {
		int pwmFactor = (int) (power * 100);
		send("PWM 4 " + pwmFactor);
	}
}
