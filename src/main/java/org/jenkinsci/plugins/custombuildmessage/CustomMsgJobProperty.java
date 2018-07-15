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

	private String briefDesc;
	private boolean on;
	private boolean insertOnFinish;

	public boolean isInsertOnFinish() {
		return insertOnFinish;
	}

	public void setInsertOnFinish(boolean insertOnFinish) {
		this.insertOnFinish = insertOnFinish;
	}

	public String getBriefDesc() {
		return briefDesc;
	}

	public void setBriefDesc(String briefDesc) {
		this.briefDesc = briefDesc;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	@DataBoundConstructor
	public CustomMsgJobProperty(boolean on, String briefDesc, boolean insertOnFinish) {
		super();
		this.on = on;
		this.briefDesc = briefDesc;
		this.insertOnFinish = insertOnFinish;
	}

	public CustomMsgJobProperty() {
		super();
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
			return "Customized Build Description";
		}

	}

}
