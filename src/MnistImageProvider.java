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
 

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MnistImageProvider extends MnistFileHelper
{
	private int imageWidth;
	private int imageHeight;
	private byte[] currentData;

	public MnistImageProvider( final File mnistImageFile ) throws IOException
	{
		super( mnistImageFile, 2051 );

		imageHeight = (int) readUnsigned32();
		imageWidth = (int) readUnsigned32();

		System.out.println( mnistImageFile.getName() + " contains " + getNumberOfItems() + " images with " + imageWidth + "x" + imageHeight + " pixels" );
	}

	public int getImageWidth()
	{
		return imageWidth;
	}

	public int getImageHeight()
	{
		return imageHeight;
	}

	public void selectNext() throws IOException
	{
		if ( !hasNext() )
		{
			throw new IOException( "No more data" );
		}

		currentData = readData( new byte[ imageWidth * imageHeight ] );

		incrementCurrentIndex();
	}

	public byte[] getCurrentData()
	{
		return currentData;
	}

	public BufferedImage getDataAsBufferedImage( final byte[] values )
	{
		final BufferedImage image = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY );

		int index = 0;
		for ( int y = 0; y < imageHeight; y++ )
		{
			for ( int x = 0; x < imageWidth; x++, index++ )
			{
				final byte data = values[ index ];
				int gray = 255 - ( ( (int) data ) & 0xFF );
				int rgb = gray | ( gray << 8 ) | ( gray << 16 );
				image.setRGB( x, y, rgb );
			}
		}

		return image;
	}

}
