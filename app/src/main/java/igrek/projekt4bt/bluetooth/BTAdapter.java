package igrek.projekt4bt.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import igrek.projekt4bt.dispatcher.AbstractEvent;
import igrek.projekt4bt.dispatcher.EventDispatcher;
import igrek.projekt4bt.dispatcher.IEventConsumer;
import igrek.projekt4bt.dispatcher.IEventObserver;
import igrek.projekt4bt.events.ConnectButtonEvent;
import igrek.projekt4bt.events.ShowInfoEvent;
import igrek.projekt4bt.logger.Logs;

public class BTAdapter implements IEventObserver {
	
	private final String DEVICE_NAME = "HC-06";
	
	private OutputStream outputStream;
	private InputStream inStream;
	
	public BTAdapter() {
		registerEvents();
	}
	
	@Override
	public void registerEvents() {
		EventDispatcher.registerEventObserver(ConnectButtonEvent.class, this);
	}
	
	@Override
	public void onEvent(AbstractEvent event) {
		
		event.bind(ConnectButtonEvent.class, new IEventConsumer<ConnectButtonEvent>() {
			@Override
			public void accept(ConnectButtonEvent e) {
				connect();
			}
		});
		
	}
	
	private void showInfo(String message) {
		Logs.info(message);
		EventDispatcher.sendEvent(new ShowInfoEvent(message));
	}
	
	private void connect() {
		
		showInfo("Connecting to bluetooth device...");
		
		BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
		if (blueAdapter != null && blueAdapter.isEnabled()) {
			
			Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
			
			Logs.info("bonded devices: " + bondedDevices.size());
			for (BluetoothDevice bt : bondedDevices) {
				Logs.info("device: " + bt.getName());
			}
			
			BluetoothDevice device = findDevice(bondedDevices);
			if (device == null) {
				Logs.error("no bonded device named " + DEVICE_NAME + " found");
				return;
			}
			
			try {
				ParcelUuid[] uuids = device.getUuids();
				BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
				socket.connect();
				outputStream = socket.getOutputStream();
				inStream = socket.getInputStream();
				Logs.info("device connected: " + device.getName());
			} catch (IOException e) {
				Logs.error("Failed connecting to device: " + e.getMessage());
			}
			
		} else {
			Logs.error("Bluetooth is disabled.");
		}
	}
	
	public void send(String s) throws IOException {
		outputStream.write((s + "\n").getBytes());
	}
	
	public void run() {
		final int BUFFER_SIZE = 1024;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytes = 0;
		int b = BUFFER_SIZE;
		
		while (true) {
			try {
				bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
}
