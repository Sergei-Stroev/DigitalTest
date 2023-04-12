package com.digdes.school;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


class JavaSchoolStarter {

    List<Map<String, Object>> dataBase = new ArrayList<Map<String, Object>>();


    public JavaSchoolStarter() {

    }

    public List<Map<String, Object>> execute(String request) throws Exception {
        Pattern insert = Pattern.compile("insert values", Pattern.CASE_INSENSITIVE);
        Pattern update = Pattern.compile("update values", Pattern.CASE_INSENSITIVE);
        Pattern delete = Pattern.compile("delete values", Pattern.CASE_INSENSITIVE);
        Pattern select = Pattern.compile("select", Pattern.CASE_INSENSITIVE);

        Matcher insertMatcher = insert.matcher(request);
        if (insertMatcher.find()) {
            insert(request);

        }
        Matcher updateMatcher = update.matcher(request);
        if (updateMatcher.find()) {
            update(request);
            return dataBase;

        }
        Matcher deleteMatcher = delete.matcher(request);
        if (deleteMatcher.find()) {
            delete(request);

        }
        Matcher selectMatcher = select.matcher(request);
        if(selectMatcher.find()){
            select(request);
        }
        if(!selectMatcher.find()) {
        }
        return dataBase;
    }

    private void insert(String request) {
        long id;
        String lastName;
        long age;
        double cost;
        Map<String, Object> map = new HashMap<String, Object>();

        Pattern idPattern = Pattern.compile("'id'=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher idMatcher = idPattern.matcher(request);
        if (idMatcher.find()) {
            id = Long.parseLong(idMatcher.group(1));
            map.put("id", id);
        }
        Pattern lastNamePattern = Pattern.compile("'lastName'\\s=\\s+('[\\wà-ÿÀ-ß]+')", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher lastNameMatcher = lastNamePattern.matcher(request);
        if (lastNameMatcher.find()) {
            lastName = lastNameMatcher.group(1);
            map.put("lastName", lastName);
        }
        Pattern agePattern = Pattern.compile("'age'=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher ageMatcher = agePattern.matcher(request);
        if (ageMatcher.find()) {
            age = Long.parseLong(ageMatcher.group(1));
            map.put("age", age);
        }
        Pattern costPattern = Pattern.compile("'cost'=\\s*([\\d.\\d]+)", Pattern.CASE_INSENSITIVE);
        Matcher costMatcher = costPattern.matcher(request);
        if (costMatcher.find()) {
            cost = Double.parseDouble(costMatcher.group(1));
            map.put("cost", cost);
        }
        Pattern activePattern = Pattern.compile("'active'=\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher activeMatcher = activePattern.matcher(request);
        if (activeMatcher.find()) {
            boolean active = Boolean.parseBoolean(activeMatcher.group(1));
            map.put("active", active);
        }
        dataBase.add(map);
    }

    private ArrayList<Map<String,Object>> update(String request)  {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        Pattern updatePattern = Pattern.compile("update\\s+values\\s+(.*?)\\s+where\\s+(.*)", Pattern.CASE_INSENSITIVE);
        Pattern setValuesPattern = Pattern.compile("'(\\w+)'\\s*=\\s*(\\d+(?:\\.\\d+)?|'(?:\\w|\\s)+'|true|false)", Pattern.CASE_INSENSITIVE);
        Matcher updateMatcher = updatePattern.matcher(request);

        Map<String, Object> updateValues;

        if (updateMatcher.find()) {
            String setClause = updateMatcher.group(1);
            String whereClause = updateMatcher.group(2);

            updateValues = new HashMap<String, Object>();

            Matcher setValuesMatcher = setValuesPattern.matcher(setClause);

            while (setValuesMatcher.find()) {
                String key = setValuesMatcher.group(1);
                String valueStr = setValuesMatcher.group(2);

                if (valueStr.matches("\\d+")) {
                    updateValues.put(key, Long.parseLong(valueStr));

                } else if (valueStr.matches("\\d+.\\d+")) {
                    updateValues.put(key, Double.parseDouble(valueStr));

                } else if (valueStr.matches("'([\\wà-ÿÀ-ß]+)'")) {
                    updateValues.put(key, valueStr);

                } else if (valueStr.matches("true|false")) {
                    updateValues.put(key, Boolean.parseBoolean(valueStr));
                }
            }

            for (Map<String, Object> row : dataBase) {
                boolean shouldUpdateRow = true;

                if (whereClause != null) {
                    shouldUpdateRow = evaluateWhereClause(whereClause, row);
                }
                if (shouldUpdateRow) {
                    row.putAll(updateValues);
                    map.putAll(updateValues);
                }
            }
            mapList.add(map);
        }
        return (ArrayList<Map<String, Object>>) mapList;
    }

    private ArrayList<Map<String, Object>> delete(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        Pattern deletePattern = Pattern.compile("\\s+values\\s+(.*)\\s*where\\s+(.*)?", Pattern.CASE_INSENSITIVE);
        Matcher deleteMatcher = deletePattern.matcher(request);

        if (deleteMatcher.find()) {
            String columnsClause = deleteMatcher.group(1);
            String whereClause = deleteMatcher.group(2);

            List<String> columnsToDelete = new ArrayList<String>();

            if(columnsClause.isEmpty()){
                columnsToDelete.add("id");
                columnsToDelete.add("lastName");
                columnsToDelete.add("age");
                columnsToDelete.add("cost");
                columnsToDelete.add("active");
            }

            Pattern columnName = Pattern.compile("'(id|lastName|age|cost|active)'");
            Matcher columnNameMatcher = columnName.matcher(columnsClause);

            while (columnNameMatcher.find()) {
                String columnStr = columnNameMatcher.group(1);
                columnsToDelete.add(columnStr);
            }

            for (Map<String, Object> row : dataBase) {
                boolean shouldDeleteRow = true;

                if (whereClause != null) {
                    shouldDeleteRow = evaluateWhereClause(whereClause, row);
                }

                if (shouldDeleteRow) {
                    for (String column : columnsToDelete) {
                        row.remove(column);
                        String key = column;
                        Object value = row.get(column);
                        map.put(key, value);
                    }
                }
                mapList.add(map);
                map = new HashMap<String, Object>();
            }
            mapList.add(map);
        }
        return (ArrayList<Map<String, Object>>) mapList;
    }

    private List<Map<String, Object>> select(String request) {

        Pattern selectPattern = Pattern.compile("\\s+(.*)\\s*where\\s+(.*\\s+)+(.*)", Pattern.CASE_INSENSITIVE);
        Matcher selectMatcher = selectPattern.matcher(request);

        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        if (selectMatcher.find()) {
            String columnsClause = selectMatcher.group(1);
            String whereClause = selectMatcher.group(2);
            String logicalClause = selectMatcher.group(3);

            List<String> columnsToSelect = new ArrayList<String>();

            if (columnsClause.isEmpty()) {
                columnsToSelect.add("id");
                columnsToSelect.add("lastName");
                columnsToSelect.add("age");
                columnsToSelect.add("cost");
                columnsToSelect.add("active");
            } else {
                Pattern columnName = Pattern.compile("'(id|lastName|age|cost|active)'");
                Matcher columnNameMatcher = columnName.matcher(columnsClause);

                while (columnNameMatcher.find()) {
                    String columnStr = columnNameMatcher.group(1);
                    columnsToSelect.add(columnStr);
                }
            }

            for (Map<String, Object> row : dataBase) {
                boolean selectColumnRow = true;
                boolean whereRow;
                boolean logicRow;

                if (whereClause != null && logicalClause != null) {
                    whereRow = evaluateWhereClause(whereClause, row);
                    logicRow = evaluateWhereClause(logicalClause, row);
                    selectColumnRow = logicalClause(whereRow, logicRow, request);

                } if(whereClause != null) {
                    selectColumnRow = evaluateWhereClause(whereClause, row);
                }

                if (selectColumnRow) {
                    for (String column : columnsToSelect) {
                        String key = column;
                        Object value = row.get(column);
                        map.put(key, value);
                    }
                }
                mapList.add(map);
                map = new HashMap<String, Object>();
            }
        }
        return mapList;
    }

    private boolean evaluateWhereClause(String whereClause, Map<String, Object> row) {
        Pattern wherePattern = Pattern.compile("'(\\w+)'\\s*(=|!=|>|<|>=|<=|like|ilike)\\s*(\\d+(?:\\.\\d+)?|'(?:\\w|\\s)+'|true|false)", Pattern.CASE_INSENSITIVE);
        Matcher whereMatcher = wherePattern.matcher(whereClause);

        while (whereMatcher.find()) {
            String column = whereMatcher.group(1);
            String operator = whereMatcher.group(2);
            String valueStr = whereMatcher.group(3);

            Object whereValue = null;
            if (valueStr.matches("\\d+")) {
                whereValue = Long.parseLong(valueStr);
            } else if (valueStr.matches("\\d+.\\d+")) {
                whereValue = Double.parseDouble(valueStr);
            } else if (valueStr.matches("'([\\wà-ÿÀ-ß]+)'")) {
                whereValue = valueStr;
            } else if (valueStr.matches("true|false")) {
                whereValue = Boolean.parseBoolean(valueStr);
            }

            for (Map<String, Object> rowData : dataBase) {
                Object value = row.get(column);
                    if (value != null) {
                        if (operator.equals("=") && value.equals(whereValue)) {
                            return true;
                        }
                        if (operator.equals("!=") && !value.equals(whereValue)) {
                            return true;
                        }
                        if (operator.equals(">") && value instanceof Number) {
                            if (((Number) value).doubleValue() > ((Number) whereValue).doubleValue() ||
                                    ((Number) value).longValue() > ((Number) whereValue).longValue()) {
                                return true;
                            }
                        }
                        if (operator.equals("<") && value instanceof Number) {
                            if (((Number) value).doubleValue() < ((Number) whereValue).doubleValue() ||
                                    ((Number) value).longValue() < ((Number) whereValue).longValue()) {
                                return true;
                            }
                        }
                        if (operator.equals(">=") && value instanceof Number) {
                            if (((Number) value).doubleValue() >= ((Number) whereValue).doubleValue() ||
                                    ((Number) value).longValue() >= ((Number) whereValue).longValue()) {
                                return true;
                            }
                        }
                        if (operator.equals("<=") && value instanceof Number) {
                            if (((Number) value).doubleValue() <= ((Number) whereValue).doubleValue() ||
                                    ((Number) value).longValue() <= ((Number) whereValue).longValue()) {
                                return true;
                            }
                        }
                        if (operator.equals("like") && value.equals(whereValue)) {
                            return true;
                        }
                    }
                }
            }
        return false;
    }

    private boolean logicalClause(boolean whereRow, boolean logicalRow, String logicalRequest) {
        Pattern comparePattern = Pattern.compile("(and|or)", Pattern.CASE_INSENSITIVE);
        Matcher compareMatcher = comparePattern.matcher(logicalRequest);
        boolean result = false;
        if (compareMatcher.find()) {
            String operator = compareMatcher.group(1);
            if (operator.matches("and")) {
                if ((whereRow && logicalRow)) result = true;
                else result = false;
            } else if (operator.matches("or")) {
                if ((whereRow || logicalRow)) result = true;
                else result = false;
            }
        }
        return result;
    }
}
