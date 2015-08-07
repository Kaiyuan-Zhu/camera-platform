package src.com.hoho.android.usbserial.examples;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.examples.SerialConsoleActivity;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * Created by kaiyuanzhu on 8/7/15.
 */
public class AndroidArduinoInterface {
    public UsbSerialPort sPort;
    private final String TAG = SerialConsoleActivity.class.getSimpleName();
    private Queue<String> dataQueue = null;

    public AndroidArduinoInterface(UsbSerialPort Port) {
        sPort = Port;
    }

    public Queue<String> getQueue() {
        return dataQueue;
    }



    public void startSPO2() {

        try {
            byte buffer[] = new byte[2];
            buffer[0] = 97;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void startLed(byte LED) {

        try {
            byte buffer[] = new byte[2];
            buffer[0] = 98;
            buffer[1] = LED;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void stopLED(byte LED) {

        try {
            byte buffer[] = new byte[2];
            buffer[0] = 99;
            buffer[1] = LED;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void startADC() {

        try {
            byte buffer[] = new byte[2];
            buffer[0] = 100;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void stopADC() {

        try {
            byte buffer[] = new byte[2];
            buffer[0] = 101;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void sendIR(String message) {
        StringTokenizer st = new StringTokenizer(message, ",");
        String frequency = "";
        if(st.hasMoreTokens()){
            frequency = "f" + st.nextToken();
        }
        while(st.hasMoreTokens()) {
            dataQueue.add(st.nextToken());
        }
        if(!(frequency.length()==0)) {
            byte buffer[] = frequency.getBytes(Charset.forName("UTF-8"));
            try {
                sPort.write(buffer, 100);
            } catch (IOException e) {
                Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
            }
        }



    }

    public void sendIRPulse(){
        String pulse = "g" + dataQueue.poll();
        byte buffer[] = pulse.getBytes(Charset.forName("UTF-8"));
        try {
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }




}
