package org.jenkinsci.plugins.custombuildmessage;

import org.kohsuke.stapler.export.ExportedBean;

import hudson.Extension;
import hudson.model.BuildBadgeAction;

@Extension
@ExportedBean(defaultVisibility=2)
public class PromptMsgBadgeAction implements BuildBadgeAction {

	private String promptUrl;
	private int buildNo;

	public PromptMsgBadgeAction() {
		// TODO Auto-generated constructor stub
	}
	
	public PromptMsgBadgeAction(String promptUrl, int buildNo) {
		this.promptUrl = promptUrl;
		this.buildNo = buildNo;
	}	

	public String getPromptUrl() {
		return promptUrl;
	}

	public int getBuildNo() {
		return buildNo;
	}

	@Override
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Message";
	}

	@Override
	public String getUrlName() {
		// TODO Auto-generated method stub
		return null;
	}

}
