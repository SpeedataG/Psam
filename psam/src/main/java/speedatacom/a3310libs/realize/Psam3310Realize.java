package speedatacom.a3310libs.realize;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.widget.Toast;

import com.speedata.libutils.DataConversionUtils;
import com.speedata.libutils.MyLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import speedatacom.a3310libs.inf.IPsam;

/**
 * Created by suntianwei on 2017/1/19.
 */

public class Psam3310Realize implements IPsam {
    private SerialPort mSerialPort;
    private DeviceControl mDeviceControl;
    private MyLogger logger = MyLogger.jLog();
    private int fd;

    public static boolean isPowering = false;

    /**
     * psam软上电
     *
     * @param type
     * @return
     */
    @Override
    public boolean PsamPower(PowerType type) {
        isPowering = true;
        if (mSerialPort == null) {
            return false;
        }
        SystemClock.sleep(500);
        mSerialPort.WriteSerialByte(fd, getPowerCmd(type));
        logger.d("===isPowering=" + isPowering);
        SystemClock.sleep(50);
        try {
            byte[] bytes = mSerialPort.ReadSerial(fd, 256);
//            int count = 0;
//            while (bytes == null && count < 5) {
//                bytes = mSerialPort.ReadSerial(fd, 256);
//                count++;
//            }
            android.os.Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isPowering = false;
                }
            }, 1500);
            //长度小于10或者串口无数据返回  上电失败
            if (bytes == null || bytes.length <= 10)
                return false;
            else return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void initDev(String serialport, int braut, DeviceControl.PowerType power_typeint,
                        Context context, int... gpio) {
        mSerialPort = new SerialPort();
        try {
            mSerialPort.OpenSerial("/dev/" + serialport, braut);
            fd = mSerialPort.getFd();
            logger.d("--onCreate--open-serial=" + fd);
        } catch (IOException e) {
            Toast.makeText(context, "The serial port is not found, forced exit！", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
            System.exit(0);
        } catch (SecurityException e) {
            Toast.makeText(context, "No serial port authority, forced exit!", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
            System.exit(0);
        }
        try {
            mDeviceControl = new DeviceControl(power_typeint, gpio);
            mDeviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
            mDeviceControl = null;
            e.printStackTrace();
        }
    }

    @Override
    public byte[] receData(int len) {
        try {
            byte[] data = mSerialPort.ReadSerial(fd, len);
            if (data != null)
                return parsePackage(data);
            else return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int sendData(byte[] data, PowerType type) {
        return mSerialPort.WriteSerialByte(fd, adpuPackage(data, type));
    }

    private android.os.Handler handler;
    private ReadThread readThread;

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
//                SystemClock.sleep(100);
                try {
                    if (!isPowering) {
                        logger.d("===isPowering=" + isPowering + this);
                        byte[] bytes = mSerialPort.ReadSerial(fd, 1024);
                        logger.d("===thread read="+this);
                        if (bytes != null) {
                            byte[] data = parsePackage(bytes);
                            Message msg = new Message();
                            msg.obj = data;
                            handler.sendMessage(msg);
                        }

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void startReadThread(Handler handler) {
        this.handler = handler;
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
        }
        readThread = new ReadThread();
        readThread.start();
    }

    @Override
    public void stopReadThread() {
        if (readThread != null) {
            readThread.interrupt();
            readThread = null;
            logger.d("===thread==stop");
        }
    }

    @Override
    public void releaseDev() throws IOException {
        mSerialPort.CloseSerial(fd);
        mDeviceControl.PowerOffDevice();
    }

    /**
     * 打包指令
     *
     * @param cmd
     * @param type
     * @return
     */
    private byte[] adpuPackage(byte[] cmd, PowerType type) {
        byte[] result = new byte[cmd.length + 9];
        result[0] = (byte) 0xaa;
        result[1] = (byte) 0xbb;
        result[2] = (byte) (cmd.length + 5);
        result[3] = 0x00;
        result[4] = 0x00;
        result[5] = 0x00;
        switch (type) {
            case Psam1:
                result[6] = 0x13;
                break;
            case Psam2:
                result[6] = 0x23;
                break;
        }
        result[7] = 0x06;
        result[result.length - 1] = 0x51;
        System.arraycopy(cmd, 0, result, 8, cmd.length);
        return result;
    }

    /**
     * 拆包
     *
     * @return
     */
    private byte[] parsePackage(byte[] orgin) {
        if (orgin.length < 4)
            return null;
        byte[] byte_len = new byte[2];
        byte_len[0] = orgin[3];
        byte_len[1] = orgin[2];
        int len = DataConversionUtils.byteArrayToInt(byte_len);
        if (len < 6)
            return null;
        byte[] result = new byte[len - 6];
        for (int i = 0; i < len - 6; i++) {
            result[i] = orgin[i + 9];
        }
        return result;
    }

    public byte[] getPowerCmd(PowerType type) {
        //IC卡复位3V
        //aabb05000000110651
        //IC卡复位5V
        //aabb05000000120651
        byte[] cmd = new byte[]{(byte) 0xaa, (byte) 0xbb, 0x05, 0x00, 0x00, 0x00, 0x11, (byte)
                0x06, 0x51};
        switch (type) {
            case Psam1:
                cmd[6] = 0x11;
                break;
            case Psam2:
                cmd[6] = 0x21;
                break;
        }
        return cmd;
    }
}
