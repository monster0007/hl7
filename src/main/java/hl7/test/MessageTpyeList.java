package hl7.test;

import java.util.ArrayList;
import java.util.List;

public class MessageTpyeList {
    public List<MessageTypeAndTableList> list;
    public List<String> sqlList = new ArrayList<>();

    public MessageTpyeList(){
        list = new ArrayList<>();
    }

    public void add(String messageType, String tableTame, String tableFieldName, String hl7FieldName) {
        int index = getMessageTpyeIndex(messageType);
        if(-1 != index){
            add(index,tableTame,tableFieldName,hl7FieldName);
        }else{
            MessageTypeAndTableList messageTypeAndTableList = new MessageTypeAndTableList(messageType,tableTame,tableFieldName,hl7FieldName);
            list.add(messageTypeAndTableList);
        }
    }

    private void add(int index, String tableTame, String tableFieldName, String hl7FieldName) {
        list.get(index).add(tableTame,tableFieldName,hl7FieldName);
    }

    private int getMessageTpyeIndex(String messageType){
        int index = -1;
        if(null == list){
            return index;
        }else{
            for(int i = 0;i < list.size();i++){
                if(list.get(i).messageTypeName.equals(messageType)){
                    index = i;
                    break;
                }
            }
        }
        return  index;

    }

    public void cleanResult() {
        for(MessageTypeAndTableList messageTypeAndTableList:list){
            messageTypeAndTableList.cleanResult();
        }
        sqlList = new ArrayList<>();
    }

    public void getSQLList() {
        for(MessageTypeAndTableList messageTypeAndTableList:list){
            sqlList.addAll(messageTypeAndTableList.getSqlList());
        }
    }
}