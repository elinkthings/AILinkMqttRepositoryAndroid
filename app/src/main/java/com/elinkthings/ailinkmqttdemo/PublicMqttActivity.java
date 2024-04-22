package com.elinkthings.ailinkmqttdemo;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.elinkthings.elinkmqttlib.listener.OnMqttConnectListener;
import com.elinkthings.elinkmqttlib.listener.OnMqttMessageListener;
import com.elinkthings.elinkmqttlib.listener.OnMqttOtherMessageListener;
import com.elinkthings.elinkmqttlib.mqtt.MqttDevice;
import com.elinkthings.elinkmqttlib.mqtt.MqttManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公共mqtt界面
 *
 * @author xing
 * @date 2024/04/19
 */
public class PublicMqttActivity extends AppBaseActivity implements View.OnClickListener, OnMqttMessageListener, OnMqttOtherMessageListener {

    private final int REFRESH_DATA = 1;
    private EditText etAppUserId;
    private EditText etDeviceId;
    private EditText etSendData;
    private EditText etSendDataDeviceId;
    private TextView tvDeviceList;
    private List<String> mList;
    private ArrayAdapter listAdapter;
    private ListView lv_log;

    /**
     * _mac地址用于加解密,这里随便写一个
     */
    private String mMac = "00:00:00:00:00:00";

    @Override
    protected void uiHandlerMessage(Message msg) {
        if (msg.what == REFRESH_DATA) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_public_mqtt;
    }

    @Override
    protected void initListener() {
        MqttManager.getInstance().setOnMqttConnectListener(new OnMqttConnectListener() {
            @Override
            public void onConnected() {
                addLog("Mqtt连接成功");
            }

            @Override
            public void onConnecting() {
                addLog("Mqtt正在连接中");
            }

            @Override
            public void onDisconnect(int errCode) {
                addLog("Mqtt断开连接,错误码：" + errCode);
            }

            @Override
            public void onSubscribeSuccess(String... topics) {
                addLog("订阅成功:\n" + Arrays.toString(topics));
                for (String topic : topics) {
                    MqttDevice device = MqttManager.getInstance().getDeviceForTopic(topic);
                    device.addOnMqttMessageListener(PublicMqttActivity.this);
                    device.addOnMqttOtherMessageListener(PublicMqttActivity.this);
                    device.setOnA7EncryptionListener(new MqttDevice.OnA7EncryptionListener() {
                        @Override
                        public byte[] onA7Encryption(int cid, byte[] cidBytes, byte[] payload) {
//                            return AiLinkPwdUtil.mcuEncrypt(cidBytes, getMacByte(mMac), payload);
                            return payload;
                        }
                    });
                }
            }

            @Override
            public void onSubscribeFail(String... topics) {
                addLog("订阅失败:\n" + Arrays.toString(topics));
                for (String topic : topics) {
                    MqttDevice device = MqttManager.getInstance().getDeviceForTopic(topic);
                    device.removeOnMqttMessageListener(PublicMqttActivity.this);
                }
            }
        });
    }

    @Override
    public void onMessage(String topic, String deviceId, int cid, byte[] payload) {
        addLog("设备ID:" + deviceId + " 接收到消息:" + BleStrUtils.byte2HexStr(payload));
    }

    @Override
    public void onSendSuccess(String topic, String deviceId, byte[] payload) {
        addLog("设备ID:" + deviceId + " 发送消息成功:" + BleStrUtils.byte2HexStr(payload));
    }

    @Override
    public void onSendFailure(String topic, String deviceId, byte[] payload, int errCode,int retryCount) {
        addLog("设备ID:" + deviceId + " 发送消息失败:" + BleStrUtils.byte2HexStr(payload));
    }

    @Override
    public void onOtherMessage(String topic, String deviceId, byte[] hex) {
        addLog("设备ID:" + deviceId + " 接收到透传消息:" + BleStrUtils.byte2HexStr(hex));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnClear) {
            mList.clear();
            mHandler.sendEmptyMessage(REFRESH_DATA);
        } else if (id == R.id.btnConnectMqtt) {
            String appId = etAppUserId.getText().toString();
            if (TextUtils.isEmpty(appId)) {
                addLog("App Id不能为空");
                return;
            }
            //禁止etAppUserId编辑
            etAppUserId.setEnabled(false);
            MqttManager.getInstance().init(this, appId);
            addLog("正在连接Mqtt:" + appId);
            MqttManager.getInstance().connectMqtt();
        } else if (id == R.id.btnDisconnectMqtt) {
            etAppUserId.setEnabled(true);
            addLog("正在断开Mqtt连接");
            MqttManager.getInstance().disconnectMqtt();
        } else if (id == R.id.btnAddDevice) {
            String deviceId = etDeviceId.getText().toString();
            MqttManager.getInstance().addDevice(deviceId);
            addLog("添加设备:" + deviceId);
            etDeviceId.setText("");
            refreshDeviceList();
        } else if (id == R.id.btnRemoveDevice) {
            String deviceId = etDeviceId.getText().toString();
            MqttManager.getInstance().removeDevice(deviceId);
            addLog("移除设备:" + deviceId);
            etDeviceId.setText("");
            refreshDeviceList();
        } else if (id == R.id.btnSendData) {
            String data = etSendData.getText().toString();
            if (TextUtils.isEmpty(data)) {
                addLog("发送数据不能为空");
                return;
            }
            String deviceId = etSendDataDeviceId.getText().toString();
            if (TextUtils.isEmpty(deviceId)) {
                addLog("设备Id不能为空");
                return;
            }
            MqttDevice device = MqttManager.getInstance().getDevice(deviceId);
            if (device != null) {
                device.sendData(data.getBytes());
                addLog("发送数据:" + data + " 设备:" + deviceId);
            } else {
                addLog("设备不存在或者未连接:" + deviceId);
            }
        }
    }

    @Override
    protected void initData() {
        mList = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        lv_log.setAdapter(listAdapter);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btnClear).setOnClickListener(this);
        findViewById(R.id.btnConnectMqtt).setOnClickListener(this);
        findViewById(R.id.btnDisconnectMqtt).setOnClickListener(this);
        etAppUserId = (EditText) findViewById(R.id.etAppUserId);
        etDeviceId = (EditText) findViewById(R.id.etDeviceId);
        findViewById(R.id.btnAddDevice).setOnClickListener(this);
        findViewById(R.id.btnRemoveDevice).setOnClickListener(this);
        tvDeviceList = (TextView) findViewById(R.id.tvDeviceList);
        lv_log = findViewById(R.id.lv_log);
        etSendData = (EditText) findViewById(R.id.etSendData);
        etSendDataDeviceId = (EditText) findViewById(R.id.etSendDataDeviceId);
        findViewById(R.id.btnSendData).setOnClickListener(this);

    }

    private void refreshDeviceList() {
        List<String> deviceList = MqttManager.getInstance().getDeviceList();
        String deviceListStr = TextUtils.join(",", deviceList);
        tvDeviceList.setText(deviceListStr);
    }

    /**
     * 添加日志
     *
     * @param log 日志
     */
    private void addLog(String log) {
        log = TimeUtils.getTimeSSS() + log;
        mList.add(0, log);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    /**
     * 获取mac字节
     *
     * @param mac mac
     * @return {@link byte[]}
     */
    private byte[] getMacByte(String mac) {
        byte[] macByte = new byte[6];
        if (mac.contains(":")) {
            String[] macArr = mac.split(":");
            for (int i = 0; i < macArr.length; i++) {
                macByte[macArr.length - i - 1] = (byte) Integer.parseInt(macArr[i], 16);
            }
        }
        return macByte;
    }
}
