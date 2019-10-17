package com.fosung.framework.common.util;

import com.google.common.collect.Sets;

import java.util.Random;
import java.util.Set;

/**
 * 随机数
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public class UtilRandomNum {
    /**
     *
     * @return 随机8为不重复数组
     * @author RICK
     */
    public static String generateNumber(int length) {
        String no = "";
        // 初始化备选数组
        int[] defaultNums = new int[10];
        for (int i = 0; i < defaultNums.length; i++) {
            defaultNums[i] = i;
        }

        Random random = new Random();
        int[] nums = new int[length];
        // 默认数组中可以选择的部分长度
        int canBeUsed = 10;
        // 填充目标数组

        for (int i = 0; i < nums.length; i++) {
            // 将随机选取的数字存入目标数组
            int index = random.nextInt(canBeUsed);
            nums[i] = defaultNums[index];
            // 将已用过的数字扔到备选数组最后，并减小可选区域
            swap(index, canBeUsed - 1, defaultNums);
            canBeUsed--;
        }
        if (nums.length > 0) {
            for (int i = 0; i < nums.length; i++) {
                no += nums[i];
            }
        }

        return no;
    }


    private static void swap(int i, int j, int[] nums) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private static Object object = new Object() ;

    // 存储毫秒中的数字
    private static Set<Integer> numsInMills = Sets.newHashSet() ;

    private static long lastMills = System.currentTimeMillis() ;

    private static Random sequenceRandom = new Random() ;

//    /**
//     * 每毫秒随机生成一个唯一数字
//     * @return
//     */
//    public static int nextIntUniqueByMills(){
//        int random = 0 ;
//        synchronized (object){
//            // 当前毫秒数
//            if( System.currentTimeMillis() != lastMills ){
//                numsInMills.clear();
//                lastMills = System.currentTimeMillis() ;
//            }
//            do {
//                random = sequenceRandom.nextInt(Integer.MAX_VALUE) ;
//            }while ( numsInMills.contains( random ) ) ;
//            //加入组合
//            numsInMills.add( random ) ;
//        }
//
//        return random ;
//    }
}

