package net.skuratani.simpleorm4j.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * <p>標準出力クラス</p>
 * <pre>
 * 標準出力に文字列等を出力する。
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.3
 */
public class StandartOutput {

	/** 標準出力ストリーム */
	private static final OutputStream standardOut = System.out;

	/** 標準出力バッファ */
	private static final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(standardOut));

	/**
	 * <p>標準出力</p>
	 * <pre>
	 * 標準出力に文字列を出力する。
	 * </pre>
	 *
	 * @param str 出力文字列
	 */
	public static void writeln(String str) {
		try {
			bufferedWriter.write(str);
			bufferedWriter.write(System.lineSeparator());
			bufferedWriter.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new RuntimeException(ioe.getMessage(), ioe);
		}
	}
}
