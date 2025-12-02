package com.optimalcharter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OptimalCharterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OptimalCharterPlugin.class);
		RuneLite.main(args);
	}
}