package cn.wanghw.utils;

public class OQLSnippets {
    public static final String isNullOrUndefined =
            "function isNullOrUndefined(val) {\n" +
            "  return val == null || val == undefined;\n" +
            "}\n";
    public static final String getValue = isNullOrUndefined +
            "function getValue(val) {\n" +
            "  return !isNullOrUndefined(val)\n" +
            "    ? !isNullOrUndefined(val.value)\n" +
            "      ? !isNullOrUndefined(val.value.value)\n" +
            "        ? !isNullOrUndefined(val.value.value.value)\n" +
            "          ? val.value.value.value.toString()\n" +
            "          : val.value.value.toString()\n" +
            "        : val.value.toString()\n" +
            "      : !isNullOrUndefined(val.str)\n" +
            "      ? val.str.toString()\n" +
            "      : val.toString()\n" +
            "    : null;\n" +
            "}\n";
    public static final String getTable = "\n" +
            "function getTable(source) {\n" +
            "   return source ? (source.table || (source.m ? source.m.m ? source.m.m.table : source.m.table : null)) : null;\n" +
            "}\n";
    public static final String isMap = "\n" +
            "function isMap(it) {\n" +
            "   return it != null && (classof(it).name == \"java.util.HashMap\" || classof(it).name == \"java.util.Properties\" || classof(it).name == \"java.util.LinkedHashMap\" || classof(it).name == \"java.util.Collections$UnmodifiableMap\")\n" +
            "}\n";
}
