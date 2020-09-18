package com.filemanager.fm;

import net.minidev.json.JSONUtil;
import org.w3c.dom.ls.LSOutput;

import java.util.*;
import java.util.stream.Collectors;

public class foreachTest {
    public static void main(String[] args) {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("name", "张三");
        tempMap.put("age", "19");
        tempMap.put("address", "四川");

        //1.传统的遍历map方式
        for (Map.Entry<String, String> entry : tempMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " " + value);
        }

        //2.使用jdk1.8提供的foreach
        tempMap.forEach((k, v) -> {
            if ("name".equals(k)) {
                System.out.println(" value=" + v);
            }
        });
        tempMap.forEach((k, v) -> {
            System.out.println(k + v);
        });


        List<String> arrList = new ArrayList<>();
        arrList.add("张三");
        arrList.add("万物");
        arrList.add("赵六");

        //1.传统list遍历
        for (String str : arrList) {
            System.out.println(str);
        }

        //2.使用foreach
        arrList.forEach((item) -> {
            if (item.equals("张三")) {
                System.out.println(item);
            }
        });

        //3.使用stream结合lambda表达式，::为lambda表达式的一个重要特性方法引用
        arrList.stream().filter(s -> s.contains("赵六")).forEach(System.out::println);

        List<Integer> list01 = Arrays.asList(1, 2, 3);
        List<Integer> integerList = list01.stream().map(x -> x * 2).collect(Collectors.toList());
        //System.out.println(integerList.toString());
        integerList.forEach(System.out::println);

        //4.创建线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread 1 start");
            }
        }).start();

        //5.使用lambda表达式创建线程
        new Thread(() ->{
            System.out.println("thread 2 start");
        }).start();

        //6.传统集合排序，对象之间的比较
        List<Integer> list2 = Arrays.asList(1,3,5);
       Collections.sort(list2, new Comparator<Integer>() {
           @Override
           public int compare(Integer o1, Integer o2) {
               return o1-o2;//升序
           }
       });
       list2.forEach(System.out::print);
        System.out.println("############");
       //7.使用lambda表达式
        List<Integer> list3 = Arrays.asList(2,4,6);
        Collections.sort(list3,(o1,o2) -> o2-o1);//降序
        list3.forEach(System.out::print);

    }
}
