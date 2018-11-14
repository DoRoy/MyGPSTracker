package mygpstracker.android.mygpstracker.Sensors;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mygpstracker.android.mygpstracker.AppExecutors;
import mygpstracker.android.mygpstracker.R;

/**
 * Created by doroy on 29-Aug-18.
 */

public class ActivityTest extends AppCompatActivity {

    public TextView textView;
    public Button button_getData;
    public Button button_alltypes;
    public Button button_getCallLog;

    private final String TAG = "ActivityTest";
    private ISensor sensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ASensorMeasures.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        initializeWidgetsAndListeners();


    }

    private void initializeWidgetsAndListeners(){
        sensor = new SensorContinuous(ASensorMeasures.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        textView = (TextView) findViewById(R.id.textView);

        button_getData = (Button) findViewById(R.id.button_getData);
        button_getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On Button Click");

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Map<String, Double> map = sensor.getData();

                        Log.d(TAG, "After getData()");
                        String temp = "";
                        for (Map.Entry<String, Double> pair : map.entrySet()) {
                            System.out.println(pair.getKey() + ": " + pair.getValue());
                            temp += pair.getKey() + ": " + pair.getValue() + "\n";

                        }
                        String finalTemp = temp;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(finalTemp);
                            }
                        });
                    }
                });

                AppExecutors.getInstance().networkIO().execute(t);
            }
        });

        button_alltypes = (Button) findViewById(R.id.button_alltypes);
        button_alltypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "On AllTypes Button Click");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<ISensor> list = SensorFactory.getAllAvailableSensors(ASensorMeasures.sensorManager);
                        Log.d(TAG, "List length: " + list.size());

                        runOnUiThread(()->textView.setText("All Sensors Data:\n\n"));

                        for (ISensor sensor: list) {
                            String temp = "";
                            Log.d(TAG, "Sensor: " + sensor.getName());
                            Map<String,Double> map = sensor.getData();
                            Log.d(TAG, "After getData()");

                            for (Map.Entry<String, Double> pair : map.entrySet()) {
                                //System.out.println(pair.getKey() + ": " + pair.getValue());
                                temp += pair.getKey() + ": " + pair.getValue() + "\n";
                            }
                            temp += "\n";
                            String finalTemp1 = temp;
                            runOnUiThread(()-> textView.append(finalTemp1));
                        }

                        printSensorStatus(list);
                    }
                });
                AppExecutors.getInstance().networkIO().execute(t);
            }
        });

        button_getCallLog = (Button) findViewById(R.id.button_getCallLog);
        button_getCallLog.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getCallLog();
                    }
                });
                AppExecutors.getInstance().networkIO().execute(t);
            }
        });
    }

    private void printSensorStatus(List<ISensor> list){
        List<Sensor> sensorsList = ASensorMeasures.sensorManager.getSensorList(Sensor.TYPE_ALL);
        String print = "";
        int[] arr = new int[sensorsList.size()];
        int counter = 0;
        for (ISensor iSensor: list) {
            for(int i = 0; i < sensorsList.size();i ++) {
                String iSensorName = ((ASensorMeasures)iSensor).sensorName.substring(0,((ASensorMeasures)iSensor).sensorName.length()-1);
                if(iSensorName.equals(sensorsList.get(i).getName())){
                    print += "+ " + sensorsList.get(i).getName() + " - " + sensorsList.get(i).getReportingMode() + "\n";
                    arr[i] = 30;
                    counter++;
                }
            }
        }
        for(int i = 0; i < sensorsList.size();i ++){
            if(arr[i] != 30) {
                print += "- " + sensorsList.get(i).getName() + " - " + sensorsList.get(i).getReportingMode() + "\n";
            }
        }
        System.out.println(print);
        System.out.println(counter + "/" + sensorsList.size() + " Covered");
        String finalPrint = counter + "/" + sensorsList.size() + " Covered";
        runOnUiThread(()-> Toast.makeText(this, finalPrint, Toast.LENGTH_LONG).show());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getCallLog(){
        Log.d(TAG, "getCallLog");
        CallLogInformation callLogInformation = new CallLogInformation(this);
        List<String[]> callLogList = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("---- No Permissions ----");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this ,Manifest.permission.READ_CALL_LOG)){

            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG},0);
                callLogList = callLogInformation.query();

            }
        }
        else{
            callLogList = callLogInformation.query();
        }
        runOnUiThread(()->textView.setText("Call Log:"));
        for(String[] record: callLogList){
            runOnUiThread(()->{
                textView.append("\nPhone Number:  " + record[0] + " \nCall Type:  " + record[1] + " \nCall Date:  " + record[2] + " \nCall duration in sec :  " + record[3] + "\n----------------------------------");
            });
        }


    }
}
