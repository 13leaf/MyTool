package android;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import org.nutz.lang.Files;


/**
 * 读取资源目录,查找符合规范后缀的选中、普通状态图片。然后在drawable目录中建造出一个标准的android Selector文件。
 * 
 * @author 13leaf
 * 
 */
public class SelectorMaker {

	String normalSuffix;
	String focusSuffix;
	String split;

	public final static String SPLIT = "_";// 文件名分割符
	
	public final static String TEMPLATE_SELECTOR;
	
	private final static String TEMPLATE_RESOURCE_NAME="selectorTemplate.xml";

	private final static String KEY_NORMAL_DRAWABLE="normalDrawable";
	
	private final static String KEY_FOCUS_DRAWABLE="focusDrawable";
	
	static{
		String templateFile=null;
		if(SelectorMaker.class.getPackage()!=null)
		{
			templateFile=SelectorMaker.class.getPackage().getName().replaceAll("\\.", "\\/")
					+"/"+TEMPLATE_RESOURCE_NAME;
		}else {
			templateFile=TEMPLATE_RESOURCE_NAME;
		}
		TEMPLATE_SELECTOR=Files.read(templateFile);
	}
	
	/**
	 * 这里特指后缀。也就是说文件名必须是xx_normal或者xx_focus。 而不是normal_xx,focus_xx
	 * 
	 * @param normalSuffix
	 * @param focusSuffix
	 */
	public SelectorMaker(String normalSuffix, String focusSuffix,String split) {
		this.normalSuffix = normalSuffix;
		this.focusSuffix = focusSuffix;
		this.split=split;
	}

	/**
	 * 使用默认名称为normal,focus的后缀
	 */
	public SelectorMaker() {
		this("normal", "focus",SPLIT);
	}
	
	/**
	 * 当出现重名时，默认不覆盖
	 * @param resPath
	 * @throws FileNotFoundException
	 */
	public void makeSelector(String resPath) throws IOException
	{
		makeSelector(resPath, false);
	}
	
	/**
	 * 
	 * @param resPath
	 * @param reWrite 当出现重复的selector文件时。是否选择覆盖
	 * @throws IOException 
	 */
	public void makeSelector(String resPath,boolean reWrite) throws IOException {
		File resDir = new File(resPath);
		if (!resDir.exists())
			throw new FileNotFoundException();
		File drawableDir = new File(resPath, "drawable");
		if (!drawableDir.exists())
			drawableDir.mkdir();
		File[] drawableDirs = resDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory()
						&& file.getName().startsWith("drawable");
			}
		});
		LinkedList<String> normalSuffixDrawables = new LinkedList<String>();
		LinkedList<String> focusSuffixDrawables = new LinkedList<String>();
		LinkedList<String> validSelectAbleDrawables = new LinkedList<String>();
		for (File dir : drawableDirs) {
			for (File file : dir.listFiles()) {
				if(file.isDirectory()) continue;
				String fileName=getFileNameNoSuffix(file);
				if (fileName.endsWith(split + normalSuffix)) {
					normalSuffixDrawables.add(cutSuffixOfName(fileName, normalSuffix));
					continue;
				}
				if (fileName.endsWith(split + focusSuffix)) {
					focusSuffixDrawables.add(cutSuffixOfName(fileName, focusSuffix));
				}
			}
		}
		if(normalSuffixDrawables.size()>focusSuffixDrawables.size())
		{
			System.err.println("The suffix drawable have normal more then focus");
		}else if(normalSuffixDrawables.size()<focusSuffixDrawables.size())
		{
			System.err.println("The suffix drawable have focus more then normal");	
		}
		
		for(String drawableName:normalSuffixDrawables)
		{
			if(focusSuffixDrawables.contains(drawableName))
			{
				validSelectAbleDrawables.add(drawableName);
			}else {
				System.err.println("Wish to have that focus drawable:"+drawableName);
			}
		}
		
		for(String validDrawableName:validSelectAbleDrawables)
		{
			File selectorFile=new File(drawableDir,validDrawableName+".xml");
			if(selectorFile.exists()){
				System.err.println(validDrawableName+" has already exists in drawable directory");
				if(!reWrite) continue;//若设置不覆盖,则跳过
			}
			selectorFile.createNewFile();
			Properties attribute=new Properties();
			attribute.put(KEY_FOCUS_DRAWABLE, validDrawableName+split+focusSuffix);
			attribute.put(KEY_NORMAL_DRAWABLE, validDrawableName+split+normalSuffix);
			String makedSelector=TemplateParser.parseTemplate(TEMPLATE_SELECTOR, attribute);
			Files.write(selectorFile, makedSelector);
			System.out.println("make selector:"+validDrawableName+" complete!");
		}

	}
	
	/**
	 * 截断指定文件名的后缀
	 * @param name
	 * @param suffix
	 * @return
	 */
	private String cutSuffixOfName(String name,String suffix)
	{
		return name.substring(0, name.lastIndexOf(split+suffix));
	}

	public static String getFileNameNoSuffix(File file) {
		String longFileName = file.getName();
		return longFileName.substring(0, longFileName.lastIndexOf('.'));
	}

	public static void main(String[] args) throws IOException {
		SelectorMaker maker=new SelectorMaker();
		maker.makeSelector("E:\\workSpace_advance\\IfengNewsV3\\res");
	}

}
