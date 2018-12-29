package speedatacom.a3310libs.inf;

import android.content.Context;
import android.serialport.DeviceControlSpd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by suntianwei on 2017/1/19.
 */

public interface IPsam {
    public enum PowerType {
        Psam1, Psam2, Psam4442On, Psam4442Dwon, ReadPsam4442, WritePsam4442, PwdReadsam44P42, CheckPwdPsam4442, ChangePwdPsam4442
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
    public void initDev(String serialport, int braut, DeviceControlSpd.PowerType power_typeint,
                        Context context, int... gpio) throws IOException;

    /**
     * 初始化串口
     *
     * @param serialport 串口
     * @param braut      波特率
     * @throws IOException
     */
    public void initDev(String serialport, int braut, Context context) throws IOException;

    //自动判断上电
    public void initDev(Context context) throws IOException;

    //阻塞型写入
    public byte[] WriteCmd(byte[] data, PowerType type) throws UnsupportedEncodingException;

    public byte[] WritePsam4442Cmd(byte[] data, PowerType type) throws UnsupportedEncodingException;

    //阻塞型写入
    public byte[] WriteCmd(byte[] data, PowerType type, int len, int delay) throws UnsupportedEncodingException;

    //释放设备
    public void releaseDev() throws IOException;

    public void resetDev(DeviceControlSpd.PowerType type, int gpio);
    public void resetGtDev( String [] gpio);

    public void resetDev();

    public byte[] WriteOriginalCmd(byte[] data, PowerType type) throws UnsupportedEncodingException;



}
