package org.jenkinsci.plugins.custombuildmessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private PrintStream listenerLogger;

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
		listenerLogger = listener.getLogger();
		listenerLogger.println(PLUGINNAME + " - running...");

		String msg = "";
		Result res = build.getResult();
		if (res != null) {
			switch (res.ordinal) {
			case 0:
				msg = sucMsg;
				break;
			case 1:
				msg = unstableMsg;
				break;
			case 2:
				msg = failMsg;
				break;
			case 3:
				msg = notRunMsg;
				break;
			case 4:
				msg = abortMsg;
				break;
			default:
				msg = "The build status is not supported";
				break;
			}

			EnvVars envVars = build.getEnvironment(listener);
			try {
				String filePath = substituteEnvVars(envFile, envVars);
				listenerLogger.println(String.format("File path after variable subsititute : [%s]", filePath));

				if (envFile != null && !envFile.isEmpty()) {

					FilePath ws = build.getWorkspace();
					FilePath fp = null;
					if (ws != null) {
						if (ws.isRemote()) {
							fp = new FilePath(ws.getChannel(), filePath);
							listenerLogger.println(String.format("Read remote file[%s]", fp.getRemote()));
						} else {
							fp = new FilePath(new File(filePath));
							listenerLogger.println(String.format("Read local file[%s]", fp.getRemote()));
						}
						updateEnvVarsByHand(fp.readToString(), envVars);
					} else {
						listenerLogger.println("Fail to get workspace.");
					}

				}

			} catch (Exception e) {
				listenerLogger.println(e);
			} finally {
				msg = substituteEnvVars(msg, envVars);
			}

			int buildNo = build.number;
			String projName = build.getProject().getName();
			build.addAction(new BuildSummaryAction(projName, buildNo, msg));
			build.addAction(new PromptMsgBadgeAction(msg, buildNo));
		}

		return true;
	}

	private void updateEnvVarsByHand(String inputStr, EnvVars envVars) {
		int lineNo = 1;
		String[] items = inputStr.split("\r\n");
		for (String item : items) {
			String[] keyVal = item.split("=");
			if (keyVal.length == 2) {
				listenerLogger.println(item);
				envVars.put(keyVal[0], keyVal[1]);
			} else {
				listenerLogger.println(String.format("Configuration format error on line[%d]: %s", lineNo, item));
			}
			++lineNo;
		}
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public BuildStepDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return (DescriptorImpl) super.getDescriptor();
	}

	private void updateEnvVars(String envFilePath, Map<String, String> envVars) {
		try {
			updateEnvVars(new FileInputStream(envFilePath), envVars);
		} catch (FileNotFoundException e) {
			listenerLogger.println(String.format("%s %s didn't exist, ingore the environment variable update",
					PLUGINNAME, envFilePath));
		} catch (Exception e) {
			listenerLogger.println(e);
		}
	}

	private void updateEnvVars(InputStream fileInputStream, Map<String, String> envVars) throws IOException {

		Properties props = new Properties();
		props.load(fileInputStream);
		Set<String> set = props.stringPropertyNames();
		for (String s : set) {
			envVars.put(s, props.getProperty(s));
		}

	}

	private String substituteEnvVars(String inputStr, Map<String, String> map) {
		return substituteEnvVars(inputStr, map, false);
	}

	private String substituteEnvVars(String inputStr, Map<String, String> map, boolean keepOriginSymbol) {
		String returnStr = "";
		String patternStr = "\\$\\{(\\w+)\\}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputStr);
		boolean isFound = matcher.find();
		int beginIdx = 0;
		while (isFound) {
			logger.info(matcher.start() + "-" + matcher.end());

			returnStr += inputStr.substring(beginIdx, matcher.start());
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				String groupStr = matcher.group(i);

				if (map.containsKey(groupStr)) {
					returnStr += map.get(groupStr);
				} else {
					logger.info(String.format("%s doesn't exist in envrionment variables!", groupStr));

					if (keepOriginSymbol) {
						logger.info("keep original symbols");
						returnStr += matcher.group(0);
					}
				}
				logger.info(i + ":" + groupStr);
			}
			beginIdx = matcher.end();
			if (beginIdx + 1 <= inputStr.length()) {
				isFound = matcher.find(matcher.end());
			} else {
				break;
			}
		}

		returnStr += inputStr.substring(beginIdx);
		return returnStr;
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
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Customized Result Prompt Message";
		}

	}

}
