package com.nuange.community;

import java.io.IOException;

public class WKTest {
    public static void main(String[] args) {
     String cmd="C:/Program Files/wkhtmltopdf/bin/wkhtmltoimage --quality 75  https://thinkwon.blog.csdn.net/article/details/104397299 C:/workplace/data/wk-pdf/1.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
