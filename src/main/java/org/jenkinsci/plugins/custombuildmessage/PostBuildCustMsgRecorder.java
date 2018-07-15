package org.jenkinsci.plugins.custombuildmessage;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.jenkinsci.plugins.custombuildmessage.util.MsgUtil;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

@Extension
public class PostBuildCustMsgRecorder extends Recorder {

	private String sucMsg;
	private String failMsg;
	private String unstableMsg;
	private String abortMsg;
	private String notRunMsg;
	private String envFile;

	private static final Logger logger = Logger.getLogger(PostBuildCustMsgRecorder.class.getName());
	private static final Object PLUGINNAME = "[CustomizedPostBuildMessagePlugin]";

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

	public PostBuildCustMsgRecorder() {
		// TODO Auto-generated constructor stub
	}

	@DataBoundConstructor
	public PostBuildCustMsgRecorder(String sucMsg, String failMsg, String unstableMsg, String abortMsg,
			String notRunMsg, String envFile) {
		this.sucMsg = sucMsg;
		this.failMsg = failMsg;
		this.unstableMsg = unstableMsg;
		this.abortMsg = abortMsg;
		this.notRunMsg = notRunMsg;
		this.envFile = envFile;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {

		PrintStream listenerLogger = listener.getLogger();
		listenerLogger.println(PLUGINNAME + " - running...");
		String msg = "";
		Result res = build.getResult();
		if (res != null) {
			switch (res.ordinal) {
			case 0:
				msg = this.sucMsg;
				break;
			case 1:
				msg = this.unstableMsg;
				break;
			case 2:
				msg = this.failMsg;
				break;
			case 3:
				msg = this.notRunMsg;
				break;
			case 4:
				msg = this.abortMsg;
				break;
			default:
				msg = "The build status is not supported";
				break;
			}

			EnvVars envVars = build.getEnvironment(listener);
			try {
				String filePath = MsgUtil.substituteEnvVars(this.envFile, envVars);
				listenerLogger.println(String.format("File path after variable subsititute : [%s]", filePath));

				if (MsgUtil.isNotNullNEmpty(this.envFile)) {
					String envSettingString = MsgUtil.extractEnvMsg(build, listenerLogger, envVars, filePath);
					MsgUtil.updateEnvVars(envSettingString, envVars, listenerLogger);
				}

			} catch (Exception e) {
				listenerLogger.println(e);
			} finally {
				msg = MsgUtil.substituteEnvVars(msg, envVars);
			}

			int buildNo = build.number;
			String projName = build.getProject().getName();
			build.addAction(new BuildSummaryAction(projName, buildNo, msg));
			build.addAction(new PromptMsgBadgeAction(msg, buildNo));
		}

		return true;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public BuildStepDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public DescriptorImpl() {
			super();
		}

		public DescriptorImpl(Class<? extends Publisher> clazz) {
			super(clazz);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Customized Result Prompt Message";
		}

	}

}
