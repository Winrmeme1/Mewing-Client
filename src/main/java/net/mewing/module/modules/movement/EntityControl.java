/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.module.modules.movement;

import net.mewing.Mewing;
import net.mewing.event.events.TickEvent.Post;
import net.mewing.event.events.TickEvent.Pre;
import net.mewing.event.listeners.TickListener;
import net.mewing.interfaces.IAbstractHorse;
import net.mewing.module.Category;
import net.mewing.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public class EntityControl extends Module implements TickListener
{
    public EntityControl()
    {
        super("EntityControl");
        setDescription("Allows you to control entities without needing a saddle.");
        setCategory(Category.of("Movement"));
    }

    @Override
    public void onDisable()
    {
        Mewing.getInstance().eventManager.RemoveListener(TickListener.class, this);

        if (MC.level != null)
        {
            for (Entity entity : Mewing.getInstance().entityManager.getEntities())
            {
                if (entity instanceof AbstractHorse)
                    ((IAbstractHorse) entity).setSaddled(false);
            }
        }
    }

    @Override
    public void onEnable()
    {
        Mewing.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle()
    {

    }

    @Override
    public void onTick(Pre event)
    {

    }

    @Override
    public void onTick(Post event)
    {
        if (MC.level != null)
        {
            for (Entity entity : Mewing.getInstance().entityManager.getEntities())
            {
                if (entity instanceof AbstractHorse)
                    ((IAbstractHorse) entity).setSaddled(true);
            }
        }
    }
}
