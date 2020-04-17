package cn.wemalls.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �Զ���ע�ͣ�����Ȩ��У��
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface SecurityMapping {
    public abstract String title();// ����

    public abstract String value();// url��ֵַ

    public abstract String rname();// ��ɫ��

    public abstract String rcode();// ��ɫ����

    public abstract int rsequence();// ��ɫ���

    public abstract String rgroup();// ��ɫ��

    public abstract String rtype();// ��ɫ����

    public abstract boolean display();// �Ƿ���ʾ
}