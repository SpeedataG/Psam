package speedatacom.a3310libs.utils.inf;

import android.content.Context;
import android.serialport.DeviceControl;

/**
 * Created by suntianwei on 2017/1/19.
 */

public interface IPsam {
    enum PowerType{
        Psam1,Psam2
    }

    /**
     *  软上电  psam1 上电 psam2 上电
     * @param type
     * @return
     */
    public boolean PsamPower(PowerType type);

    /**
     * 设备上电
     * @param serialport  串口
     * @param braut  波特率
     * @param power_typeint 上电类型 主板 或外部 或主板和外部
     * @param gpio
     */
    public  void DevicePower(String serialport, int braut, DeviceControl.PowerType power_typeint, Context context, int ...  gpio );
    /**
     *收到数据处理
     * @param data
     */
    public abstract void receData(byte[] data);

    /**
     *发送原始数据
     * @param data
     * @return
     */
    public int sendData(byte[] data);








//    public byte[] adpuPackage(byte[] cmd,int pasm);
//    public  byte[] getRandom(int pasm);


}
