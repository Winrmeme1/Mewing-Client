/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.gui.colors;

import net.mewing.Mewing;
import net.mewing.event.events.TickEvent;
import net.mewing.event.listeners.TickListener;

public abstract class AnimatedColor extends Color implements TickListener {
    public AnimatedColor() {
        super(255, 0, 0);
        Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onTick(TickEvent.Pre event) { }
    
    @Override
    public abstract void onTick(TickEvent.Post event);
}
