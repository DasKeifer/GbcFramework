package gbc_framework;

import java.io.IOException;
import java.util.Set;

import gbc_framework.rom_addressing.AssignedAddresses;
import gbc_framework.rom_addressing.BankAddress;

public interface SegmentedByteBlock 
{
	public String getId();
	public default int getWorstCaseSize()
	{
		return getWorstCaseSize(null);
	}
	public int getWorstCaseSize(AssignedAddresses assignedAddresses);
	public Set<String> getSegmentIds();
	public void assignBank(byte bank, AssignedAddresses assignedAddresses);
	public BankAddress getSegmentsRelativeAddresses(
			BankAddress blockAddress,
			AssignedAddresses assignedAddresses, 
			AssignedAddresses relAddresses);
	public void removeAddresses(AssignedAddresses assignedAddresses);
	public BankAddress write(QueuedWriter writer, AssignedAddresses assignedAddresses) throws IOException;
	public void checkAndFillSegmentGaps(
			BankAddress expectedFromPrevSegAddress,
			BankAddress nextSegAddress, 
			QueuedWriter writer,
			String nextSegName
	) throws IOException;
}
