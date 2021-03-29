package hl7.test;

import java.util.ArrayList;
import java.util.List;

public class MessageTypeAndTableList {
    public String messageTypeName;
    public List<TableNameAndFieldMap> list = new ArrayList<>();

    public MessageTypeAndTableList(String messageType, String tableTame, String tableFieldName, String hl7FieldName) {
        messageTypeName = messageType;
        add(tableTame,tableFieldName,hl7FieldName);
    }

    public void add(String tableTame, String tableFieldName, String hl7FieldName) {
        int index = getTableNameIndex(tableTame);
        if(-1 != index){
            list.get(index).add(tableFieldName,hl7FieldName);
        }else{
            TableNameAndFieldMap tableNameAndFieldMap = new TableNameAndFieldMap(tableTame,tableFieldName,hl7FieldName);
            list.add(tableNameAndFieldMap);
        }
    }

    public int getTableNameIndex(String messageType){
        int index = -1;
        if(null == list){
            return index;
        }else{
            for(int i = 0;i < list.size();i++){
                if(list.get(i).tableName.equals(messageType)){
                    index = i;
                    break;
                }
            }
        }
        return  index;

    }

    public void cleanResult() {
        for(TableNameAndFieldMap tableNameAndFieldMap:list){
            tableNameAndFieldMap.cleanResult();
        }
    }

    public List<String> getSqlList() {
        List<String> sqlList = new ArrayList<>();
        for(TableNameAndFieldMap tableNameAndFieldMap:list){
            sqlList.addAll(tableNameAndFieldMap.getSqlList());
        }
        return sqlList;
    }

}
