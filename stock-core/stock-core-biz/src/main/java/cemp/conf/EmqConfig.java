package cemp.conf;

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
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EmqConfig {


    @Value("${emq.broker}")
    private String broker;
    @Value("${emq.topic}")
    private String topic;
    @Value("${emq.username}")
    private String username;
    @Value("${emq.password}")
    private String password;
    @Value("${emq.clientid}")
    private String clientid;


    private void publish(MqttClient client,String topic,String msg,int qos ){
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

//    @Bean
    public MqttClient emq(){
        System.out.println(broker);
        System.out.println(clientid);
        int qos = 0;
        MqttClient client = null;
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

        HHOOK hhk = null;
        LowLevelKeyboardProc keyboardHook;
        List<Character> singleInput = new ArrayList<Character>();

        final User32 lib = User32.INSTANCE;
        WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        MqttClient finalClient = client;
        HHOOK finalHhk = hhk;
        keyboardHook = new WinUser.LowLevelKeyboardProc() {
            boolean isShiftUp = false;

            @Override
            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT info) {
                if (nCode >= 0) {
                    if(wParam.intValue() == WinUser.WM_KEYDOWN){
                        //只监听键盘按下
                        if(info.vkCode == 112){     //f1 AOE输出
                            //mqtt push message
                            publish(finalClient,topic,"1",0);
                        }else if(info.vkCode == 115){       //f4 单体输出
                            publish(finalClient,topic,"2",0);
                        }else if(info.vkCode == 53){       //5 跟随
                            publish(finalClient,topic,"3",0);
                        }else if(info.vkCode == 54){       //6 打断循环/吃喝
                            publish(finalClient,topic,"4",0);
                        }
                    }

                }
                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return lib.CallNextHookEx(finalHhk, nCode, wParam, new WinDef.LPARAM(peer));
            }
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);

        // This bit never returns from GetMessage
        int result;
        WinUser.MSG msg = new WinUser.MSG();
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
        return client;


    }


}
