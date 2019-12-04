public class NullPointerClass
{
	public static void main(String[] args)
	{
		String foo = null;
		String bar = new String("Hello");
		String baz = "world";
		if(foo != null)
			System.out.println(foo);
		if(bar != null)
			System.out.println(bar);
		if(baz != null)
			System.out.println(baz);
	}
}