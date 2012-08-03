package gen;


public class ClassEntry {
	
	public static enum Modifier
	{
		_publich("public"),_public_static("public"),_private("private");
		
		private final String string;
		
		private Modifier(String string)
		{
			this.string=string;
		}
		@Override
		public String toString() {
			return string;
		}
	}

	/**
	 * 将ClassEntry包装进当前的ClassEntry
	 * @param entry
	 */
	public void wrap(ClassEntry entry)
	{
		
	}
	
	/**
	 * 将entry添加到当前的ClassEntry(作为一个static class声明)
	 * @param entry
	 */
	public void append(ClassEntry entry)
	{
		
	}
	
}
