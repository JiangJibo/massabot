package com.massabot.control.model;

import java.util.Date;

/**
 * 语音识别指令
 * 
 * @since 2017年4月21日 下午3:15:34
 * @version $Id$
 * @author JiangJibo
 *
 */
public class VoiceCommand {

	private Integer id;

	private String text;

	private String gcode;

	private Date createDate;

	private Date updateDate;

	private Boolean enableFlag;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text == null ? null : text.trim();
	}

	public String getGcode() {
		return gcode;
	}

	public void setGcode(String gcode) {
		this.gcode = gcode == null ? null : gcode.trim();
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Boolean getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(Boolean enableFlag) {
		this.enableFlag = enableFlag;
	}
}