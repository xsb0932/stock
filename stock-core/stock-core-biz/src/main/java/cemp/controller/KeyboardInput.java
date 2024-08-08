package cemp.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        System.out.println("按下的字符为：" + keyChar);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // 这个方法用于处理按键按下事件，如果需要获取按下的字符，可以使用e.getKeyChar()方法
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // 这个方法用于处理按键释放事件
    }

    public static void main(String[] args) {
        KeyboardInput keyboardInput = new KeyboardInput();
        // 给某个组件添加键盘事件监听器
        JFrame jf=new JFrame();//创建窗体对象
        jf.setTitle("创建一个JFrame 窗体");//设置窗体标题
        Container container=jf.getContentPane();//获取主容器
        JLabel jl=new JLabel("这是一个 JFrame 窗体");//一个文本标签
        jl.setHorizontalAlignment(SwingConstants.CENTER);//使用标签上的文字居中
        container.add(jl);//将标签添加到主容器中
        jf.addKeyListener(keyboardInput);
        jf.setSize(300,150);//设置窗体宽高
        jf.setLocation(320,240);//设置窗体在屏幕中出现的位置
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//关闭窗体则停止程序
        jf.setVisible(true);//让窗体展示出来

    }
}
