package org.jenkinsci.plugins.custombuildmessage;

import java.io.IOException;

import org.jenkinsci.plugins.custombuildmessage.util.MsgUtil;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Environment;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.Run.RunnerAbortedException;
import hudson.model.listeners.RunListener;

@Extension
public class CustomMsgListener extends RunListener<Run<?, ?>> {

	@Override
	public void onCompleted(@SuppressWarnings("rawtypes") Run r, TaskListener listener) {
		try {
			CustomMsgJobProperty prop = (CustomMsgJobProperty) r.getParent().getProperty(CustomMsgJobProperty.class);
			if (prop != null && prop.isOn()) {
				if (prop.isInsertOnFinish()) {
					setBuildDesc(r, listener, prop);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onCompleted(r, listener);
	}

	@Override
	public void onStarted(@SuppressWarnings("rawtypes") Run r, TaskListener listener) {
		super.onStarted(r, listener);
		try {

			CustomMsgJobProperty prop = (CustomMsgJobProperty) r.getParent().getProperty(CustomMsgJobProperty.class);
			if (prop != null && prop.isOn()) {
				if (!prop.isInsertOnFinish()) {
					setBuildDesc(r, listener, prop);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setBuildDesc(Run r, TaskListener listener, CustomMsgJobProperty prop)
			throws IOException, InterruptedException {
		EnvVars envVars = r.getEnvironment(listener);
		String descMsg = prop.getBriefDesc();
		if (MsgUtil.isNotNullNEmpty(descMsg)) {
			descMsg = MsgUtil.substituteEnvVars(descMsg, envVars, true);
			r.setDescription(descMsg);
		}
	}

	@Override
	public Environment setUpEnvironment(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws IOException, InterruptedException, RunnerAbortedException {
		// TODO Auto-generated method stub
		return super.setUpEnvironment(build, launcher, listener);
	}

}
