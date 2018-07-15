package org.jenkinsci.plugins.custombuildmessage.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.AbstractBuild;

public class MsgUtil {

	private static final Logger logger = Logger.getLogger(MsgUtil.class.getName());

	public static String substituteEnvVars(String inputStr, Map<String, String> map) {
		return substituteEnvVars(inputStr, map, false);
	}

	public static boolean isNotNullNEmpty(String msg) {
		return msg != null && !msg.isEmpty();
	}

	public static String extractEnvMsg(AbstractBuild<?, ?> build, PrintStream listenerLogger, EnvVars envVars,
			String filePath) {
		FilePath ws = build.getWorkspace();
		FilePath fp = null;
		String envSettingString = "";
		if (ws != null) {
			try {
				if (ws.isRemote()) {
					fp = new FilePath(ws.getChannel(), filePath);
					listenerLogger.println(String.format("Read remote file[%s]", fp.getRemote()));
				} else {
					fp = new FilePath(new File(filePath));
					listenerLogger.println(String.format("Read local file[%s]", fp.getRemote()));
				}
				envSettingString = fp.readToString();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			listenerLogger.println("Fail to get workspace.");
		}
		return envSettingString;
	}

	public static void updateEnvVars(String inputStr, EnvVars envVars, PrintStream listenerLogger) {
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

	public static String substituteEnvVars(String inputStr, Map<String, String> map, boolean keepOriginSymbol) {
		String returnStr = "";
		String patternStr = "\\$\\{(\\w+)\\}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputStr);
		boolean isFound = matcher.find();
		int beginIdx = 0;
		while (isFound) {
			logger.finer(matcher.start() + "-" + matcher.end());

			returnStr += inputStr.substring(beginIdx, matcher.start());
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				String groupStr = matcher.group(i);

				if (map.containsKey(groupStr)) {
					returnStr += map.get(groupStr);
				} else {
					logger.finer(String.format("%s doesn't exist in envrionment variables!", groupStr));

					if (keepOriginSymbol) {
						logger.finer("keep original symbols");
						returnStr += matcher.group(0);
					}
				}
				logger.finer(i + ":" + groupStr);
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
}
