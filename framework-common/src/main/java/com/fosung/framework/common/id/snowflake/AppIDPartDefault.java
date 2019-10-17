package com.fosung.framework.common.id.snowflake;

import com.fosung.framework.common.util.UtilBeanFactory;
import com.fosung.framework.common.util.UtilIp;
import com.fosung.framework.common.util.UtilNumber;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.commons.util.InetUtils;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 不同部分的id生成类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public class AppIDPartDefault implements AppIDPart {

    protected Integer[] machineIp = new Integer[]{} ;

    @Setter
    @Getter
    private Map<AppIDItem,Integer> idItems ;

    private RateLimiter idRateLimiter ;

    @Getter
    private LoadingCache<Long,Set<Integer>> numCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5 , TimeUnit.SECONDS)
            .softValues()
            .build(new CacheLoader<Long, Set<Integer>>() {
                @Override
                public Set<Integer> load(Long key) throws Exception {
                    return Sets.newHashSet() ;
                }
            }) ;

    private static Random sequenceRandom = new Random() ;

    @Getter
    private int maxIdsPerSecond = 0 ;

    /**
     * 设置当前的id组成信息
     * @param idItems
     */
    @Override
    public void setIdItems(Map<AppIDItem,Integer> idItems){
        this.idItems = idItems ;
        //每毫秒的个数
        int secondNumLength = this.idItems.get( AppIDItem.num ) ;

        //通过令牌桶的控流算法，控制每秒id的生成个数。并且为每秒生成的id个数保留一定的20%的余量
        this.maxIdsPerSecond = Double.valueOf( UtilNumber.getMaxNum(secondNumLength)*0.8 ).intValue() ;

        this.idRateLimiter = RateLimiter.create( maxIdsPerSecond ) ;

        log.info("每秒生成{}个左右的id" , maxIdsPerSecond) ;
    }

    /**
     * 获取本机器的ip地址
     * @return
     */
    public void initMachineIp(){
        if( machineIp.length < 4 ){
            InetUtils inetUtils = null ;
            try{
                inetUtils = UtilBeanFactory.getApplicationContext().getBean( InetUtils.class ) ;

                InetUtils.HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo() ;

                machineIp = UtilIp.getIp4Parts( hostInfo.getIpAddress() ) ;
            }catch( Exception e ){
                log.error( "无法获取本机ip地址 : {}" , e ) ;
            }

        }
    }

    @Override
    public String getName() {
        return "基于snowflake的id生成器";
    }

    /**
     * 按照秒生成唯一的随机数。
     * @param binaryLength
     * @return
     */
    @Override
    public long getNumIndex(int binaryLength , long timeSeconds , Object object ){
        initMachineIp() ;

        // 限制id生成的速度
        idRateLimiter.acquire() ;

        // 生成一个随机的id
        Set<Integer> numSets = numCache.getUnchecked( timeSeconds ) ;

        // 允许生成最大十进制数字
        int maxNum = UtilNumber.getMaxNum(binaryLength) ;
        int random = 0 ;

        // 使用随机算法生成一个不重复的数字，避免固定的数值取余数运算时分布不均匀
        synchronized ( machineIp ){
            do {
                random = sequenceRandom.nextInt( maxNum ) ;
            }while ( numSets.contains( random ) ) ;
            //加入组合
            numSets.add( random ) ;
        }

        return random ;
    }

    /**
     * 获取自定义字段内容索引
     * @param binaryLength
     * @return
     */
    @Override
    public long getCustomIndex(int binaryLength , long timeSeconds , Object object ){
        return 0 ;
    }

    /**
     * 获取机器的标识，默认收集第三部分，<b>在docker中宿主机的ip默认是第三部分</b>
     * @return
     */
    @Override
    public long getMachineIndex(int binaryLength , long timeSeconds , Object object ){
        initMachineIp() ;
        return machineIp.length < 4 ? 0 : machineIp[ machineIp.length - 1 ] ;
    }

    /**
     * 获取时间索引
     * @return
     */
    @Override
    public long getTimeIndex(Integer binaryLength , long timeSeconds , Object object ){
        return timeSeconds ;
    }

//    public static void main(String[] args) throws Exception {
//        AppIDGenerator snowflake2IDGenerator = new AppIDGenerator() ;
//        long second = 0 ;
//        Long nextId = null ;
//        Stopwatch stopwatch = Stopwatch.createStarted() ;
//        while (stopwatch.elapsed(TimeUnit.SECONDS)<10){
//            second = (System.currentTimeMillis()/1000) ;
//            nextId = snowflake2IDGenerator.getNextId(null) ;
////            System.out.println(second+"== "+nextId);
//        }
//        stopwatch.stop() ;
//
//        for (Map.Entry<Long,Set<Integer>> entry : ((DefaultSnowflakeIDPart) snowflake2IDGenerator.getSnowflakeIDPart()).getNumCache().asMap().entrySet()) {
//            System.out.println(entry.getKey()+"  "+entry.getValue().size());
//        }
//
//        System.out.println("完成");
//
//    }
//    public static void main(String[] args) {
//        String hostIp = "" ;
//        try {
//            hostIp = InetAddress.getLocalHost().getHostAddress() ;
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        List<Integer> tmpIp = null ;
//        //分割ip地址
//        List<String> ipParts = Splitter.on('.').trimResults().splitToList( hostIp ) ;
//        if( ipParts!=null && ipParts.size()==4 ){
//            tmpIp = Lists.newArrayListWithCapacity( 4 ) ;
//            for (String ipPart : ipParts) {
//                tmpIp.add( Ints.tryParse( ipPart ) ) ;
//            }
//        }
//
//
//
//    }

}
