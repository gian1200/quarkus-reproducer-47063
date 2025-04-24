package org.acme.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class DummyObject {

	private String randomString;
	private String method;

}
