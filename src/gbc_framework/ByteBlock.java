package gbc_framework;

import java.util.Set;

import gbc_framework.rom_addressing.AssignedAddresses;
import gbc_framework.rom_addressing.BankAddress;

public interface ByteBlock 
{
	public String getId();
	public int getWorstCaseSize(AssignedAddresses assignedAddresses);
	public void addAllIds(Set<String> usedIds);
	public void assignBank(byte bank, AssignedAddresses assignedAddresses);
	public void assignAddresses(BankAddress fixedAddress, AssignedAddresses assignedAddresses);
	public void removeAddresses(AssignedAddresses assignedAddresses);
}
