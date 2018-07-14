package org.jenkinsci.plugins.custombuildmessage;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

@Extension
public class CustomMsgJobProperty extends JobProperty<Job<?, ?>> {

	private String sucMsg;
	private String failMsg;
	private String unstableMsg;
	private String abortMsg;
	private String notRunMsg;
	private String envFile;
	private boolean on;
	private boolean applySuccToAll;

	public boolean isApplySuccToAll() {
		return applySuccToAll;
	}

	public void setApplySuccToAll(boolean applySuccToAll) {
		this.applySuccToAll = applySuccToAll;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	@DataBoundConstructor
	public CustomMsgJobProperty(String sucMsg, String failMsg, String unstableMsg, String abortMsg, String notRunMsg,
			String envFile, boolean on, boolean applySuccToAll) {
		super();
		this.sucMsg = sucMsg;
		this.failMsg = failMsg;
		this.unstableMsg = unstableMsg;
		this.abortMsg = abortMsg;
		this.notRunMsg = notRunMsg;
		this.envFile = envFile;
		this.on = on;
		this.applySuccToAll = applySuccToAll;
	}

	public CustomMsgJobProperty() {
		super();
	}

	public String getSucMsg() {
		return sucMsg;
	}

	public void setSucMsg(String sucMsg) {
		this.sucMsg = sucMsg;
	}

	public String getFailMsg() {
		return failMsg;
	}

	public void setFailMsg(String failMsg) {
		this.failMsg = failMsg;
	}

	public String getUnstableMsg() {
		return unstableMsg;
	}

	public void setUnstableMsg(String unstableMsg) {
		this.unstableMsg = unstableMsg;
	}

	public String getAbortMsg() {
		return abortMsg;
	}

	public void setAbortMsg(String abortMsg) {
		this.abortMsg = abortMsg;
	}

	public String getNotRunMsg() {
		return notRunMsg;
	}

	public void setNotRunMsg(String notRunMsg) {
		this.notRunMsg = notRunMsg;
	}

	public String getEnvFile() {
		return envFile;
	}

	public void setEnvFile(String envFile) {
		this.envFile = envFile;
	}

	@Override
	public JobPropertyDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return super.getDescriptor();
	}

	@Override
	public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
		// TODO Auto-generated method stub
		return super.prebuild(build, listener);
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		return super.perform(build, launcher, listener);
	}

	@Extension
	public static final class DescriptorImpl extends JobPropertyDescriptor {

		@Override
		public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			if (formData.optBoolean("on")) {
				return (CustomMsgJobProperty) super.newInstance(req, formData);
			}
			return null;
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
			return true;
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			save();
			return super.configure(req, json);
		}

		public DescriptorImpl() {
		}

		public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
			return FormValidation.ok();
		}

		public String getDisplayName() {
			return "Customized Prompt Message";
		}

	}

}
