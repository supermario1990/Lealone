package org.lealone.test.service.generated.executor;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;
import org.lealone.test.orm.generated.User;
import org.lealone.test.service.impl.AllTypeServiceImpl;

/**
 * Service executor for 'all_type_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class AllTypeServiceExecutor implements ServiceExecutor {

    private final AllTypeServiceImpl s = new AllTypeServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "TEST_TYPE":
            Integer p_f1_1 = methodArgs[0].getInt();
            Boolean p_f2_1 = methodArgs[1].getBoolean();
            Byte p_f3_1 = methodArgs[2].getByte();
            Short p_f4_1 = methodArgs[3].getShort();
            Long p_f5_1 = methodArgs[4].getLong();
            Long p_f6_1 = methodArgs[5].getLong();
            BigDecimal p_f7_1 = methodArgs[6].getBigDecimal();
            Double p_f8_1 = methodArgs[7].getDouble();
            Float p_f9_1 = methodArgs[8].getFloat();
            Time p_f10_1 = methodArgs[9].getTime();
            Date p_f11_1 = methodArgs[10].getDate();
            Timestamp p_f12_1 = methodArgs[11].getTimestamp();
            byte[] p_f13_1 = methodArgs[12].getBytes();
            Object p_f14_1 = methodArgs[13].getObject();
            String p_f15_1 = methodArgs[14].getString();
            String p_f16_1 = methodArgs[15].getString();
            String p_f17_1 = methodArgs[16].getString();
            Blob p_f18_1 = methodArgs[17].getBlob();
            Clob p_f19_1 = methodArgs[18].getClob();
            UUID p_f20_1 = methodArgs[19].getUuid();
            Array p_f21_1 = methodArgs[20].getArray();
            User result1 = this.s.testType(p_f1_1, p_f2_1, p_f3_1, p_f4_1, p_f5_1, p_f6_1, p_f7_1, p_f8_1, p_f9_1, p_f10_1, p_f11_1, p_f12_1, p_f13_1, p_f14_1, p_f15_1, p_f16_1, p_f17_1, p_f18_1, p_f19_1, p_f20_1, p_f21_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(JsonObject.mapFrom(result1).encode());
        case "TEST_UUID":
            UUID p_f1_2 = methodArgs[0].getUuid();
            UUID result2 = this.s.testUuid(p_f1_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueUuid.get(result2);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, String> methodArgs) {
        switch (methodName) {
        case "TEST_TYPE":
            Integer p_f1_1 = Integer.valueOf(methodArgs.get("F1"));
            Boolean p_f2_1 = Boolean.valueOf(methodArgs.get("F2"));
            Byte p_f3_1 = Byte.valueOf(methodArgs.get("F3"));
            Short p_f4_1 = Short.valueOf(methodArgs.get("F4"));
            Long p_f5_1 = Long.valueOf(methodArgs.get("F5"));
            Long p_f6_1 = Long.valueOf(methodArgs.get("F6"));
            BigDecimal p_f7_1 = new java.math.BigDecimal(methodArgs.get("F7"));
            Double p_f8_1 = Double.valueOf(methodArgs.get("F8"));
            Float p_f9_1 = Float.valueOf(methodArgs.get("F9"));
            Time p_f10_1 = java.sql.Time.valueOf(methodArgs.get("F10"));
            Date p_f11_1 = java.sql.Date.valueOf(methodArgs.get("F11"));
            Timestamp p_f12_1 = java.sql.Timestamp.valueOf(methodArgs.get("F12"));
            byte[] p_f13_1 = methodArgs.get("F13").getBytes();
            Object p_f14_1 = (methodArgs.get("F14"));
            String p_f15_1 = methodArgs.get("F15");
            String p_f16_1 = methodArgs.get("F16");
            String p_f17_1 = methodArgs.get("F17");
            Blob p_f18_1 = new org.lealone.db.value.ReadonlyBlob(methodArgs.get("F18"));
            Clob p_f19_1 = new org.lealone.db.value.ReadonlyClob(methodArgs.get("F19"));
            UUID p_f20_1 = java.util.UUID.fromString(methodArgs.get("F20"));
            Array p_f21_1 = new org.lealone.db.value.ReadonlyArray(methodArgs.get("F21"));
            User result1 = this.s.testType(p_f1_1, p_f2_1, p_f3_1, p_f4_1, p_f5_1, p_f6_1, p_f7_1, p_f8_1, p_f9_1, p_f10_1, p_f11_1, p_f12_1, p_f13_1, p_f14_1, p_f15_1, p_f16_1, p_f17_1, p_f18_1, p_f19_1, p_f20_1, p_f21_1);
            if (result1 == null)
                return null;
            return JsonObject.mapFrom(result1).encode();
        case "TEST_UUID":
            UUID p_f1_2 = java.util.UUID.fromString(methodArgs.get("F1"));
            UUID result2 = this.s.testUuid(p_f1_2);
            if (result2 == null)
                return null;
            return result2.toString();
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "TEST_TYPE":
            ja = new JsonArray(json);
            Integer p_f1_1 = Integer.valueOf(ja.getValue(0).toString());
            Boolean p_f2_1 = Boolean.valueOf(ja.getValue(1).toString());
            Byte p_f3_1 = Byte.valueOf(ja.getValue(2).toString());
            Short p_f4_1 = Short.valueOf(ja.getValue(3).toString());
            Long p_f5_1 = Long.valueOf(ja.getValue(4).toString());
            Long p_f6_1 = Long.valueOf(ja.getValue(5).toString());
            BigDecimal p_f7_1 = new java.math.BigDecimal(ja.getValue(6).toString());
            Double p_f8_1 = Double.valueOf(ja.getValue(7).toString());
            Float p_f9_1 = Float.valueOf(ja.getValue(8).toString());
            Time p_f10_1 = java.sql.Time.valueOf(ja.getValue(9).toString());
            Date p_f11_1 = java.sql.Date.valueOf(ja.getValue(10).toString());
            Timestamp p_f12_1 = java.sql.Timestamp.valueOf(ja.getValue(11).toString());
            byte[] p_f13_1 = ja.getString(12).getBytes();
            Object p_f14_1 = ja.getJsonObject(13).mapTo(Object.class);
            String p_f15_1 = ja.getString(14);
            String p_f16_1 = ja.getString(15);
            String p_f17_1 = ja.getString(16);
            Blob p_f18_1 = ja.getJsonObject(17).mapTo(java.sql.Blob.class);
            Clob p_f19_1 = ja.getJsonObject(18).mapTo(java.sql.Clob.class);
            UUID p_f20_1 = java.util.UUID.fromString(ja.getValue(19).toString());
            Array p_f21_1 = ja.getJsonObject(20).mapTo(java.sql.Array.class);
            User result1 = this.s.testType(p_f1_1, p_f2_1, p_f3_1, p_f4_1, p_f5_1, p_f6_1, p_f7_1, p_f8_1, p_f9_1, p_f10_1, p_f11_1, p_f12_1, p_f13_1, p_f14_1, p_f15_1, p_f16_1, p_f17_1, p_f18_1, p_f19_1, p_f20_1, p_f21_1);
            if (result1 == null)
                return null;
            return JsonObject.mapFrom(result1).encode();
        case "TEST_UUID":
            ja = new JsonArray(json);
            UUID p_f1_2 = java.util.UUID.fromString(ja.getValue(0).toString());
            UUID result2 = this.s.testUuid(p_f1_2);
            if (result2 == null)
                return null;
            return result2.toString();
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
