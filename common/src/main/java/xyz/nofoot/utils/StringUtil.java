package xyz.nofoot.utils;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: StringUtil
 * @author: NoFoot
 * @date: 4/17/2023 6:27 PM
 * @description: 字符串工具类
 */
public class StringUtil {

    /**
     * @param s:
     * @return: boolean
     * @author: NoFoot
     * @date: 4/18/2023 1:28 PM
     * @description: 判断字符串是否无内容
     */
    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
