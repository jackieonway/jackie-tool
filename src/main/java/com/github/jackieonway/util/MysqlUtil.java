package com.github.jackieonway.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Jackie
 * @version \$Id: MysqlUtil.java, v 0.1 2018-09-05 12:42 Jackie Exp $$
 */
public class MysqlUtil {
	private MysqlUtil() {
	}

	/**
	 * 自动转义特殊字符，例如%,_,/等
	 * @param keyword
	 * @return
	 */
	public static String escapeSpecialChar(String keyword){
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.trim();
			String[] fbsArr = { "\\","\0", "\b", "\n","\r","\t","%","_" , "\'", "\""};
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}
}
