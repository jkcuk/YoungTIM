package library.list;


public class ComplexArray2DCrossSection implements DoubleList1D
{
	public final static int
		DIRECTION_X = 0,
		DIRECTION_Y = 1,
		TYPE_INTENSITY = 0,
		TYPE_PHASE = 1,
		TYPE_REAL_PART = 2,
		TYPE_IMAGINARY_PART = 3;
	
	private ComplexArray2D complexArray2D;
	private int type, direction, index;
	
	public ComplexArray2DCrossSection(ComplexArray2D complexArray2D, int type, int direction, int index)
	{
		this.complexArray2D = complexArray2D;
		this.type = type;
		this.direction = direction;
		this.index = index;
	}
	
	public ComplexArray2DCrossSection(ComplexArray2D complexArray2D)
	{
		this(complexArray2D, TYPE_REAL_PART, DIRECTION_X, 0);
	}
	
	// method that returns the size (in elements) of the table
	@Override
	public int getSize()
	{
		if(direction == DIRECTION_X)
		{	
			// the cross-section is in the x direction, so return the width
			return complexArray2D.getWidth();
		}
		else
		{
			// the cross-section is in the y direction, so return the height
			return complexArray2D.getHeight();
		}
	}
	
	// method that returns the value at index i;
	// indices run between 0 and getSize()-1
	@Override
	public double getElement(int i)
	{
		int horizontalIndex, verticalIndex;
		
		if(direction == DIRECTION_X)
		{
			horizontalIndex = i;
			verticalIndex = index;
		}
		else
		{
			horizontalIndex = index;
			verticalIndex = i;
		}
		
		// System.out.println("(horizontal index, vertical index) = ("+horizontalIndex+","+verticalIndex+")");
		
		if(type == TYPE_REAL_PART)
		{
			return complexArray2D.getElementRe(horizontalIndex, verticalIndex);
		}
		else if(type == TYPE_IMAGINARY_PART)
		{
			return complexArray2D.getElementIm(horizontalIndex, verticalIndex);
		}
		else if(type == TYPE_INTENSITY)
		{
			return complexArray2D.getElement(horizontalIndex, verticalIndex).getAbsSqr();
		}
		else if(type == TYPE_PHASE)
		{
			return complexArray2D.getElement(horizontalIndex, verticalIndex).getArg();
		}
		else
		{
			// not quite sure what this could be
			return -1;
		}
	}

	public ComplexArray2D getComplexArray2D() {
		return complexArray2D;
	}

	public void setComplexArray2D(ComplexArray2D complexArray2D) {
		this.complexArray2D = complexArray2D;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}