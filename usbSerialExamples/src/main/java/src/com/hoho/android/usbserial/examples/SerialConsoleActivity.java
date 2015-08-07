/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.hoho.android.usbserial.examples;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import src.com.hoho.android.usbserial.examples.AndroidArduinoInterface;

/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

    private final String TAG = SerialConsoleActivity.class.getSimpleName();

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     *
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    private static UsbSerialPort sPort = null;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private final AndroidArduinoInterface arduino = new AndroidArduinoInterface(sPort);

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SerialConsoleActivity.this.updateReceivedData(data);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_console);
        mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        Button SPO2 = (Button) findViewById(R.id.SPO2);
        Button RedLedOn = (Button) findViewById(R.id.RedLedOn);
        Button UVLedOn = (Button) findViewById(R.id.UVLedOn);
        Button IRLedOn = (Button) findViewById(R.id.IRLedOn);
        Button RedLedOff = (Button) findViewById(R.id.RedLedOff);
        Button UVLedOff = (Button) findViewById(R.id.UVLedOff);
        Button IRLedOff = (Button) findViewById(R.id.IRLedOff);
        Button Test = (Button) findViewById(R.id.Test);
        Button StopADC = (Button) findViewById(R.id.StopADC);

        StopADC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Stop ADC message");
                arduino.stopADC();
            }
        });

        Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Test the IR PWM message");
                String message = "100,100,200,300,400";
                arduino.sendIR(message);
            }
        });

        RedLedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Turn Red LED on");
                byte buffer =53;
                arduino.startLed(buffer);
            }
        });
        RedLedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Turn Red LED off");
                byte buffer = 53;
                arduino.stopLED(buffer);
            }
        });

        IRLedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Turn IR LED on");
                byte buffer =51;
                arduino.startLed(buffer);
            }
        });
        IRLedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Turn IR LED off");
                byte buffer = 51;
                arduino.stopLED(buffer);
            }
        });

        UVLedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Turn UV LED on");
                byte buffer =52;
                arduino.startLed(buffer);
            }
        });
        UVLedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "Turn UV LED off");
                byte buffer = 52;
                arduino.stopLED(buffer);
            }
        });





        SPO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("activity", "SP02");
                arduino.startSPO2();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                sPort.setDTR(true);

            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        String message = new String(data);
        //final String message = "Read " + data.length + " bytes: \n" + HexDump.dumpHexString(data) + "\n\n";
        //mDumpTextView.append(message);
        mDumpTextView.append(message + "\n");
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
        if(message.contains("complete")){
            if(!arduino.getQueue().isEmpty()){
                arduino.sendIRPulse();
            }
        }
    }

    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     * @param driver
     */
    static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

}
