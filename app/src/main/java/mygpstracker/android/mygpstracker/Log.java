package mygpstracker.android.mygpstracker;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by doroy on 18-Jul-18.
 */

class Log {

    private static final Log ourInstance = new Log();

    private String fileName = "myLogFile.csv";
    private File file;

    static Log getInstance() {
        return ourInstance;
    }

    private Log() {
        file = new File(fileName);
        if (!file.exists()) {

            try {
                file.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean write(String line){
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.flush();
            fileWriter.write(line);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String read(){
        String ans;

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            ans = fileReader.readLine();
            fileReader.close();
            return ans;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}

