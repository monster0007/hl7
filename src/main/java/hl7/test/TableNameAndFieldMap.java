package hl7.test;

import java.util.*;

public class TableNameAndFieldMap {
    // 表名字
    public String tableName = null;
    // 表中字段名与HL7字段名之间的映射关系
    public Map<String,String> table_hl7_fieldMap = new HashMap<>();
    // HL7字段名与拆解之后的索引之间的关系
    public Map<String,int[]> hl7_index_map = new HashMap<>();
    // 记录该表对应了HL7中的哪些 Segment 名字
    public Set<String> segmentNameSet = new HashSet<>(); //所有要找寻的Segment名称集合

    public Map<String, List> segmentNameAndIndexMap = new HashMap<>(); //Segment名称和Segment中的field中的位置

    public List<String> stacklList = new ArrayList<>();  //用作栈
    public Map<String,String> currentResultMap = new HashMap<>();

    public List<Map> resultMapLsit = new ArrayList<>();

    public TableNameAndFieldMap(String tableTame, String tableFieldName, String hl7FieldName) {
        tableName = tableTame;
        add(tableFieldName,  hl7FieldName);
    }

    public void add(String tableFieldName, String hl7FieldName) {
        table_hl7_fieldMap.put(tableFieldName,hl7FieldName);
        String[] strs = hl7FieldName.split("-");
        int[] intArray = new int[strs.length-1];
        for (int i = 0; i < strs.length; i++){
            if(i == 0){
                segmentNameSet.add(strs[0]);
                putToSegmentNameAndIndexMap(hl7FieldName);
//                segmentNameAndIndexMap.put(strs[0],tableFieldName);
            }else{
                intArray[i-1] = Integer.parseInt(strs[i]);
            }
        }
        hl7_index_map.put(hl7FieldName,intArray);
    }

    private void putToSegmentNameAndIndexMap(String hl7FieldName) {
        String hl7SegmentName = hl7FieldName.substring(0,3);
        List<String> list = new ArrayList<>();
        if(segmentNameAndIndexMap.containsKey(hl7SegmentName)){
            list.addAll(segmentNameAndIndexMap.get(hl7SegmentName));
        }else{

        }
        list.add(hl7FieldName);
        segmentNameAndIndexMap.put(hl7SegmentName,list);

    }

    public boolean inSegmentNameSet(String segmentName) {
        return segmentNameSet.contains(segmentName);
    }

    public boolean isInSegmentFieldStack(String segmentField) {
        return stacklList.contains(segmentField);
    }

    public void updateStackAandResult(String segmentField) {
        Map<String,String> newMap = new HashMap<>();
        newMap.putAll(currentResultMap);
        resultMapLsit.add(newMap);
        int indecx = stacklList.indexOf(segmentField);
        for(int i = stacklList.size(); i > indecx ;i--){
            currentResultMap.remove(stacklList.get(i-1));
            stacklList.remove(i-1);
        }
    }

    public void setSegmentFieldResult(String segmentField, String hl7FieldValue) {
        currentResultMap.put(segmentField,hl7FieldValue);
        stacklList.add(segmentField);
    }

    public void updateStackAandResult() {
        resultMapLsit.add(currentResultMap);
    }

    public void cleanResult() {
        resultMapLsit = new ArrayList<>();
        currentResultMap = new HashMap<>();
    }

    public List<String> getSqlList() {
        List<String> list = new ArrayList<>();

        if(null != resultMapLsit && resultMapLsit.size() > 0){
            for(Map<String,String> map:resultMapLsit){
                String tableFieldNames = "";
                String values = "";
                for (String tableFieldName:table_hl7_fieldMap.keySet()){
                    String hl7_field_name = table_hl7_fieldMap.get(tableFieldName);
                    tableFieldNames = tableFieldNames + ",`"+tableFieldName+"`";
                    values = values + ",'"+map.get(hl7_field_name)+"'";
                }
                tableFieldNames = "INSERT INTO `"+tableName+"`("+tableFieldNames.substring(1)+")";
                values = " VALUES ("+ values.substring(1)+");";
                list.add(tableFieldNames + values);
            }
        }
        return list;
    }

}
