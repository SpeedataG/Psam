package speedatagroup.brxu.com.workdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libutils.DataConversionUtils;

import java.io.IOException;
import java.util.Timer;

import speedatacom.a3310libs.PsamManager;
import speedatacom.a3310libs.inf.IPsam;

public class MainActivity extends Activity implements View.OnClickListener {

    //19200 9600
    private Button btn1Activite, btn2Activite, btnGetRomdan, btnSendAdpu, btnClear;
    private Spinner spinnerSerialport;
    private Spinner spinnerGpio;
    private Spinner spinner;
    private EditText edvADPU;
    private TextView tvShowData;
    private SerialPort mSerialPort;
    private int fd;
    private int psamflag = 0;
    private DeviceControl mDeviceControl;
    private Context mContext;
    private Timer timer;
    private int gpio;
    private Button btnReSet;
    private Button btnPower;
    private TextView tvVerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        sharedPreferences = getSharedPreferences("rember", MODE_PRIVATE);
        initUI();
        baurate = sharedPreferences.getInt("baurate", 0);
        serialport = sharedPreferences.getString("serialport", serialport);
        int position = sharedPreferences.getInt("selectionGpio", 0);
        setGpio(position);
        psam3310Realize.DevicePower(serialport, baurate, DeviceControl.PowerType.MAIN_AND_EXPAND,
                this, 88, 2);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String verson = getVersion();
                tvVerson.setText("V" + verson);
            }
        });
    }

    private SharedPreferences sharedPreferences;

    private void initUI() {
        setContentView(R.layout.activity_main);
        btn1Activite = (Button) findViewById(R.id.btn1_active);
        btn2Activite = (Button) findViewById(R.id.btn2_active);
        btnGetRomdan = (Button) findViewById(R.id.btn_get_ramdon);
        btnSendAdpu = (Button) findViewById(R.id.btn_send_adpu);
        btnReSet = (Button) findViewById(R.id.btn_reset);
        btnPower = (Button) findViewById(R.id.btn_power);
        btnClear = (Button) findViewById(R.id.btn_clear);
        tvVerson = (TextView) findViewById(R.id.tv_verson);
        btnPower.setOnClickListener(this);
        btn1Activite.setOnClickListener(this);
        btn2Activite.setOnClickListener(this);
        btnGetRomdan.setOnClickListener(this);
        btnSendAdpu.setOnClickListener(this);
        btnReSet.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.sp_select);
        spinner.setSelection(sharedPreferences.getInt("selection", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                baurate = Integer.parseInt(spinner.getSelectedItem().toString());
//                PowerOpenDev();
                sharedPreferences.edit().putInt("baurate", baurate).commit();
                sharedPreferences.edit().putInt("selection", position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSerialport = (Spinner) findViewById(R.id.sp_select_serialport);
        spinnerSerialport.setSelection(sharedPreferences.getInt("selectionSerialport", 0));
        spinnerSerialport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialport = spinnerSerialport.getSelectedItem().toString();
                sharedPreferences.edit().putString("serialport", serialport).commit();
                sharedPreferences.edit().putInt("selectionSerialport", position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerGpio = (Spinner) findViewById(R.id.sp_select_gpio);
        spinnerGpio.setSelection(sharedPreferences.getInt("selectionGpio", 0));
        spinnerGpio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setGpio(position);
                sharedPreferences.edit().putInt("selectionGpio", position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        tvShowData = (TextView) findViewById(R.id.tv_show_message);
        tvShowData.setMovementMethod(ScrollingMovementMethod.getInstance());
        edvADPU = (EditText) findViewById(R.id.edv_adpu_cmd);
        edvADPU.setText("0084000008");
        edvADPU.setText("0020000003");
        edvADPU.setText("80f0800008122a31632a3bafe0");
        edvADPU.setText("00A404000BA000000003454E45524759");
    }

    private void setGpio(int position) {
        switch (position) {
            case 0:
                gpio = 14;
                break;
            case 1:
                gpio = 96;
                break;
            case 2:
                gpio = 7;
                break;
            case 3:
                gpio = 48;
                break;
        }
    }

    private void PowerOpenDev() {
        SystemClock.sleep(100);
        initDevice();
    }

    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号错误";
        }
    }


    String send_data = "";
    IPsam psam3310Realize = PsamManager.getPsamIntance();

    @Override
    public void onClick(View v) {
        if (v == btn1Activite) {
            psamflag = 1;
            boolean result = psam3310Realize.PsamPower(IPsam.PowerType.Psam1);
            if (result)
                tvShowData.setText("Psam1 activite ok\n");
            else
                tvShowData.setText("Psam1 activite failed\n");
        } else if (v == btn2Activite) {
            psamflag = 2;
            boolean result = psam3310Realize.PsamPower(IPsam.PowerType.Psam2);
            if (result)
                tvShowData.setText("Psam2 activite ok\n");
            else
                tvShowData.setText("Psam2 activite failed\n");
        } else if (v == btnGetRomdan) {
            if (psamflag == 1) {
                int len = psam3310Realize.sendData(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                        0x08}, IPsam
                        .PowerType.Psam1);
                if (len >= 0) {
                    tvShowData.setText("Psam1 Send data：00 84 00 00 08\n");
                } else {
                    tvShowData.setText("Psam1 Send data failed\n");
                }
            } else if (psamflag == 2) {
                int len = psam3310Realize.sendData(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                        0x08}, IPsam
                        .PowerType.Psam2);
                if (len >= 0) {
                    tvShowData.setText("Psam2 Send data：00 84 00 00 08\n");
                } else {
                    tvShowData.setText("Psam2 Send data failed\n");
                }
            }
        } else if (v == btnSendAdpu) {
            String temp_cmd = edvADPU.getText().toString();
            if ("".equals(temp_cmd) || temp_cmd.length() % 2 > 0 || temp_cmd.length() < 4) {
                Toast.makeText(mContext, "Please enter a valid instruction！", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            send_data = temp_cmd;
            if (psamflag == 1) {
                int len = psam3310Realize.sendData(com.speedata.libutils.DataConversionUtils
                        .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam1);
                if (len >= 0)
                    tvShowData.setText("Psam1 Send data：\n" + send_data + "\n\n");
                else
                    tvShowData.setText("Psam1 Send data：failed");
            } else if (psamflag == 2) {
                int len = psam3310Realize.sendData(com.speedata.libutils.DataConversionUtils
                        .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam2);
                if (len >= 0)
                    tvShowData.setText("Psam2 Send data：\n" + send_data + "\n\n");
                else
                    tvShowData.setText("Psam2 Send data：failed");
            }
        } else if (v == btnClear) {
            tvShowData.setText("");
        } else if (v == btnReSet) {
        } else if (v == btnPower) {
            PowerOpenDev();
        }
    }

    private int baurate = 115200;
    private String serialport = "ttyMT2";

    private void initDevice() {
        psam3310Realize.DevicePower(serialport, baurate, DeviceControl.PowerType.MAIN_AND_EXPAND,
                this, 88, 2);
        psam3310Realize.startReadThread(handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            psam3310Realize.stopReadThread();
            psam3310Realize.releaseDev();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] temp_cmd = (byte[]) msg.obj;
            tvShowData.append("rece data:" + DataConversionUtils.byteArrayToString(temp_cmd));
        }
    };


    private byte[] getRomdan() {
        //0084000008
        //aabb 0A00 0000 2306 00A4040012 51
        //aabb 0a00 0000 2306 0084000008 51
        //aabb 0500 0000 1306 0084000008 51
        byte[] cmd = new byte[]{(byte) 0xaa, (byte) 0xbb, 0x0a, 0x00, 0x00, 0x00, 0x13, 0x06,
                0x00, (byte)
                0x84, 0x00, 0x00, 0x08, 0x51};
        if (psamflag == 1)
            cmd[6] = 0x13;
        else if (psamflag == 2)
            cmd[6] = 0x23;

        return cmd;
    }


}
