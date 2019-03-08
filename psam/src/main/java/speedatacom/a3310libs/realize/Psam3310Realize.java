package speedatacom.a3310libs.realize;

import android.content.Context;
import android.os.SystemClock;
import android.serialport.DeviceControlSpd;
import android.serialport.SerialPortSpd;
import android.util.Log;

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
    //    private boolean isPower = false;
    PowerType type;
    int len = 1024;
    int delay = 10;
    private SerialPortSpd mSerialPort;
    private DeviceControlSpd mDeviceControlSpd;
    private MyLogger logger = MyLogger.jLog();
    private int fd;
    private boolean fla = true;
    private Context mContext;
    private int resetGpio = 1;
    private DeviceControlSpd.PowerType power_type = DeviceControlSpd.PowerType.MAIN;
    private DeviceControlSpd mDeviceReset = null;

    /**
     * @param cmd  adpu指令
     * @param type 0x13卡1 0x23卡2
     * @return 3310格式指令
     */
    public static byte[] adpuPackages(byte[] cmd, PowerType type) {

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
            default:
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
            return WriteCmdWithoutUnpackage(getPowerCmd(type), 50, 15);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initDev(String serialport, int braut, DeviceControlSpd.PowerType power_typeint,
                        Context context, int... gpio) throws IOException {
        mContext = context;
        mSerialPort = new SerialPortSpd();
        mSerialPort.OpenSerial("/dev/" + serialport, braut);
        fd = mSerialPort.getFd();
        logger.d("--onCreate--open-serial=" + fd);
        mDeviceControlSpd = new DeviceControlSpd(power_typeint, gpio);
        mDeviceControlSpd.PowerOnDevice();
    }

    @Override
    public void initDev(String serialport, int braut, Context context) throws IOException {
        mContext = context;
        mSerialPort = new SerialPortSpd();
        mSerialPort.OpenSerial("/dev/" + serialport, braut);
        fd = mSerialPort.getFd();
        logger.d("--onCreate--open-serial=" + fd);
        fla = false;
    }

    @Override
    public void initDev(Context context) throws IOException {

        ReadBean readBean = ConfigUtils.readConfig(context);
        if (readBean == null) {
            throw new IOException();
        } else {
            mSerialPort = new SerialPortSpd();
            //        try {
            ReadBean.PasmBean pasm = readBean.getPasm();
            mSerialPort.OpenSerial(pasm.getSerialPort(), pasm.getBraut());
            resetGpio = pasm.getResetGpio();
            fd = mSerialPort.getFd();
            logger.d("--onCreate--open-serial=" + fd);
            String type = pasm.getPowerType();

            switch (type) {
                case "MAIN":
                    power_type = DeviceControlSpd.PowerType.MAIN;
                    break;
                case "MAIN_AND_EXPAND":
                    power_type = DeviceControlSpd.PowerType.MAIN_AND_EXPAND;
                    break;
                case "EXPAND":
                    power_type = DeviceControlSpd.PowerType.EXPAND;
                    break;
                case "NEW_MAIN":
                    this.power_type = DeviceControlSpd.PowerType.NEW_MAIN;
                    break;
                case "EXPAND2":
                    this.power_type = DeviceControlSpd.PowerType.EXPAND2;
                    break;
                case "MAIN_AND_EXPAND2":
                    this.power_type = DeviceControlSpd.PowerType.MAIN_AND_EXPAND2;
                    break;
                case "GAOTONG_MAIN":
                    this.power_type = DeviceControlSpd.PowerType.GAOTONG_MAIN;
                    List<Integer> gtGpio = pasm.getGpio();
                    String[] gpios = new String[gtGpio.size()];
                    for (int i = 0; i < gtGpio.size(); i++) {
                        gpios[i] = gtGpio.get(i) + "";
                    }
                    mDeviceControlSpd = new DeviceControlSpd(DeviceControlSpd.POWER_GAOTONG, gpios);
                    mDeviceControlSpd.PowerOnDevice();
                    return;
                default:
                    power_type = DeviceControlSpd.PowerType.MAIN;
                    break;
            }
            List<Integer> gpio = pasm.getGpio();
            int[] gpios = new int[gpio.size()];
            for (int i = 0; i < gpio.size(); i++) {
                gpios[i] = gpio.get(i);
            }
            mDeviceControlSpd = new DeviceControlSpd(power_type, gpios);
            mDeviceControlSpd.PowerOnDevice();
        }
    }

    /**
     * 获取type
     *
     * @return
     */
    public DeviceControlSpd.PowerType getPowerType() {
        return power_type;
    }

    /**
     * 此指令大概20ms左右
     *
     * @param data 写入指令
     * @param type 卡类型
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public byte[] WriteCmd(byte[] data, PowerType type) throws
            UnsupportedEncodingException {
        return WriteCmd(adpuPackages(data, type), len, delay);
    }

    @Override
    public byte[] WritePsam4442Cmd(byte[] data, PowerType type) throws UnsupportedEncodingException {
        return WriteCmd(psam4442Packages(data, type), len, delay);
    }

    @Override
    public byte[] WriteCmd(byte[] data, PowerType type, int len, int delay) throws
            UnsupportedEncodingException {
        return WriteCmd(adpuPackages(data, type), len, delay);
    }

    private byte[] WriteCmd(byte[] data, int len, int delay) throws
            UnsupportedEncodingException {
        mSerialPort.WriteSerialByte(fd, data);
        byte[] read = null;
        long currentTime = System.currentTimeMillis();
        int count = 0;
        while (read == null && count < 10) {
            count++;
            SystemClock.sleep(5);
            read = mSerialPort.ReadSerial(fd, len, delay);
        }
        if (read != null) {
            read = unPackage(read);
        }
        Log.d("sssss", "readCmd: " + count);
        return read;
    }

    private byte[] WriteCmdWithoutUnpackage(byte[] data, int len, int delay) throws
            UnsupportedEncodingException {
        mSerialPort.WriteSerialByte(fd, data);
        byte[] read = null;
        long currentTime = System.currentTimeMillis();
        int count = 0;
        while (read == null && count < 10) {
            count++;
            SystemClock.sleep(5);
            read = mSerialPort.ReadSerial(fd, len, delay);
        }

        return read;
    }

    @Override
    public void releaseDev() throws IOException {
        mSerialPort.CloseSerial(fd);
        if (fla) {
            if (mDeviceControlSpd != null) {
                mDeviceControlSpd.PowerOffDevice();
            }
        }
        if (mDeviceReset != null) {
            mDeviceReset.PowerOffDevice();
        }
    }

    @Override
    public void resetDev(DeviceControlSpd.PowerType type, int Gpio) {

        try {

            mDeviceReset = new DeviceControlSpd(type, Gpio);
            mDeviceReset.PowerOnDevice();
            mDeviceReset.PowerOffDevice();
            mDeviceReset.PowerOnDevice();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void resetGtDev(String[] gpio) {
        mDeviceControlSpd.gtPower(gpio[0]);
        mDeviceControlSpd.gtPower(gpio[1]);
        mDeviceControlSpd.gtPower(gpio[0]);
    }

    @Override
    public void resetDev() {
        if (power_type.equals(DeviceControlSpd.PowerType.MAIN_AND_EXPAND)) {
            resetDev(DeviceControlSpd.PowerType.EXPAND, resetGpio);
        } else {
            resetDev(power_type, resetGpio);
        }
    }

    @Override
    public byte[] WriteOriginalCmd(byte[] data, PowerType type) throws UnsupportedEncodingException {
        return WriteCmd(data, len, delay);
    }

    /**
     * psam4442类型卡片
     *
     * @param cmd
     * @param type
     * @return 返回不同结果
     */
    private byte[] psam4442Packages(byte[] cmd, PowerType type) {
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
            case ReadPsam4442:
                result[6] = 0x33;
                break;
            case WritePsam4442:
                result[6] = 0x34;
                break;
            case PwdReadsam44P42:
                result[6] = 0x36;
                break;
            case CheckPwdPsam4442:
                result[6] = 0x37;
                break;
            case ChangePwdPsam4442:
                result[6] = 0x38;
                break;
            default:
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
            case Psam4442On:
                cmd[6] = 0x31;
                break;
            default:

                break;

        }
        return cmd;
    }
}