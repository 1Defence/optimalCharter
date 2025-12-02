package com.optimalcharter;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.optimalcharter.CharterOptimalRoute.Result;
import com.optimalcharter.CharterOptimalRoute.Cargo;

import com.optimalcharter.PortData.*;
import static com.optimalcharter.PortData.PORT_TASKS_DATA;
import static com.optimalcharter.PortData.PORT_AREAS_DATA;

@Slf4j
@PluginDescriptor(
		name = "Optimal Charter",
		description = "Calculates an optimal path for your ABA port tasks"
)
public class OptimalCharterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OptimalCharterConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private OptimalCharterOverlay overlay;

	@Inject
	private PathOverlay pathOverlay;

	@Inject
	private ItemManager itemManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientThread clientThread;

	int[] currentTaskValues = new int[5];

	Result currentResult = null;

	boolean initialPathSet = false;

	int currentPortDestination = 0;

	public List<Widget> activeWidgets = null;


	private Widget button = null;

	public List<String> taskStrings;


	@Provides
	OptimalCharterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OptimalCharterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		overlayManager.add(pathOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.remove(pathOverlay);
		clientThread.invoke(this::hideButton);
	}

	public static PortAreaData getAreaDataByPickup(WorldPoint pickup) {
		if (pickup == null) return null;

		for (PortAreaData data : PORT_AREAS_DATA.values()) {
			if (pickup.equals(data.pickup)) {
				//log.debug("area by pickup is "+data.name);
				return data;
			}
		}
		return null;
	}

	void SetNextPath(){
		if(currentResult == null)
			return;

		PortAreaData currentPort = getAreaDataByPickup(client.getLocalPlayer().getWorldLocation());

		if(currentPort == null)
			return;

		int currentPortIndex = -1;
		for (int i=0; i<currentResult.uniquePortsInOrder.size(); i++)
		{
			if(currentResult.uniquePortsInOrder.get(i).name.equals(currentPort.name)){
				//found the port index
				currentPortIndex = i;
				currentPortDestination = i+1;
				break;
			}
		}


		int nextPortIndex = -1;
		for(int i=currentPortIndex; i<currentResult.uniquePortsInOrder.size(); i++){
			if(!currentResult.uniquePortsInOrder.get(i).name.equals(currentPort.name)){
				//found first non dupe
				nextPortIndex = i;
				break;
			}
		}

		if(nextPortIndex != -1)
			GeneratePath(currentResult.uniquePortsInOrder.get(currentPortIndex).dock,currentResult.uniquePortsInOrder.get(nextPortIndex).dock);
	}

	void SetPortTask(int taskIndex, int taskValue){
		currentTaskValues[taskIndex] = taskValue;
		if(taskValue != 0)
		{
			log.debug("Setting "+taskIndex+" : "+taskValue);
			if(!PORT_TASKS_DATA.containsKey(taskValue)){
				log.error("PORT TASK NOT FOUND "+taskValue+"",new Throwable("TASK NOT FOUND"));
			}
		}
	}


	void FindFurthestPath(){

		List<Cargo> tasks = new ArrayList<>();

		for(int i=0; i<5; i++){
			PortTaskData dataI = PORT_TASKS_DATA.get(currentTaskValues[i]);
			if(dataI != null)
			{
				tasks.add(new Cargo(dataI.start, dataI.end, "task"+i));
			}
		}

		currentResult = CharterOptimalRoute.findOptimalRoute(tasks);
		currentPortDestination = 0;
		GeneratePath(currentResult.uniquePortsInOrder.get(0).dock,currentResult.uniquePortsInOrder.get(1).dock);

		taskStrings = new ArrayList<>();
		String lastPortName = "";
		int lastTaskIndex = 0;

		int pickupCount = 0;
		int deliverCount = 0;

		for(int i=0; i<currentResult.steps.size(); i++){
			CharterOptimalRoute.RouteStep step = currentResult.steps.get(i);
			PortData.PortAreaData portAreaData = step.port;

			String portName = portAreaData.name;
			if(portName.equals(lastPortName)){

				//add to it

				String taskText = "["+portName+"]";
				if(step.action.equals("Pickup")){
					pickupCount++;
				}else if(step.action.equals("Deliver")){
					deliverCount++;
				}

				if(pickupCount > 0)
					taskText += " | P "+pickupCount;
				if(deliverCount > 0)
					taskText += " | D "+deliverCount;

				taskStrings.set(lastTaskIndex,taskText);

			}else{

				pickupCount = 0;
				deliverCount = 0;
				String taskText = "["+portName+"]";
				if(step.action.equals("Pickup")){
					taskText += " | P 1";
					pickupCount = 1;
				}else if(step.action.equals("Deliver")){
					taskText += " | D 1";
					deliverCount = 1;
				}
				taskStrings.add(taskText);
				lastTaskIndex = taskStrings.size()-1;

			}

			lastPortName = portName;
		}

	}

	void GeneratePath(WorldPoint start, WorldPoint end){
		Map<String, Object> data = new HashMap<>();
		data.put("start", start);
		data.put("target", end);
		eventBus.post(new PluginMessage("shortestpath", "path", data));
	}


	int DistanceBetweenPorts(PortAreaData start, PortAreaData end){
		return start.dock.distanceTo(end.dock);
	}


	@Subscribe
	public void onPluginMessage(PluginMessage event) {
		if(event.getName().equals("transports") && !initialPathSet){
			initialPathSet = true;
			GeneratePath(currentResult.uniquePortsInOrder.get(0).dock,currentResult.uniquePortsInOrder.get(1).dock);
		}
	}



	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		int baseId = VarbitID.PORT_TASK_SLOT_0_ID;
		int increment = 3;

		int varbitId = varbitChanged.getVarbitId();
		for(int i=0; i<5; i++){
			int portVarbitId = baseId+(increment*i);
			if(varbitId == portVarbitId){
				SetPortTask(i,client.getVarbitValue(portVarbitId));
			}
		}

		if(varbitId == VarbitID.SAILING_CARRYING_CARGO){

			if(currentResult != null)
			{
				SetNextPath();
			}
		}
	}


	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged e){
		if(e.getContainerId() != InventoryID.EQUIPMENT.getId())
			return;
		ItemContainer equips = client.getItemContainer(InventoryID.EQUIPMENT);
		if(equips != null)
		{
			Item crate = equips.getItem(3);
			if(crate != null){
				ItemComposition crateHeld = client.getItemDefinition(crate.getId());
				currentCrateModel = crateHeld.getInventoryModel();
			}else{
				currentCrateModel = -1;
			}
		}
	}

	int currentCrateModel = -1;
	@Subscribe
	public void onWidgetClosed(WidgetClosed e)
	{
		if(e.getGroupId() == InterfaceID.PORT_TASK_BOARD){
			clientThread.invokeLater(()->
			{
				activeWidgets = null;
			});
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e)
	{
		if(e.getGroupId() == InterfaceID.PORT_TASK_BOARD){
			clientThread.invokeLater(()->
			{
				Widget boardWidget = client.getWidget(941,3);
				if(boardWidget != null){
					createButton(client.getWidget(941,0));
					int desiredModelId = BoardTaskData.GetModelIdForLocation(client.getLocalPlayer().getWorldLocation());
					activeWidgets = new ArrayList<>();
					Widget[] boardChildren = boardWidget.getDynamicChildren();
					if(boardChildren != null){

						//0 to 42, +6
						int increment = 6;
						for(int i=0; i<8; i++){
							int modelId = boardChildren[i*increment].getModelId();
							if(modelId == desiredModelId){
								activeWidgets.add(boardChildren[i*increment]);
							}
						}

					}


				}

			});
		}else if(e.getGroupId() == InterfaceID.SAILING_LOG){
			clientThread.invokeLater(()->
			{
				Widget logWidget = client.getWidget(935,1);
				if(logWidget != null){
					createButton(logWidget);
				}
			});
		}

	}

	private void hideButton()
	{
		if (button == null)
		{
			return;
		}

		button.setHidden(true);
		button = null;
	}

	private void createButton(Widget parent)
	{

		if (parent == null)
		{
			return;
		}

		hideButton();

		button = parent.createChild(-1, WidgetType.GRAPHIC);
		button.setOriginalHeight(51);
		button.setOriginalWidth(51);
		button.setOriginalX(434);
		button.setOriginalY(380);
		button.setSpriteId(1211);
		button.setAction(0, "Generate optimal path");
		button.setOnOpListener((JavaScriptCallback) (e) -> clientThread.invokeLater(this::ButtonPress));
		button.setHasListener(true);
		button.revalidate();

		button.setOnMouseOverListener((JavaScriptCallback) (e) -> button.setSpriteId(1213));
		button.setOnMouseLeaveListener((JavaScriptCallback) (e) -> button.setSpriteId(1211));
	}

	void ButtonPress(){
		clientThread.invokeLater(this::FindFurthestPath);
	}


}
