package mygpstracker.android.mygpstracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by doroy on 18-Jul-18.
 * The location Log, saving the location and the date on a file.
 * implemented as Singleton.
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
    }

    /**
     * Write a string to the log file
     * @param line - The line that will be written to the log.
     * @return - True if wrote the line, False if the were a problem and it didn't.
     */
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
            fileWriter.write(line);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Read the entire Log file content.
     * @return - A String containing all the log content.
     */
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
            //System.out.println("read  - "+ ans);
            fileReader.close();
            return ans;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}

