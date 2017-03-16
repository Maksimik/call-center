package com.callcenter.logs;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Logging {

    public static int logInFile(String msg) {
        int result = 0;
        File file = new File("sdcard/logs_callcenter.txt");

        String previousLog = "";

        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                try {

                    BufferedReader br = new BufferedReader(new FileReader(file));

                    String log = "";
                    while ((log = br.readLine()) != null) {
                        if (!log.equals("")) {
                            previousLog = log;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String timeLog = new SimpleDateFormat("dd.MM.yy hh:mm:ss:SSS").format(new Date());
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            String difference = writeTime(timeLog, previousLog);
            if (!difference.equals("")) {
                bw.append("\n").append(writeTime(timeLog, previousLog)).append("\n").append(timeLog).append(" ").append(msg);
            } else {
                bw.append("\n").append(timeLog).append(" ").append(msg);
            }
            bw.close();
            result = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String writeTime(String timeLog, String previousLog) {

        int differenceMillisecond;
        int differenceSecond;
        int differenceMinute;
        int differenceHour;

        if (!previousLog.equals("")) {
            if (previousLog.split(" ").length != 1) {
                String prevTime = previousLog.split(" ")[1];
                int prevHour = Integer.parseInt(prevTime.split(":")[0]);
                int prevMinute = Integer.parseInt(prevTime.split(":")[1]);
                int prevSecond = Integer.parseInt(prevTime.split(":")[2]);
                int prevMillisecond = Integer.parseInt(prevTime.split(":")[3]);

                String currentTime = timeLog.split(" ")[1];
                int currentHour = Integer.parseInt(currentTime.split(":")[0]);
                int currentMinute = Integer.parseInt(currentTime.split(":")[1]);
                int currentSecond = Integer.parseInt(currentTime.split(":")[2]);
                int currentMillisecond = Integer.parseInt(currentTime.split(":")[3]);

                differenceMillisecond = currentMillisecond - prevMillisecond;
                if (differenceMillisecond < 0) {
                    differenceMillisecond += 1000;
                    currentSecond -= 1;
                }

                differenceSecond = currentSecond - prevSecond;
                if (differenceSecond < 0) {
                    differenceSecond += 60;
                    currentMinute -= 1;
                }

                differenceMinute = currentMinute - prevMinute;
                if (differenceMinute < 0) {
                    differenceMinute += 60;
                    currentHour -= 1;
                }

                differenceHour = currentHour - prevHour;
                if (differenceHour < 0) {
                    differenceHour += 24;
                }

            } else {
                return "";
            }
        } else {
            return "";
        }
        String messageForLongTime = "";
        if (differenceHour > 0 || differenceMinute > 0 || differenceSecond > 0 || differenceMillisecond > 500) {
            messageForLongTime = " ===============================";
        }
        return differenceHour + ":" + differenceMinute + ":" + differenceSecond + ":" + differenceMillisecond + messageForLongTime;
    }

}