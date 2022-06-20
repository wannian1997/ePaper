package com.ch.epaper.pyapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunPythonCode {
    public static void main(String[] srg){
        run();
    }
    public static void run(){
        // TODO Auto-generated method stub
        Process proc;
        try {
            // 执行bat文件，需要使用绝对路径
            proc = Runtime.getRuntime().exec("cmd /c D:\\projects_IDEA\\paperStorage\\ePaper\\src\\main\\java\\com\\ch\\epaper\\pyapi\\runPython.bat");

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
