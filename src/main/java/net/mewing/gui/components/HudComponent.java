/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.components;

import net.mewing.Mewing;
import net.mewing.gui.GridDefinition;
import net.mewing.gui.GridDefinition.RelativeUnit;
import net.mewing.gui.VerticalAlignment;
import net.mewing.gui.colors.Color;
import net.mewing.gui.colors.Colors;
import net.mewing.gui.navigation.HudWindow;
import net.mewing.utils.types.MouseAction;
import net.mewing.utils.types.MouseButton;

public class HudComponent extends Component {
	private final HudWindow hud;
	private final StringComponent statusComponent;

	public HudComponent(String text, HudWindow hud) {
		this.hud = hud;
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		StringComponent nameComponent = new StringComponent(text);
		nameComponent.setVerticalAlignment(VerticalAlignment.Center);
		grid.addChild(nameComponent);

		statusComponent = new StringComponent(hud.activated.getValue() ? "-" : "+");
		statusComponent.setColor(hud.activated.getValue() ? new Color(255, 0, 0) : new Color(0, 255, 0));
		statusComponent.setVerticalAlignment(VerticalAlignment.Center);
		grid.addChild(statusComponent);

		addChild(grid);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				boolean visibility = hud.activated.getValue();
				Mewing.getInstance().guiManager.setHudActive(hud, !visibility);
				e.cancel();
			}
		});
	}

	@Override
	public void update() {
		super.update();

		if (hud.activated.getValue()) {
			statusComponent.setText("-");
			statusComponent.setColor(Colors.Red);
		} else {
			statusComponent.setText("+");
			statusComponent.setColor(Colors.Green);
		}
	}
}
