package hl7.test;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.model.v24.message.*;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import hl7.test.entity.MY_ORU_R01;
import hl7.test.entity.MY_OUL_R21;
import hl7.test.util.Hl7StringConverter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyReceivingApplication implements ReceivingApplication {

    private JdbcConnectionsPool pool = new JdbcConnectionsPool();

    public MessageTpyeList messageTpyeList = new MessageTpyeList();

    private String MSHId = "";

    private String SetIDPID = "1";

    private String SetIDOBR = "1";


    public MyReceivingApplication() throws SQLException {
//        String sql = "SELECT * FROM `view_hl7_map` ";//SQL语句
//        Connection conn = pool.getConnection();
//        Statement st = conn.createStatement();
//        ResultSet resultSet = null;
//        resultSet = st.executeQuery(sql);
//        List<String[]> list = new ArrayList<>();
//        while (resultSet.next()) {
//            String messageType = resultSet.getString("message_type");
//            String tableTame = resultSet.getString("table_name");
//            String tableFieldName = resultSet.getString("table_field_name");
//            String hl7FieldName = resultSet.getString("hl7_field_name");
//            messageTpyeList.add(messageType, tableTame, tableFieldName, hl7FieldName);
//        }
//        release(conn, st, resultSet);
    }

    @Override
    public Message processMessage(Message message, Map map) throws HL7Exception {
        //判断string中消息类型，调用对应类型的parse方法，返回Message
        Message convert = Hl7StringConverter.convert(map.get("raw-message").toString());
        //调用saveMessage方法将message中的消息拼成多条sql
        List<String> sqlList = saveMessage(convert);
//        Terser terser = new Terser(message);
//        String sendingApplication = terser.get("/.MSH-3");
        for (String name : message.getNames()) {
            message.getAll(name);
        }
        String messageID = map.get("/MSH-10").toString();
        String messageType = message.getName();
        String SendIP = map.get("SENDING_IP").toString();
        String SendPort = map.get("SENDING_PORT").toString();
        String rawMessage = map.get("raw-message").toString();
        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        System.out.println(rawMessage);
        System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        String rawMessageSql = "INSERT INTO `raw_hl7_message`(`messageType`, `messageID`, `SendIP`, `SendPort`,`rawMessage`)" +
                " VALUES ('" + messageType + "', '" + messageID + "', '" + SendIP + "', '" + SendPort + "','" + rawMessage + "');";
        Message message1 = null;
        Connection conn = null;
        try {
            conn = pool.getConnection();
            conn.setAutoCommit(false);
            Statement st = conn.createStatement();
            st.execute(rawMessageSql);
            for (String sql : sqlList) {
                st.execute(sql);
            }
            conn.commit();
//            Terser terser = new Terser(message1);
            conn.close();
            st.close();
            message1 = message.generateACK();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
                message1 =  returnMSG(e.getMessage(), message);
            } catch (SQLException e1) {
                e1.printStackTrace();
                message1 = returnMSG(e1.getMessage(), message);
            }
        }finally {
            return message1;
        }
    }


    public boolean canProcess(Message message) {
        return true;
    }

    private Message returnMSG(String exceptionMSG, Message message) {
        Message message1 = null;
        try {
            message1 = message.generateACK(AcknowledgmentCode.AE, new HL7Exception(exceptionMSG));
        } catch (HL7Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message1;
    }

    private List<String> saveMessage(Message message) {
        if (message instanceof MY_ORU_R01) {  // todo 不存在相同，已经重写
            MY_ORU_R01 oru_r01 = (MY_ORU_R01) message;
            return dealORU_R01Message(oru_r01);
        }
        if (message instanceof MY_OUL_R21) {  // todo 不存在相同，已经重写
            MY_OUL_R21 MY_OUL_R21 = (MY_OUL_R21) message;
            return dealOUL_R21Message(MY_OUL_R21);
        }
        if (message instanceof QCK_Q02) {
            QCK_Q02 qck_q02 = (QCK_Q02) message;
        } // todo 存在相同的，不需要重写
        // TODO: 2020-08-17 剩余的就不一个一个写了，
        if (message instanceof DSR_Q03) {
            DSR_Q03 dsr_q03 = (DSR_Q03) message;
        } // todo 未知数据字段
        if (message instanceof QRY_Q02) {
            QRY_Q02 qry_q02 = (QRY_Q02) message;
        } // todo 未知数据字段
        if (message instanceof ACK) {
            ACK ack = (ACK) message;
        }                 // todo 未知数据字段
        return null;
    }

    private String getObxParams(OBX obx) throws HL7Exception {
        StringBuffer sqlColumnStr = new StringBuffer();
        sqlColumnStr.append(" insert into T4(");
        StringBuffer sqlParamStr = new StringBuffer();
        sqlParamStr.append(" values(");
        String[] names = obx.getNames();
        for (int i = 1; i <= names.length; i++) {
            Type[] field = obx.getField(i);
            for (Type type : field) {
                String name = NamingRules(names[i - 1]);
                sqlColumnStr.append(name);
                sqlParamStr.append("'" + type.encode() + "'");
//                        System.out.println(type.encode());
            }
            if (i != names.length && field.length != 0) {
                sqlColumnStr.append(",");
                sqlParamStr.append(",");
            }
        }
        if (sqlColumnStr.lastIndexOf(",") == sqlColumnStr.length() - 1)
            sqlColumnStr.deleteCharAt(sqlColumnStr.length() - 1);
        if (sqlParamStr.lastIndexOf(",") == sqlParamStr.length() - 1)
            sqlParamStr.deleteCharAt(sqlParamStr.length() - 1);
        sqlColumnStr.append(")");
        sqlParamStr.append(")");
        return sqlColumnStr.append(sqlParamStr).toString();
    }

    private List<String> dealOUL_R21Message(MY_OUL_R21 ro1) {
        List<String> sqlList = new ArrayList<String>();

        try {

            System.out.println("deal 解析开始");
            OBR obr = ro1.getOBR();
            OBX obx = ro1.getOBX();
            String[] namesArr = ro1.getNamesArr();
            int obrNum = Hl7StringConverter.getConNum("OBR", namesArr);
            for (String name : namesArr) {
//                if (Hl7StringConverter.getConNum("OBR",namesArr)!=-1&&Hl7StringConverter.getConNum("OBX",namesArr)!=-1){
//                    if (Hl7StringConverter.getConNum("OBR",namesArr)<Hl7StringConverter.getConNum("OBX",namesArr)){
//
//                    }else{
//
//                    }
//                }
                if (name.contains("MSH")) {
                    sqlList.add(getMSHParams(ro1.getMSH(name)));
                } else if (name.contains("PID")) {
                    sqlList.add(getPIDParams(ro1.getPID(name)));
                } else if (name.contains("PV1")) {

                } else if (name.contains("OBR")) {
                    sqlList.add(getOBRParams(ro1.getOBR(name)));
                } else if (name.contains("OBX")) {
                    List<OBX> obxList = ro1.getOBXList(name);
                    for (OBX obx1 : obxList) {
                        sqlList.add(getOBXParams(obx1));
                    }
                }
            }
//            if (Hl7StringConverter.getConNum("PID",ro1.getNamesArr())==1){
//                sqlList.add(getPIDParams(ro1.getPID()));
//            }
//            List<OBX> allOBX = ro1.getAllOBX();
//            for (OBX allOBX1 : allOBX) {
//                sqlList.add(getObxParams(allOBX1));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlList;
    }

    private List<String> dealORU_R01Message(MY_ORU_R01 ro1) {
        List<String> sqlList = new ArrayList<String>();

        try {

            System.out.println("deal 解析开始");
            OBR obr = ro1.getOBR();
            OBX obx = ro1.getOBX();
            String[] namesArr = ro1.getNamesArr();
            int obrNum = Hl7StringConverter.getConNum("OBR", namesArr);
            for (String name : namesArr) {
//                if (Hl7StringConverter.getConNum("OBR",namesArr)!=-1&&Hl7StringConverter.getConNum("OBX",namesArr)!=-1){
//                    if (Hl7StringConverter.getConNum("OBR",namesArr)<Hl7StringConverter.getConNum("OBX",namesArr)){
//
//                    }else{
//
//                    }
//                }
                if (name.contains("MSH")) {
                    sqlList.add(getMSHParams(ro1.getMSH(name)));
                } else if (name.contains("PID")) {
                    sqlList.add(getPIDParams(ro1.getPID(name)));
                } else if (name.contains("PV1")) {

                } else if (name.contains("OBR")) {
                    sqlList.add(getOBRParams(ro1.getOBR(name)));
                } else if (name.contains("OBX")) {
                    List<OBX> obxList = ro1.getOBXList(name);
                    for (OBX obx1 : obxList) {
                        sqlList.add(getOBXParams(obx1));
                    }
                }
            }
//            if (Hl7StringConverter.getConNum("PID",ro1.getNamesArr())==1){
//                sqlList.add(getPIDParams(ro1.getPID()));
//            }
//            List<OBX> allOBX = ro1.getAllOBX();
//            for (OBX allOBX1 : allOBX) {
//                sqlList.add(getObxParams(allOBX1));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlList;

    }

    private String getPIDParams(PID pid) throws HL7Exception {
        StringBuffer sqlColumnStr = new StringBuffer();
        sqlColumnStr.append(" insert into hl7_message_pid(MessageControlID,");
        StringBuffer sqlParamStr = new StringBuffer();
        sqlParamStr.append(" values('" + MSHId + "',");
        String[] names = pid.getNames();
        for (int i = 1; i <= names.length; i++) {
            Type[] field = pid.getField(i);
            if (i == 1 && field.length != 0) SetIDPID = field[0].encode();
            for (Type type : field) {
                String name = NamingRules(names[i - 1]);
                sqlColumnStr.append(name);
                sqlParamStr.append("'" + type.encode() + "'");
            }
            if (i != names.length && field.length != 0) {
                sqlColumnStr.append(",");
                sqlParamStr.append(",");
            }
        }
        if (sqlColumnStr.lastIndexOf(",") == sqlColumnStr.length() - 1)
            sqlColumnStr.deleteCharAt(sqlColumnStr.length() - 1);
        if (sqlParamStr.lastIndexOf(",") == sqlParamStr.length() - 1)
            sqlParamStr.deleteCharAt(sqlParamStr.length() - 1);
        sqlColumnStr.append(")");
        sqlParamStr.append(")");
        return sqlColumnStr.append(sqlParamStr).toString();
    }

    private String getMSHParams(MSH msh) throws HL7Exception {
        MSHId = msh.getMsh10_MessageControlID().toString();
        StringBuffer sqlColumnStr = new StringBuffer();
        sqlColumnStr.append(" insert into hl7_message_msh(");
        StringBuffer sqlParamStr = new StringBuffer();
        sqlParamStr.append(" values(");
        String[] names = msh.getNames();
        for (int i = 1; i <= names.length; i++) {
            Type[] field = msh.getField(i);
            for (Type type : field) {
                String name = NamingRules(names[i - 1]);
                sqlColumnStr.append(name);
                sqlParamStr.append("'" + type.encode().replaceAll("\\\\", "") + "'");
            }
            if (i != names.length && field.length != 0) {
                sqlColumnStr.append(",");
                sqlParamStr.append(",");
            }
        }
        if (sqlColumnStr.lastIndexOf(",") == sqlColumnStr.length() - 1)
            sqlColumnStr.deleteCharAt(sqlColumnStr.length() - 1);
        if (sqlParamStr.lastIndexOf(",") == sqlParamStr.length() - 1)
            sqlParamStr.deleteCharAt(sqlParamStr.length() - 1);
        sqlColumnStr.append(")");
        sqlParamStr.append(")");
        return sqlColumnStr.append(sqlParamStr).toString();
    }

    private String getOBRParams(OBR obr) throws HL7Exception {
        StringBuffer sqlColumnStr = new StringBuffer();
        sqlColumnStr.append(" insert into hl7_message_obr(MessageControlID,SetIDPID,");
        StringBuffer sqlParamStr = new StringBuffer();
        sqlParamStr.append(" values('" + MSHId + "','" + SetIDPID + "',");
        String[] names = obr.getNames();
        for (int i = 1; i <= names.length; i++) {
            Type[] field = obr.getField(i);

            for (Type type : field) {
                if (i == 1 && field.length != 0) SetIDOBR = field[0].encode();
                String name = NamingRules(names[i - 1]);
                sqlColumnStr.append(name);
                sqlParamStr.append("'" + type.encode().replaceAll("\\\\", "") + "'");
            }
            if (i != names.length && field.length != 0) {
                sqlColumnStr.append(",");
                sqlParamStr.append(",");
            }
        }
        if (sqlColumnStr.lastIndexOf(",") == sqlColumnStr.length() - 1)
            sqlColumnStr.deleteCharAt(sqlColumnStr.length() - 1);
        if (sqlParamStr.lastIndexOf(",") == sqlParamStr.length() - 1)
            sqlParamStr.deleteCharAt(sqlParamStr.length() - 1);
        sqlColumnStr.append(")");
        sqlParamStr.append(")");
        return sqlColumnStr.append(sqlParamStr).toString();
    }

    private String getOBXParams(OBX obx) throws HL7Exception {
        StringBuffer sqlColumnStr = new StringBuffer();
        sqlColumnStr.append(" insert into hl7_message_obx(MessageControlID,SetIDPID,SetIDOBR,");
        StringBuffer sqlParamStr = new StringBuffer();
        sqlParamStr.append(" values('" + MSHId + "','" + SetIDPID + "','" + SetIDOBR + "',");
        String[] names = obx.getNames();
        for (int i = 1; i <= names.length; i++) {
            Type[] field = obx.getField(i);
            for (Type type : field) {
                String name = NamingRules(names[i - 1]);
//                System.out.println(name);
                sqlColumnStr.append(name);
                sqlParamStr.append("'" + type.encode().replaceAll("\\\\", "") + "'");
            }
            if (i != names.length && field.length != 0) {
                sqlColumnStr.append(",");
                sqlParamStr.append(",");
            }
        }
        if (sqlColumnStr.lastIndexOf(",") == sqlColumnStr.length() - 1)
            sqlColumnStr.deleteCharAt(sqlColumnStr.length() - 1);
        if (sqlParamStr.lastIndexOf(",") == sqlParamStr.length() - 1)
            sqlParamStr.deleteCharAt(sqlParamStr.length() - 1);
        sqlColumnStr.append(")");
        sqlParamStr.append(")");
        return sqlColumnStr.append(sqlParamStr).toString();
    }

    //简单更改命名规则
    private String NamingRules(String name) {
        String str = name.replaceAll(" ", "")
                .replaceAll("-", "").replaceAll("/", "").
                        replaceAll("'s", "").replaceAll("#", "").
                        replaceAll("\\+", "");
        return str;
    }
}
