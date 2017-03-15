package speedatagroup.brxu.com.workdemo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.serialport.DeviceControl;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.DataConversionUtils;
import com.speedata.libutils.ReadBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import speedatacom.a3310libs.PsamManager;
import speedatacom.a3310libs.inf.IPsam;

public class MainActivity extends Activity implements View.OnClickListener {

    //19200 9600
    private Button btn1Activite, btn2Activite, btnGetRomdan, btnSendAdpu, btnClear;
    private EditText edvADPU;
    private TextView tvShowData;
    private int psamflag = 0;
    private Context mContext;
    private Button btnReSet;
    private Button btnPower;
    private TextView tvVerson;
    private TextView tvConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initUI();
        mContext = this;
        try {
            psam.initDev(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String verson = getVersion();
                tvVerson.setText("V" + verson);
            }
        });
        boolean isExit = ConfigUtils.isConfigFileExists();
        if (isExit)
            tvConfig.setText("定制配置：\n");
        else
            tvConfig.setText("标准配置：\n");
        ReadBean.PasmBean pasm = ConfigUtils.readConfig(this).getPasm();
        String gpio = "";
        List<String> gpio1 = pasm.getGpio();
        for (String s : gpio1) {
            gpio += s + ",";
        }
        tvConfig.append("串口:" + pasm.getSerialPort() + "  波特率：" + pasm.getBraut() + " 上电类型:" +
                pasm.getPowerType() + " GPIO:" + gpio);
        PowerOpenDev();
    }


    private void initUI() {
        setContentView(R.layout.activity_main);
        tvConfig = (TextView) findViewById(R.id.tv_config);
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
        tvShowData = (TextView) findViewById(R.id.tv_show_message);
        tvShowData.setMovementMethod(ScrollingMovementMethod.getInstance());
        edvADPU = (EditText) findViewById(R.id.edv_adpu_cmd);
        edvADPU.setText("0084000008");
        edvADPU.setText("0020000003");
        edvADPU.setText("80f0800008122a31632a3bafe0");
        edvADPU.setText("00A404000BA000000003454E45524759");
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
    IPsam psam = PsamManager.getPsamIntance();


    @Override
    public void onClick(View v) {
        if (v == btn1Activite) {
            psamflag = 1;
            byte[] data = psam.PsamPower(IPsam.PowerType.Psam1);
            if (data != null)
                tvShowData.setText("Psam1 activite \n" + DataConversionUtils.byteArrayToString
                        (data));
            else {
                tvShowData.setText("failed");
            }
        } else if (v == btn2Activite) {
            psamflag = 2;
            byte[] data = psam.PsamPower(IPsam.PowerType.Psam2);
            if (data != null)
                tvShowData.setText("Psam2 activite \n" + DataConversionUtils.byteArrayToString
                        (data));
            else {
                tvShowData.setText("failed");
            }
        } else if (v == btnGetRomdan) {
            if (psamflag == 1) {
                try {
                    tvShowData.setText("Psam1 Send data：00 84 00 00 08\n");
                    byte[] data = psam.WriteCmd(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                            0x08}, IPsam
                            .PowerType.Psam1);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (psamflag == 2) {
                try {
                    tvShowData.setText("Psam2 Send data：00 84 00 00 08\n");
                    byte[] data = psam.WriteCmd(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                            0x08}, IPsam
                            .PowerType.Psam2);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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
                tvShowData.setText("Psam1 Send data：\n" + send_data + "\n");
                try {
                    byte[] data = psam.WriteCmd(DataConversionUtils
                            .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam1);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else if (psamflag == 2) {
                tvShowData.setText("Psam2 Send data：\n" + send_data + "\n");
                try {
                    byte[] data = psam.WriteCmd(DataConversionUtils
                            .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam2);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnClear) {
            tvShowData.setText("");
        } else if (v == btnReSet) {
            psam.resetDev(DeviceControl.PowerType.EXPAND, 1);
        } else if (v == btnPower) {
            PowerOpenDev();
        }
    }


    private void initDevice() {
        try {
            psam.initDev(this);
            psam.resetDev(DeviceControl.PowerType.MAIN, 98);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            psam.releaseDev();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
