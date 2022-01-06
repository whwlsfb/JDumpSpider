package cn.wanghw.utils;

public class OQLSnippets {
    public static final String isNullOrUndefined = "function isNullOrUndefined(val) {\n" +
            "   return val == null || val == undefined\n" +
            "}\n";
    public static final String getValue = isNullOrUndefined + "function getValue(val) {\n" +
            "   return !isNullOrUndefined(val.value) ? " +  // String
            "               !isNullOrUndefined(val.value.value) ? " +
            "                   !isNullOrUndefined(val.value.value.value) ? " +
            "                   val.value.value.value.toString() : " +
            "               val.value.value.toString() : " +
            "           val.value.toString() : " +
            "   !isNullOrUndefined(val.str) ? val.str.toString() : " + // java.lang.ProcessEnvironment$Value
            "val.toString();\n" +
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
