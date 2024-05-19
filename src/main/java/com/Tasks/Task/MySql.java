package com.Tasks.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MySql {

    public MySql(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final JdbcTemplate jdbcTemplate;

    public String get(String[] targets, String[] from, String[] when,String[] whenValue) {
        String response = "";
        if(whenValue.length == when.length){
            String sqlQuery = "SELECT ";
            int targetsLength = targets.length;
            for(String target:targets){
                sqlQuery += target;
                if(targetsLength != 1){
                    sqlQuery += ",";
                }
                targetsLength--;
            }
            sqlQuery += " FROM ";
            int fromLength = from.length;
            for(String location:from){
                sqlQuery += location;
                if(fromLength != 1){
                    sqlQuery += ",";
                }
                fromLength--;
            }
            sqlQuery +=" WHERE ";
            int whenLength = when.length;
            for(int time = 0; time<= whenLength; time++){
                sqlQuery += when[time]+" = ?";
                if(whenLength != 1){
                    sqlQuery += " AND ";
                }
                whenLength--;
            }
            try{
                response = jdbcTemplate.queryForObject(sqlQuery,whenValue, String.class);
            }catch (EmptyResultDataAccessException e){
                response = "";
            }
        }
        else{
            response = "ERROR 101";
        }
        return response;
    }

    public String[] get(String[] targets, String[] from, String[] when,String[] whenValue,Boolean table) {
        String[] response;
        if(whenValue.length == when.length){
            String sqlQuery = "SELECT ";
            int targetsLength = targets.length;
            for(String target:targets){
                sqlQuery += target;
                if(targetsLength != 1){
                    sqlQuery += ",";
                }
                targetsLength--;
            }
            sqlQuery += " FROM ";
            int fromLength = from.length;
            for(String location:from){
                sqlQuery += location;
                if(fromLength != 1){
                    sqlQuery += ",";
                }
                fromLength--;
            }
            sqlQuery +=" WHERE ";
            int whenLength = when.length;
            for(int time = 0; time<= whenLength; time++){
                sqlQuery += when[time]+" = ?";
                if(whenLength != 1){
                    sqlQuery += " AND ";
                }
                whenLength--;
            }
            try{
                response = jdbcTemplate.query(sqlQuery,whenValue,(rs)->{
//                    tutaj coś nie działa przez SPRING
                    String[] columns = new String[targets.length];
                    for(int i = 0; i < targets.length; i++) {
                        columns[i] = rs.getString(i + 1);
                    }
                    return columns;
                });
            }catch (EmptyResultDataAccessException e){
                response = new String[]{""};
            }
        }
        else{
            response = new String[]{"ERROR 101"};
        }
        return response;
    }

    public void put(String target,String[] nameValues,String[] values){
        int valuesLeft = values.length;
        int namesLeft = nameValues.length;
        String putQuery = "INSERT INTO "+target+" (";
        for(String name:nameValues){
            putQuery += name;
            if(namesLeft != 1){
                putQuery += ",";
            }
            namesLeft = namesLeft - 1;
        }
        putQuery += ") VALUES (";
        for(String value:values){
            putQuery += "?";
            if(valuesLeft != 1){
                putQuery += ",";
            }
            valuesLeft = valuesLeft - 1;
        }
        putQuery += ")";
        jdbcTemplate.update(putQuery,values);
    }

    public void singleUpdate(String target,String[] name,String[] value){
        String sqlQuery = "UPDATE "+target+" SET "+name[0]+" = ? WHERE "+name[1]+" = ?";
        jdbcTemplate.update(sqlQuery,value);
    }
}
