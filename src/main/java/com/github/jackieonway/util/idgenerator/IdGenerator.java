package com.github.jackieonway.util.idgenerator;

import com.github.jackieonway.util.StringUtils;

import java.math.BigInteger;

/**
 * @author Jackie
 */
public final class IdGenerator {

    private static final int MAX_LENGTH = 9;
    private static final int DEFAULT_LENGTH = 8;
    private String businessType;

    private String workId;

    private int radix;

	private IdGenerator(String workId, String businessType, int radix){
	    this.workId = workId;
	    this.businessType = businessType;
	    this.radix= radix;
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
        if (len == MAX_LENGTH) {
			seq = 0;
			len = 1;
			str = "0";
			seq++;
		}
        return getSequence(str, len, DEFAULT_LENGTH);
    }

        /**
     * 得到指定位数的序列号,长度不足指定位,前面补0
     * @return idSequence
     */
    public synchronized String getSequenceByLength(int length) {
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

    private String getSequence(String str, int len, int length) {
        if (length < 1) {
            length = DEFAULT_LENGTH;
        }
        int rest = length - len;
        StringBuilder sb = new StringBuilder();
        sb.append(TimeThreadLocal.getTime());
        sb.append(businessType);
        sb.append(workId);
        for (int i = 0; i < rest; i++) {
            sb.append('0');
        }
        sb.append(str);
        return new BigInteger(sb.toString()).toString(this.radix);
    }

    public static class IdGeneratorBuilder{
        private String businessType;

        private String workId;

        private int radix;

        public static IdGeneratorBuilder builder(){
            return new IdGeneratorBuilder();
        }

        public IdGeneratorBuilder businessType(String businessType) {
            this.businessType = businessType;
            return this;
        }


        public IdGeneratorBuilder workId(String workId) {
            this.workId = workId;
            return this;
        }

        public IdGeneratorBuilder radix(int radix) {
            this.radix = radix;
            return this;
        }

        public IdGenerator build(){
            if (StringUtils.isEmpty(this.workId)){
                throw new IllegalArgumentException(String.format("workId can not be null, value: [%s]",
                        workId));
            }
            if (StringUtils.isEmpty(this.businessType)){
                throw new IllegalArgumentException(String.format("businessType can not be null, value: [%s]",
                        businessType));
            }
            if (this.radix < 2 || this.radix > 36){
                this.radix = 36;
            }
            return new IdGenerator(this.workId,this.businessType,this.radix);
        }
    }
}
