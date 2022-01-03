package gbc_framework.rom_addressing;

import gbc_framework.RomConstants;
import gbc_framework.utils.RomUtils;


public class BankAddress 
{
	public static final byte UNASSIGNED_BANK = -1;
	public static final short UNASSIGNED_ADDRESS = -1;

	public static final BankAddress UNASSIGNED = new BankAddress(UNASSIGNED_BANK, UNASSIGNED_ADDRESS);
	public static final BankAddress ZERO = new BankAddress((byte) 0, (short) 0);
	
	private byte bank;
	private short addressInBank;
	
	public BankAddress()
	{
		bank = UNASSIGNED.bank;
		addressInBank = UNASSIGNED.addressInBank;
	}
	
	public BankAddress(byte bank, short addressInBank)
	{
		setBank(bank);
		setAddressInBank(addressInBank);
	}

	public BankAddress(BankAddress toCopy) 
	{
		bank = toCopy.bank;
		addressInBank = toCopy.addressInBank;
	}
	
	public BankAddress(int globalAddress) 
	{		
		if (globalAddress < 0 || globalAddress >= RomConstants.NUMBER_OF_BANKS * RomConstants.BANK_SIZE)
		{
			throw new IllegalArgumentException("BankAddress - invalid globalAddress given (" + globalAddress + 
					") - the globalAddress must be between 0 and " + RomConstants.NUMBER_OF_BANKS * RomConstants.BANK_SIZE); 
		}
		setBank(RomUtils.determineBank(globalAddress));
		setAddressInBank(RomUtils.convertToBankOffset(globalAddress));
	}
	
	public BankAddress newAtStartOfBank()
	{
		return new BankAddress(bank, (short) 0);
	}

	public BankAddress newAtStartOfNextBank() 
	{
		if (isBankRange((byte) (bank + 1)))
		{
			return new BankAddress((byte) (bank + 1), (short) 0);
		}
		return null;
	}
	
	public BankAddress newOffsettedWithinBank(int offset)
	{
		if (!isAddressInBankRange(addressInBank + offset))
		{
			return null;
		}
		return new BankAddress(bank, (short) (addressInBank + offset));
	}
	
	public enum LimitType
	{
		IN_VALID_RANGES,
		WITHIN_BANK,
		WITHIN_BANK_OR_START_OF_NEXT
	}
	
	public enum ToUseType
	{
		BANK_AND_BANK_IN_ADDRESS,
		BANK_ONLY,
		ADDRESS_IN_BANK_ONLY
	}
	
	public BankAddress newSum(BankAddress toAdd)
	{
		return newSum(toAdd, ToUseType.BANK_AND_BANK_IN_ADDRESS, LimitType.IN_VALID_RANGES);
	}
	
	public BankAddress newSum(BankAddress toAdd, ToUseType whatToAdd)
	{
		return newSum(toAdd, whatToAdd, LimitType.IN_VALID_RANGES);
	}

	// TODO: Add isFullAddress check to many of these?
	public BankAddress newSum(BankAddress toAdd, ToUseType whatToAdd, LimitType limit)
	{
		boolean valid = true;
		int bankSum = bank;
		int addressSum = addressInBank;
		switch(whatToAdd)
		{
		case BANK_ONLY:
			bankSum += toAdd.bank;
			break;
		case ADDRESS_IN_BANK_ONLY:
			addressSum += toAdd.addressInBank;
			break;
		case BANK_AND_BANK_IN_ADDRESS:
		default:
			bankSum += toAdd.bank;
			addressSum += toAdd.addressInBank;
			break;
		}
		
		// Check the address
		if (!isAddressInBankRange(addressSum))
		{
			// Switch behavior based on our limit
			switch (limit)
			{
			case IN_VALID_RANGES:
				bankSum++;
				addressSum -= RomConstants.BANK_SIZE;
				break;
			case WITHIN_BANK_OR_START_OF_NEXT:
				if (!isAddressInBankRange(addressSum - 1))
				{
					valid = false;
				}
				break;
			case WITHIN_BANK:
			default:
				valid = false;
				break;
			}
		}
		
		// Now check bank if address is valid
		if (valid)
		{
			switch (limit)
			{
			case IN_VALID_RANGES:
				valid = isBankRange(bankSum);
				break;
			case WITHIN_BANK_OR_START_OF_NEXT:
				valid = bankSum == bank ||
					(bankSum - 1 == bank && addressSum == 0);
				break;
			case WITHIN_BANK:
			default:
				valid = bankSum == bank;
				break;
			}
		}
		
		return valid ? new BankAddress((byte) bankSum, (short) addressSum) : null;
	}
	
	public BankAddress newAbsoluteDifferenceBetween(BankAddress toGetRelativeOf)
	{
		return new BankAddress(Math.abs(getDifference(toGetRelativeOf)));
	}
	
	public boolean offsetWithinBank(int offset)
	{
		if (!isAddressInBankRange(addressInBank + offset))
		{
			return false;
		}
		addressInBank += offset;
		return true;
	}

	public void setToCopyOf(BankAddress toCopy) 
	{
		bank = toCopy.bank;
		addressInBank = toCopy.addressInBank;
	}

	public void setBank(byte bank) 
	{
		if (!isBankRange(bank))
		{
			throw new IllegalArgumentException("BankAddress - invalid bank given (" + bank + 
					") - the bank must be between 0 and " + RomConstants.NUMBER_OF_BANKS + 
					" or the reserved UNASSIGNED_BANK value (" + UNASSIGNED_BANK + ")"); 
		}
		this.bank = bank;
	}
	
	public void setAddressInBank(short addressInBank) 
	{
		if (!isAddressInBankRange(addressInBank))
		{
			throw new IllegalArgumentException("BankAddress - invalid addressInBank given (" + addressInBank + 
					") - the bank must be between 0 and " + RomConstants.BANK_SIZE +
					" or the reserved UNASSIGNED_ADDRESS value (" + UNASSIGNED_ADDRESS + ")"); 
		}
		this.addressInBank = addressInBank;
	}
	
	private boolean isBankRange(int toCheck)
	{
		return (toCheck >= 0 && toCheck < RomConstants.NUMBER_OF_BANKS) ||
				toCheck == UNASSIGNED_BANK;
	}
	
	private boolean isAddressInBankRange(int toCheck)
	{
		return (toCheck >= 0 && toCheck < RomConstants.BANK_SIZE) ||
				toCheck == UNASSIGNED_ADDRESS;
	}

	public boolean isFullAddress() 
	{
		return bank != UNASSIGNED_BANK && addressInBank != UNASSIGNED_ADDRESS;
	}

	public boolean isBankUnassigned() 
	{
		return bank == UNASSIGNED_BANK;
	}

	public boolean isAddressInBankUnassigned() 
	{
		return addressInBank == UNASSIGNED_ADDRESS;
	}

	public boolean isSameBank(BankAddress toCheck) 
	{
		return bank == toCheck.bank;
	}

	public boolean fitsInBankAddressWithOffset(int offset) 
	{
		// Can't check if this address isn't assigned yet
		if (addressInBank == UNASSIGNED_ADDRESS)
		{
			return false;
		}
		
		// -1 to make the check "inclusive"
		// i.e. if the address is the last address in the bank, it can still fit
		// one byte at that address. If its the first address in the bank, then
		// it can fit the whole bank size
		return isAddressInBankRange(addressInBank + offset - 1);
	}

	public byte getBank()
	{
		return bank;
	}

	public short getAddressInBank()
	{
		return addressInBank;
	}
	
	public int getDifference(BankAddress other)
	{
		return (other.bank - bank) * RomConstants.BANK_SIZE +
				other.addressInBank - addressInBank;
	}
	
	public AddressRange getDifferenceAsRange(BankAddress other)
	{
		int globalAddress = RomUtils.convertToGlobalAddress(bank, addressInBank);
		int otherGlobalAddress = RomUtils.convertToGlobalAddress(other.bank, other.addressInBank);
		if (globalAddress > otherGlobalAddress)
		{
			return new AddressRange(otherGlobalAddress, globalAddress);
		}
		else if (globalAddress < otherGlobalAddress)
		{
			return new AddressRange(globalAddress, otherGlobalAddress);
		}

		return new AddressRange(globalAddress, globalAddress + 1);
	}
	
    @Override
    public boolean equals(Object o) 
    {
        // If the object is compared with itself then return true 
        if (o == this) {
            return true;
        }
 
        // Check if it is an instance of BankAddress
        if (!(o instanceof BankAddress)) 
        {
            return false;
        }
        
        // Compare the data and return accordingly
        BankAddress ba = (BankAddress) o;
        return Byte.compare(bank, ba.bank) == 0 && Short.compare(addressInBank, ba.addressInBank) == 0;
    }
    
    @Override
    // Not used but to added since equals was overridden
    public int hashCode() 
    {
    	return bank << 16 + addressInBank;
    }

	@Override
	public String toString()
	{
		return String.format("0x%x:%4x(%d)", bank, addressInBank + 0x4000, RomUtils.convertToGlobalAddress(bank, addressInBank));
	}
}
