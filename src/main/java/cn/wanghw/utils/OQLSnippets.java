package cn.wanghw.utils;

public class OQLSnippets {
    public static final String isNullOrUndefined = "function isNullOrUndefined(val) {\n" +
            "   return val == null || val == undefined\n" +
            "}\n";
    public static final String getValue = isNullOrUndefined + "function getValue(val) {\n" +
            "   return !isNullOrUndefined(val.value) ? !isNullOrUndefined(val.value.value) ? !isNullOrUndefined(val.value.value.value) ? val.value.value.value.toString() : val.value.value.toString() : val.value.toString() : val.toString();\n" +
            "}\n";
}
