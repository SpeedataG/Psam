package speedatacom.a3310libs.inf;

import android.content.Context;
import android.serialport.DeviceControl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by suntianwei on 2017/1/19.
 */

public interface IPsam {
    public enum PowerType {
        Psam1, Psam2
    }

    /**
     * 软上电  psam1 上电 psam2 上电
     *
     * @param type
     * @return
     */
    public byte[] PsamPower(PowerType type);


    /**
     * 初始化设备
     *
     * @param serialport    串口
     * @param braut         波特率
     * @param power_typeint 上电类型 主板 或外部 或主板和外部
     * @param gpio
     */
    public void initDev(String serialport, int braut, DeviceControl.PowerType power_typeint,
                        Context context, int... gpio) throws IOException;
    //自动判断上电
    public void initDev(Context context) throws IOException;
    //阻塞型写入
    public byte[] WriteCmd(byte[] data, PowerType type) throws UnsupportedEncodingException;

    //阻塞型写入
    public byte[] WriteCmd(byte[] data, PowerType type,int len,int delay) throws UnsupportedEncodingException;

    //释放设备
    public void releaseDev() throws IOException;

    public void resetDev(DeviceControl.PowerType type,int Gpio);
    public void resetDev();
    public byte[] WriteOriginalCmd(byte[] data, PowerType type) throws UnsupportedEncodingException;
//    /**
//     * 单次读串口
//     *
//     * @param len 最大长度
//     * @return 返回串口数据
//     */
//    public byte[] receData(int len);
//
//    /**
//     * 发送数据
//     *
//     * @param data
//     * @return
//     */
//    public int sendData(byte[] data, PowerType type);
//
//    /**
//     * 开启读线程
//     */
//    public void startReadThread(Handler handler);
//
//    /**
//     * 停止读线程
//     */
//    public void stopReadThread();



}
