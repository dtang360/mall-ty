package cn.wemalls.foundation.service;


import cn.wemalls.foundation.domain.OrderForm;
import cn.wemalls.foundation.domain.StorePrinterLog;


public abstract interface  StorePrinterApiService{

	public static String PRINTER_API_URL = "";
	public static String PRINTER_USER = "";
	public static String PRINTER_UKEY = "";

	/**==================添加打印机接口（支持批量）==================
			返回值JSON字符串
			正确例子：{"msg":"ok","ret":0,"data":{"ok":["sn#key#remark#carnum","316500011#abcdefgh#快餐前台"],"no":["316500012#abcdefgh#快餐前台#13688889999  （错误：识别码不正确）"]},"serverExecutedTime":3}
			错误：{"msg":"参数错误 : 该帐号未注册.","ret":-2,"data":null,"serverExecutedTime":37}
			提示：打印机编号(必填) # 打印机识别码(必填) # 备注名称(选填) # 流量卡号码(选填)，多台打印机请换行（\n）添加新打印机信息，每次最多100行(台)。
			String snlist = "sn1#key1#remark1#carnum1\nsn2#key2#remark2#carnum2";
	 *
	 **/			
	public abstract  String addprinter(String snlist);

	/**==================方法1.打印订单==================
	 *返回值JSON字符串
			成功：{"msg":"ok","ret":0,"data":"xxxxxxx_xxxxxxxx_xxxxxxxx","serverExecutedTime":5}
			失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}
	 * 
	 */
	public abstract String print(OrderForm of,String printerSn,String content,String times);

	/**
	 * ===========方法2.查询某订单是否打印成功=============
	 ***返回值JSON字符串***
	 		成功：{"msg":"ok","ret":0,"data":true,"serverExecutedTime":2}//data:true为已打印,false为未打印
			失败：{"msg":"错误描述","ret":非0, "data":null,"serverExecutedTime":7}
			String orderid = "xxxxxxx_xxxxxxxx_xxxxxxxx";//订单ID，从方法1返回值data获取
			String method2 = queryOrderState(orderid);
	 */
	public abstract String queryOrderState(String orderid);

	/**
	 * ===========方法3.查询指定打印机某天的订单详情============
	 ***返回值JSON字符串***
			成功：{"msg":"ok","ret":0,"data":{"print":6,"waiting":1},"serverExecutedTime":9}//print已打印，waiting为打印
			失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}
			String strdate = "2016-11-12";//注意时间格式为"yyyy-MM-dd"
	 */
	public abstract String queryOrderInfoByDate(String sn,String strdate);

	/**
	 * ===========方法4.查询打印机的状态==========================
	 ***返回值JSON字符串***
	             成功：{"msg":"ok","ret":0,"data":"状态","serverExecutedTime":4}
		  失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}
	 */
	public abstract String queryPrinterStatus(String sn);

	/**
	 * ===========方法4.修改打印机==========================
	 ***返回值JSON字符串***
	             成功：{"msg":"ok","ret":0,"data":"状态","serverExecutedTime":4}
		  失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}
	 */
	public abstract String PrinterEdit(String sn);

	/**
	 * ===========方法7.清空待打印队列==========================
	 ***返回值JSON字符串***
	             成功：{"msg":"ok","ret":0,"data":"状态","serverExecutedTime":4}
		  失败：{"msg":"错误描述","ret":非0,"data":"null","serverExecutedTime":5}
	 */
	public abstract String delPrinterSqs(String sn);
}
