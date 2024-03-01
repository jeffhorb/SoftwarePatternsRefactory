/*
 * 
 * This class is for accessing, creating and modifying records in a file
 * 
 * */

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;

	// Create new file
	public void createFile(String fileName) {
		RandomAccessFile file = null;
		try // open file for reading and writing
		{
			file = new RandomAccessFile(fileName, "rw");
		} // end try
		catch (IOException ioException) {
			handleFileError("Error processing file!");
		} // end catch
		finally {
			closeFile(file);
			} // end try
	} // end createFile

	// Open file for adding or changing records
	public void openWriteFile(String fileName) {
		try // open file
		{
			output = new RandomAccessFile(fileName, "rw");
		} // end try
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "File does not exist!");
		} // end catch
	} // end method openFile

	// Close file for adding or changing records
	public void closeWriteFile() {
		// close file and exit
		closeFile(output);
		} // end closeFile

	// Add records to file
	public long addRecords(Employee employeeToAdd) {
		Employee newEmployee = employeeToAdd;
		long currentRecordStart = 0;

		// object to be written to file
		RandomAccessEmployeeRecord record;

		try // output values to file
		{
			record = new RandomAccessEmployeeRecord(newEmployee);
			output.seek(output.length());// Look for proper position
			record.write(output);// Write object to file
			currentRecordStart = output.length();
		} // end try
		catch (IOException ioException) {
			 handleFileError("Error writing to file!");
		} // end catch

		// Return position where object starts in the file
		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;
	}// end addRecords

	// Change details for existing object
	public void changeRecords(Employee newDetails, long byteToStart) {
		long currentRecordStart = byteToStart;
		// object to be written to file
		RandomAccessEmployeeRecord record;
		Employee oldDetails = newDetails;
		try // output values to file
		{
            record = new RandomAccessEmployeeRecord(oldDetails);
			output.seek(currentRecordStart);// Look for proper position
			record.write(output);// Write object to file
		} // end try
		catch (IOException ioException) {
			handleFileError("Error writing to file!");
		} // end catch
	}// end changeRecors

	// Delete existing object
	public void deleteRecords(long byteToStart) {
		long currentRecordStart = byteToStart;
		
		// object to be written to file
		RandomAccessEmployeeRecord record;

		try // output values to file
		{
			record = new RandomAccessEmployeeRecord();// Create empty object
			output.seek(currentRecordStart);// Look for proper position
			record.write(output);// Replace existing object with empty object
		} // end try
		catch (IOException ioException) {
			handleFileError("Error writing to file!");
		} // end catch
	}// end deleteRecords

	// Open file for reading
	public void openReadFile(String fileName) {
		try // open file
		{
			input = new RandomAccessFile(fileName, "r");
		} // end try
		catch (IOException ioException) {
			handleFileError("Error writing to file!");
		} // end catch
	} // end method openFile

	// Close file
	public void closeReadFile() {
		closeFile(input);
	} // end method closeFile

	// Get position of first record in file
	public long getFirst() {
		long byteToStart = 0;

		try {// try to get file
			input.length();
		} // end try
		catch (IOException e) {
		}// end catch
		
		return byteToStart;
	}// end getFirst

	// Get position of last record in file
	public long getLast() {
		long byteToStart = 0;

		try {// try to get position of last record
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}// end try 
		catch (IOException e) {
		}// end catch

		return byteToStart;
	}// end getFirst

	// Get position of next record in file
	public long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {// try to read from file
			input.seek(byteToStart);// Look for proper position in file
			// if next position is end of file go to start of file, else get next position
			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException e) {
		} // end catch
		catch (IOException e) {
		}// end catch
		return byteToStart;
	}// end getFirst

	// Get position of previous record in file
	public long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {// try to read from file
			input.seek(byteToStart);// Look for proper position in file
			// if previous position is start of file go to end of file, else get previous position
			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		} // end try
		catch (NumberFormatException e) {
		} // end catch
		catch (IOException e) {
		}// end catch
		return byteToStart;
	}// end getPrevious

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) {
		Employee thisEmp = null;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read file and get record
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);// Read record from file
		} // end try
		catch (IOException e) {
		}// end catch
		
		thisEmp = record;

		return thisEmp;
	}// end readRecords

	// Check if PPS Number already in use
	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long oldByteStart = currentByteStart;
		long currentByte = 0;

		try {// try to read from file and look for PPS Number
			// Start from start of file and loop until PPS Number is found or search returned to start position
			while (currentByte != input.length() && !ppsExist) {
				//if PPS Number is in position of current object - skip comparison
				if (currentByte != oldByteStart) {
					input.seek(currentByte);// Look for proper position in file
					record.read(input);// Get record from file
					// If PPS Number already exist in other record display message and stop search
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, "PPS number already exist!");
					}// end if
				}// end if
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		} // end try
		catch (IOException e) {
		}// end catch

		return ppsExist;
	}// end isPpsExist

	// Check if any record contains valid ID - greater than 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {// try to read from file and look for ID
			// Start from start of file and loop until valid ID is found or search returned to start position
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);// Look for proper position in file
				record.read(input);// Get record from file
				// If valid ID exist in stop search
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}// end while
		}// end try
		catch (IOException e) {
		}// end catch

		return someoneToDisplay;
	}// end isSomeoneToDisplay
	
	/**
     * Handles file-related errors by displaying an error message.
     *
     * @param errorMessage The error message to be displayed.
     */
    private void handleFileError(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage);
        System.exit(1);
    }
    
    /*  * Closes the given RandomAccessFile.
     * @param file The RandomAccessFile to be closed.
     */
    private void closeFile(RandomAccessFile file) {
        try {
            if (file != null)
                file.close();
        } catch (IOException ioException) {
            handleFileError("Error closing file!");
        }
    }
}// end class RandomFile
