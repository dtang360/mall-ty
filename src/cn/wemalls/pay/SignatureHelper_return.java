package cn.wemalls.pay;

import cn.wemalls.core.tools.Md5Encrypt;

import java.io.FileWriter;
import java.util.*;

/**
 * ǩ�����ַ���
 */
public class SignatureHelper_return {
    public static String sign(Map params, String privateKey){
        Properties properties = new Properties();

        for (Iterator iter = params.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            Object value = params.get(name);

            if ((name == null) || (name.equalsIgnoreCase("sign")) ||
                    (name.equalsIgnoreCase("sign_type"))){
                continue;
            }
            properties.setProperty(name, value.toString());
        }

        String content = getSignatureContent(properties);
        return sign(content, privateKey);
    }

    public static String getSignatureContent(Properties properties){
        StringBuffer content = new StringBuffer();
        List keys = new ArrayList(properties.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++){
            String key = (String)keys.get(i);
            String value = properties.getProperty(key);

            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }

    public static String sign(String content, String privateKey){
        if (privateKey == null){
            return null;
        }
        String signBefore = content + privateKey;
        try {
            FileWriter writer = new FileWriter("D:/alipay_log/alipay_logalipay_log" +
                                               System.currentTimeMillis() + ".txt");
            writer.write(signBefore);
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return Md5Encrypt.md5(signBefore);
    }
}




