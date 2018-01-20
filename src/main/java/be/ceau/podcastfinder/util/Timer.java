package be.ceau.podcastfinder.util;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class Timer {

	private final String msg;
	private final long start = System.currentTimeMillis();
	private long mark = System.currentTimeMillis();

	public Timer(String msg) {
		Objects.requireNonNull(msg);
		this.msg = msg;
	}
	
	public String mark(String tag) {
		long now = System.currentTimeMillis();
		try {
			return new StringBuilder()
					.append(StringUtils.leftPad(String.valueOf(now - mark), 7))
					.append(" ms\t")
					.append(tag)
					.append("\t")
					.append(msg)
					.toString();
		} finally {
			mark = now;
		}
	}

	public String total() {
		return new StringBuilder()
				.append(StringUtils.leftPad(String.valueOf(System.currentTimeMillis() - start), 7))
				.append(" ms\ttotal")
				.append("\t")
				.append(msg)
				.toString();
	}
	
}
