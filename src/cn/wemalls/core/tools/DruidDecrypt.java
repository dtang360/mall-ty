package cn.wemalls.core.tools;

import com.alibaba.druid.filter.config.ConfigTools;

/**
����Ͱ���ݿ�����㷨������ݿ����������滻��r2vv5fcp��������main�����󽫴�ӡ����������jdbc.properties��
*/
@SuppressWarnings("all")
public class DruidDecrypt {
	public static void main(String[] args){
		try {
			ConfigTools configTools = new ConfigTools ();
			System.out.println(configTools.encrypt("1234"));
			
		} catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
