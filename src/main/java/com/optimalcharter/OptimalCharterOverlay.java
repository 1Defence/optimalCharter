package com.optimalcharter;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class OptimalCharterOverlay extends Overlay
{
    private final Client client;
    private final OptimalCharterPlugin plugin;


    @Inject
    private OptimalCharterOverlay(Client client, OptimalCharterPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(PRIORITY_HIGHEST);
        drawAfterInterface(InterfaceID.TOPLEVEL_DISPLAY);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        if(plugin.activeWidgets != null){
            for (Widget widget : plugin.activeWidgets)
            {
                renderWidget(graphics,widget,Color.green);
            }
        }
        return null;
    }

    private void renderWidget(Graphics2D g, Widget w, Color color)
    {
        g.setColor(color);
        g.draw(w.getBounds());
    }


}