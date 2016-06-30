package com.cooksys.ftd.assessment.filesharing.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileD {
	
	private Integer fileId;
	private String filepath;
	
	public FileD() {
		super();
	}

	public FileD(Integer fileId, String filepath) {
		super();
		this.fileId = fileId;
		this.filepath = filepath;
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		result = prime * result + ((filepath == null) ? 0 : filepath.hashCode());
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
		FileD other = (FileD) obj;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		if (filepath == null) {
			if (other.filepath != null)
				return false;
		} else if (!filepath.equals(other.filepath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FileD [fileId=" + fileId + ", filepath=" + filepath + "]";
	}
	
}
