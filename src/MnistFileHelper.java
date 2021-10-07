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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class MnistFileHelper
{
	private final InputStream inputStream;

	private final long numberOfItems;
	private long currentIndex = -1;

	public MnistFileHelper( final File mnistFile, final long magic ) throws IOException
	{
		if ( mnistFile.getName().endsWith( ".gz" ) )
		{
			this.inputStream = new GZIPInputStream( new FileInputStream( mnistFile ) );
		}
		else
		{
			this.inputStream = new FileInputStream( mnistFile );
		}

		if ( readUnsigned32() != magic ) throw new IOException( "Header is not correct" );

		numberOfItems = readUnsigned32();
	}

	public void close() throws IOException
	{
		inputStream.close();
	}

	public int getNumberOfItems()
	{
		return (int) numberOfItems;
	}

	public boolean hasNext()
	{
		return currentIndex + 1 < numberOfItems;
	}

	public long currentIndex()
	{
		return currentIndex;
	}

	protected void incrementCurrentIndex()
	{
		currentIndex++;
	}

	protected long readUnsigned32() throws IOException
	{
		final byte[] buffy = readData( new byte[ 4 ] );

		long n = 0;
		int shift = 24;
		for ( byte b : buffy )
		{
			final long v = ( ( (long) b ) & 0xFF ) << shift;
			n = n + v;
			shift -= 8;
		}

		return n;
	}

	protected byte[] readData( final byte[] data ) throws IOException
	{
		int n = 0;
		do
		{
			final int len = inputStream.read( data, n, data.length - n );
			if ( len < 1 )
			{
				throw new IOException( "no more data" );
			}
			n += len;
		}
		while ( n < data.length );

		return data;
	}

}
