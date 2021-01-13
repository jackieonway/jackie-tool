package com.github.jackieonway.util.idgenerator;

import java.math.BigInteger;

/**
 * @author Jackie
 */
public class IdGenerator {

    private String businessType;

    private String workId;

	public IdGenerator(String workId, String businessType){
	    this.workId = workId;
	    this.businessType = businessType;
    }

    private long seq = 0;

    /**
     * 得到8位的序列号,长度不足8位,前面补0
     * @return idSequence
     */
    public synchronized String getSequence() {
        String str = String.valueOf(seq++);
        int len = str.length();
        //达到8位则重新开始
        if (len == 9) {
			seq = 0;
			len = 1;
			str = "0";
			seq++;
		}
        int defualtLength = 8;
        return getSequence(str, len, defualtLength);
    }

        /**
     * 得到指定位数的序列号,长度不足指定位,前面补0
     * @return idSequence
     */
    public synchronized String getSequenceByLength(Integer length) {
        String str = String.valueOf(seq++);
        int len = str.length();
        //达到length+1位则重新开始
        if (len == length+1) {
            seq = 0;
            len = 1;
            str = "0";
            seq++;
        }
        return getSequence(str, len, length);
    }

    private String getSequence(String str, int len, Integer length) {
        int defualtLength = 8;
        if (length != null) {
            defualtLength = length;
        }
        return getSequence(str, len, defualtLength);
    }

    private String getSequence(String str, int len, int defualtLength) {
        int rest = defualtLength - len;
        StringBuilder sb = new StringBuilder();
        sb.append(businessType);
        sb.append(workId);
        sb.append(TimeThreadLocal.getTime());
        for (int i = 0; i < rest; i++) {
            sb.append('0');
        }
        sb.append(str);
        return new BigInteger(sb.toString()).toString(36);
    }
}
