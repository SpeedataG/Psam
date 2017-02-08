package speedatacom.a3310libs.utils.realize;

import android.content.Context;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.widget.Toast;

import java.io.IOException;

import speedatacom.a3310libs.utils.inf.IPsam;
import speedatacom.a3310libs.utils.utils.MyLogger;

/**
 * Created by suntianwei on 2017/1/19.
 */

public abstract class Psam3310Realize implements IPsam {
    private SerialPort mSerialPort;
    private DeviceControl mDeviceControl;
    private MyLogger logger = MyLogger.jLog();
    private int fd;

    @Override
    public boolean PsamPower(PowerType type) {
        mSerialPort.WriteSerialByte(fd, getPowerCmd(type));
        return false;
    }

    @Override
    public void DevicePower(String serialport, int braut, DeviceControl.PowerType power_typeint, Context context, int... gpio) {
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
    public int sendData(byte[] data) {
        mSerialPort.WriteSerialByte(fd, data);

        return 0;
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
