package com.optimalcharter;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;

class PathOverlay extends OverlayPanel
{
    private final Client client;
    private final OptimalCharterPlugin plugin;
    private final OptimalCharterConfig config;

    @Inject
    private PathOverlay(Client client, OptimalCharterPlugin plugin, OptimalCharterConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPriority(PRIORITY_LOW);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        if (plugin.currentResult != null)
        {

            final FontMetrics fontMetrics = graphics.getFontMetrics();
            int largestWidth = -1;

            String heldCrate = plugin.currentCrateModel == -1 ? "-" : "Crate ("+PortData.PORT_CRATE_IDS.get(plugin.currentCrateModel)+")";
            AddTextLine(graphics,Color.orange,heldCrate);

            for(int i=0; i<plugin.taskStrings.size(); i++){
                String text = plugin.taskStrings.get(i);
                int lineTextWidth = fontMetrics.stringWidth(text);
                if(lineTextWidth > largestWidth){
                    largestWidth = lineTextWidth;
                }
                Color col = i == plugin.currentPortDestination ? Color.green : Color.white;
                AddTextLine(graphics,col,text);
            }

            int textWidth = Math.max(ComponentConstants.STANDARD_WIDTH, largestWidth);
            panelComponent.setPreferredSize(new Dimension(textWidth, 0));

        }

        return super.render(graphics);
    }

    public void AddTextLine(Graphics2D graphics, Color col, String text){
        graphics.setFont(new Font(FontManager.getRunescapeFont().toString(), Font.PLAIN, 12));

        panelComponent.getChildren().add(LineComponent.builder()
                .left(text)
                .leftColor(col)
                .build());
        panelComponent.setBackgroundColor(new Color(92,80,68,110));
        panelComponent.setBorder(new Rectangle(2,2,2,2));
    }

}
