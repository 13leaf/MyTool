package gen;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 根据输入的json来分析并且产生一个对应的class
 * @author 13leaf
 *
 */
public class JsonClassGenerator {

	public static void gen(String json,String wrapClassName,String parentPath)
	{
		JsonParser parser=new JsonParser();
		JsonElement element=parser.parse(json);
		
		if(element.isJsonObject())
		{
			
		}
		
	}
	
	public static void main(String[] args) {
		
	}
}
