package com.pheonix.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class JwtUpdateRequest {
	
	@NotNull @NotEmpty
	private String id;
	@NotEmpty
	private String modifiedBy;
	
	public JwtUpdateRequest() {}	
	
	public JwtUpdateRequest(@NotNull @NotEmpty String id, @NotEmpty String modifiedBy) {
		super();
		this.id = id;
		this.modifiedBy = modifiedBy;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	
	
}
