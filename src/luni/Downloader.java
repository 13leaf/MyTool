package luni;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

public class Downloader {

	/**
	 * 通知下载完成情况
	 * 
	 * @author 13leaf
	 * 
	 */
	public interface PublishCallBack {
		/**
		 * @param downSize
		 *            已下载字节大小
		 * @param fullSize
		 *            总下载字节大小
		 * @param percent
		 *            下载百分比。公式如下
		 *            Math.floor(downSize/fullSize*100)。注意不是百分比例,如返回50,则表示完成百分之50
		 */
		boolean publish(long downSize, long fullSize, int percent);
	}

	public final static int DEFAULT_BLOCK_SIZE = 1024*8;

	public static void downLoad(String url, String targetPath, int blockSize,
			PublishCallBack publisher) throws IOException {
		File target = new File(targetPath);
		Files.createNewFile(target);
		OutputStream targetOutputStream = new FileOutputStream(target,true);
		byte[] block = new byte[blockSize];

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(new HttpGet(url));

		InputStream is = response.getEntity().getContent();

		long downSize = target.length();
		long fullSize = response.getEntity().getContentLength();

		try {
			int count;
			is.skip(downSize);
			while ((count = is.read(block)) != -1) {
				// write block and publish task
				targetOutputStream.write(block, 0, count);
				downSize += count;
				if (publisher != null) {
					if(publisher.publish(downSize, fullSize, (int) (Math
							.floor(downSize / (double) fullSize * 100))))
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			Streams.safeClose(targetOutputStream);
			Streams.safeClose(is);
		}
	}

	public static void downLoad(String url, String targetPath)
			throws IOException {
		downLoad(url, targetPath, DEFAULT_BLOCK_SIZE, null);
	}

	public static void main(String[] args) throws IOException {
		downLoad("http://services.google.com/fh/files/blogs/our_mobile_planet_china_zh_CN.pdf", "zh.pdf");
//		downLoad("http://202.107.35.126:8011/main_setup.exe", "waga.exe",DEFAULT_BLOCK_SIZE,new PublishCallBack() {
//			
//			@Override
//			public boolean publish(long downSize, long fullSize, int percent) {
////				if(downSize>1024*1024*2) return true;
//				System.out.println("down "+percent);
//				return false;
//			}
//		});
	}
}
