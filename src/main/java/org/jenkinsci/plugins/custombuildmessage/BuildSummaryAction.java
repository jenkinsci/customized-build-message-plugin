package org.jenkinsci.plugins.custombuildmessage;

import org.kohsuke.stapler.export.ExportedBean;

import hudson.Extension;
import hudson.model.InvisibleAction;

@Extension
@ExportedBean(defaultVisibility=2)
public class BuildSummaryAction extends InvisibleAction {

	private String projName;
	private int buildNo;

	public String getProjName() {
		return projName;
	}

	public int getBuildNo() {
		return buildNo;
	}

	public String getMsg() {
		return msg;
	}

	private String msg;

	public BuildSummaryAction() {
	}

	public BuildSummaryAction(String projName, int buildNo, String msg) {
		this.projName = projName;
		this.buildNo = buildNo;
		this.msg = msg;
	}

}
