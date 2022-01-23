package gbc_framework;

import java.io.IOException;
import java.util.Set;

import gbc_framework.rom_addressing.AddressRange;
import gbc_framework.rom_addressing.AssignedAddresses;
import gbc_framework.rom_addressing.BankAddress;

// TODO: Make a base class?
public interface SegmentedByteBlock 
{
	public String getId();
	public default int getWorstCaseSize()
	{
		return getWorstCaseSize(null);
	}
	public int getWorstCaseSize(AssignedAddresses assignedAddresses);
	public Set<String> getSegmentIds();
	// TODO: Split and keep only absolute stuff here/When there aren't already addresses?
	// Might not be very feasible
	public BankAddress getSegmentsRelativeAddresses(
			BankAddress blockAddress,
			AssignedAddresses assignedAddresses, 
			AssignedAddresses relAddresses);
	public void addByteSourceHint(AddressRange hint);
	public BankAddress write(QueuedWriter writer, AssignedAddresses assignedAddresses) throws IOException;
	public void checkAndFillSegmentGaps(
			BankAddress expectedFromPrevSegAddress,
			BankAddress nextSegAddress, 
			QueuedWriter writer,
			String nextSegName
	) throws IOException;
}
