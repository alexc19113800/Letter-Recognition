/*
The MIT License (MIT)

Copyright (c) 2017 Ralf Th. Pietsch <ratopi@abwesend.de>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
 

import java.io.File;
import java.io.IOException;

public class MnistLabelProvider extends MnistFileHelper
{
	private byte currentValue;

	public MnistLabelProvider( final File mnistLabelFile ) throws IOException
	{
		super( mnistLabelFile, 2049 );
		System.out.println( mnistLabelFile.getName() + " contains " + getNumberOfItems() + " labels" );
	}

	public void selectNext() throws IOException
	{
		incrementCurrentIndex();
		final byte[] buffy = readData( new byte[ 1 ] );
		currentValue = buffy[ 0 ];
	}

	public byte getCurrentValue()
	{
		return currentValue;
	}

}
