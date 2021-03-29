package hl7.test.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.*;
import hl7.test.entity.MY_ORU_R01;
import hl7.test.entity.MY_OUL_R21;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: Mission Lee
 * @create: 2020-08-12 11:30
 * <p>
 * 开发目标：
 * 1. 根据入参 String判断消息类型
 * 2. 调用对应 类型的 parse 方法，返回Message
 * 注意： Urit 每行数据之间会添加一个 “回车”
 */
public class Hl7StringConverter {
    public static Message convert(String hl7Str) throws HL7Exception {
        QCK_Q02 msg = new QCK_Q02();
        try {
            msg.parse(hl7Str);
            String type = "";
            type = match(msg.getMSH().getMessageType() + "");
            type = type == "" ? "QCK^Q02" : type;
            switch (type) {
                case "OUL^R21":
                    MY_OUL_R21 oul_r21 = new MY_OUL_R21();
                    oul_r21.parse(hl7Str);
                    return oul_r21;
                case "QCK^Q02":
                    QCK_Q02 qck_q02 = new QCK_Q02();
                    qck_q02.parse(hl7Str);
                    return qck_q02;
                case "ORU^R01":
                    MY_ORU_R01 oru_r01 = new MY_ORU_R01();
                    oru_r01.parse(hl7Str);
                    return oru_r01;
                case "DSR^Q03":
                    DSR_Q03 dsr_q03 = new DSR_Q03();
                    dsr_q03.parse(hl7Str);
                    return dsr_q03;
                case "QRY^Q02":
                    QRY_Q02 qry_q02 = new QRY_Q02();
                    qry_q02.parse(hl7Str);
                    return qry_q02;
                case "ACK^Q03":
                    ACK ack = new ACK();
                    ack.parse(hl7Str);
                    return ack;
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
        throw new HL7Exception("未知错误，Message Type 为正确设置");
    }

    private static String match(String string) {
        String regex = "\\[(.*?)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            buffer.append(matcher.group(1));
        }
        return buffer.toString();
    }

    //从名称数组中查找包含元素的名称个数
    public static int getConNum(String str, String[] names) {
        int flag = 0;
        for (String name : names) {
            if (name.contains(str)) {
                flag++;
            }
        }
        return flag;
    }
//返回传入消息段字符串的索引
    public static int getConIndex(String str, String[] names) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }
}
