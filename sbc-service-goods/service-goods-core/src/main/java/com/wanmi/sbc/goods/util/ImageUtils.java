package com.wanmi.sbc.goods.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

/**
 * 图片添加水印
 * 
 */
@Slf4j
public class ImageUtils {

	private static byte[] _watermark(String URL) throws IOException {
//		// 读取原图片信息 得到文件（本地图片）
//		File srcImgFile = new File(URL); // D:/install/4.jpg
//		// 将文件对象转化为图片对象
//		Image srcImg = ImageIO.read(srcImgFile);
//		// 获取图片的宽
//		int srcImgWidth = srcImg.getWidth(null);
//		// 获取图片的高
//		int srcImgHeight = srcImg.getHeight(null);
//		System.out.println("图片的宽:" + srcImgWidth);
//		System.out.println("图片的高:" + srcImgHeight);

		// 创建一个URL对象,获取网络图片的地址信息（网络图片）
		URL url = new URL(URL); // "https://xyytest-image.oss-cn-zhangjiakou.aliyuncs.com/202212081004596586.jpg"
		//将URL对象输入流转化为图片对象 (url.openStream()方法，获得一个输入流)
		Image srcImg = ImageIO.read(url.openStream());
		//获取图片的宽
		int srcImgWidth = srcImg.getWidth(null);
		//获取图片的高
		int srcImgHeight = srcImg.getHeight(null);
//		System.out.println("图片的宽:"+srcImgWidth);
//		System.out.println("图片的高:"+srcImgHeight);

		BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
		// 加水印
		// 创建画笔
		Graphics2D g = bufImg.createGraphics();
		// 绘制原始图片
		g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
		// -------------------------文字水印 start----------------------------
		//        //根据图片的背景设置水印颜色
		//        g.setColor(new Color(255,255,255,128));
		//        //设置字体  画笔字体样式为微软雅黑，加粗，文字大小为60pt
		//        g.setFont(new Font("微软雅黑", Font.BOLD, 60));
		//        String waterMarkContent="图片来源：https://image.baidu.com/";
		//        //设置水印的坐标(为原图片中间位置)
		//        int x=(srcImgWidth - getWatermarkLength(waterMarkContent, g)) / 2;
		//        int y=srcImgHeight / 2;
		//        //画出水印 第一个参数是水印内容，第二个参数是x轴坐标，第三个参数是y轴坐标
		//        g.drawString(waterMarkContent, x, y);
		//        g.dispose();
		// -------------------------文字水印 end----------------------------

		// -------------------------图片水印 start----------------------------
		// 水印文件
		String waterMarkImage = "src/main/resources/image/XYY.png";
		Image srcWaterMark = ImageIO.read(new File(waterMarkImage));
		// 获取水印图片的宽度
		int widthWaterMark = srcWaterMark.getWidth(null);
		// 获取水印图片的高度
		int heightWaterMark = srcWaterMark.getHeight(null);
		// 设置 alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
		// 绘制水印图片 坐标为中间位置
		g.drawImage(srcWaterMark, (srcImgWidth - widthWaterMark) / 2, (srcImgHeight - heightWaterMark) / 2,
				widthWaterMark, heightWaterMark, null);
		// 水印文件结束
		g.dispose();
		// -------------------------图片水印 end----------------------------

		// 待存储的地址
//		int i_idx_1 = URL.lastIndexOf('\\');
//		int i_idx_2 = URL.lastIndexOf('.');
//		String str_fileName = URL.substring(i_idx_1 + 1, i_idx_2);
//		String str_extName = URL.substring(i_idx_2 + 1);
//		String tarImgPath = "d:/install/" + str_fileName + "_wm." + str_extName; // png jpg
		// 输出图片
//		FileOutputStream os = new FileOutputStream(tarImgPath);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(bufImg, "jpg", os); // png
		os.flush();
		os.close();
//		System.out.println("添加水印完成");
		return os.toByteArray();
	}

	/**
	 * 获取水印文字的长度
	 * 
	 * @param waterMarkContent
	 * @param g
	 * @return
	 */
	private static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
		return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
	}
	
	/**
	 * 水印图
	 * @param URL
	 * @return
	 */
	public static byte[] watermark(String URL) {
		try {
			return _watermark(URL);
		} catch (IOException e) {
			log.error("URL为：" + URL, e);
		}
		return null;
	}
	
	public static void main(String[] args) {
//		watermark("https://xyytest-image.oss-cn-zhangjiakou.aliyuncs.com/202212081004596586.jpg");
		watermark("D:\\install\\202011131631399054.jpg");
		watermark("D:\\install\\202011131634242750.jpg");
		watermark("D:\\install\\202011131746253753.jpg");
		watermark("D:\\install\\202011150939438112.jpg");
		watermark("D:\\install\\20221209101357.jpg");
		watermark("D:\\install\\202212091013571.jpg");
		watermark("D:\\install\\202212091013572.jpg");
		watermark("D:\\install\\202212091013573.jpg");
	}
	
}
