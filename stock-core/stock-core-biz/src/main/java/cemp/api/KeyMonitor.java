package cemp.api;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class KeyMonitor {
    private static HHOOK hhk;
    private static LowLevelKeyboardProc keyboardHook;
    static List<Character> singleInput = new ArrayList<Character>();
    private static String broker = "tcp://39.98.82.109:1883";
    private static String topic = "/wow/lr/hlxz";
    private static String username = "admin";
    private static String password = "public";
    private static String clientid = "publish_client_001";
    private static String content = "Hello MQTT";
    private static int qos = 0;
    private static MqttClient client = null;
    static{

        try {
            client = new MqttClient(broker, clientid, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        // 连接参数
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置用户名和密码
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        // 连接
        try {
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        // 创建消息并设置 QoS
    }


    private static String caseCode() {
        StringBuffer buffer = new StringBuffer();
        for (Character i : singleInput) {
            buffer.append(i);
        }
        return buffer.toString();
    }

    private static void publish(String msg){
        try {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(qos);
            // 发布消息
            client.publish(topic, message);
            // 关闭连接
            //client.disconnect();
            // 关闭客户端
            //client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        final User32 lib = User32.INSTANCE;
        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        keyboardHook = new LowLevelKeyboardProc() {
            boolean isShiftUp = false;

            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
                if (nCode >= 0) {
                    if(wParam.intValue() == WinUser.WM_KEYDOWN){
                        //只监听键盘按下
                        if(info.vkCode == 49){
                            //mqtt push message
                            publish("1");

                        }else if(info.vkCode == 50){
                            publish("2");
                        }
                    }

                }
                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return lib.CallNextHookEx(hhk, nCode, wParam, new LPARAM(peer));
            }
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);

        // This bit never returns from GetMessage
        int result;
        MSG msg = new MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            if (result == -1) {
                // System.err.println("error in get message");
                break;
            } else {
                // System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(hhk);
    }
}
