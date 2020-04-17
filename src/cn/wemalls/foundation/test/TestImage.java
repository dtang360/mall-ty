package cn.wemalls.foundation.test;

import cn.wemalls.core.tools.CommUtil;
import java.io.File;

public class TestImage {
    public static void main(String[] args){
        File f = new File("F://JAVA_PRO//wemalls//upload//store//163840");
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++){
            File temp = files[i];
            String source = temp.getPath();
            String target = temp.getPath() + "_small.jpg";
            CommUtil.createSmall(source, target, 160, 160);
        }
    }
}




