package com.example.parserEngine;

import java.util.Map;

public record ValueSet(long index, Map<String, Object> map) {

	@Override
	public String toString() {
		return "[index=" + index + ", map=" + map + "]";
	} 
 
}
