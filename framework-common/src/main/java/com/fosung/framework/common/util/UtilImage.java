package com.fosung.framework.common.util;

import com.fosung.framework.common.util.support.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

@Slf4j
public abstract class UtilImage {

    /**
     * 解析图片信息
     * @param imagePath
     * @return
     */
    public static ImageInfo parseImageInfo(String imagePath) {

        ImageInfo imageInfo = null ;

        try {
            // 获取图片高度和宽度
            FileInputStream fileInputStream = null ;
            try{
                fileInputStream = new FileInputStream( imagePath ) ;
                BufferedImage sourceImg = ImageIO.read( fileInputStream );
                imageInfo = new ImageInfo() ;
                imageInfo.setWidth( sourceImg.getWidth() ) ;
                imageInfo.setHeight( sourceImg.getHeight() ) ;
            }catch( Exception e ){
                log.error( " 获取图片宽度和高度失败: {}" , imagePath ) ;
                throw new IllegalArgumentException("获取logo宽度和高度失败 "+imagePath) ;
            }finally {
                IOUtils.closeQuietly( fileInputStream ) ;
            }

        }catch (Exception e) {
            log.error("获取图片信息异常" , e);
        }

        return imageInfo ;

    }

}
