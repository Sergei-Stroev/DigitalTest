package com.digdes.school;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {

            List<Map<String,Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Корпачев' , 'id'= 1, 'age'=30, 'cost'= 21.16, 'active'= true");

            List<Map<String,Object>> result2 = starter.execute("INSERT VALUES 'lastName' = 'Бувкин' , 'id'= 2, 'age'=10, 'cost'= 19.38, 'active'= true");

            List<Map<String,Object>> result3 = starter.execute("INSERT VALUES 'lastName' = 'Слабински' , 'id'= 3, 'age'=24, 'cost'= 99.99, 'active'=true");

            List<Map<String,Object>> result6 = starter.execute("SELECT where 'cost'> 20  OR 'age' >= 18");
            System.out.println(Arrays.toString(result6.toArray()));

            List<Map<String,Object>> result4 = starter.execute("UPDATE VALUES 'active'=false, 'age'=67, 'cost'=10.14 where 'id' < 3");
             System.out.println(Arrays.toString(result4.toArray()));

            List<Map<String,Object>> result5 = starter.execute("DELETE values 'cost', 'age' where 'id' =2");
           System.out.println(Arrays.toString(result5.toArray()));


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
