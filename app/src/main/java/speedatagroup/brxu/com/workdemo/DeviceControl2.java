package speedatagroup.brxu.com.workdemo;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 上电操作
 */
class DeviceControl2 {
    //   /sys/class/misc/mtgpio/pin
    //    /sys/class/misc/aw9524/gpio

    public int resetGPIO = 98;

    //44 复位管脚
    public void PsamResetDevice() throws IOException        //reset psam device
    {
        //变成输出
        WriteFile("/sys/class/misc/mtgpio/pin", "-wdir" + resetGPIO + " 1");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WriteFile("/sys/class/misc/mtgpio/pin", "-wdout" + resetGPIO + " 0");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WriteFile("/sys/class/misc/mtgpio/pin", "-wdout" + resetGPIO + " 1");
    }

    void PowerOnDevice() {
        try {
            WriteFile("/sys/class/misc/mtgpio/pin", "-wdout 94 1");
            Log.i("lei", "-----------PowerOnDevice-----------");
            WriteFile("/sys/class/misc/mtgpio/pin", "-wdout 96 1");
            // 给模块上电时间
            Thread.sleep(220);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void ExpandPowerOn(int gpio) {
        try {
            WriteFile("/sys/class/misc/aw9524/gpio", gpio + "on");
            Log.i("lei", "-----------ExpandPowerOn-----------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void ExpandPowerOff(int gpio) {
        try {
            WriteFile("/sys/class/misc/aw9524/gpio", gpio + "off");
            Log.i("lei", "-----------ExpandPowerOff-----------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void PowerOffDevice() {
        try {
            WriteFile("/sys/class/misc/mtgpio/pin", "-wdout94 0");
            //WriteFile("/sys/class/misc/mtgpio/pin", "-wdout96 0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteFile(String path, String value) throws IOException {
        File DeviceName = new File(path);
        if (DeviceName.exists()) {
            BufferedWriter CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
            CtrlFile.write(value);
            CtrlFile.flush();
            CtrlFile.close();
        }
    }
}