//package speedatagroup.brxu.com.workdemo;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//import android.serialport.DeviceControl;
//import android.serialport.SerialPort;
//import android.text.method.ScrollingMovementMethod;
//import android.view.View;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import speedatagroup.brxu.com.workdemo.utils.DataConversionUtils;
//import speedatagroup.brxu.com.workdemo.utils.MyLogger;
//
//public class MainActivityback extends Activity implements View.OnClickListener {
//
//    //19200 9600
//    private Button btn1Activite, btn2Activite, btnGetRomdan, btnSendAdpu, btnClear;
//    private Spinner spinnerSerialport;
//    private Spinner spinnerGpio;
//    private Spinner spinner;
//    private EditText edvADPU;
//    private TextView tvShowData;
//    private SerialPort mSerialPort;
//    private int fd;
//    private int psamflag = 0;
//    private speedatagroup.brxu.com.workdemo.DeviceControl mDeviceControl;
//    private MyLogger logger = MyLogger.jLog();
//    private Context mContext;
//    private Timer timer;
//    private String gpio;
//    private Button btnReSet;
//    private Button btnPower;
//    private TextView tvVerson;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        super.onCreate(savedInstanceState);
//        mContext = this;
//        try {
//            mDeviceControl = new DeviceControl("sys/class/misc/mtgpio/pin");
//        } catch (IOException e) {
//            e.printStackTrace();
//            mDeviceControl = null;
//            return;
//        }
//        mDeviceControl.PowerOnDevice();
//        sharedPreferences = getSharedPreferences("rember", MODE_PRIVATE);
//        initUI();
//        baurate = sharedPreferences.getInt("baurate", 0);
//        serialport = sharedPreferences.getString("serialport", serialport);
//
///*
//        if (sharedPreferences.getString("gpio", "").equals("GPIO14（KT45）")) {
//            gpio = "14";
//        } else if (sharedPreferences.getString("gpio", "").equals("GPIO96（KT45Q）")) {
//            gpio = "96";
//        } else if (sharedPreferences.getString("gpio", "").equals("GPIO7（KT40Q）")) {
//            gpio = "7";
//        } else if (sharedPreferences.getString("gpio", "").equals("GPIO48（KT40）")) {
//            gpio = "48";
//        }*/
//        int position = sharedPreferences.getInt("selectionGpio", 0);
//        setGpio(position);
//        initDevice();
//
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String verson = getVersion();
//                tvVerson.setText("V" + verson);
//            }
//        });
////        byte[] cmd = DataConversionUtils.hexStringToByteArray("80f0800008122a31632a3bafe0");
////        logger.d("bb--" + DataConversionUtils.byteArrayToString(DataConversionUtils
////                .HexString2Bytes("80f0800008122a31632a3bafe0")));
//
//
//    }
//
//    private void startTask() {
//        if (timer == null) {
//            timer = new Timer();
//            timer.schedule(new ReadTask(), 1000, 300);
//        }
//    }
//
//    private SharedPreferences sharedPreferences;
//
//    private void initUI() {
//        setContentView(R.layout.activity_main);
//        btn1Activite = (Button) findViewById(R.id.btn1_active);
//        btn2Activite = (Button) findViewById(R.id.btn2_active);
//        btnGetRomdan = (Button) findViewById(R.id.btn_get_ramdon);
//        btnSendAdpu = (Button) findViewById(R.id.btn_send_adpu);
//        btnReSet = (Button) findViewById(R.id.btn_reset);
//        btnPower = (Button) findViewById(R.id.btn_power);
//        btnClear = (Button) findViewById(R.id.btn_clear);
//        tvVerson = (TextView) findViewById(R.id.tv_verson);
//
//        btnPower.setOnClickListener(this);
//        btn1Activite.setOnClickListener(this);
//        btn2Activite.setOnClickListener(this);
//        btnGetRomdan.setOnClickListener(this);
//        btnSendAdpu.setOnClickListener(this);
//        btnReSet.setOnClickListener(this);
//        btnClear.setOnClickListener(this);
//        spinner = (Spinner) findViewById(R.id.sp_select);
//        spinner.setSelection(sharedPreferences.getInt("selection", 0));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                baurate = Integer.parseInt(spinner.getSelectedItem().toString());
////                PowerOpenDev();
//                sharedPreferences.edit().putInt("baurate", baurate).commit();
//                sharedPreferences.edit().putInt("selection", position).commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spinnerSerialport = (Spinner) findViewById(R.id.sp_select_serialport);
//        spinnerSerialport.setSelection(sharedPreferences.getInt("selectionSerialport", 0));
//        spinnerSerialport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                serialport = spinnerSerialport.getSelectedItem().toString();
//                sharedPreferences.edit().putString("serialport", serialport).commit();
//                sharedPreferences.edit().putInt("selectionSerialport", position).commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spinnerGpio = (Spinner) findViewById(R.id.sp_select_gpio);
//        spinnerGpio.setSelection(sharedPreferences.getInt("selectionGpio", 0));
//        spinnerGpio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                gpio = spinnerGpio.getSelectedItem().toString();
//                setGpio(position);
////                sharedPreferences.edit().putString("gpio", gpio).commit();
//                sharedPreferences.edit().putInt("selectionGpio", position).commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        tvShowData = (TextView) findViewById(R.id.tv_show_message);
//        tvShowData.setMovementMethod(ScrollingMovementMethod.getInstance());
//        edvADPU = (EditText) findViewById(R.id.edv_adpu_cmd);
//        edvADPU.setText("0084000008");
//        //pin = { 0x00, 0x20, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00 };
//        edvADPU.setText("0020000003");
//        edvADPU.setText("80f0800008122a31632a3bafe0");
//        edvADPU.setText("00A404000BA000000003454E45524759");
////        edvADPU.setText("80f002 00 01 02");
//    }
//
//    private void setGpio(int position) {
//        switch (position) {
//            case 0:
//                gpio = "14";
//                break;
//            case 1:
//                gpio = "96";
//                break;
//            case 2:
//                gpio = "7";
//                break;
//            case 3:
//                gpio = "48";
//                break;
//        }
//    }
//
//    private void PowerOpenDev() {
//        releaseDev();
//        SystemClock.sleep(100);
//        initDevice();
//    }
//
//    /**
//     * 获取当前应用程序的版本号
//     */
//    private String getVersion() {
//        PackageManager pm = getPackageManager();
//        try {
//            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
//            String version = packinfo.versionName;
//            return version;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return "版本号错误";
//        }
//    }
//
//
//    String send_data = "";
//
//    @Override
//    public void onClick(View v) {
//        if (v == btn1Activite) {
//            psamflag = 1;
//            mSerialPort.WriteSerialByte(fd, getPowerCmd());
//            send_data = DataConversionUtils.byteArrayToString(getPowerCmd());
//            if (psamflag == 1)
//                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
//            else if (psamflag == 2)
//                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
//        } else if (v == btn2Activite) {
//            psamflag = 2;
//            mSerialPort.WriteSerialByte(fd, getPowerCmd());
//            send_data = DataConversionUtils.byteArrayToString(getPowerCmd());
//            if (psamflag == 1)
//                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
//            else if (psamflag == 2)
//                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
//        } else if (v == btnGetRomdan) {
//            mSerialPort.WriteSerialByte(fd, getRomdan());
//            send_data = DataConversionUtils.byteArrayToString(getRomdan());
//            if (psamflag == 1)
//                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
//            else if (psamflag == 2)
//                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
//        } else if (v == btnSendAdpu) {
//            String temp_cmd = edvADPU.getText().toString();
//            if ("".equals(temp_cmd) || temp_cmd.length() % 2 > 0 || temp_cmd.length() < 4) {
//                Toast.makeText(mContext, "Please enter a valid instruction！", Toast.LENGTH_SHORT)
//                        .show();
//                return;
//            }
//            mSerialPort.WriteSerialByte(fd, adpuPackage(DataConversionUtils.HexString2Bytes
//                    (temp_cmd)));
//            send_data = temp_cmd;
//            if (psamflag == 1)
//                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
//            else if (psamflag == 2)
//                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
//        } else if (v == btnClear) {
//            tvShowData.setText("");
//        } else if (v == btnReSet) {
//
//            //复位
//            try {
//                mDeviceControl.PsamResetDevice();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(mContext,"reset failed",Toast.LENGTH_SHORT).show();
//            }
//            Toast.makeText(mContext,"reset ok",Toast.LENGTH_SHORT).show();
//        } else if (v == btnPower) {
////            Toast.makeText(mContext,"power",Toast.LENGTH_SHORT).show();
//            PowerOpenDev();
//        }
//    }
//
//    private int baurate = 19200;
//    private String serialport = "ttyMT1";
//
//    private void initDevice() {
//        mSerialPort = new SerialPort();
//        try {
//            //19200
//            logger.d("====serialport" + serialport + "==baurate" + baurate + "==gpio" + gpio);
//            mSerialPort.OpenSerial("/dev/" + serialport, baurate);
//            fd = mSerialPort.getFd();
//            logger.d("--onCreate--open-serial=" + fd);
//        } catch (SecurityException e) {
//            // TODO Auto-generated catch block
//            Toast.makeText(this, "No serial port authority, forced exit!", Toast.LENGTH_LONG)
//                    .show();
//            e.printStackTrace();
//            System.exit(0);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            Toast.makeText(this, "The serial port is not found, forced exit！", Toast.LENGTH_LONG)
//                    .show();
//            e.printStackTrace();
//            System.exit(0);
//        }
//        try {
//            mDeviceControl = new DeviceControl("sys/class/misc/mtgpio/pin", gpio);
//        } catch (IOException e) {
//            e.printStackTrace();
//            mDeviceControl = null;
//            return;
//        }
//        mDeviceControl.PowerOnDevice();
//        startTask();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        releaseDev();
//
//        stopTask();
//    }
//
//    private void stopTask() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }
//
//    private void releaseDev() {
//        stopTask();
//        mSerialPort.CloseSerial(fd);
//        mSerialPort = null;
//        mDeviceControl.PowerOffDevice();
//        mDeviceControl = null;
//
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            byte[] temp_cmd = (byte[]) msg.obj;
//            byte[] byte_len = new byte[2];
//            byte_len[0] = temp_cmd[3];
//            byte_len[1] = temp_cmd[2];
//            int len = DataConversionUtils.byteArrayToInt(byte_len);
//            if (len <= 6) {
//                tvShowData.append("error:" + DataConversionUtils.byteArrayToString(temp_cmd));
//                return;
//            }
//            byte[] temp_data = new byte[len - 6];
//            logger.d("---len=" + len + temp_cmd[0] + "  " + temp_cmd[1] + "  " + temp_cmd.length);
//            String data;// = DataConversionUtils.byteArrayToString(temp_cmd);
//
//            tvShowData.append("Rx data：\n" + DataConversionUtils.byteArrayToString(temp_cmd) +
//                    "\n\n");
////            tvShowData.append("命令头：" + DataConversionUtils.byteArrayToString(new
////                    byte[]{temp_cmd[0], temp_cmd[1]}) + "\n");
////            tvShowData.append("长度字：" + DataConversionUtils.byteArrayToString(new
////                    byte[]{temp_cmd[2], temp_cmd[3]}) + "-->" + DataConversionUtils
////                    .byteArrayToInt(new byte[]{temp_cmd[3], temp_cmd[2]}) + "\n");
////            tvShowData.append("设备标识：" + DataConversionUtils.byteArrayToString(new
////                    byte[]{temp_cmd[4], temp_cmd[5]}) + "\n");
////            tvShowData.append("命令码：" + DataConversionUtils.byteArrayToString(new
////                    byte[]{temp_cmd[6], temp_cmd[7]}) + "\n");
////            tvShowData.append("状态字：" + DataConversionUtils.byteArrayToString(new
////                    byte[]{temp_cmd[8]}));
////            if (temp_cmd[8] == 0) {
////                tvShowData.append("-->成功\n");
////            }
//            tvShowData.append("Unpack data：" + "\n");
//            for (int i = 0; i < len - 6; i++) {
//                temp_data[i] = temp_cmd[i + 9];
//            }
//            if (len <= 6) {
//                return;
//            }
//            tvShowData.append(DataConversionUtils.byteArrayToString(temp_data));
////            String temp = tvShowData.getText().toString();
////            if (temp.length() < 10000) {
////                tvShowData.append(data + "\n");
////            } else {
////                tvShowData.setText(data);
////            }
//        }
//    };
//
//    /**
//     * 读串口
//     */
//    private class ReadTask extends TimerTask {
//        public void run() {
//            try {
//                byte[] temp1 = mSerialPort.ReadSerial(fd, 1024);
////                    logger.d("----read--run");
////                logger.d("----read");
//                if (temp1 != null) {
//                    logger.d("----read--ok---" + DataConversionUtils.byteArrayToStringLog(temp1,
//                            temp1.length));
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = temp1;
//                    handler.sendMessage(msg);
//                    SystemClock.sleep(1000);
//                }
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 打包adpu指令
//     *
//     * @param cmd
//     * @return
//     */
//    private byte[] adpuPackage(byte[] cmd) {
//        byte[] result = new byte[cmd.length + 9];
//        result[0] = (byte) 0xaa;
//        result[1] = (byte) 0xbb;
//        result[2] = (byte) (cmd.length + 5);
//        result[3] = 0x00;
//        result[4] = 0x00;
//        result[5] = 0x00;
//        if (psamflag == 1)
//            result[6] = 0x13;
//        else if (psamflag == 2)
//            result[6] = 0x23;
//        result[7] = 0x06;
//        result[result.length - 1] = 0x51;
//        System.arraycopy(cmd, 0, result, 8, cmd.length);
//        return result;
//    }
//
//    private byte[] getRomdan() {
//        //0084000008
//        //aabb 0A00 0000 2306 00A4040012 51
//        //aabb 0a00 0000 2306 0084000008 51
//        //aabb 0500 0000 1306 0084000008 51
//        byte[] cmd = new byte[]{(byte) 0xaa, (byte) 0xbb, 0x0a, 0x00, 0x00, 0x00, 0x13, 0x06,
//                0x00, (byte)
//                0x84, 0x00, 0x00, 0x08, 0x51};
//        if (psamflag == 1)
//            cmd[6] = 0x13;
//        else if (psamflag == 2)
//            cmd[6] = 0x23;
//
//        return cmd;
//    }
//
//    private byte[] getPowerCmd() {
//        //aabb05000000110651
//        //aabb05000000120651
//        //  //IC卡复位3V
////        aabb05000000110651
////IC卡复位5V
////        aabb05000000120651
//        byte[] cmd = new byte[]{(byte) 0xaa, (byte) 0xbb, 0x05, 0x00, 0x00, 0x00, 0x11, (byte)
//                0x06, 0x51};
//        if (psamflag == 1)
//            cmd[6] = 0x11;
//        else if (psamflag == 2)
//            cmd[6] = 0x21;
//        return cmd;
//    }
//}
