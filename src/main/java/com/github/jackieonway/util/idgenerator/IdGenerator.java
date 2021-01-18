package com.github.jackieonway.util.idgenerator;

import com.github.jackieonway.util.StringUtils;

import java.math.BigInteger;

/**
 * @author Jackie
 */
public final class IdGenerator {

    private static final int MAX_LENGTH = 9;
    private String businessType;

    private String workId;

	private IdGenerator(String workId, String businessType){
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
        if (len == MAX_LENGTH) {
			seq = 0;
			len = 1;
			str = "0";
			seq++;
		}
        int defaultLength = 8;
        return getSequence(str, len, defaultLength);
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
        int defaultLength = 8;
        if (length != null) {
            defaultLength = length;
        }
        return getSequence(str, len, defaultLength);
    }

    private String getSequence(String str, int len, int defaultLength) {
        int rest = defaultLength - len;
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

    public static class IdGeneratorBuilder{
        private String businessType;

        private String workId;

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

        public IdGenerator build(){
            if (StringUtils.isEmpty(this.workId)){
                throw new IllegalArgumentException(String.format("workId can not be null, value: [%s]",
                        workId));
            }
            if (StringUtils.isEmpty(this.businessType)){
                throw new IllegalArgumentException(String.format("businessType can not be null, value: [%s]",
                        businessType));
            }
            return new IdGenerator(this.workId,this.businessType);
        }
    }

    public static void main(String[] args) {
        final IdGenerator idGenerator = IdGeneratorBuilder.builder().build();
        for (int i = 0; i < 1000; i++) {
            System.out.println(idGenerator.getSequence());
        }
    }

}
