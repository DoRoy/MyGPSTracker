package mygpstracker.android.mygpstracker;


import android.os.Bundle;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Intent;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by doroy on 18-Jul-18.
 */

class Log {

    private static Log ourInstance;



    public void setFile(File file) {
        this.file = file;
    }

    private File file;

    static Log getInstance() {
        if(ourInstance == null)
            ourInstance = new Log();
        return ourInstance;
    }

    private Log() {
/*        file = new File(fileName);
        if (!file.exists()) {

            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public boolean write(String line){
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file,true));
            //fileWriter.flush();
            fileWriter.write(line);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String read(){
        String ans = "";
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String temp = null;
            temp = fileReader.readLine();
            while(temp != null){
                ans += temp +"\n";
                temp = fileReader.readLine();
            }
            System.out.println("read  - "+ ans);
            fileReader.close();
            return ans;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}

