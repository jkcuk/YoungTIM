/*

classes for simple control over public variables in objects

public interface:
	public interface SelfExplainingObject

public classes:
	public class ObjectPanel extends Panel
	public class ObjectDialog extends Dialog

example:

	import java.applet.Applet;
	import johannes.util.*;

	public class TestClass
	implements SelfExplainingObject
	{
		public int i = 1;
		public double d = 123.456;
		public float f = 3.14;
		public String s = "Hello, world!";
		public boolean b = false;
		
		public String getExplanation()
		{
			return "Explanation.\nNewlines allowed.";
		}
	}
	
	public class TrivialApplet extends Applet
	{
		public void init()
		{
			TestClass t = new TestClass();
			Frame f = new Frame();
			ObjectDialog od = new ObjectDialog(f, "t", t);

			// free system resources associated with dialog
			od.dispose();
                        // free system resources associated with frame
                        f.dispose();
			
			System.out.println(
				"i = " + t.i + ", " +
				"d = " + t.d + ", " +
				"f = " + t.f + ", " +
				"s = " + t.s + ", " +
				"b = " + t.b
			);
		}		
	}

*/

package library.util;


public interface SelfExplainingObject
{
	public String getExplanation();
}