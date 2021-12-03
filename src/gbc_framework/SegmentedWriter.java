package gbc_framework;

import java.io.IOException;

public abstract interface SegmentedWriter 
{	
	public abstract void append(byte... bytes) throws IOException;
	public abstract void newSegment(int segmentStartAddress);
} 
