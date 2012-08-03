package other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.nutz.lang.Files;

public class ProjectEncodeConvertor {

	private static FileCharsetDetector charsetDetector=new FileCharsetDetector();
	
	public static void main(String[] args) {
		args=new String[]{"E:\\workSpace_web\\MercuryAtmo\\WebContent\\olympic\\platform\\android"};
		
		if(args.length!=1){
			System.out.println("use this command: ProjectEncodeConvertor xxx\n" +
					"and then convert the project to utf8\n" +
					"example:java -jar ProjectEncodeConvertor projects\\AutoPackageDemo");
		}else {
			String projectPath=args[0];
			convertProjectEncode(new File(projectPath));
		}
//		detectProjectEncode(new File("E:\\workSpace\\AndroidPackageTool\\projects\\IfengOpenBook"), "UTF-8");
	}

	/**
	 * 递归调用,转换文件编码
	 * @param parentDir
	 */
	public static void convertProjectEncode(File parentDir) {
		if(parentDir.isFile())
		{
			convertFileEncode(parentDir);
			return;
		}
		for(File file : parentDir.listFiles())
		{
			if(file.isDirectory()) convertProjectEncode(file);
			else if(file.getName().endsWith(".java")){
				convertFileEncode(file);
			}
		}
	}
	
	/**
	 * 递归调用,转换文件编码
	 * @param parentDir
	 */
	public static void detectProjectEncode(File parentDir,String expect) {
		if(parentDir.isFile())
		{
			detectProjectEncode(parentDir,expect);
			return;
		}
		for(File file : parentDir.listFiles())
		{
			if(file.isDirectory()) detectProjectEncode(file,expect);
			else if(file.getName().endsWith(".java")){
				try {
					String charsetName=charsetDetector.guestFileEncoding(file,FileCharsetDetector.HINT_CHINESE);
					if(!charsetName.equals(expect))
						System.out.println(file.getName()+" -> "+charsetName);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 自动识别读入编码，并转换文件编码至UTF8格式
	 * @param file
	 */
	public static void convertFileEncode(File file)
	{
		try {
			String charsetName=charsetDetector.guestFileEncoding(file,FileCharsetDetector.HINT_CHINESE);
			System.out.println("detect "+file.getName()+"'s encode is "+charsetName);
			String content=readTextFile(file, true,Charset.forName(charsetName));
			Files.write(file, content);
			System.out.println(file.getName()+"convert2Utf8 complete");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 读取一个指定编码格式的文本文件。若设置wrapLine为false，则读取的String将去除换行符。
	 * 
	 * @param file
	 * @param 是否包含换行符
	 * @return
	 */
	public static String readTextFile(File file, boolean wrapLine,
			Charset charset) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), charset));
			String line = reader.readLine();
			do {
				sb.append(line);
				if (wrapLine)
					sb.append("\r\n");
				line = reader.readLine();
			} while (line != null);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
}
