package speedatacom.a3310libs.realize;

import android.content.Context;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;

import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.MyLogger;
import com.speedata.libutils.ReadBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import speedatacom.a3310libs.inf.IPsam;

/**
 * Created by suntianwei on 2017/1/19.
 */

public class Psam3310Realize implements IPsam {
    private SerialPort mSerialPort;
    private DeviceControl mDeviceControl;
    private MyLogger logger = MyLogger.jLog();
    private int fd;
    private Context mContext;
    //    private boolean isPower = false;
    PowerType type;

    /**
     * psam软上电
     *
     * @param type
     * @return
     */
    @Override
    public byte[] PsamPower(PowerType type) {
        this.type = type;
        if (mSerialPort == null) {
            return null;
        }
        try {
            return WriteCmd(getPowerCmd(type));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initDev(String serialport, int braut, DeviceControl.PowerType power_typeint,
                        Context context, int... gpio) throws IOException {
        mContext = context;
        mSerialPort = new SerialPort();
        mSerialPort.OpenSerial("/dev/" + serialport, braut);
        fd = mSerialPort.getFd();
        logger.d("--onCreate--open-serial=" + fd);
        mDeviceControl = new DeviceControl(power_typeint, gpio);
        mDeviceControl.PowerOnDevice();
    }

    private int resetGpio = 1;
    private DeviceControl.PowerType power_type = DeviceControl.PowerType.MAIN;

    @Override
    public void initDev(Context context) throws IOException {

        ReadBean readBean = ConfigUtils.readConfig(context);
        if (readBean == null) {
            throw new IOException();
        } else {
            mSerialPort = new SerialPort();
//        try {
            ReadBean.PasmBean pasm = readBean.getPasm();
            mSerialPort.OpenSerial(pasm.getSerialPort(), pasm.getBraut());
            resetGpio = pasm.getResetGpio();
            fd = mSerialPort.getFd();
            logger.d("--onCreate--open-serial=" + fd);
            String type = pasm.getPowerType();

            switch (type) {
                case "MAIN":
                    power_type = DeviceControl.PowerType.MAIN;
                    break;
                case "MAIN_AND_EXPAND":
                    power_type = DeviceControl.PowerType.MAIN_AND_EXPAND;
                    break;
                case "EXPAND":
                    power_type = DeviceControl.PowerType.EXPAND;
                    break;
                default:
                    power_type = DeviceControl.PowerType.MAIN;
                    break;
            }
            List<Integer> gpio = pasm.getGpio();
            int[] gpios = new int[gpio.size()];
            for (int i = 0; i < gpio.size(); i++) {
                gpios[i] = gpio.get(i);
            }
            mDeviceControl = new DeviceControl(power_type, gpios);
            mDeviceControl.PowerOnDevice();
        }
    }

    /**
     * @param data 写入指令
     * @param type 卡类型
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public byte[] WriteCmd(byte[] data, PowerType type) throws
            UnsupportedEncodingException {
        return WriteCmd(adpuPackages(data, type));
//        mSerialPort.WriteSerialByte(fd, );
//        byte[] read = null;
//        long currentTime = System.currentTimeMillis();
//        int count = 0;
//        while (read == null && count < 10) {
//            count++;
//            SystemClock.sleep(5);
//            read = mSerialPort.ReadSerial(fd, 50);
//        }
//        if (read != null)
//            read = unPackage(read);
//        return read;
    }
    //此指令大概15ms左右
    private byte[] WriteCmd(byte[] data) throws
            UnsupportedEncodingException {
        mSerialPort.WriteSerialByte(fd, data);
        byte[] read = null;
        long currentTime = System.currentTimeMillis();
        int count = 0;
        while (read == null && count < 10) {
            count++;
            SystemClock.sleep(5);
            read = mSerialPort.ReadSerial(fd, 20);
        }
        if (read != null)
            read = unPackage(read);
        return read;
    }
//
//    @Override
//    public byte[] receData(int len) {
//        try {
//            byte[] data = mSerialPort.ReadSerial(fd, len);
//            if (data != null)
//                return parsePackage(data);
//            else return null;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @Override
//    public int sendData(byte[] data, PowerType type) {
//        return mSerialPort.WriteSerialByte(fd, adpuPackage(data, type));
//    }

//    private android.os.Handler handler;
//    private ReadThread readThread;
//
//    private class ReadThread extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            while (!isInterrupted()) {
//                try {
//                    //aa bb 06 00 00 00 11 06 14 17
//                    byte[] bytes = mSerialPort.ReadSerial(fd, 1024);
//                    logger.d("===thread read=" + this);
//                    if (bytes != null) {
//                        if (isPower && bytes.length > 10) {
//                            sendPowerResult(type, true);
//                        } else if (isPower && bytes.length <= 10) {
//                            sendPowerResult(type, false);
//                        } else {
//                            byte[] data = parsePackage(bytes);
//                            Message msg = new Message();
//                            msg.obj = data;
//                            handler.sendMessage(msg);
//                        }
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public static String POWER_ACTION = "Power";
//    public static String POWER_RESULT = "result";
//    public static String POWER_TYPE = "type";

//    private void sendPowerResult(PowerType type, boolean result) {
//
//        Intent intent = new Intent();
//        intent.setAction(POWER_ACTION);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(POWER_RESULT, result);
//        if (type.equals(PowerType.Psam1))
//            bundle.putInt(POWER_TYPE, 1);
//        else
//            bundle.putInt(POWER_TYPE, 2);
//        intent.putExtras(bundle);
//        mContext.sendBroadcast(intent);
//    }

//    @Override
//    public void startReadThread(Handler handler) {
//        this.handler = handler;
//        if (readThread != null) {
//            readThread.interrupt();
//            readThread = null;
//        }
//        readThread = new ReadThread();
//        readThread.start();
//    }
//
//    @Override
//    public void stopReadThread() {
//        if (readThread != null) {
//            readThread.interrupt();
//            readThread = null;
//            logger.d("===thread==stop");
//        }
//    }

    @Override
    public void releaseDev() throws IOException {
        mSerialPort.CloseSerial(fd);
        mDeviceControl.PowerOffDevice();
    }

    @Override
    public void resetDev(DeviceControl.PowerType type, int Gpio) {
        DeviceControl mDeviceControl = null;
        try {
            mDeviceControl = new DeviceControl(type, Gpio);
            mDeviceControl.PowerOnDevice();
            mDeviceControl.PowerOffDevice();
            mDeviceControl.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetDev() {
        resetDev(power_type, resetGpio);
    }

//    /**
//     * 打包指令
//     *
//     * @param cmd
//     * @param type
//     * @return
//     */
//    private byte[] adpuPackage(byte[] cmd, PowerType type) {
//        byte[] result = new byte[cmd.length + 9];
//        result[0] = (byte) 0xaa;
//        result[1] = (byte) 0xbb;
//        result[2] = (byte) (cmd.length + 5);
//        result[3] = 0x00;
//        result[4] = 0x00;
//        result[5] = 0x00;
//        switch (type) {
//            case Psam1:
//                result[6] = 0x13;
//                break;
//            case Psam2:
//                result[6] = 0x23;
//                break;
//        }
//        result[7] = 0x06;
//        result[result.length - 1] = 0x51;
//        System.arraycopy(cmd, 0, result, 8, cmd.length);
//        return result;
//    }
    /**
     * @param cmd  adpu指令
     * @param type 0x13卡1 0x23卡2
     * @return 3310格式指令
     */
    public static byte[] adpuPackages(byte[] cmd,  PowerType type) {

        int addCount = 0;
        for (int j = 0; j < cmd.length; j++) {
            if (cmd[j] == (byte) 0xaa) {
                addCount++;
            }
        }
        byte[] result = new byte[cmd.length + 9 + addCount];
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
        int startCount = 8;
        for (int i = 0; i < cmd.length; i++) {
            if (cmd[i] == (byte) 0xaa) {
                result[startCount] = cmd[i];
                result[startCount + 1] = 0x00;
                startCount++;
            } else {
                result[startCount] = cmd[i];
            }
            startCount++;
        }
        //不能这样复制
//        System.arraycopy(cmd, 0, result, 8, cmd.length);
        return result;
    }
    /**
     * 拆包
     *
     * @return
     */
//    private byte[] parsePackage(byte[] orgin) {
//        if (orgin.length < 4)
//            return null;
//        byte[] byte_len = new byte[2];
//        byte_len[0] = orgin[3];
//        byte_len[1] = orgin[2];
//        int len = DataConversionUtils.byteArrayToInt(byte_len);
//        if (len < 6)
//            return null;
//
//        byte[] result = new byte[len - 6];
//        for (int i = 0; i < len - 6; i++) {
//            result[i] = orgin[i + 9];
//        }
//        return result;
//    }

    public static byte[] unPackage(byte[] cmd) {// 解包

        if (cmd == null || cmd.length <= 10) {
            return null;
        }
        if (cmd[0] != (byte) 0xaa || cmd[1] != (byte) 0xbb) {
            return null;
        }
        int subCount = 0;// 记录返回的数据中有几个0xaa
        int startCount = 9;//数据头  9位
        //最后一位为校验位
        byte[] result = new byte[cmd.length - startCount - 1];
        for (int i = 0; i < cmd.length - startCount - 1; i++) {
            int offset = startCount + i;
            System.out.println("offset=" + offset);
            byte data = cmd[offset];
            result[i] = data;
            if (data == (byte) 0xaa) {
                System.out.println("current=" + 0xaa);
                startCount++;
                subCount++;
            }
        }
        int length = result.length - subCount;
        byte[] finalresult = new byte[length];
        System.arraycopy(result, 0, finalresult, 0, length);
        return finalresult;
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