package com.catangame.catan.utils.glwrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.catangame.catan.local.Framework;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

public class RenderTarget {
    SpriteBatch sb;
    ShapeRenderer sr;
    org.jsfml.graphics.RenderTarget rt;

    public RenderTarget() {
        if( Framework.usingGdx) {
            sb = new SpriteBatch();
            sr = new ShapeRenderer();
        }
    }
    public void setSFML_RT(org.jsfml.graphics.RenderTarget rt) {
        this.rt = rt;
    }

    public void draw(Renderable renderable) {
        renderable.draw(this);
    }
}
