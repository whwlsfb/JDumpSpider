package cn.wanghw.utils;

public class OQLSnippets {
    public static final String isNullOrUndefined =
            "function isNullOrUndefined(val) {\n" +
            "  return val == null || val == undefined;\n" +
            "}\n";
    public static final String getValue = isNullOrUndefined +
            "function getValue(val) {\n" +
            "    if (val != null && !isNullOrUndefined(val)) {\n" +
            "        if (unwrapJavaObject(val) != null && (classof(val) == undefined || classof(val).name == \"java.lang.String\") ) {\n" +
            "            return val.toString();\n" +
            "        } else if (!isNullOrUndefined(val.str)) {\n" +
            "            return val.str.toString();\n" +
            "        } else if (!isNullOrUndefined(val.value)) {\n" +
            "            return getValue(val.value);\n" +
            "        } else {\n" +
            "            return null;\n" +
            "        } \n" +
            "    }else {\n" +
            "        return null;\n" +
            "    }\n" +
            "}";
    public static final String getTable = "\n" +
            "function getTable(source) {\n" +
            "   return source ? (source.table || (source.m ? source.m.m ? source.m.m.table : source.m.table : null)) : null;\n" +
            "}\n";
    public static final String isMap = "\n" +
            "function isMap(it) {\n" +
            "   return it != null && (classof(it).name == \"java.util.HashMap\" || classof(it).name == \"java.util.Properties\" || classof(it).name == \"java.util.LinkedHashMap\" || classof(it).name == \"java.util.Collections$UnmodifiableMap\")\n" +
            "}\n";
}
