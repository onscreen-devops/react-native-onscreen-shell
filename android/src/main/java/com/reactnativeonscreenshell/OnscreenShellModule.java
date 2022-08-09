package com.reactnativeonscreenshell;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;


@ReactModule(name = OnscreenShellModule.NAME)
public class OnscreenShellModule extends ReactContextBaseJavaModule {
    public static final String NAME = "OnscreenShell";

    public OnscreenShellModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    public void multiply(double a, double b, Promise promise) {
        promise.resolve(a * b);
    }

    @ReactMethod
    public void executeCommand(final String command, final Callback callback) {
    // To avoid UI freezes run in thread 
        new Thread(new Runnable() { 
            public void run() { 
                OutputStream out = null; 
                InputStream in = null; 
                try { 
                    // Send script into runtime process 
                    Process child = Runtime.getRuntime().exec(command);
                    // Get input and output streams 
                    out = child.getOutputStream(); 
                    in = child.getInputStream();
                    // Input stream can return anything
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in)); 
                    String line; 
                    String result = ""; 
                    while ((line = bufferedReader.readLine()) != null)
                    result += line+"\n";
                    // Handle input stream returned message
                    callback.invoke(result); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } finally { 
                    if (in != null) { 
                        try { 
                            in.close(); 
                        } catch (IOException e) { 
                            e.printStackTrace(); 
                        } 
                    } 
                    if (out != null) { 
                        try { 
                            out.flush(); 
                            out.close(); 
                        } catch (IOException e) { 
                            e.printStackTrace(); 
                        } 
                    } 
                } 
            } 
        }).start(); 
    }

}
