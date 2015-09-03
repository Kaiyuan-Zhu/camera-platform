package src.com.hoho.android.usbserial.examples;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.examples.SerialConsoleActivity;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * Created by kaiyuanzhu on 8/7/15.
 */
public class AndroidArduinoInterface {
    public UsbSerialPort sPort;
    private final String TAG = SerialConsoleActivity.class.getSimpleName();


    public AndroidArduinoInterface(UsbSerialPort Port) {
        sPort = Port;
    }





    public void clockwise() {

        try {
            byte buffer[] = new byte[1];
            buffer[0] = 97;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void counterclockwise() {

        try {
            byte buffer[] = new byte[1];
            buffer[0] = 98;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void stop() {

        try {
            byte buffer[] = new byte[1];
            buffer[0] = 99;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void fullclockwise() {

        try {
            byte buffer[] = new byte[1];
            buffer[0] = 100;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }

    public void fullcounterclockwise() {

        try {
            byte buffer[] = new byte[1];
            buffer[0] = 101;
            sPort.write(buffer, 100);
        }catch(IOException e){
            Log.e(TAG, "Error sending bytes to arduino: " + e.getMessage(), e);
        }
    }






}
