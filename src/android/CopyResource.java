package android;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Files;

public class CopyResource {

	static Pattern drawable = Pattern.compile("drawable\\.([^ \\.),;]+)");
	static Pattern layout = Pattern.compile("layout\\.([^ \\.),;]+)");
	static Pattern ref_drawable = Pattern.compile("@drawable/([^\"]+)");

	public static void main(String[] args) {
		copyLayoutWithDrawable(new File(""), new File(""),"");
		
	}

	private static void copyLayoutWithDrawable(File fromResDir,
			File destResDir, String resName) {
		move(fromResDir, destResDir, resName,"layout");// copy layout first
		File toResFile = find(destResDir, resName,"layout");
		if (toResFile != null) {
			String resText = Files.read(toResFile);
			Matcher matcher = ref_drawable.matcher(resText);
			while (matcher.find()) {
				System.out.println("find layout_drawable:"+matcher.group(1));
				move(fromResDir, destResDir, matcher.group(1),"drawable");// copy drawable also
			}
		}
	}

	private static void move(File fromResDir, File destResDir, String resName,String resType) {
		File fromResFile = find(fromResDir, resName,resType);
		if (fromResFile != null) {
			// do copy
			File toResFile = new File(new File(destResDir,
					Files.getMajorName(fromResFile.getParentFile())),
					fromResFile.getName());
			if (toResFile.exists()) {
				System.out.println("contains:" + toResFile.getName());
				Files.deleteFile(fromResFile);
				ensureDrawable(fromResDir,destResDir,resName,resType,toResFile);
				return;
			}
			try {
				Files.createNewFile(toResFile);
				Files.copyFile(fromResFile, toResFile);
				Files.deleteFile(fromResFile);
				ensureDrawable(fromResDir,destResDir,resName,resType,toResFile);
				System.out.println("copy " + fromResFile.getName() + "->"
						+ toResFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void ensureDrawable(File fromResDir, File destResDir,
			String resName, String resType,File toResFile) {
		System.out.println(toResFile.getName());
		if(!"drawable".equals(resType)) return;
		if(toResFile.getName().endsWith(".xml"))
		{
			String resText = Files.read(toResFile);
			Matcher matcher = ref_drawable.matcher(resText);
			while (matcher.find()) {
				System.out.println("find xml_drawable:"+matcher.group(1));
				move(fromResDir, destResDir, matcher.group(1),"drawable");// copy drawable also
			}
		}
	}

	private static File find(File fromRes, String drawableName,final String resType) {
		File[] dirs = fromRes.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().startsWith(resType);
			}
		});
		for (File dir : dirs) {
			for (File f : dir.listFiles()) {
				String name = Files.getMajorName(f);
				if (name.equals(drawableName)) {
					return f;
				}
			}
		}
		return null;
	}
}
