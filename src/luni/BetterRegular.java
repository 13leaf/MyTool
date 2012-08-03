package luni;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.el.El;
import org.nutz.lang.Files;

/**
 * 具备更强的替换功能,可以支持$1~$9这样的替换符去替换指定内容。
 * 并支持<%%>做eval
 * @author 13leaf
 *
 */
public class BetterRegular {

	/**
	 * 
	 * @param regExp 抓取的正则表达式
	 * @param replaceExp 替换表达式。包含占位符功能
	 * @param string 目标文本
	 * @param eval 是否eval表达式
	 * @return
	 */
	public static LinkedList<String> replace(String regExp,String replaceExp,String string)
	{
		Matcher matcher=Pattern.compile(regExp).matcher(string);
		LinkedList<String> result=new LinkedList<String>();
		while(matcher.find())
		{
			String aResult=replaceExp;
			for(int i=1;i<=matcher.groupCount();i++)
			{
				aResult=aResult.replaceAll("\\$"+i, matcher.group(i));//注意$在正则中是个特殊字符,表示末尾的意思。必须要转义
				aResult=ensureEval(aResult);
			}
			if(aResult!=null)
				result.add(aResult);
		}
		return result;
	}
	
	/**
	 * 支持eval。对<%.+?%>部分进行El。
	 * @param regExp
	 * @param replaceExp
	 * @param s
	 * @return
	 */
	public static String searchAndReplace(String regExp,String replaceExp,String s)
	{
		Matcher matcher=Pattern.compile(regExp).matcher(s);
		StringBuffer result=new StringBuffer();
		while(matcher.find())
		{
			String aResult=replaceExp;
			for(int i=1;i<=matcher.groupCount();i++)
			{
				aResult=aResult.replaceAll("\\$"+i, matcher.group(i));//注意$在正则中是个特殊字符,表示末尾的意思。必须要转义
				aResult=ensureEval(aResult);
			}
			if(aResult!=null)
				matcher.appendReplacement(result, aResult);
		}
		matcher.appendTail(result);
		return result.toString();
	}
	
	public static void searchAndReplaceFile(String regExp,String replaceExp,String filePath)
	{
		String content=Files.read(filePath);
		Files.write(filePath, searchAndReplace(regExp, replaceExp, content));
	}
	
	static final Pattern EVAL_PATTERN=Pattern.compile("<%(.+?)%>");
	private static String ensureEval(String s)
	{
		if(s==null) return null;
		Matcher matcher=EVAL_PATTERN.matcher(s);
		StringBuffer buffer=new StringBuffer();
		while(matcher.find())
		{
			matcher.appendReplacement(buffer, El.eval(matcher.group(1)).getString());
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(replace("(wang\\d)", "$1", "wang1feng & fengwang2"));
		System.out.println(searchAndReplace("(\\d+)px", "<%($1/2)%>px", ".schedule-card .game-list {;padding-left: 10px;padding-right: 10px;}"));
	}
}
