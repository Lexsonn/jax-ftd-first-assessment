package com.cooksys.ftd.assessment.filesharing.model;

public class UserFile {
	
	private Integer userId;
	private Integer fileId;
	
	public UserFile() {
		super();
	}
	
	public UserFile(Integer userId, Integer fileId) {
		super();
		this.userId = userId;
		this.fileId = fileId;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getFileId() {
		return fileId;
	}
	
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserFile other = (UserFile) obj;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "UserFile [userId=" + userId + ", fileId=" + fileId + "]";
	}
	
}
