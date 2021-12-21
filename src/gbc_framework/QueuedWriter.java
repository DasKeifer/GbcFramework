package gbc_framework;

import java.io.IOException;
import java.util.List;

import gbc_framework.rom_addressing.AddressRange;

public abstract interface QueuedWriter 
{	
	public abstract void append(byte... bytes) throws IOException;
	public abstract void startNewBlock(int segmentStartAddress);
	public abstract void startNewBlock(int segmentStartAddress, List<AddressRange> reuseHints);
	public abstract void blankUnusedSpace(AddressRange range);
	
	public default void blankUnusedSpace(List<AddressRange> ranges) 
	{
		for (AddressRange range : ranges)
		{
			blankUnusedSpace(range);
		}
	}
} 
