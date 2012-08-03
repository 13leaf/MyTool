package android;
import java.io.File;
import java.util.regex.Matcher;

import org.nutz.lang.Files;

/**
 * TODO 做一点小重构
 * @author 13leaf
 *
 */
public class Px2Dp {

	static final double factor=1.5;
	
	public static final int DENSITY_NONE = 0;
	
	//copy from android source (Bitmap)
	public static int scaleFromDensity(int size, int sdensity, int tdensity) {
        if (sdensity == DENSITY_NONE || sdensity == tdensity) {
            return size;
        }
        // Scale by tdensity / sdensity, rounding up.
        return ( (size * tdensity) + (sdensity >> 1) ) / sdensity;
    }
	
	public static void main(String[] args) {
		java.io.File resDir=new java.io.File("res");
		java.io.File[] dirs=org.nutz.lang.Files.scanDirs(resDir);
		java.util.regex.Pattern pxPattern=java.util.regex.Pattern.compile("([\\d\\.]+)px");
		java.util.regex.Pattern textSizePattern=java.util.regex.Pattern.compile("android:textSize\\s*=\\s*\\\"(\\d+)px\\\"|<item\\s+name=\\\"android:textSize\\\"\\s*>\\s*(\\d+)px\\s*</item>");
		System.err.println("px->sp");
		//change px to sp size
		for(java.io.File dir:dirs)
		{
			for(java.io.File file:dir.listFiles())
			{
				if(!file.getName().endsWith(".xml")) continue;
				if(file.isFile())
				{
					System.out.println("handler file:"+file.getName());
					String content=org.nutz.lang.Files.read(file);
					java.util.regex.Matcher matcher=textSizePattern.matcher(content);
					StringBuffer sb=new StringBuffer();
					boolean matched=false;
					while(matcher.find())
					{
						matched=true;
						double mTextSize=0;
						long caledTextSize=0;
						if(matcher.group(1)!=null){
							mTextSize=Double.parseDouble(matcher.group(1));
							caledTextSize=Math.round(mTextSize/factor);
							matcher.appendReplacement(sb, "android:textSize=\""+caledTextSize+"sp\"");
						}else if(matcher.group(2)!=null){
							mTextSize=Double.parseDouble(matcher.group(2));
							caledTextSize=Math.round(mTextSize/factor);
							matcher.appendReplacement(sb, "<item name=\"android:textSize\">"+caledTextSize+"sp</item>");
						}
					}
					matcher.appendTail(sb);
					if(matched)
					{
						Files.write(file, sb.toString());
					}
				}
			}
		}
		System.err.println("px->dp");
		for(File dir:dirs)
		{
			for(File file:dir.listFiles())
			{
				if(file.isFile()&&file.getName().endsWith(".xml"))
				{
					System.out.println("handler file:"+file.getName());
					String content=Files.read(file);
					Matcher matcher=pxPattern.matcher(content);
					boolean matched=false;
					StringBuffer sb=new StringBuffer();
					while(matcher.find())
					{
						matched=true;
						double mSize=Double.parseDouble(matcher.group(1));
						long caledSize=Math.round(mSize/factor);
						matcher.appendReplacement(sb, caledSize+"dip");
					}
					matcher.appendTail(sb);
					if(matched)
					{
						Files.write(file, sb.toString());
					}
				}
			}
		}
	}
}
