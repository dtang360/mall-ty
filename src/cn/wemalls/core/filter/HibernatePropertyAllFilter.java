package cn.wemalls.core.filter;

import org.hibernate.collection.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import com.alibaba.fastjson.serializer.PropertyFilter;

public class HibernatePropertyAllFilter implements PropertyFilter {

	private String[] propertyNames=null;
	private Boolean nullFlag=false;
	private Boolean LazyFlag=false;
	private Boolean collectionFlag=false;	

	/**
	 * 过滤不需要被序列化的属性，主要是应用于Hibernate的代理和管理。
	 * @param object 属性所在的对象
	 * @param name 属性名
	 * @param value 属性值
	 * @return 返回false属性将被忽略，ture属性将被保留
	 */
	@Override
	public boolean apply(Object object, String name, Object value) {
		//根据属性名判断是否需要返回属性
		if(null!=propertyNames&&propertyNames.length>0) {
			for(int i=0;i<propertyNames.length;i++) {
//				if(propertyNames[i]!=null&&propertyNames[i].equals(object.getClass().getName())){
				if(propertyNames[i]!=null&&propertyNames[i].length()>0&&propertyNames[i].equals(name)) {
						return true;
				}
			}
		}

		//代理对象是否获取
		if (value instanceof HibernateProxy) {//hibernate代理对象
			LazyInitializer initializer = ((HibernateProxy) value).getHibernateLazyInitializer();
			if (initializer.isUninitialized()) {
				if(LazyFlag!=null&&LazyFlag) {
					return true;
				}
					return false;
			}
		} else	if (value instanceof PersistentCollection) {//实体关联集合一对多等
			//集合是否获取
			PersistentCollection collection = (PersistentCollection) value;
			if (!collection.wasInitialized()) {
				if(collectionFlag!=null&&collectionFlag) {
					return true;
				}
					return false;
			}			
			//空值是否获取	
			Object val = collection.getValue();
			if (val == null) {
				if(nullFlag!=null&&nullFlag) {
					return true;
				}
					return false;
			}
		}
		return isBaseType(value);
	}
	
	public boolean isBaseType(Object object) {
		if(object!=null){
			Class className = object.getClass();
			if (className!=null&&(java.lang.Integer.class.equals(className) ||
					java.lang.Byte.class.equals(className) ||
					java.lang.Long.class.equals(className) ||
					java.lang.Double.class.equals(className) ||
					java.lang.Float.class.equals(className) ||
					java.lang.Character.class.equals(className) ||
					java.lang.Short.class.equals(className) ||
					java.lang.Boolean.class.equals(className)||
					java.lang.String.class.equals(className)||
					java.math.BigDecimal.class.equals(className)||
					java.util.Date.class.equals(className)||
					java.sql.Timestamp.class.equals(className)
					)) {
				return true;
			}}
		return false;
	}


	public String[] getPropertyNames() {
		return propertyNames;
	}


	public void setPropertyNames(String[] propertyNames) {
		this.propertyNames = propertyNames;
	}


	public Boolean getNullFlag() {
		return nullFlag;
	}


	public void setNullFlag(Boolean nullFlag) {
		this.nullFlag = nullFlag;
	}


	public Boolean getLazyFlag() {
		return LazyFlag;
	}


	public void setLazyFlag(Boolean lazyFlag) {
		LazyFlag = lazyFlag;
	}


	public Boolean getCollectionFlag() {
		return collectionFlag;
	}


	public void setCollectionFlag(Boolean collectionFlag) {
		this.collectionFlag = collectionFlag;
	}

}