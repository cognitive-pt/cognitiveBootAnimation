package pt.codeforce.cognitivebootanimation;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_context = this.getApplicationContext();
        System.out.println("-----------------------------------------App Starting");
        System.out.println("getExternalStorageDirectory:" + Environment.getExternalStorageDirectory().getAbsolutePath());
        copyAssets();


        Process suProcess;
        DataOutputStream os;


        try{
            //Get Root
            suProcess = Runtime.getRuntime().exec("su");
            os= new DataOutputStream(suProcess.getOutputStream());

            //Remount writable FS within the root process
            os.writeBytes("mount -o remount,rw /system\n");
            os.flush();
            String command = "cp " + getExternalFilesDir(null)+ "/bootanimation.zip" + " /system/media/bootanimation.zip\n";
            System.out.println("command:" + command);
            os.writeBytes(command);
            os.flush();

            os.writeBytes("mount -o remount,ro /system\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("getExternalStorageDirectory:" + Environment.getExternalStorageDirectory().getAbsolutePath());

        setContentView(R.layout.activity_main);
        this.finish();
    }




    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            if (filename.equals("bootanimation.zip")){


            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}
