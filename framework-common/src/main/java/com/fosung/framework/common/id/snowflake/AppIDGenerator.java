package com.fosung.framework.common.id.snowflake;

import com.fosung.framework.common.id.ContextIdGenerator;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 全局唯一的id生成器，参考twitter的snowflake算法，参考：https://my.oschina.net/CandyDesire/blog/619122 <br>
 * 线程安全，可以创建多个Snowflake2IDGenerator
 */
@Slf4j
public class AppIDGenerator extends ContextIdGenerator<Long> {

    protected char defaultPadChar = '0' ;

    @Getter
    protected AppIDPart defaultAppIDPart ;

    //id每个部分的长度，实际数字为64位，包括一个符号位和63位二进制
    protected Map<AppIDItem,Integer> idItems = Maps.newLinkedHashMap() ;
    {
        //时间戳，更改为以秒为单位，二进制长度改为32位，到2100年
//        idItems.put(IDItem.timestamp , 41) ;
        idItems.put(AppIDItem.timestamp , 32) ;
        //业务线或模块，最多32个。可以在一个产品中区分不同模块，也可用来区分不同产品
//        idItems.put(AppIDItem.business , 5) ;
//        //每条业务线，机房，最多4个
//        idItems.put(AppIDItem.machineroom , 2) ;
        //每个机房，机器小于256
        idItems.put(AppIDItem.machine , 8) ;
        //9位留作自定义，可有512个不同的标识。在分表的时候，可以用做基因，将同类的记录分到一个表或库中
        idItems.put(AppIDItem.custom ,9) ;
        // 每秒内的序列号，单台机器每毫秒最大128写请求，每秒的写请求10w+，调整为5，
        // 单台机器每毫秒最多生成32个id，即每秒最多3.2w个id，为了保证正常运行，每秒控制最多3w个id生成
//        idItems.put(IDItem.num , 5) ;
        //更改为以秒为计数单位，单台机器每秒最多生成2^14个id（1.6w）
        idItems.put(AppIDItem.num , 14) ;
    }

    public AppIDGenerator(){
        this( new AppIDPartDefault() ) ;
    }

    public AppIDGenerator(AppIDPart defaultAppIDPart){
        this.defaultAppIDPart = defaultAppIDPart ;
        //设置生成器中id的组成及每一部分的长度
        this.defaultAppIDPart.setIdItems( idItems );
    }

    /**
     * 将long转换二进制位
     * @return
     */
    public String toBinary( long index ){
        return Long.toString( index , 2 ) ;
    }

    public Long getNextId(AppIDPart appIDPart , Object object){
        StringBuffer idBinary = new StringBuffer() ;
        //id每段索引值和二进制
        long itemIndex = 0  ;
        String itemBinary = "" ;

        //当前的时间，单位秒
        long timeSeconds = System.currentTimeMillis()/1000 ;
        for (Map.Entry<AppIDItem, Integer> idItem : idItems.entrySet()) {
            itemIndex = 0 ;
            switch (idItem.getKey()){
                case timestamp:{
                    itemIndex += appIDPart.getTimeIndex( idItem.getValue() , timeSeconds , object ) ;
                    break;
                }
//                case business:{
//                    itemIndex = appIDPart.getBusinessIndex( idItem.getValue() , timeSeconds , object ) ;
//                    break;
//                }
//                case machineroom:{
//                    itemIndex = appIDPart.getMachineRoomIndex( idItem.getValue() , timeSeconds , object ) ;
//                    break;
//                }
                case machine:{
                    itemIndex = appIDPart.getMachineIndex( idItem.getValue() , timeSeconds , object ) ;
                    break;
                }
                case custom:{
                    itemIndex = appIDPart.getCustomIndex( idItem.getValue() , timeSeconds , object ) ;
                    break;
                }
                case num:{
                    itemIndex = appIDPart.getNumIndex( idItem.getValue() , timeSeconds , object ) ;
                    break;
                }
            }

            //转换为二进制
            itemBinary = toBinary( itemIndex ) ;
            //二进制长度的有效性验证
            Assert.isTrue( itemBinary.length() <= idItem.getValue() , idItem.getKey()+"的值"+itemIndex+"转换为二级制后,长度超过"+idItem.getValue() ) ;
            //长度不够的用0填补
            itemBinary = StringUtils.leftPad( itemBinary , idItem.getValue() , defaultPadChar ) ;
            //合并每一部分
            idBinary.append( itemBinary ) ;
        }

        Assert.isTrue( idBinary.length()>0 && idBinary.length() < 64 , "生成的id长度应超过63位，大于0。先生成id长度为"+idBinary.length() ) ;

        //将二进制转换为long
        long nextId = Long.parseLong( idBinary.toString() , 2 ) ;

        log.debug("生成id为{} , 二进制为{}" , nextId  , idBinary.toString() ) ;

        return nextId ;
    }

    @Override
    public Long getNextId( Object object ){
        return getNextId( this.defaultAppIDPart , object ) ;
    }

}
