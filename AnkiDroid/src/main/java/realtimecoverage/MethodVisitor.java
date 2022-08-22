package realtimecoverage;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MethodVisitor {
    public static BlockingQueue<String> visitedMethods;

    static {
        visitedMethods = new LinkedBlockingQueue<String>();
    }

    public static void visit(String className, String methodName) {
        try {
            visitedMethods.put(className + "/" + methodName);
//            if(methodName.equals("onOptionsItemSelected")){
//                Log.i("Themis", className + "/" + methodName);
//            }
        } catch (Exception e) {
//            Log.e(Utils.LOG_TAG, e.getMessage());
            Log.e("Error: ", e.getMessage());
        }
    }

    public static void visitFinish(String className, String methodName) {
        try {
            visitedMethods.put("[" + className + "/" + methodName + "]");
        } catch (Exception e) {
//            Log.e(Utils.LOG_TAG, e.getMessage());
            Log.e("Error: ", e.getMessage());
        }
    }

    public static void tearDown() throws InterruptedException {
        Log.i("hahaha", "dengdai");
        Thread.sleep(5000);
    }
}
