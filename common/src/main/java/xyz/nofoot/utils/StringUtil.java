package xyz.nofoot.utils;

/**
 * @projectName: nRPC
 * @package: xyz.nofoot.utils
 * @className: StringUtil
 * @author: NoFoot
 * @date: 4/17/2023 6:27 PM
 * @description 字符串工具类
 */
public class StringUtil {
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
